/*
ACR-92a85ecce5f9453a9d73b9b25b1b0239
ACR-f68d66954a2143d3a021d5a999a13d04
ACR-f32de40f5f374d72b0b5a4e82612859a
ACR-9bce291e25bf43f5a90e048698f16e0b
ACR-6ae6426607654ad38cd3708a2b3f796c
ACR-a73c7c27fe324ae792e35ea26944dbaf
ACR-860a2210b61644969198410ad6510dc1
ACR-5c5e3826ebb84614b894a07d2c357dd6
ACR-e0388a3b5e174aeda74db1ff5ff0f377
ACR-a45cb8eef4be4386bdbc3a9df60268dc
ACR-abf4a0a4fcf5498b9bd99b6dbcd74326
ACR-fcae7d6e03964080970e1cc5c97b76dd
ACR-4d824d0fd4b94b598d3c730391fd74d3
ACR-1bf563645c9f4003bc60fa03d929d9d4
ACR-fa3e44f34b7a423cafd96da38b018daf
ACR-b6ad10c9fa0d46a2a00aaa1ed6afada4
ACR-8966f943d353464c884dfaae09d31241
 */
package org.sonarsource.sonarlint.core.serverapi.sca;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScaApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  private static final String EMPTY_ISSUES_RELEASES_JSON = """
    {
      "issuesReleases": [],
      "page": {
        "pageIndex": 1,
        "pageSize": 100,
        "total": 0
      }
    }
    """;

  private ScaApi scaApi;

  @Nested
  class SonarQubeServer {

    @BeforeEach
    void prepare() {
      scaApi = new ScaApi(mockServer.serverApiHelper());
    }

    @Test
    void should_get_issues_releases_with_empty_response() {
      mockServer.addStringResponse("/api/v2/sca/issues-releases?projectKey=my-project&branchKey=main&pageSize=500&pageIndex=1", EMPTY_ISSUES_RELEASES_JSON);

      var response = scaApi.getIssuesReleases("my-project", "main", new SonarLintCancelMonitor());

      assertThat(response.issuesReleases()).isEmpty();
    }

    @Test
    void should_get_issues_releases_of_vulnerability_type() {
      var uuid = UUID.randomUUID();
      var jsonResponse = String.format("""
      {
        "issuesReleases": [
          {
            "key": "%s",
            "type": "VULNERABILITY",
            "severity": "HIGH",
            "quality": "MAINTAINABILITY",
            "vulnerabilityId": "CVE-2023-12345",
            "cvssScore": "7.5",
            "release": {
              "packageName": "com.example.vulnerable",
              "version": "1.0.0"
            },
            "transitions": ["CONFIRM", "REOPEN"]
          }
        ],
        "page": {
          "pageIndex": 1,
          "pageSize": 100,
          "total": 1
        }
      }
      """, uuid);
      mockServer.addStringResponse("/api/v2/sca/issues-releases?projectKey=test-project&branchKey=feature%2Fmy-branch&pageSize=500&pageIndex=1", jsonResponse);

      var response = scaApi.getIssuesReleases("test-project", "feature/my-branch", new SonarLintCancelMonitor());

      assertThat(response.issuesReleases()).hasSize(1);
      var issueRelease = response.issuesReleases().get(0);
      assertThat(issueRelease.key()).isEqualTo(uuid);
      assertThat(issueRelease.type()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Type.VULNERABILITY);
      assertThat(issueRelease.severity()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Severity.HIGH);
      assertThat(issueRelease.quality()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.SoftwareQuality.MAINTAINABILITY);
      assertThat(issueRelease.vulnerabilityId()).isEqualTo("CVE-2023-12345");
      assertThat(issueRelease.cvssScore()).isEqualTo("7.5");
      assertThat(issueRelease.release().packageName()).isEqualTo("com.example.vulnerable");
      assertThat(issueRelease.release().version()).isEqualTo("1.0.0");
      assertThat(issueRelease.transitions()).containsExactly(
        GetIssuesReleasesResponse.IssuesRelease.Transition.CONFIRM,
        GetIssuesReleasesResponse.IssuesRelease.Transition.REOPEN);
    }

    @Test
    void should_get_issues_releases_of_prohibited_license_type() {
      var uuid = UUID.randomUUID();
      var jsonResponse = String.format("""
      {
        "issuesReleases": [
          {
            "key": "%s",
            "type": "PROHIBITED_LICENSE",
            "severity": "BLOCKER",
            "quality": "SECURITY",
            "vulnerabilityId": null,
            "cvssScore": null,
            "release": {
              "packageName": "com.example.prohibited",
              "version": "2.1.0"
            },
            "transitions": ["ACCEPT", "SAFE"]
          }
        ],
        "page": {
          "pageIndex": 1,
          "pageSize": 100,
          "total": 1
        }
      }
      """, uuid);
      mockServer.addStringResponse("/api/v2/sca/issues-releases?projectKey=license-project&branchKey=develop&pageSize=500&pageIndex=1", jsonResponse);

      var response = scaApi.getIssuesReleases("license-project", "develop", new SonarLintCancelMonitor());

      assertThat(response.issuesReleases()).hasSize(1);
      var issueRelease = response.issuesReleases().get(0);
      assertThat(issueRelease.key()).isEqualTo(uuid);
      assertThat(issueRelease.type()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Type.PROHIBITED_LICENSE);
      assertThat(issueRelease.severity()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Severity.BLOCKER);
      assertThat(issueRelease.quality()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.SoftwareQuality.SECURITY);
      assertThat(issueRelease.vulnerabilityId()).isNull();
      assertThat(issueRelease.cvssScore()).isNull();
      assertThat(issueRelease.release().packageName()).isEqualTo("com.example.prohibited");
      assertThat(issueRelease.release().version()).isEqualTo("2.1.0");
      assertThat(issueRelease.transitions()).containsExactly(
        GetIssuesReleasesResponse.IssuesRelease.Transition.ACCEPT,
        GetIssuesReleasesResponse.IssuesRelease.Transition.SAFE);
    }

    @Test
    void should_get_issues_releases_with_multiple_issues() {
      var uuid1 = UUID.randomUUID();
      var uuid2 = UUID.randomUUID();
      var jsonResponse = String.format("""
      {
        "issuesReleases": [
          {
            "key": "%s",
            "type": "VULNERABILITY",
            "severity": "MEDIUM",
            "quality": "RELIABILITY",
            "vulnerabilityId": "CVE-2023-12345",
            "cvssScore": "7.5",
            "release": {
              "packageName": "com.example.first",
              "version": "1.0.0"
            },
            "transitions": ["CONFIRM"]
          },
          {
            "key": "%s",
            "type": "PROHIBITED_LICENSE",
            "severity": "LOW",
            "quality": "MAINTAINABILITY",
            "vulnerabilityId": null,
            "cvssScore": null,
            "release": {
              "packageName": "com.example.second",
              "version": "2.0.0"
            },
            "transitions": ["ACCEPT", "SAFE", "FIXED"]
          }
        ],
        "page": {
          "pageIndex": 1,
          "pageSize": 100,
          "total": 2
        }
      }
      """, uuid1, uuid2);
      mockServer.addStringResponse("/api/v2/sca/issues-releases?projectKey=multi-project&branchKey=master&pageSize=500&pageIndex=1", jsonResponse);

      var response = scaApi.getIssuesReleases("multi-project", "master", new SonarLintCancelMonitor());

      assertThat(response.issuesReleases()).hasSize(2);

      var firstIssue = response.issuesReleases().get(0);
      assertThat(firstIssue.key()).isEqualTo(uuid1);
      assertThat(firstIssue.type()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Type.VULNERABILITY);
      assertThat(firstIssue.severity()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Severity.MEDIUM);
      assertThat(firstIssue.quality()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.SoftwareQuality.RELIABILITY);
      assertThat(firstIssue.vulnerabilityId()).isEqualTo("CVE-2023-12345");
      assertThat(firstIssue.cvssScore()).isEqualTo("7.5");
      assertThat(firstIssue.release().packageName()).isEqualTo("com.example.first");
      assertThat(firstIssue.release().version()).isEqualTo("1.0.0");
      assertThat(firstIssue.transitions()).containsExactly(GetIssuesReleasesResponse.IssuesRelease.Transition.CONFIRM);

      var secondIssue = response.issuesReleases().get(1);
      assertThat(secondIssue.key()).isEqualTo(uuid2);
      assertThat(secondIssue.type()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Type.PROHIBITED_LICENSE);
      assertThat(secondIssue.severity()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Severity.LOW);
      assertThat(secondIssue.quality()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.SoftwareQuality.MAINTAINABILITY);
      assertThat(secondIssue.vulnerabilityId()).isNull();
      assertThat(secondIssue.cvssScore()).isNull();
      assertThat(secondIssue.release().packageName()).isEqualTo("com.example.second");
      assertThat(secondIssue.release().version()).isEqualTo("2.0.0");
      assertThat(secondIssue.transitions()).containsExactly(
        GetIssuesReleasesResponse.IssuesRelease.Transition.ACCEPT,
        GetIssuesReleasesResponse.IssuesRelease.Transition.SAFE,
        GetIssuesReleasesResponse.IssuesRelease.Transition.FIXED);
    }

    @Test
    void should_handle_special_characters_in_project_key_and_branch_name() {
      mockServer.addStringResponse("/api/v2/sca/issues-releases?projectKey=my%3Aproject%2Bkey&branchKey=feature%2Fmy-branch%3Atest&pageSize=500&pageIndex=1", EMPTY_ISSUES_RELEASES_JSON);

      var response = scaApi.getIssuesReleases("my:project+key", "feature/my-branch:test", new SonarLintCancelMonitor());

      assertThat(response.issuesReleases()).isEmpty();
    }

    @Test
    void should_handle_malformed_json_response() {
      mockServer.addStringResponse("/api/v2/sca/issues-releases?projectKey=test&branchName=main&pageSize=500&pageIndex=1", "invalid json");

      assertThatThrownBy(() -> scaApi.getIssuesReleases("test", "main", new SonarLintCancelMonitor()))
        .isInstanceOf(Exception.class);
    }

    @Test
    void should_handle_empty_transitions() {
      var uuid = UUID.randomUUID();
      var jsonResponse = String.format("""
      {
        "issuesReleases": [
          {
            "key": "%s",
            "type": "VULNERABILITY",
            "severity": "INFO",
            "release": {
              "packageName": "com.example.minimal",
              "version": "0.1.0"
            },
            "transitions": []
          }
        ],
        "page": {
          "pageIndex": 1,
          "pageSize": 100,
          "total": 1
        }
      }
      """, uuid);
      mockServer.addStringResponse("/api/v2/sca/issues-releases?projectKey=minimal-project&branchKey=main&pageSize=500&pageIndex=1", jsonResponse);

      var response = scaApi.getIssuesReleases("minimal-project", "main", new SonarLintCancelMonitor());

      assertThat(response.issuesReleases()).hasSize(1);
      var issueRelease = response.issuesReleases().get(0);
      assertThat(issueRelease.key()).isEqualTo(uuid);
      assertThat(issueRelease.type()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Type.VULNERABILITY);
      assertThat(issueRelease.severity()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Severity.INFO);
      assertThat(issueRelease.transitions()).isEmpty();
    }
  }

  @Nested
  class SonarQubeCloud {

    @BeforeEach
    void prepare() {
      scaApi = new ScaApi(mockServer.serverApiHelper("orgKey"));
    }

    @Test
    void should_get_issues_releases_with_empty_response() {
      mockServer.addStringResponse("/sca/issues-releases?projectKey=my-project&branchKey=main&pageSize=500&pageIndex=1", EMPTY_ISSUES_RELEASES_JSON);

      var response = scaApi.getIssuesReleases("my-project", "main", new SonarLintCancelMonitor());

      assertThat(response.issuesReleases()).isEmpty();
    }

    @Test
    void should_get_issues_releases_of_vulnerability_type() {
      var uuid = UUID.randomUUID();
      var jsonResponse = String.format("""
      {
        "issuesReleases": [
          {
            "key": "%s",
            "type": "VULNERABILITY",
            "severity": "HIGH",
            "quality": "MAINTAINABILITY",
            "vulnerabilityId": "CVE-2023-12345",
            "cvssScore": "7.5",
            "release": {
              "packageName": "com.example.vulnerable",
              "version": "1.0.0"
            },
            "transitions": ["CONFIRM", "REOPEN"]
          }
        ],
        "page": {
          "pageIndex": 1,
          "pageSize": 100,
          "total": 1
        }
      }
      """, uuid);
      mockServer.addStringResponse("/sca/issues-releases?projectKey=test-project&branchKey=feature%2Fmy-branch&pageSize=500&pageIndex=1", jsonResponse);

      var response = scaApi.getIssuesReleases("test-project", "feature/my-branch", new SonarLintCancelMonitor());

      assertThat(response.issuesReleases()).hasSize(1);
      var issueRelease = response.issuesReleases().get(0);
      assertThat(issueRelease.key()).isEqualTo(uuid);
      assertThat(issueRelease.type()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Type.VULNERABILITY);
      assertThat(issueRelease.severity()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.Severity.HIGH);
      assertThat(issueRelease.quality()).isEqualTo(GetIssuesReleasesResponse.IssuesRelease.SoftwareQuality.MAINTAINABILITY);
      assertThat(issueRelease.vulnerabilityId()).isEqualTo("CVE-2023-12345");
      assertThat(issueRelease.cvssScore()).isEqualTo("7.5");
      assertThat(issueRelease.release().packageName()).isEqualTo("com.example.vulnerable");
      assertThat(issueRelease.release().version()).isEqualTo("1.0.0");
      assertThat(issueRelease.transitions()).containsExactly(
        GetIssuesReleasesResponse.IssuesRelease.Transition.CONFIRM,
        GetIssuesReleasesResponse.IssuesRelease.Transition.REOPEN);
    }
  }
}
