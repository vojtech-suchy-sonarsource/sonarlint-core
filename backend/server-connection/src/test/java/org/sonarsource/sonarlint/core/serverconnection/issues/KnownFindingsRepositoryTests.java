/*
ACR-bf71150a8e564be2bbf7fd8b7ac77964
ACR-388214d0f08f419bb2b9221fdec0f0c2
ACR-ca4bceaaa2ce4d1eb4c54a652359b58c
ACR-1505c2d59d8149eab7e110ed3ea90319
ACR-6525f5faed0343b6bfca96712fe244e6
ACR-7d83475670fe41c5a83f6d2bcd548bfe
ACR-039e9a3456244513ae8c7ecb8ddb9388
ACR-4f114d90660f4cc3a1fa9117cdcd964b
ACR-f8bec392fc0348c2959fc31dbc263f73
ACR-9015bf42d6724d3bbb8fc40b8be31c10
ACR-ededc054d47e4c21b9dc250615883a4e
ACR-5749ef02bd734b12a3c46b2f69b9ce67
ACR-0b93fedb6350472b9bbfba1022fc77df
ACR-3fe204e9f2e34e1d93ec3a3900e351d2
ACR-9018d03ece924b3ea92e2fa7b5de2354
ACR-685555770c784310a9e59bee2fabb437
ACR-d66a5873b1ad44a18ddda74388e91479
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.KnownFinding;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class KnownFindingsRepositoryTests {

  @RegisterExtension
  static SonarLintLogTester logTester = new SonarLintLogTester();
  private SonarLintDatabase db;

  @AfterEach
  void shutdown() {
    db.shutdown();
  }

  @Test
  void testKnownFindingsRepository(@TempDir Path temp) {
    var storageRoot = temp.resolve("storage");

    db = new SonarLintDatabase(storageRoot);
    var repo = new KnownFindingsRepository(db);

    var filePath = Path.of("/file/path");
    var configScopeId = "configScopeId";
    var issues = new ArrayList<KnownFinding>();
    var issueUuid1 = UUID.randomUUID();
    var issueIntroDate1 = Instant.now();
    var issue1 = new KnownFinding(issueUuid1, "test-message", new TextRangeWithHash(1, 2, 3, 4, "hash1"),
      new LineWithHash(1, "hash"), "test-issue-rule-1", "Test issue message 1", issueIntroDate1);
    issues.add(issue1);
    var issueUuid2 = UUID.randomUUID();
    var issueIntroDate2 = Instant.now();
    var issue2 = new KnownFinding(issueUuid2, "test-message", new TextRangeWithHash(5, 6, 7, 8, "hash2"),
      new LineWithHash(1, "hash"), "test-issue-rule-2", "Test issue message 2", issueIntroDate2);
    issues.add(issue2);
    var hotspots = new ArrayList<KnownFinding>();
    var hotspotUuid1 = UUID.randomUUID();
    var hotspotIntroDate1 = Instant.now();
    var hotspot1 = new KnownFinding(hotspotUuid1, "test-message", new TextRangeWithHash(1, 2, 3, 4, "hash1"),
      new LineWithHash(1, "hash"), "test-hotspot-rule-1", "Test hotspot message 1", hotspotIntroDate1);
    hotspots.add(hotspot1);
    var hotspotUuid2 = UUID.randomUUID();
    var hotspotIntroDate2 = Instant.now();
    var hotspot2 = new KnownFinding(hotspotUuid2, "test-message", new TextRangeWithHash(5, 6, 7, 8, "hash2"),
      new LineWithHash(1, "hash"), "test-hotspot-rule-2", "Test hotspot message 2", hotspotIntroDate2);
    hotspots.add(hotspot2);

    repo.storeKnownIssues(configScopeId, filePath, issues);
    repo.storeKnownSecurityHotspots(configScopeId, filePath, hotspots);

    var knownIssues = repo.loadIssuesForFile(configScopeId, filePath);
    var knownHotspots = repo.loadSecurityHotspotsForFile(configScopeId, filePath);

    assertThat(knownIssues).hasSize(2);
    assertThat(knownHotspots).hasSize(2);
    var knownIssue = knownIssues.get(0);
    assertThat(knownIssue.getRuleKey()).isEqualTo(issue1.getRuleKey());
    assertThat(knownIssue.getServerKey()).isEqualTo(issue1.getServerKey());
    assertThat(knownIssue.getMessage()).isEqualTo(issue1.getMessage());
    assertThat(knownIssue.getTextRangeWithHash()).isEqualTo(issue1.getTextRangeWithHash());
    assertThat(knownIssue.getLineWithHash().getNumber()).isEqualTo(issue1.getLineWithHash().getNumber());
    assertThat(knownIssue.getLineWithHash().getHash()).isEqualTo(issue1.getLineWithHash().getHash());
    var knownHotspot = knownHotspots.get(0);
    assertThat(knownHotspot.getRuleKey()).isEqualTo(hotspot1.getRuleKey());
    assertThat(knownHotspot.getServerKey()).isEqualTo(hotspot1.getServerKey());
    assertThat(knownHotspot.getMessage()).isEqualTo(hotspot1.getMessage());
    assertThat(knownHotspot.getTextRangeWithHash()).isEqualTo(hotspot1.getTextRangeWithHash());
    assertThat(knownHotspot.getLineWithHash().getNumber()).isEqualTo(hotspot1.getLineWithHash().getNumber());
    assertThat(knownHotspot.getLineWithHash().getHash()).isEqualTo(hotspot1.getLineWithHash().getHash());
  }

  @Test
  void should_allow_for_a_long_message(@TempDir Path temp) {
    var storageRoot = temp.resolve("storage");

    db = new SonarLintDatabase(storageRoot);
    var repo = new KnownFindingsRepository(db);
    var longMessage = "m".repeat(10000);
    var path = Path.of("path");
    repo.storeKnownIssues("configScope", path, List.of(new KnownFinding(UUID.randomUUID(), "serverKey", null, null, "rule:key", longMessage, Instant.now())));

    var issues = repo.loadIssuesForFile("configScope", path);

    assertThat(issues).hasSize(1);
    assertThat(issues.get(0).getMessage()).isEqualTo(longMessage);
  }

}
