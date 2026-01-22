/*
ACR-a6bccd1742aa4f90b97c84ea1a8b61ff
ACR-b7f9270e2e1544f4ad5a7a88c5d52b8b
ACR-5ca8a6569a8f40429b098fb721b8e283
ACR-dea849c9fa7449b68170d9e957c5e45a
ACR-60c74ceed94644318ef1e0fe51267c2e
ACR-069616516cdf4c798d07e4d21625241d
ACR-f611d321b2f14e7d8dc2b24aef43509b
ACR-3bd2c76a48ed490ea58a721644c24ee9
ACR-00d0f5bf193543ca9b0d31eb370f41cb
ACR-2e00c5c899914f5d94010465194b940d
ACR-917b1c40a3e94d62ac5503c5490c1c1c
ACR-a85e68ad64af412b982d8328506ccdc1
ACR-df12a5fbe1ac40f899c5302fb5b9afcc
ACR-5fbaea9a3f794a9d969e65ad1a18780f
ACR-405ea01c47994d65ac51f68c3e8515cb
ACR-0afd5ecec3174f0fb6988f5bd073541f
ACR-6d73a2df722f468894fcc6665ba3a6ec
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
