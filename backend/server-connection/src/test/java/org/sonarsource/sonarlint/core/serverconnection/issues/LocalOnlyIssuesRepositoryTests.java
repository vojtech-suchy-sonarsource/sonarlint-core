/*
ACR-2a760f41ca434b0a902924db4c897307
ACR-dc4587238fd54b31ad28ca54ddcb5ecb
ACR-1d916f5716584561ba8571c348a855a3
ACR-f4ca0bb3e57b421a873faf59915e2b0e
ACR-036fdfd86ec24cbb917d21beb5ea0328
ACR-4ccb20b016024803b561253850e42708
ACR-84f95f7b9bf24c7a9ed28ef133c7e8b0
ACR-2448c5db20bd449fa5f2afc73ed53cc5
ACR-f3ef2c3ed0f549cbb10c047fd07485f0
ACR-50977868e4774240a32109a8a3a403f7
ACR-89d7b42303e84c218de41218263c76c0
ACR-c91ddf8b7a0140a5bb2c29a22b532a41
ACR-04a0872d21f446e3a44135bfa5024718
ACR-f4acac9853464bd38cb18ffabb717c40
ACR-95c2718d7aaf45559f658c03be51115e
ACR-9825b238582446a6a9364bfe4ad0e67c
ACR-0a5b69cd266f430daf57008918514187
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssueResolution;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;

import static org.assertj.core.api.Assertions.assertThat;

class LocalOnlyIssuesRepositoryTests {

  @RegisterExtension
  static SonarLintLogTester logTester = new SonarLintLogTester();
  private LocalOnlyIssuesRepository repository;
  private SonarLintDatabase db;

  @BeforeEach
  void prepare(@TempDir Path temp) {
    var storageRoot = temp.resolve("storage");
    db = new SonarLintDatabase(storageRoot);
    repository = new LocalOnlyIssuesRepository(db.dsl());
  }

  @AfterEach
  void shutdown() {
    db.shutdown();
  }

  @Test
  void should_store_and_load_issues_for_file() {
    var filePath = Path.of("/file/path");
    var configScopeId = "configScopeId";
    var issueUuid1 = UUID.randomUUID();
    var issue1 = new LocalOnlyIssue(issueUuid1, filePath, new TextRangeWithHash(1, 2, 3, 4, "hash1"),
      new LineWithHash(1, "linehash1"), "test-rule-1", "Test issue message 1", null);
    var issueUuid2 = UUID.randomUUID();
    var issue2 = new LocalOnlyIssue(issueUuid2, filePath, new TextRangeWithHash(5, 6, 7, 8, "hash2"),
      new LineWithHash(2, "linehash2"), "test-rule-2", "Test issue message 2", null);

    repository.storeLocalOnlyIssue(configScopeId, issue1);
    repository.storeLocalOnlyIssue(configScopeId, issue2);

    var loadedIssues = repository.loadForFile(configScopeId, filePath);

    assertThat(loadedIssues).hasSize(2);
    assertThat(loadedIssues).extracting(LocalOnlyIssue::getId).containsExactlyInAnyOrder(issueUuid1, issueUuid2);
    var loadedIssue1 = loadedIssues.stream().filter(i -> i.getId().equals(issueUuid1)).findFirst().orElseThrow();
    assertThat(loadedIssue1.getRuleKey()).isEqualTo("test-rule-1");
    assertThat(loadedIssue1.getMessage()).isEqualTo("Test issue message 1");
    assertThat(loadedIssue1.getTextRangeWithHash()).isEqualTo(new TextRangeWithHash(1, 2, 3, 4, "hash1"));
    assertThat(loadedIssue1.getLineWithHash().getNumber()).isEqualTo(1);
    assertThat(loadedIssue1.getLineWithHash().getHash()).isEqualTo("linehash1");
    assertThat(loadedIssue1.getResolution()).isNull();
  }

  @Test
  void should_store_and_load_resolved_issue() {
    var filePath = Path.of("/file/path");
    var configScopeId = "configScopeId";
    var issueUuid = UUID.randomUUID();
    var resolutionDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    var resolution = new LocalOnlyIssueResolution(IssueStatus.WONT_FIX, resolutionDate, "Test comment");
    var issue = new LocalOnlyIssue(issueUuid, filePath, new TextRangeWithHash(1, 2, 3, 4, "hash1"),
      new LineWithHash(1, "linehash1"), "test-rule-1", "Test issue message", resolution);

    repository.storeLocalOnlyIssue(configScopeId, issue);

    var loadedIssues = repository.loadForFile(configScopeId, filePath);

    assertThat(loadedIssues).hasSize(1);
    var loadedIssue = loadedIssues.get(0);
    assertThat(loadedIssue.getId()).isEqualTo(issueUuid);
    assertThat(loadedIssue.getResolution()).isNotNull();
    assertThat(loadedIssue.getResolution().getStatus()).isEqualTo(IssueStatus.WONT_FIX);
    assertThat(loadedIssue.getResolution().getResolutionDate()).isEqualTo(resolutionDate);
    assertThat(loadedIssue.getResolution().getComment()).isEqualTo("Test comment");
  }

  @Test
  void should_store_issue_without_text_range_and_line_hash() {
    var filePath = Path.of("/file/path");
    var configScopeId = "configScopeId";
    var issueUuid = UUID.randomUUID();
    var issue = new LocalOnlyIssue(issueUuid, filePath, null, null, "test-rule-1", "Test issue message", null);

    repository.storeLocalOnlyIssue(configScopeId, issue);

    var loadedIssues = repository.loadForFile(configScopeId, filePath);

    assertThat(loadedIssues).hasSize(1);
    var loadedIssue = loadedIssues.get(0);
    assertThat(loadedIssue.getId()).isEqualTo(issueUuid);
    assertThat(loadedIssue.getTextRangeWithHash()).isNull();
    assertThat(loadedIssue.getLineWithHash()).isNull();
  }

  @Test
  void should_load_all_issues_for_configuration_scope() {
    var configScopeId = "configScopeId";
    var filePath1 = Path.of("/file/path1");
    var filePath2 = Path.of("/file/path2");
    var issue1 = new LocalOnlyIssue(UUID.randomUUID(), filePath1, null, null, "test-rule-1", "Message 1", null);
    var issue2 = new LocalOnlyIssue(UUID.randomUUID(), filePath2, null, null, "test-rule-2", "Message 2", null);
    var issue3 = new LocalOnlyIssue(UUID.randomUUID(), filePath1, null, null, "test-rule-3", "Message 3", null);

    repository.storeLocalOnlyIssue(configScopeId, issue1);
    repository.storeLocalOnlyIssue(configScopeId, issue2);
    repository.storeLocalOnlyIssue(configScopeId, issue3);

    var allIssues = repository.loadAll(configScopeId);

    assertThat(allIssues).hasSize(3);
    assertThat(allIssues).extracting(LocalOnlyIssue::getId)
      .containsExactlyInAnyOrder(issue1.getId(), issue2.getId(), issue3.getId());
  }

  @Test
  void should_find_issue_by_id() {
    var configScopeId = "configScopeId";
    var issueUuid = UUID.randomUUID();
    var issue = new LocalOnlyIssue(issueUuid, Path.of("/file/path"), null, null, "test-rule-1", "Test message", null);

    repository.storeLocalOnlyIssue(configScopeId, issue);

    var foundIssue = repository.find(issueUuid);

    assertThat(foundIssue).isPresent();
    assertThat(foundIssue.get().getId()).isEqualTo(issueUuid);
    assertThat(foundIssue.get().getRuleKey()).isEqualTo("test-rule-1");
    assertThat(foundIssue.get().getMessage()).isEqualTo("Test message");
  }

  @Test
  void should_return_empty_when_issue_not_found() {
    var foundIssue = repository.find(UUID.randomUUID());

    assertThat(foundIssue).isEmpty();
  }

  @Test
  void should_remove_issue() {
    var configScopeId = "configScopeId";
    var filePath = Path.of("/file/path");
    var issueUuid1 = UUID.randomUUID();
    var issueUuid2 = UUID.randomUUID();
    var issue1 = new LocalOnlyIssue(issueUuid1, filePath, null, null, "test-rule-1", "Message 1", null);
    var issue2 = new LocalOnlyIssue(issueUuid2, filePath, null, null, "test-rule-2", "Message 2", null);

    repository.storeLocalOnlyIssue(configScopeId, issue1);
    repository.storeLocalOnlyIssue(configScopeId, issue2);

    var removed = repository.removeIssue(issueUuid1);

    assertThat(removed).isTrue();
    var remainingIssues = repository.loadForFile(configScopeId, filePath);
    assertThat(remainingIssues).hasSize(1);
    assertThat(remainingIssues.get(0).getId()).isEqualTo(issueUuid2);
  }

  @Test
  void should_return_false_when_removing_nonexistent_issue() {
    var removed = repository.removeIssue(UUID.randomUUID());

    assertThat(removed).isFalse();
  }

  @Test
  void should_remove_all_issues_for_file() {
    var configScopeId = "configScopeId";
    var filePath1 = Path.of("/file/path1");
    var filePath2 = Path.of("/file/path2");
    var issue1 = new LocalOnlyIssue(UUID.randomUUID(), filePath1, null, null, "test-rule-1", "Message 1", null);
    var issue2 = new LocalOnlyIssue(UUID.randomUUID(), filePath1, null, null, "test-rule-2", "Message 2", null);
    var issue3 = new LocalOnlyIssue(UUID.randomUUID(), filePath2, null, null, "test-rule-3", "Message 3", null);

    repository.storeLocalOnlyIssue(configScopeId, issue1);
    repository.storeLocalOnlyIssue(configScopeId, issue2);
    repository.storeLocalOnlyIssue(configScopeId, issue3);

    var removed = repository.removeAllIssuesForFile(configScopeId, filePath1);

    assertThat(removed).isTrue();
    assertThat(repository.loadForFile(configScopeId, filePath1)).isEmpty();
    assertThat(repository.loadForFile(configScopeId, filePath2)).hasSize(1);
  }

  @Test
  void should_update_existing_issue() {
    var configScopeId = "configScopeId";
    var filePath = Path.of("/file/path");
    var issueUuid = UUID.randomUUID();
    var issue1 = new LocalOnlyIssue(issueUuid, filePath, null, null, "test-rule-1", "Original message", null);

    repository.storeLocalOnlyIssue(configScopeId, issue1);

    var resolution = new LocalOnlyIssueResolution(IssueStatus.WONT_FIX, Instant.now().truncatedTo(ChronoUnit.MILLIS), "Updated comment");
    var issue2 = new LocalOnlyIssue(issueUuid, filePath, new TextRangeWithHash(1, 2, 3, 4, "hash"),
      new LineWithHash(1, "linehash"), "test-rule-1", "Updated message", resolution);

    repository.storeLocalOnlyIssue(configScopeId, issue2);

    var loadedIssues = repository.loadForFile(configScopeId, filePath);

    assertThat(loadedIssues).hasSize(1);
    var loadedIssue = loadedIssues.get(0);
    assertThat(loadedIssue.getId()).isEqualTo(issueUuid);
    assertThat(loadedIssue.getMessage()).isEqualTo("Updated message");
    assertThat(loadedIssue.getTextRangeWithHash()).isEqualTo(new TextRangeWithHash(1, 2, 3, 4, "hash"));
    assertThat(loadedIssue.getLineWithHash().getNumber()).isEqualTo(1);
    assertThat(loadedIssue.getLineWithHash().getHash()).isEqualTo("linehash");
    assertThat(loadedIssue.getResolution()).isNotNull();
    assertThat(loadedIssue.getResolution().getStatus()).isEqualTo(IssueStatus.WONT_FIX);
    assertThat(loadedIssue.getResolution().getComment()).isEqualTo("Updated comment");
  }

  @Test
  void should_purge_old_resolved_issues() {
    var configScopeId = "configScopeId";
    var filePath = Path.of("/file/path");
    var oldDate = Instant.now().minus(10, ChronoUnit.DAYS);
    var recentDate = Instant.now().minus(1, ChronoUnit.DAYS);
    var limit = Instant.now().minus(5, ChronoUnit.DAYS);

    var oldIssue = new LocalOnlyIssue(UUID.randomUUID(), filePath, null, null, "test-rule-1", "Old issue",
      new LocalOnlyIssueResolution(IssueStatus.WONT_FIX, oldDate.truncatedTo(ChronoUnit.MILLIS), "comment"));
    var recentIssue = new LocalOnlyIssue(UUID.randomUUID(), filePath, null, null, "test-rule-2", "Recent issue",
      new LocalOnlyIssueResolution(IssueStatus.WONT_FIX, recentDate.truncatedTo(ChronoUnit.MILLIS), "comment"));
    var unresolvedIssue = new LocalOnlyIssue(UUID.randomUUID(), filePath, null, null, "test-rule-3", "Unresolved issue", null);

    repository.storeLocalOnlyIssue(configScopeId, oldIssue);
    repository.storeLocalOnlyIssue(configScopeId, recentIssue);
    repository.storeLocalOnlyIssue(configScopeId, unresolvedIssue);

    repository.purgeIssuesOlderThan(limit);

    var remainingIssues = repository.loadAll(configScopeId);
    assertThat(remainingIssues).hasSize(2);
    assertThat(remainingIssues).extracting(LocalOnlyIssue::getId)
      .containsExactlyInAnyOrder(recentIssue.getId(), unresolvedIssue.getId());
  }

  @Test
  void should_isolate_issues_by_configuration_scope() {
    var configScopeId1 = "configScopeId1";
    var configScopeId2 = "configScopeId2";
    var filePath = Path.of("/file/path");
    var issue1 = new LocalOnlyIssue(UUID.randomUUID(), filePath, null, null, "test-rule-1", "Message 1", null);
    var issue2 = new LocalOnlyIssue(UUID.randomUUID(), filePath, null, null, "test-rule-2", "Message 2", null);

    repository.storeLocalOnlyIssue(configScopeId1, issue1);
    repository.storeLocalOnlyIssue(configScopeId2, issue2);

    assertThat(repository.loadAll(configScopeId1)).hasSize(1);
    assertThat(repository.loadAll(configScopeId2)).hasSize(1);
    assertThat(repository.loadForFile(configScopeId1, filePath)).hasSize(1);
    assertThat(repository.loadForFile(configScopeId2, filePath)).hasSize(1);
  }

  @Test
  void should_handle_issue_with_only_text_range() {
    var configScopeId = "configScopeId";
    var filePath = Path.of("/file/path");
    var issueUuid = UUID.randomUUID();
    var issue = new LocalOnlyIssue(issueUuid, filePath, new TextRangeWithHash(1, 2, 3, 4, "hash1"),
      null, "test-rule-1", "Test message", null);

    repository.storeLocalOnlyIssue(configScopeId, issue);

    var loadedIssues = repository.loadForFile(configScopeId, filePath);

    assertThat(loadedIssues).hasSize(1);
    var loadedIssue = loadedIssues.get(0);
    assertThat(loadedIssue.getTextRangeWithHash()).isEqualTo(new TextRangeWithHash(1, 2, 3, 4, "hash1"));
    assertThat(loadedIssue.getLineWithHash()).isNull();
  }

  @Test
  void should_handle_issue_with_only_line_hash() {
    var configScopeId = "configScopeId";
    var filePath = Path.of("/file/path");
    var issueUuid = UUID.randomUUID();
    var issue = new LocalOnlyIssue(issueUuid, filePath, null,
      new LineWithHash(5, "linehash"), "test-rule-1", "Test message", null);

    repository.storeLocalOnlyIssue(configScopeId, issue);

    var loadedIssues = repository.loadForFile(configScopeId, filePath);

    assertThat(loadedIssues).hasSize(1);
    var loadedIssue = loadedIssues.get(0);
    assertThat(loadedIssue.getTextRangeWithHash()).isNull();
    assertThat(loadedIssue.getLineWithHash().getNumber()).isEqualTo(5);
    assertThat(loadedIssue.getLineWithHash().getHash()).isEqualTo("linehash");
  }

}

