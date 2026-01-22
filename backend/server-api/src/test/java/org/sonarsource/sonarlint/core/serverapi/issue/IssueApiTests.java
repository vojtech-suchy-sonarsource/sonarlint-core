/*
ACR-9ace54a5d4d945fc86dfd47c9533495f
ACR-e337b89299374755a7be48c38fffe3e9
ACR-5251152f749f4e109873298c7d17eb98
ACR-1daffe81d1e544a4a8b576b6ac0eba38
ACR-8c3d9f6270794913a5cca7c31be36c4f
ACR-f18004d24c9b436f9ea8740c1a74aa4f
ACR-08fbb3d7f370468e86c70ad58df2814a
ACR-c5c4b4d47b4f48509e32ac855afc84cd
ACR-bd521e586d7d426c8ed82c477880322e
ACR-278700e4adde4701a0985a5b5182a1a0
ACR-efb1428befd44ccf94fe26c803d19c1b
ACR-6066acafab1b4d0dbc50917252b380f8
ACR-efa1c80c7dcb491b9ad10443cd678d6a
ACR-bec304faee394c619e04178cc43d087e
ACR-5cc25380e54c4c6fafaea575934792d9
ACR-6eacbde50f474841b21b5d47e936ddbf
ACR-eb5baf2f6fe841a49b44339e5728f6dc
 */
package org.sonarsource.sonarlint.core.serverapi.issue;

import java.nio.file.Path;
import java.util.Set;
import mockwebserver3.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.scanner.protocol.input.ScannerInput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.exception.ServerErrorException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.entry;
import static org.sonarsource.sonarlint.core.serverapi.UrlUtils.urlEncode;

class IssueApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  private IssueApi underTest;

  @BeforeEach
  void setUp() {
    underTest = new IssueApi(mockServer.serverApiHelper());
  }

  @Test
  void should_download_all_issues_as_batch() {
    mockServer.addProtobufResponseDelimited("/batch/issues?key=keyyy", ScannerInput.ServerIssue.newBuilder().setRuleKey("ruleKey").build());

    var issues = underTest.downloadAllFromBatchIssues("keyyy", null, new SonarLintCancelMonitor());

    assertThat(issues)
      .extracting("ruleKey")
      .containsOnly("ruleKey");
  }

  @Test
  void should_download_all_issues_as_batch_from_branch() {
    mockServer.addProtobufResponseDelimited("/batch/issues?key=keyyy&branch=branchName", ScannerInput.ServerIssue.newBuilder().setRuleKey("ruleKey").build());

    var issues = underTest.downloadAllFromBatchIssues("keyyy", "branchName", new SonarLintCancelMonitor());

    assertThat(issues)
      .extracting("ruleKey")
      .containsOnly("ruleKey");
  }

  @Test
  void should_return_no_batch_issue_if_download_is_forbidden() {
    mockServer.addResponse("/batch/issues?key=keyyy", new MockResponse.Builder().code(403).build());

    var issues = underTest.downloadAllFromBatchIssues("keyyy", null, new SonarLintCancelMonitor());

    assertThat(issues).isEmpty();
  }

  @Test
  void should_return_no_batch_issue_if_endpoint_is_not_found() {
    mockServer.addResponse("/batch/issues?key=keyyy", new MockResponse.Builder().code(404).build());

    var issues = underTest.downloadAllFromBatchIssues("keyyy", null, new SonarLintCancelMonitor());

    assertThat(issues).isEmpty();
  }

  @Test
  void should_throw_an_error_if_batch_issue_download_fails() {
    mockServer.addResponse("/batch/issues?key=keyyy", new MockResponse.Builder().code(500).build());

    var throwable = catchThrowable(() -> underTest.downloadAllFromBatchIssues("keyyy", null, new SonarLintCancelMonitor()));

    assertThat(throwable).isInstanceOf(ServerErrorException.class);
  }

  @Test
  void should_throw_an_error_if_batch_issue_body__format_is_unexpected() {
    mockServer.addStringResponse("/batch/issues?key=keyyy", "nope");

    var throwable = catchThrowable(() -> underTest.downloadAllFromBatchIssues("keyyy", null, new SonarLintCancelMonitor()));

    assertThat(throwable).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void should_download_all_vulnerabilities() {
    mockServer.addProtobufResponse("/api/issues/search.protobuf?statuses=OPEN,CONFIRMED,REOPENED,RESOLVED&types=VULNERABILITY&componentKeys=keyyy&rules=ruleKey&ps=500&p=1",
      Issues.SearchWsResponse.newBuilder().addIssues(Issues.Issue.newBuilder().setKey("issueKey").build()).build());
    mockServer.addProtobufResponse("/api/issues/search.protobuf?statuses=OPEN,CONFIRMED,REOPENED,RESOLVED&types=VULNERABILITY&componentKeys=keyyy&rules=ruleKey&ps=500&p=2",
      Issues.SearchWsResponse.newBuilder().addComponents(Issues.Component.newBuilder().setKey("componentKey").setPath("componentPath").build()).build());

    var result = underTest.downloadVulnerabilitiesForRules("keyyy", Set.of("ruleKey"), null, new SonarLintCancelMonitor());

    assertThat(result.getIssues())
      .extracting("key")
      .containsOnly("issueKey");
    assertThat(result.getComponentPathsByKey())
      .containsOnly(entry("componentKey", Path.of("componentPath")));
  }

  @Test
  void should_fetch_server_issue_by_key() {
    var issueKey = "issueKey";
    var path = "/home/file.java";
    var projectKey = "projectKey";
    mockServer.addProtobufResponse("/api/issues/search.protobuf?issues=".concat(urlEncode(issueKey)).concat("&componentKeys=").concat(projectKey).concat("&ps=1&p=1"),
      Issues.SearchWsResponse.newBuilder()
        .addIssues(Issues.Issue.newBuilder().setKey(issueKey).build())
        .addComponents(Issues.Component.newBuilder().setPath(path).build())
        .setRules(Issues.SearchWsResponse.newBuilder().getRulesBuilder().addRules(Common.Rule.newBuilder().setKey("ruleKey").build()))
        .build());
    var serverIssueDetails = underTest.fetchServerIssue(issueKey, projectKey, "", "", new SonarLintCancelMonitor());
    assertThat(serverIssueDetails).isPresent();
    assertThat(serverIssueDetails.get().key).isEqualTo(issueKey);
    assertThat(serverIssueDetails.get().path).isEqualTo(Path.of(path));
  }

  @Test
  void should_not_fail_when_no_issue_found_by_key() {
    mockServer.addProtobufResponse("/api/issues/search.protobuf?issues=".concat(urlEncode("qwert")).concat("&componentKeys=myProject").concat("&ps=1&p=1"),
      Issues.SearchWsResponse.newBuilder().addIssues(Issues.Issue.newBuilder().build()).build());
    var serverIssueDetails = underTest.fetchServerIssue("non-existent", "myProject", "", "", new SonarLintCancelMonitor());
    assertThat(serverIssueDetails).isEmpty();
    assertThat(logTester.logs()).contains("Error while fetching issue");
  }

  @Test
  void should_not_fetch_server_issue_by_key_with_no_matching_component() {
    var issueKey = "issueKey";
    var path = "/home/file.java";
    mockServer.addProtobufResponse("/api/issues/search.protobuf?issues=".concat(urlEncode(issueKey)).concat("&componentKeys=differentIssueComponent").concat("&ps=1&p=1"),
      Issues.SearchWsResponse.newBuilder()
        .addIssues(Issues.Issue.newBuilder().setKey(issueKey).setComponent("issueComponent").build())
        .addComponents(Issues.Component.newBuilder().setPath(path).setKey("differentIssueComponent").build())
        .setRules(Issues.SearchWsResponse.newBuilder().getRulesBuilder().addRules(Common.Rule.newBuilder().setKey("ruleKey").build()))
        .build());
    var serverIssueDetails = underTest.fetchServerIssue(issueKey, "differentIssueComponent", "", "", new SonarLintCancelMonitor());
    assertThat(serverIssueDetails).isEmpty();
    assertThat(logTester.logs()).contains("No path found in components for the issue with key 'issueKey'");
  }

  @Test
  void should_fetch_branch_issue() {
    var issueKey = "issueKey";
    var path = "/home/file.java";
    var projectKey = "projectKey";
    var branch = "branch";
    mockServer.addProtobufResponse("/api/issues/search.protobuf?issues=".concat(urlEncode(issueKey))
        .concat("&componentKeys=").concat(projectKey)
        .concat("&ps=1&p=1")
        .concat("&branch=").concat(branch),
      Issues.SearchWsResponse.newBuilder()
        .addIssues(Issues.Issue.newBuilder().setKey(issueKey).build())
        .addComponents(Issues.Component.newBuilder().setPath(path).build())
        .setRules(Issues.SearchWsResponse.newBuilder().getRulesBuilder().addRules(Common.Rule.newBuilder().setKey("ruleKey").build()))
        .build());
    var serverIssueDetails = underTest.fetchServerIssue(issueKey, projectKey, branch, "", new SonarLintCancelMonitor());
    assertThat(serverIssueDetails).isPresent();
    assertThat(serverIssueDetails.get().key).isEqualTo(issueKey);
    assertThat(serverIssueDetails.get().path).isEqualTo(Path.of(path));
  }

  @Test
  void should_fetch_pull_request_issue() {
    var issueKey = "issueKey";
    var path = "/home/file.java";
    var projectKey = "projectKey";
    var pullRequest = "1234";
    mockServer.addProtobufResponse("/api/issues/search.protobuf?issues=".concat(urlEncode(issueKey))
        .concat("&componentKeys=").concat(projectKey)
        .concat("&ps=1&p=1")
        .concat("&pullRequest=").concat(pullRequest),
      Issues.SearchWsResponse.newBuilder()
        .addIssues(Issues.Issue.newBuilder().setKey(issueKey).build())
        .addComponents(Issues.Component.newBuilder().setPath(path).build())
        .setRules(Issues.SearchWsResponse.newBuilder().getRulesBuilder().addRules(Common.Rule.newBuilder().setKey("ruleKey").build()))
        .build());
    var serverIssueDetails = underTest.fetchServerIssue(issueKey, projectKey, "prbranch", pullRequest, new SonarLintCancelMonitor());
    assertThat(serverIssueDetails).isPresent();
    assertThat(serverIssueDetails.get().key).isEqualTo(issueKey);
    assertThat(serverIssueDetails.get().path).isEqualTo(Path.of(path));
  }

}
