/*
ACR-03251f9cff6d4b08aaae888307ffdcbe
ACR-b6723fb27fc64a10850047c03d015b82
ACR-0e0c0045d3bd425eaedcca63c2bfb9b2
ACR-39502de4e16d4a329eec196e67d3659c
ACR-202db8223bed43828e32dab4364b99ab
ACR-1059beadf0ad4c4f8330135e6ae2fdb4
ACR-43aa09b5120344dba5aa8ea4a2e6209a
ACR-958878c790cc473e819c5c7e89ea3df4
ACR-74ae566515f7458b99eb40a9fe224fd6
ACR-0841206936f94932a1b7169d4c35c325
ACR-4efe8c761d164e6e805391f74a484281
ACR-c52659bb7e724c848bb4ba7f91a7f511
ACR-5ce97dd326ca4866bbd5e34b6011aabf
ACR-a8999dbf9611418fad8a681a3260caa1
ACR-a85dad342ba6442ca19fd299c8287f8c
ACR-4011e6c9e07145b4a1c40635a219197a
ACR-3bfb15fe01ad40c1aea3c4a07c4492ac
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import mockwebserver3.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.scanner.protocol.Constants.Severity;
import org.sonar.scanner.protocol.input.ScannerInput;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.exception.ServerErrorException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.IssueLite;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.Location;
import org.sonarsource.sonarlint.core.serverconnection.issues.FileLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.LineLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.RangeLevelServerIssue;
import testutils.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssueDownloaderTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final String DUMMY_KEY = "dummyKey";

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();
  private ServerApi serverApi;

  private IssueDownloader underTest;

  @BeforeEach
  void prepare() {
    underTest = new IssueDownloader(Set.of(SonarLanguage.JAVA));
    serverApi = new ServerApi(mockServer.serverApiHelper());
  }

  @Test
  void test_download_one_issue_old_batch_ws() {
    var response = ScannerInput.ServerIssue.newBuilder()
      .setKey("uuid")
      .setRuleRepository("sonarjava")
      .setRuleKey("S123")
      .setChecksum("hash")
      .setMsg("Primary message")
      .setLine(1)
      .setCreationDate(123456789L)
      .setPath("foo/bar/Hello.java")
      .setType("BUG")
      .setManualSeverity(false)
      .setSeverity(Severity.BLOCKER)
      .build();

    mockServer.addProtobufResponseDelimited("/batch/issues?key=" + DUMMY_KEY, response);

    var issues = underTest.downloadFromBatch(serverApi, DUMMY_KEY, null, new SonarLintCancelMonitor());
    assertThat(issues).hasSize(1);

    var serverIssue = issues.get(0);
    assertThat(serverIssue).isInstanceOf(LineLevelServerIssue.class);
    assertThat(serverIssue.getKey()).isEqualTo("uuid");
    assertThat(serverIssue.getType()).isEqualTo(RuleType.BUG);
    assertThat(serverIssue.getUserSeverity()).isNull();
    assertThat(((LineLevelServerIssue) serverIssue).getLineHash()).isEqualTo("hash");
    assertThat(serverIssue.getMessage()).isEqualTo("Primary message");
    assertThat(serverIssue.getFilePath()).isEqualTo(Path.of("foo/bar/Hello.java"));
    assertThat(((LineLevelServerIssue) serverIssue).getLine()).isEqualTo(1);
  }

  @Test
  void test_download_one_issue_old_batch_ws_with_user_severity() {
    var response = ScannerInput.ServerIssue.newBuilder()
      .setKey("uuid")
      .setRuleRepository("sonarjava")
      .setRuleKey("S123")
      .setChecksum("hash")
      .setMsg("Primary message")
      .setLine(1)
      .setCreationDate(123456789L)
      .setPath("foo/bar/Hello.java")
      .setType("BUG")
      .setManualSeverity(true)
      .setSeverity(Severity.BLOCKER)
      .build();

    mockServer.addProtobufResponseDelimited("/batch/issues?key=" + DUMMY_KEY, response);

    var issues = underTest.downloadFromBatch(serverApi, DUMMY_KEY, null, new SonarLintCancelMonitor());
    assertThat(issues).hasSize(1);

    var serverIssue = issues.get(0);
    assertThat(serverIssue.getUserSeverity()).isEqualTo(IssueSeverity.BLOCKER);
  }

  @Test
  void test_download_one_file_level_issue_old_batch_ws() {
    var response = ScannerInput.ServerIssue.newBuilder()
      .setKey("uuid")
      .setRuleRepository("sonarjava")
      .setRuleKey("S123")
      .setMsg("Primary message")
      .setCreationDate(123456789L)
      .setPath("foo/bar/Hello.java")
      .setType("BUG")
      .build();

    mockServer.addProtobufResponseDelimited("/batch/issues?key=" + DUMMY_KEY, response);

    var issues = underTest.downloadFromBatch(serverApi, DUMMY_KEY, null, new SonarLintCancelMonitor());
    assertThat(issues).hasSize(1);

    var serverIssue = issues.get(0);
    assertThat(serverIssue).isInstanceOf(FileLevelServerIssue.class);
    assertThat(serverIssue.getKey()).isEqualTo("uuid");
    assertThat(serverIssue.getMessage()).isEqualTo("Primary message");
    assertThat(serverIssue.getFilePath()).isEqualTo(Path.of("foo/bar/Hello.java"));
  }

  @Test
  void test_download_one_issue_pull_ws() {
    var timestamp = Issues.IssuesPullQueryTimestamp.newBuilder().setQueryTimestamp(123L).build();
    var issue = IssueLite.newBuilder()
      .setKey("uuid")
      .setRuleKey("sonarjava:S123")
      .setType(Common.RuleType.BUG)
      .setMainLocation(Location.newBuilder().setFilePath("foo/bar/Hello.java").setMessage("Primary message")
        .setTextRange(org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.TextRange.newBuilder().setStartLine(1).setStartLineOffset(2).setEndLine(3)
          .setEndLineOffset(4).setHash("hash")))
      .setCreationDate(123456789L)
      .build();

    mockServer.addProtobufResponseDelimited("/api/issues/pull?projectKey=" + DUMMY_KEY + "&branchName=myBranch&languages=java", timestamp, issue);

    var result = underTest.downloadFromPull(serverApi, DUMMY_KEY, "myBranch", Optional.empty(), new SonarLintCancelMonitor());
    assertThat(result.getChangedIssues()).hasSize(1);
    assertThat(result.getClosedIssueKeys()).isEmpty();

    var serverIssue = result.getChangedIssues().get(0);
    assertThat(serverIssue).isInstanceOf(RangeLevelServerIssue.class);
    assertThat(serverIssue.getKey()).isEqualTo("uuid");
    assertThat(serverIssue.getMessage()).isEqualTo("Primary message");
    assertThat(serverIssue.getFilePath()).isEqualTo(Path.of("foo/bar/Hello.java"));
    assertThat(serverIssue.getUserSeverity()).isNull();
    assertThat(serverIssue.getType()).isEqualTo(RuleType.BUG);
    assertThat(((RangeLevelServerIssue) serverIssue).getTextRange().getStartLine()).isEqualTo(1);
    assertThat(((RangeLevelServerIssue) serverIssue).getTextRange().getStartLineOffset()).isEqualTo(2);
    assertThat(((RangeLevelServerIssue) serverIssue).getTextRange().getEndLine()).isEqualTo(3);
    assertThat(((RangeLevelServerIssue) serverIssue).getTextRange().getEndLineOffset()).isEqualTo(4);
    assertThat(((RangeLevelServerIssue) serverIssue).getTextRange().getHash()).isEqualTo("hash");
  }

  @Test
  void test_download_one_issue_pull_ws_with_user_severity() {
    var timestamp = Issues.IssuesPullQueryTimestamp.newBuilder().setQueryTimestamp(123L).build();
    var issue = IssueLite.newBuilder()
      .setKey("uuid")
      .setRuleKey("sonarjava:S123")
      .setType(Common.RuleType.BUG)
      .setUserSeverity(Common.Severity.MAJOR)
      .setMainLocation(Location.newBuilder().setFilePath("foo/bar/Hello.java").setMessage("Primary message")
        .setTextRange(org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.TextRange.newBuilder().setStartLine(1).setStartLineOffset(2).setEndLine(3)
          .setEndLineOffset(4).setHash("hash")))
      .setCreationDate(123456789L)
      .build();

    mockServer.addProtobufResponseDelimited("/api/issues/pull?projectKey=" + DUMMY_KEY + "&branchName=myBranch&languages=java", timestamp, issue);

    var result = underTest.downloadFromPull(serverApi, DUMMY_KEY, "myBranch", Optional.empty(), new SonarLintCancelMonitor());
    assertThat(result.getChangedIssues()).hasSize(1);
    assertThat(result.getClosedIssueKeys()).isEmpty();

    var serverIssue = result.getChangedIssues().get(0);
    assertThat(serverIssue.getUserSeverity()).isEqualTo(IssueSeverity.MAJOR);
  }

  @Test
  void test_download_one_issue_pull_ws_with_user_impacts() {
    var timestamp = Issues.IssuesPullQueryTimestamp.newBuilder().setQueryTimestamp(123L).build();
    var issue = IssueLite.newBuilder()
      .setKey("uuid")
      .setRuleKey("sonarjava:S123")
      .setType(Common.RuleType.BUG)
      .setMainLocation(Location.newBuilder().setFilePath("foo/bar/Hello.java").setMessage("Primary message")
        .setTextRange(Issues.TextRange.newBuilder().setStartLine(1).setStartLineOffset(2).setEndLine(3)
          .setEndLineOffset(4).setHash("hash")))
      .setCreationDate(123456789L)
      .addImpacts(Common.Impact.newBuilder()
        .setSoftwareQuality(Common.SoftwareQuality.SECURITY)
        .setSeverity(Common.ImpactSeverity.HIGH)
        .build())
      .build();

    mockServer.addProtobufResponseDelimited("/api/issues/pull?projectKey=" + DUMMY_KEY + "&branchName=myBranch&languages=java", timestamp, issue);

    var result = underTest.downloadFromPull(serverApi, DUMMY_KEY, "myBranch", Optional.empty(), new SonarLintCancelMonitor());
    assertThat(result.getChangedIssues()).hasSize(1);
    assertThat(result.getClosedIssueKeys()).isEmpty();

    var serverIssue = result.getChangedIssues().get(0);
    assertThat(serverIssue.getImpacts()).isEqualTo(Map.of(SoftwareQuality.SECURITY, ImpactSeverity.HIGH));
  }

  @Test
  void test_download_one_file_level_issue_pull_ws() {
    var timestamp = Issues.IssuesPullQueryTimestamp.newBuilder().setQueryTimestamp(123L).build();
    var issue = IssueLite.newBuilder()
      .setKey("uuid")
      .setRuleKey("sonarjava:S123")
      .setMainLocation(Location.newBuilder().setFilePath("foo/bar/Hello.java").setMessage("Primary message"))
      .setCreationDate(123456789L)
      .setType(Common.RuleType.BUG)
      .build();

    mockServer.addProtobufResponseDelimited("/api/issues/pull?projectKey=" + DUMMY_KEY + "&branchName=myBranch&languages=java", timestamp, issue);

    var result = underTest.downloadFromPull(serverApi, DUMMY_KEY, "myBranch", Optional.empty(), new SonarLintCancelMonitor());
    assertThat(result.getChangedIssues()).hasSize(1);
    assertThat(result.getClosedIssueKeys()).isEmpty();

    var serverIssue = result.getChangedIssues().get(0);
    assertThat(serverIssue).isInstanceOf(FileLevelServerIssue.class);
    assertThat(serverIssue.getKey()).isEqualTo("uuid");
    assertThat(serverIssue.getMessage()).isEqualTo("Primary message");
    assertThat(serverIssue.getFilePath()).isEqualTo(Path.of("foo/bar/Hello.java"));
    assertThat(serverIssue.getType()).isEqualTo(RuleType.BUG);
  }

  @Test
  void test_download_closed_file_level_issues_from_pull_ws() {
    var timestamp = Issues.IssuesPullQueryTimestamp.newBuilder().setQueryTimestamp(123L).build();
    var issue = IssueLite.newBuilder()
      .setKey("key")
      .setClosed(true)
      .setMainLocation(Location.newBuilder().setFilePath("foo/bar/Hello.java").setMessage("Primary message")
        .setTextRange(org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.TextRange.newBuilder().setStartLine(1).setStartLineOffset(2).setEndLine(3)
          .setEndLineOffset(4).setHash("hash")))
      .build();
    mockServer.addProtobufResponseDelimited("/api/issues/pull?projectKey=" + DUMMY_KEY + "&branchName=myBranch&languages=java&changedSince=123456789", timestamp, issue);

    var result = underTest.downloadFromPull(serverApi, DUMMY_KEY, "myBranch", Optional.of(Instant.ofEpochMilli(123456789)), new SonarLintCancelMonitor());

    assertThat(result.getChangedIssues()).isEmpty();
    assertThat(result.getClosedIssueKeys()).containsOnly("key");
  }

  @Test
  void test_download_issue_ignore_project_level() {
    var response = ScannerInput.ServerIssue.newBuilder()
      .setRuleRepository("sonarjava")
      .setRuleKey("S123")
      .setChecksum("hash")
      .setMsg("Primary message")
      .setLine(1)
      .setCreationDate(123456789L)
      //ACR-545433907ef24a2aa6e5f775389e5348
      .setModuleKey("project")
      .build();

    mockServer.addProtobufResponseDelimited("/batch/issues?key=" + DUMMY_KEY, response);

    var issues = underTest.downloadFromBatch(serverApi, DUMMY_KEY, null, new SonarLintCancelMonitor());
    assertThat(issues).isEmpty();
  }

  @Test
  void test_pull_issue_ignore_project_level() {
    var timestamp = Issues.IssuesPullQueryTimestamp.newBuilder().setQueryTimestamp(123L).build();
    var issue = IssueLite.newBuilder()
      .setKey("uuid")
      .setRuleKey("sonarjava:S123")
      .setMainLocation(Location.newBuilder().setMessage("Primary message"))
      .setCreationDate(123456789L)
      .build();

    mockServer.addProtobufResponseDelimited("/api/issues/pull?projectKey=" + DUMMY_KEY + "&branchName=myBranch&languages=java", timestamp, issue);

    var issues = underTest.downloadFromPull(serverApi, DUMMY_KEY, "myBranch", Optional.empty(), new SonarLintCancelMonitor());
    assertThat(issues.getChangedIssues()).isEmpty();
    assertThat(issues.getClosedIssueKeys()).isEmpty();
  }

  @Test
  void test_ignore_taint_vulnerabilities() {
    var issue1 = ScannerInput.ServerIssue.newBuilder()
      .setRuleRepository("sonarjava")
      .setRuleKey("S123")
      .setChecksum("hash1")
      .setMsg("Primary message 1")
      .setLine(1)
      .setCreationDate(123456789L)
      .setPath("foo/bar/Hello.java")
      .setModuleKey("project")
      .setType("BUG")
      .build();

    var taint1 = ScannerInput.ServerIssue.newBuilder()
      .setRuleRepository("javasecurity")
      .setRuleKey("S789")
      .setChecksum("hash2")
      .setMsg("Primary message 2")
      .setLine(2)
      .setCreationDate(123456789L)
      .setPath("foo/bar/Hello2.java")
      .setModuleKey("project")
      .setType("VULNERABILITY")
      .build();

    mockServer.addProtobufResponseDelimited("/batch/issues?key=" + DUMMY_KEY, issue1, taint1);

    var issues = underTest.downloadFromBatch(serverApi, DUMMY_KEY, null, new SonarLintCancelMonitor());

    assertThat(issues).hasSize(1);
  }

  @Test
  void test_download_no_issues() {
    mockServer.addProtobufResponseDelimited("/batch/issues?key=" + DUMMY_KEY);

    var issues = underTest.downloadFromBatch(serverApi, DUMMY_KEY, null, new SonarLintCancelMonitor());
    assertThat(issues).isEmpty();
  }

  @Test
  void test_fail_other_codes() {
    mockServer.addResponse("/batch/issues?key=" + DUMMY_KEY, new MockResponse.Builder().code(503).build());

    var cancelMonitor = new SonarLintCancelMonitor();
    var thrown = assertThrows(ServerErrorException.class,
      () -> underTest.downloadFromBatch(serverApi, DUMMY_KEY, null, cancelMonitor));
    assertThat(thrown).hasMessageContaining("Error 503");
  }

  @Test
  void test_return_empty_if_404() {
    mockServer.addResponse("/batch/issues?key=" + DUMMY_KEY, new MockResponse.Builder().code(404).build());

    var issues = underTest.downloadFromBatch(serverApi, DUMMY_KEY, null, new SonarLintCancelMonitor());
    assertThat(issues).isEmpty();
  }

  @Test
  void test_filter_batch_issues_by_branch_if_branch_parameter_provided() {
    var response = ScannerInput.ServerIssue.newBuilder()
      .setRuleRepository("sonarjava")
      .setRuleKey("S123")
      .setPath("src/Foo.java")
      .setType("BUG")
      .build();

    mockServer.addProtobufResponseDelimited("/batch/issues?key=" + DUMMY_KEY + "&branch=branchName", response);

    var issues = underTest.downloadFromBatch(serverApi, DUMMY_KEY, "branchName", new SonarLintCancelMonitor());
    assertThat(issues).hasSize(1);
  }

}
