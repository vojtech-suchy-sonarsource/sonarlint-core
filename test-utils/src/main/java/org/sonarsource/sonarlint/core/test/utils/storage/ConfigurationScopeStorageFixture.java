/*
ACR-24558c87b6cd48b88918278696ffb6d1
ACR-367cfc4a77f64e02b372a02aa78668a2
ACR-b3d0b1051dcf4908b72e7e2f55ee4b4d
ACR-c06de2331c194b24a447500c532dc468
ACR-0df4099cc4c64e6381abde68173a6494
ACR-bba1e8cad3654274ae47cab108bbbd12
ACR-4b41a521c2af41b8aebe7f216d46d3d3
ACR-d53f550f97bd4b2ab59564fb540c03cd
ACR-482f31c812ba4ad79bd64eb895b4ef2c
ACR-db7abc4537b9481c90d2f6db16c0d00d
ACR-70defd485c2f424f9817ce1ad7c97ff1
ACR-cf58b03e752645c9922013a3ca37896d
ACR-5f673691c0e340988826d94dd9ecb881
ACR-56de1a0526454934b565344ac3e973e1
ACR-b73f9b0f0d154ff0affc765eb557fe9e
ACR-88b79d49e3ff4855836caee883f4a738
ACR-3e5c1003802647ac93d75e5c7e2998f5
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import jetbrains.exodus.env.Environments;
import jetbrains.exodus.util.CompressBackupUtil;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.LocalOnlyIssuesRepository;
import org.sonarsource.sonarlint.core.local.only.IssueStatusBinding;
import org.sonarsource.sonarlint.core.serverconnection.storage.InstantBinding;
import org.sonarsource.sonarlint.core.serverconnection.storage.UuidBinding;

import static java.util.Objects.requireNonNull;

public class ConfigurationScopeStorageFixture {
  public static ConfigurationScopeStorageBuilder newBuilder(String configScopeId) {
    return new ConfigurationScopeStorageBuilder(configScopeId);
  }

  public static class ConfigurationScopeStorageBuilder {
    private final List<LocalOnlyIssue> localOnlyIssues = new ArrayList<>();
    private final String configScopeId;
    private boolean usingXodus;

    public ConfigurationScopeStorageBuilder(String configScopeId) {
      this.configScopeId = configScopeId;
    }

    public ConfigurationScopeStorageBuilder usingXodus() {
      this.usingXodus = true;
      return this;
    }

    public ConfigurationScopeStorageBuilder withLocalOnlyIssue(LocalOnlyIssue issue) {
      localOnlyIssues.add(issue);
      return this;
    }

    public void populate(Path storageRoot, TestDatabase database) {
      populateDatabase(database);
      if (!usingXodus) {
        return;
      }
      var xodusTempDbPath = storageRoot.resolve("xodus_temp_db");
      var xodusBackupPath = storageRoot.resolve("local_only_issue_backup.tar.gz");
      try {
        Files.createDirectories(xodusBackupPath.getParent());
      } catch (IOException e) {
        throw new IllegalStateException("Unable to create the Xodus backup parent folders", e);
      }
      var environment = Environments.newInstance(xodusTempDbPath.toAbsolutePath().toFile());
      var entityStore = PersistentEntityStores.newInstance(environment);
      entityStore.executeInTransaction(txn -> {
        entityStore.registerCustomPropertyType(txn, Instant.class, new InstantBinding());
        entityStore.registerCustomPropertyType(txn, UUID.class, new UuidBinding());
        entityStore.registerCustomPropertyType(txn, IssueStatus.class, new IssueStatusBinding());
        var scopeEntity = txn.newEntity("Scope");
        localOnlyIssues.stream()
          .collect(Collectors.groupingBy(LocalOnlyIssue::getServerRelativePath))
          .forEach((filePath, issues) -> {
            var fileEntity = txn.newEntity("File");
            issues.forEach(issue -> {
              var issueEntity = txn.newEntity("Issue");
              issueEntity.setProperty("uuid", issue.getId());
              issueEntity.setProperty("ruleKey", issue.getRuleKey());
              issueEntity.setBlobString("message", issue.getMessage());
              var resolution = requireNonNull(issue.getResolution());
              issueEntity.setProperty("resolvedStatus", resolution.getStatus());
              issueEntity.setProperty("resolvedDate", resolution.getResolutionDate());
              var comment = resolution.getComment();
              if (comment != null) {
                issueEntity.setBlobString("comment", comment);
              }
              var textRange = issue.getTextRangeWithHash();
              var lineWithHash = issue.getLineWithHash();
              if (textRange != null) {
                issueEntity.setProperty("startLine", textRange.getStartLine());
                issueEntity.setProperty("startLineOffset", textRange.getStartLineOffset());
                issueEntity.setProperty("endLine", textRange.getEndLine());
                issueEntity.setProperty("endLineOffset", textRange.getEndLineOffset());
                issueEntity.setProperty("rangeHash", textRange.getHash());
              }
              if (lineWithHash != null) {
                issueEntity.setProperty("lineHash", lineWithHash.getHash());
              }

              issueEntity.setLink("file", fileEntity);
              fileEntity.addLink("issues", issueEntity);
            });

            scopeEntity.setProperty("name", configScopeId);
            fileEntity.setProperty("path", filePath.toString());
            scopeEntity.addLink("files", fileEntity);
          });
      });
      try {
        CompressBackupUtil.backup(entityStore, xodusBackupPath.toFile(), false);
      } catch (Exception e) {
        throw new IllegalStateException("Unable to backup server issue database", e);
      }
    }

    private void populateDatabase(TestDatabase database) {
      if (!usingXodus) {
        var localOnlyIssuesRepository = new LocalOnlyIssuesRepository(database.dsl());
        localOnlyIssues.forEach(issue -> localOnlyIssuesRepository.storeLocalOnlyIssue(configScopeId, issue));
      }
    }
  }

  private ConfigurationScopeStorageFixture() {
    //ACR-f6f97fc5a71f46379ef8fb93596d689a
  }
}
