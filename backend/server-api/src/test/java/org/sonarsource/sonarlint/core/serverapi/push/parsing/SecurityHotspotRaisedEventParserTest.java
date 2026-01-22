/*
ACR-7a7b51acaaed4ff7b688cfa02ef00a94
ACR-966fec8d8be4489b9b1adbac7abe6018
ACR-aa7d5edb98c74292860fa6dfba1bf9a1
ACR-6ec2efd259664a6e9bb8652819ca9fa5
ACR-53565eccc3fe42b5ae41c5f148333d9a
ACR-57897148643d43b69caa36a3cc20e40b
ACR-e4c3cf30158e466d9bf0aeaeb9be9b88
ACR-49e756d040cf4a0da29dcff7d54c824e
ACR-3f522222ccee4602ad0e41c216a82b8b
ACR-fc1c7add65094f6fb099bd8598785701
ACR-59ea1e18117342c0a9682126bdf7a8fe
ACR-17cac17155aa44b08cc05e6cf203e556
ACR-a98306a262f34c88a2634dafa83a73cc
ACR-7fcadddbacb846aeaffaf8e2d46587b8
ACR-1ef2218ec6354789a639573b9d282fcb
ACR-d2247d56a4224e0c83b7820a857e3d9f
ACR-fa59489a9933485397822158c00444b5
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityHotspotRaisedEventParserTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  SecurityHotspotRaisedEventParser parser = new SecurityHotspotRaisedEventParser();
  private static final String TEST_PAYLOAD_WITHOUT_KEY = """
    {
      "status": "TO_REVIEW",
      "vulnerabilityProbability": "MEDIUM",
      "creationDate": 1685006550000,
      "mainLocation": {
        "filePath": "src/main/java/org/example/Main.java",
        "message": "Make sure that using this pseudorandom number generator is safe here.",
        "textRange": {
          "startLine": 12,
          "startLineOffset": 29,
          "endLine": 12,
          "endLineOffset": 36,
          "hash": "43b5c9175984c071f30b873fdce0a000"
        }
      },
      "ruleKey": "java:S2245",
      "projectKey": "test",
      "branch": "some-branch"
    }""";

  private static final String TEST_PAYLOAD_WITHOUT_BRANCH = """
    {
      "status": "TO_REVIEW",
      "vulnerabilityProbability": "MEDIUM",
      "creationDate": 1685006550000,
      "mainLocation": {
        "filePath": "src/main/java/org/example/Main.java",
        "message": "Make sure that using this pseudorandom number generator is safe here.",
        "textRange": {
          "startLine": 12,
          "startLineOffset": 29,
          "endLine": 12,
          "endLineOffset": 36,
          "hash": "43b5c9175984c071f30b873fdce0a000"
        }
      },
      "ruleKey": "java:S2245",
      "key": "AYhSN6mVrRF_krvNbHl1",
      "projectKey": "test"
    }""";

  private static final String TEST_PAYLOAD_WITHOUT_PROJECT_KEY = """
    {
      "status": "TO_REVIEW",
      "vulnerabilityProbability": "MEDIUM",
      "creationDate": 1685006550000,
      "mainLocation": {
        "filePath": "src/main/java/org/example/Main.java",
        "message": "Make sure that using this pseudorandom number generator is safe here.",
        "textRange": {
          "startLine": 12,
          "startLineOffset": 29,
          "endLine": 12,
          "endLineOffset": 36,
          "hash": "43b5c9175984c071f30b873fdce0a000"
        }
      },
      "ruleKey": "java:S2245",
      "key": "AYhSN6mVrRF_krvNbHl1",
      "branch": "some-branch"
    }""";

  private static final String TEST_PAYLOAD_WITHOUT_FILE_PATH = """
    {
      "status": "TO_REVIEW",
      "vulnerabilityProbability": "MEDIUM",
      "creationDate": 1685006550000,
      "mainLocation": {
        "message": "Make sure that using this pseudorandom number generator is safe here.",
        "textRange": {
          "startLine": 12,
          "startLineOffset": 29,
          "endLine": 12,
          "endLineOffset": 36,
          "hash": "43b5c9175984c071f30b873fdce0a000"
        }
      },
      "ruleKey": "java:S2245",
      "key": "AYhSN6mVrRF_krvNbHl1",
      "projectKey": "test",
      "branch": "some-branch"
    }""";

  private static final String VALID_PAYLOAD = """
    {
      "status": "TO_REVIEW",
      "vulnerabilityProbability": "MEDIUM",
      "creationDate": 1685006550000,
      "mainLocation": {
        "filePath": "src/main/java/org/example/Main.java",
        "message": "Make sure that using this pseudorandom number generator is safe here.",
        "textRange": {
          "startLine": 12,
          "startLineOffset": 29,
          "endLine": 12,
          "endLineOffset": 36,
          "hash": "43b5c9175984c071f30b873fdce0a000"
        }
      },
      "ruleKey": "java:S2245",
      "key": "AYhSN6mVrRF_krvNbHl1",
      "projectKey": "test",
      "branch": "some-branch"
    }""";

  private static final String VALID_PAYLOAD_REVIEWED = """
    {
      "status": "REVIEWED",
      "vulnerabilityProbability": "MEDIUM",
      "creationDate": 1685006550000,
      "mainLocation": {
        "filePath": "src/main/java/org/example/Main.java",
        "message": "Make sure that using this pseudorandom number generator is safe here.",
        "textRange": {
          "startLine": 12,
          "startLineOffset": 29,
          "endLine": 12,
          "endLineOffset": 36,
          "hash": "43b5c9175984c071f30b873fdce0a000"
        }
      },
      "ruleKey": "java:S2245",
      "key": "AYhSN6mVrRF_krvNbHl1",
      "projectKey": "test",
      "branch": "some-branch"
    }""";

  @ParameterizedTest
  @ValueSource(strings = {TEST_PAYLOAD_WITHOUT_KEY, TEST_PAYLOAD_WITHOUT_PROJECT_KEY, TEST_PAYLOAD_WITHOUT_FILE_PATH, TEST_PAYLOAD_WITHOUT_BRANCH})
  void shouldReturnEmptyOptionalWhenPayloadIsInvalid(String invalidPayload) {
    var parseResult = parser.parse(invalidPayload);
    assertThat(parseResult).isEmpty();
  }

  @Test
  void shouldReturnChangeEventWhenPayloadIsValid() {
    var parsedResult = parser.parse(VALID_PAYLOAD);
    assertThat(parsedResult).isPresent();
    assertThat(parsedResult.get().getHotspotKey()).isEqualTo("AYhSN6mVrRF_krvNbHl1");
    assertThat(parsedResult.get().getStatus()).isEqualTo(HotspotReviewStatus.TO_REVIEW);
    assertThat(parsedResult.get().getProjectKey()).isEqualTo("test");
    assertThat(parsedResult.get().getMainLocation().getFilePath()).isEqualTo(Path.of("src/main/java/org/example/Main.java"));
    assertThat(parsedResult.get().getBranch()).isEqualTo("some-branch");
    assertThat(parsedResult.get().getRuleKey()).isEqualTo("java:S2245");
    assertThat(parsedResult.get().getMainLocation().getMessage()).isEqualTo("Make sure that using this pseudorandom number generator is safe here.");
  }

  @Test
  void shouldReturnChangeEventWhenPayloadIsValidAndHotspotIsReviewed() {
    var parsedResult = parser.parse(VALID_PAYLOAD_REVIEWED);
    assertThat(parsedResult).isPresent();
    assertThat(parsedResult.get().getHotspotKey()).isEqualTo("AYhSN6mVrRF_krvNbHl1");
    assertThat(parsedResult.get().getStatus()).isEqualTo(HotspotReviewStatus.SAFE);
    assertThat(parsedResult.get().getProjectKey()).isEqualTo("test");
    assertThat(parsedResult.get().getMainLocation().getFilePath()).isEqualTo(Path.of("src/main/java/org/example/Main.java"));
    assertThat(parsedResult.get().getBranch()).isEqualTo("some-branch");
    assertThat(parsedResult.get().getRuleKey()).isEqualTo("java:S2245");
    assertThat(parsedResult.get().getMainLocation().getMessage()).isEqualTo("Make sure that using this pseudorandom number generator is safe here.");
  }
}
