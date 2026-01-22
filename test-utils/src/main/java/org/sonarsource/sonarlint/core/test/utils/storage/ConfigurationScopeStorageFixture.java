/*
ACR-2a5fca597ea54e6581059bb0ea832833
ACR-a3eee1ae0a1443f89fc4fb12aeffa975
ACR-cde59ffb39f2411ca36fc97c9693235c
ACR-1a9f8279e3f449e7847108d96e8ab993
ACR-e3ea9d52bf444af9be83a330df05602d
ACR-73965ec3a82b421b8745f75b389a5158
ACR-c676914255574c45be41a2640fe4074c
ACR-b9447dbebf784f96ba93915ea2b3b383
ACR-192ad1eca25943c78abc11c0f400d34e
ACR-0240f2b45b2e4f6b8877b34021feb360
ACR-51c5cdb3430549b181ec9a4f2ec2c7e8
ACR-a6940730c4bd4681a5bdd2a58dd2ca71
ACR-67867a300e4f4ca6bedb87b7839af815
ACR-d0305f35a4984a248395e3fd3cf84f07
ACR-25fa4bb8e53347f7b4869f51a5f26f24
ACR-0fcae6ab0e614def94c41d0705c84c17
ACR-2d42f0dfefb44a01809eb0f083675630
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
    //ACR-3b43adaafca644cabdc71315349ffc6b
  }
}
