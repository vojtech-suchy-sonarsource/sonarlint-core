/*
ACR-dee60adb81b449f480281276fd7c942e
ACR-29780b65215e4caebb256605fd2e0fe3
ACR-b993bc0ebb724899903f89a4eff87b6e
ACR-994071c8101346a0bf34d183120a4fc8
ACR-757274fca367460a85665ffa89f07339
ACR-54ee6c8a25c54dc3b38eac28471b6608
ACR-fc23c7c327364b5392937e936fb82a9e
ACR-41334afecebb487a9e25fb9e4ef8e871
ACR-eea5f33f48994d47ae14406d00e9a250
ACR-61d15ce340ed40ae93ed48d4ebca0db7
ACR-d3a4db20eccf42b3a2e0fd811aec9034
ACR-18bb3f2b2bc540768577c485fa861599
ACR-94e2cdeb5c70423294637a27dc819f7b
ACR-e8e6e8e8eb1346ecb55e197444f368b3
ACR-678f72292da4473a9be94c9dcdc69dce
ACR-915007fbe3bc4c8398668cdcab766fe3
ACR-e30d4c10fba34c07a71d478a5b4b3872
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
