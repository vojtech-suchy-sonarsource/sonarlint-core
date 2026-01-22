/*
ACR-b22d662caf404f778dc78307548bbcc9
ACR-c8672e3476d644dcac5fe4b9f13e6592
ACR-920f5b189bf2451d96ffc06188e545f1
ACR-3d3f8c8425ce42dcb4943ffa4d0a02f9
ACR-32b04b3144a24d4e9135abc455ad6878
ACR-e2cf8f63e2814d9db1a7df67ebc18124
ACR-40a8a7cdf6b04865877cf8cd300a953c
ACR-a497bfaa20db4131a039980ccd81e50a
ACR-8db12bd2adf641c68c70cfe9226438b6
ACR-493c3ede7221452998c8878aa4967aa8
ACR-a534cdd51c0e4f52bc4ca1b88ca18874
ACR-36c9663f17124d28b806299947643dfc
ACR-b5b68e7b02c44100a9429b25687daff9
ACR-40d1b8c4d78f417b867523d6af4e6491
ACR-4a579bcc2e8d4ae4b842ae80c650d9af
ACR-495280027a8140779d9ffe2fda03bbcf
ACR-a466b02d0a124e2a96d5207c13aefcf4
 */
package org.sonarsource.sonarlint.core.local.only;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import jetbrains.exodus.env.EnvironmentConfig;
import jetbrains.exodus.env.Environments;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssueResolution;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverconnection.storage.InstantBinding;
import org.sonarsource.sonarlint.core.serverconnection.storage.TarGzUtils;
import org.sonarsource.sonarlint.core.serverconnection.storage.UuidBinding;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class XodusLocalOnlyIssueStore {

  static final String LOCAL_ONLY_ISSUE = "xodus-local-only-issue-store";
  private static final String CONFIGURATION_SCOPE_ID_ENTITY_TYPE = "Scope";
  private static final String CONFIGURATION_SCOPE_ID_TO_FILES_LINK_NAME = "files";
  private static final String PATH_PROPERTY_NAME = "path";
  private static final String NAME_PROPERTY_NAME = "name";
  private static final String FILE_TO_ISSUES_LINK_NAME = "issues";
  private static final String UUID_PROPERTY_NAME = "uuid";
  private static final String ISSUE_TO_FILE_LINK_NAME = "file";
  private static final String COMMENT_PROPERTY_NAME = "comment";
  private static final String RESOLVED_STATUS_PROPERTY_NAME = "resolvedStatus";
  private static final String RESOLUTION_DATE_PROPERTY_NAME = "resolvedDate";
  private static final String RULE_KEY_PROPERTY_NAME = "ruleKey";
  private static final String RANGE_HASH_PROPERTY_NAME = "rangeHash";
  private static final String LINE_HASH_PROPERTY_NAME = "lineHash";
  private static final String START_LINE_PROPERTY_NAME = "startLine";
  private static final String START_LINE_OFFSET_PROPERTY_NAME = "startLineOffset";
  private static final String END_LINE_PROPERTY_NAME = "endLine";
  private static final String END_LINE_OFFSET_PROPERTY_NAME = "endLineOffset";
  private static final String MESSAGE_BLOB_NAME = "message";
  static final String BACKUP_TAR_GZ = "local_only_issue_backup.tar.gz";
  private final PersistentEntityStore entityStore;
  private final Path xodusDbDir;
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public XodusLocalOnlyIssueStore(Path backupDir, Path workDir) throws IOException {
    xodusDbDir = Files.createTempDirectory(workDir, LOCAL_ONLY_ISSUE);
    var backupFile = backupDir.resolve(BACKUP_TAR_GZ);
    if (Files.isRegularFile(backupFile)) {
      LOG.debug("Restoring previous local-only issue database from {}", backupFile);
      try {
        TarGzUtils.extractTarGz(backupFile, xodusDbDir);
      } catch (Exception e) {
        LOG.error("Unable to restore local-only issue backup {}", backupFile);
      }
    }
    LOG.debug("Starting local-only issue database from {}", xodusDbDir);
    this.entityStore = buildEntityStore();
    entityStore.executeInTransaction(txn -> {
      entityStore.registerCustomPropertyType(txn, Instant.class, new InstantBinding());
      entityStore.registerCustomPropertyType(txn, UUID.class, new UuidBinding());
      entityStore.registerCustomPropertyType(txn, IssueStatus.class, new IssueStatusBinding());
    });
  }

  public Map<String, List<LocalOnlyIssue>> loadAll() {
    return entityStore.computeInReadonlyTransaction(txn -> StreamSupport.stream(txn.getAll(CONFIGURATION_SCOPE_ID_ENTITY_TYPE).spliterator(), false)
      .collect(groupingBy(
        e -> (String) requireNonNull(e.getProperty(NAME_PROPERTY_NAME)),
        flatMapping(e -> StreamSupport.stream(e.getLinks(CONFIGURATION_SCOPE_ID_TO_FILES_LINK_NAME).spliterator(), false)
          .flatMap(file -> StreamSupport.stream(file.getLinks(XodusLocalOnlyIssueStore.FILE_TO_ISSUES_LINK_NAME).spliterator(), false)
            .map(XodusLocalOnlyIssueStore::adapt)),
          toList()))));
  }

  private static LocalOnlyIssue adapt(Entity storedIssue) {
    var filePath = (String) requireNonNull(storedIssue.getLink(ISSUE_TO_FILE_LINK_NAME).getProperty(PATH_PROPERTY_NAME));
    var uuid = (UUID) requireNonNull(storedIssue.getProperty(UUID_PROPERTY_NAME));
    var status = (IssueStatus) requireNonNull(storedIssue.getProperty(RESOLVED_STATUS_PROPERTY_NAME));
    var resolvedDate = (Instant) requireNonNull(storedIssue.getProperty(RESOLUTION_DATE_PROPERTY_NAME));
    var ruleKey = (String) requireNonNull(storedIssue.getProperty(RULE_KEY_PROPERTY_NAME));
    var msg = requireNonNull(storedIssue.getBlobString(MESSAGE_BLOB_NAME));
    var comment = storedIssue.getBlobString(COMMENT_PROPERTY_NAME);
    var startLine = (Integer) storedIssue.getProperty(START_LINE_PROPERTY_NAME);

    TextRangeWithHash textRange = null;
    LineWithHash lineWithHash = null;
    if (startLine != null) {
      var rangeHash = (String) storedIssue.getProperty(RANGE_HASH_PROPERTY_NAME);
      if (rangeHash != null) {
        var startLineOffset = (Integer) storedIssue.getProperty(START_LINE_OFFSET_PROPERTY_NAME);
        var endLine = (Integer) storedIssue.getProperty(END_LINE_PROPERTY_NAME);
        var endLineOffset = (Integer) storedIssue.getProperty(END_LINE_OFFSET_PROPERTY_NAME);
        textRange = new TextRangeWithHash(startLine, startLineOffset, endLine, endLineOffset, rangeHash);
      }
      var lineHash = (String) storedIssue.getProperty(LINE_HASH_PROPERTY_NAME);
      if (lineHash != null) {
        lineWithHash = new LineWithHash(startLine, lineHash);
      }
    }
    return new LocalOnlyIssue(
      uuid,
      Path.of(filePath),
      textRange,
      lineWithHash,
      ruleKey,
      msg,
      new LocalOnlyIssueResolution(status, resolvedDate, comment));
  }

  private PersistentEntityStore buildEntityStore() {
    var environment = Environments.newInstance(xodusDbDir.toAbsolutePath().toFile(), new EnvironmentConfig()
      .setLogAllowRemote(true)
      .setLogAllowRemovable(true)
      .setLogAllowRamDisk(true));
    var entityStoreImpl = PersistentEntityStores.newInstance(environment);
    entityStoreImpl.setCloseEnvironment(true);
    return entityStoreImpl;
  }

  public void close() {
    entityStore.close();
    FileUtils.deleteQuietly(xodusDbDir.toFile());
  }
}
