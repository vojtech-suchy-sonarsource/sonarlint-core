/*
ACR-f825a4fc4aa54615860f00dc69235f3d
ACR-c3b14ddbf39f498d82965b11806c9985
ACR-03168ed0704249beaebe64b418c23982
ACR-1e7c8653b8784b0695debb46d22f8dff
ACR-4781c46be5d14defbf0a7aef93171762
ACR-2374587aeb8240189e12375e53926728
ACR-26ddc5f92b9b4137bbdb174288c5c833
ACR-b257b9f45f5e4a12970f57a90e1cef16
ACR-0b228d0c11f049978317190617cca9bd
ACR-66f527d8abe9478393a3a641e84ebb73
ACR-92da4a2420d942369c8c8392868368db
ACR-1cad405c4d0847dca7a77c7f86a30ad4
ACR-e6312d23cc8d4ee5b93970d7c680e03d
ACR-eda8bac0016b4b3e89ac9a90b8bd9bfa
ACR-c56c431071aa4adaa42b47e558d60b09
ACR-9904b98d1e054d448b0c4e26f594767a
ACR-fb44d3f1e707488babf9f69496be29b8
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

