/*
ACR-4c14ae55175f490c82d69e27865c66ae
ACR-32b6675712c84080925820c33a2dd57c
ACR-96e8af24cbb949168fdbeb1f8154aeed
ACR-875c8b554af14c7f9c0126f718184da9
ACR-81ad060c6a19446389c7fb780cf01fbc
ACR-f25c12bcae3a49b8b59472ae64ed5fd6
ACR-ab3c12c5efd2426b922fa55541bb7e8e
ACR-de607005a0374468a137508169b8c130
ACR-f2aecc658c404528aed0976db306d42a
ACR-25dfd3770cef4d8aacfa7315627702dd
ACR-d721938fc22f490b96e3a817d25d1b94
ACR-63fe84b6132c4e15971ebb915f02ee8e
ACR-e88102fc85964ff6b0aa9f21deb54fd2
ACR-52b0d238e874456abf7d6d1fbb08fabe
ACR-ff2adf52fee246d1b6e10f8f98055fd0
ACR-972a53e6adf14c30bb680869dd1ab66f
ACR-266126cc107546fa95281543083ab957
 */
package org.sonarsource.sonarlint.core.tracking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import jetbrains.exodus.env.EnvironmentConfig;
import jetbrains.exodus.env.Environments;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.commons.KnownFinding;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverconnection.issues.Findings;
import org.sonarsource.sonarlint.core.serverconnection.storage.InstantBinding;
import org.sonarsource.sonarlint.core.serverconnection.storage.TarGzUtils;
import org.sonarsource.sonarlint.core.serverconnection.storage.UuidBinding;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class XodusKnownFindingsStore {

  static final String KNOWN_FINDINGS_STORE = "known-findings-store";
  private static final String CONFIGURATION_SCOPE_ID_ENTITY_TYPE = "Scope";
  private static final String CONFIGURATION_SCOPE_ID_TO_FILES_LINK_NAME = "files";
  private static final String PATH_PROPERTY_NAME = "path";
  private static final String NAME_PROPERTY_NAME = "name";
  private static final String FILE_TO_ISSUES_LINK_NAME = "issues";
  private static final String FILE_TO_SECURITY_HOTSPOTS_LINK_NAME = "hotspots";
  private static final String UUID_PROPERTY_NAME = "uuid";
  private static final String SERVER_KEY_PROPERTY_NAME = "serverKey";
  private static final String INTRODUCTION_DATE_PROPERTY_NAME = "introductionDate";
  private static final String RULE_KEY_PROPERTY_NAME = "ruleKey";
  private static final String RANGE_HASH_PROPERTY_NAME = "rangeHash";
  private static final String LINE_HASH_PROPERTY_NAME = "lineHash";
  private static final String START_LINE_PROPERTY_NAME = "startLine";
  private static final String START_LINE_OFFSET_PROPERTY_NAME = "startLineOffset";
  private static final String END_LINE_PROPERTY_NAME = "endLine";
  private static final String END_LINE_OFFSET_PROPERTY_NAME = "endLineOffset";
  private static final String MESSAGE_BLOB_NAME = "message";
  static final String BACKUP_TAR_GZ = "known_findings_backup.tar.gz";
  private final PersistentEntityStore entityStore;
  private final Path xodusDbDir;
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public XodusKnownFindingsStore(Path backupDir, Path workDir) throws IOException {
    xodusDbDir = Files.createTempDirectory(workDir, KNOWN_FINDINGS_STORE);
    var backupFile = backupDir.resolve(BACKUP_TAR_GZ);
    if (Files.isRegularFile(backupFile)) {
      LOG.debug("Restoring previous known findings database from {}", backupFile);
      try {
        TarGzUtils.extractTarGz(backupFile, xodusDbDir);
      } catch (Exception e) {
        LOG.error("Unable to restore known findings backup {}", backupFile);
      }
    }
    LOG.debug("Starting known findings database from {}", xodusDbDir);
    this.entityStore = buildEntityStore();
    entityStore.executeInTransaction(txn -> {
      entityStore.registerCustomPropertyType(txn, Instant.class, new InstantBinding());
      entityStore.registerCustomPropertyType(txn, UUID.class, new UuidBinding());
    });
  }

  public Map<String, Map<Path, Findings>> loadAll() {
    return entityStore.computeInReadonlyTransaction(txn -> StreamSupport.stream(txn.getAll(CONFIGURATION_SCOPE_ID_ENTITY_TYPE).spliterator(), false)
      .collect(groupingBy(
        e -> (String) requireNonNull(e.getProperty(NAME_PROPERTY_NAME)),
        flatMapping(e -> StreamSupport.stream(e.getLinks(CONFIGURATION_SCOPE_ID_TO_FILES_LINK_NAME).spliterator(), false),
          toMap(
            f -> Paths.get((String) requireNonNull(f.getProperty(PATH_PROPERTY_NAME))),
            f -> new Findings(
              StreamSupport.stream(f.getLinks(FILE_TO_ISSUES_LINK_NAME).spliterator(), false)
                .map(XodusKnownFindingsStore::adapt).toList(),
              StreamSupport.stream(f.getLinks(FILE_TO_SECURITY_HOTSPOTS_LINK_NAME).spliterator(), false)
                .map(XodusKnownFindingsStore::adapt).toList()),
            Findings::mergeWith)))));
  }

  private static KnownFinding adapt(Entity storedFinding) {
    var uuid = (UUID) requireNonNull(storedFinding.getProperty(UUID_PROPERTY_NAME));
    var serverKey = (String) storedFinding.getProperty(SERVER_KEY_PROPERTY_NAME);
    var introductionDate = (Instant) requireNonNull(storedFinding.getProperty(INTRODUCTION_DATE_PROPERTY_NAME));
    var ruleKey = (String) requireNonNull(storedFinding.getProperty(RULE_KEY_PROPERTY_NAME));
    var msg = requireNonNull(storedFinding.getBlobString(MESSAGE_BLOB_NAME));
    var startLine = (Integer) storedFinding.getProperty(START_LINE_PROPERTY_NAME);

    TextRangeWithHash textRange = null;
    LineWithHash lineWithHash = null;
    if (startLine != null) {
      var rangeHash = (String) storedFinding.getProperty(RANGE_HASH_PROPERTY_NAME);
      if (rangeHash != null) {
        var startLineOffset = (Integer) storedFinding.getProperty(START_LINE_OFFSET_PROPERTY_NAME);
        var endLine = (Integer) storedFinding.getProperty(END_LINE_PROPERTY_NAME);
        var endLineOffset = (Integer) storedFinding.getProperty(END_LINE_OFFSET_PROPERTY_NAME);
        textRange = new TextRangeWithHash(startLine, startLineOffset, endLine, endLineOffset, rangeHash);
      }
      var lineHash = (String) storedFinding.getProperty(LINE_HASH_PROPERTY_NAME);
      if (lineHash != null) {
        lineWithHash = new LineWithHash(startLine, lineHash);
      }
    }
    return new KnownFinding(
      uuid,
      serverKey,
      textRange,
      lineWithHash,
      ruleKey,
      msg,
      introductionDate);
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
