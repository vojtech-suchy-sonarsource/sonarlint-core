/*
ACR-8e21b25f5d5042009b4c247cdcb3f5f3
ACR-4a222a05be6947608f9000c75df8c88b
ACR-f9434002e54d41a980e73ba742b25fb4
ACR-0b4887990a6d464a9d2adec77fbc202d
ACR-d97d7f3007b44159a0354abe483142c8
ACR-562aaa74f8fd4f51a9efa63f3f698861
ACR-c772fa7dd84b49d09f31ce653c052709
ACR-6090a52a16e24f74a62409392090d307
ACR-b3c34d6d56014fdcbe906ca1238a8fba
ACR-2907cedd0ee2419487b175a5de9ed3ad
ACR-c970d8a45e3a42e1993cf1d5d22b20e2
ACR-1753b4b954a941b0a35568903a15a71e
ACR-456ee5d82a2d4d64a41764d8adaee7d3
ACR-871c4e42996a4eaaa7177e91f53c18f0
ACR-3ef2f02664d1478eb96f87a8ff741ad3
ACR-5afd8a4586d94b3383f18daa72e30846
ACR-ae13f04d717748359f791c02d826d1ac
 */
package org.sonarsource.sonarlint.core.serverapi.fixsuggestions;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.exception.UnexpectedBodyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FixSuggestionsApiTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  private FixSuggestionsApi underTest;

  @BeforeEach
  void setUp() {
    underTest = new FixSuggestionsApi(mockServer.serverApiHelper());
  }

  @Nested
  class GetAiSuggestion {

    @Test
    void it_should_throw_an_exception_if_the_body_is_malformed() {
      mockServer.addStringResponse("/fix-suggestions/ai-suggestions", """
        {
          "id": "XXX
        }
        """);

      var throwable = catchThrowable(() -> underTest.getAiSuggestion(
        new AiSuggestionRequestBodyDto("orgKey", "projectKey", new AiSuggestionRequestBodyDto.Issue("message", 0, 0, "rule:key", "source")), new SonarLintCancelMonitor()));

      assertThat(throwable).isInstanceOf(UnexpectedBodyException.class);
    }

    @Test
    void it_should_return_the_generated_suggestion_for_sonarqube_cloud() {
      mockServer.addStringResponse("/fix-suggestions/ai-suggestions", """
        {
          "id": "9d4e18f6-f79f-41ad-a480-1c96bd58d58f",
          "explanation": "This is the way",
          "changes": [
            {
              "startLine": 0,
              "endLine": 0,
              "newCode": "This is the new code"
            }
          ]
        }
        """);
      underTest = new FixSuggestionsApi(mockServer.serverApiHelper("orgKey"));

      var response = underTest.getAiSuggestion(new AiSuggestionRequestBodyDto("orgKey", "projectKey", new AiSuggestionRequestBodyDto.Issue("message", 0, 0, "rule:key", "source")),
        new SonarLintCancelMonitor());

      assertThat(response)
        .isEqualTo(new AiSuggestionResponseBodyDto(UUID.fromString("9d4e18f6-f79f-41ad-a480-1c96bd58d58f"), "This is the way",
          List.of(new AiSuggestionResponseBodyDto.ChangeDto(0, 0, "This is the new code"))));
    }

    @Test
    void it_should_return_the_generated_suggestion_for_sonarqube_server() {
      mockServer.addStringResponse("/api/v2/fix-suggestions/ai-suggestions", """
        {
          "id": "9d4e18f6-f79f-41ad-a480-1c96bd58d58f",
          "explanation": "This is the way",
          "changes": [
            {
              "startLine": 0,
              "endLine": 0,
              "newCode": "This is the new code"
            }
          ]
        }
        """);

      var response = underTest.getAiSuggestion(new AiSuggestionRequestBodyDto("orgKey", "projectKey", new AiSuggestionRequestBodyDto.Issue("message", 0, 0, "rule:key", "source")),
        new SonarLintCancelMonitor());

      assertThat(response)
        .isEqualTo(new AiSuggestionResponseBodyDto(UUID.fromString("9d4e18f6-f79f-41ad-a480-1c96bd58d58f"), "This is the way",
          List.of(new AiSuggestionResponseBodyDto.ChangeDto(0, 0, "This is the new code"))));
    }
  }

  @Nested
  class GetSupportedRules {

    @Test
    void it_should_throw_an_exception_if_the_body_is_malformed() {
      mockServer.addStringResponse("/fix-suggestions/supported-rules", """
        [
        """);

      var throwable = catchThrowable(() -> underTest.getSupportedRules(new SonarLintCancelMonitor()));

      assertThat(throwable).isInstanceOf(UnexpectedBodyException.class);
    }

    @Test
    void it_should_return_the_list_of_supported_rules_for_sonarqube_cloud() {
      mockServer.addStringResponse("/fix-suggestions/supported-rules", """
        {
          "rules": ["repo:rule1", "repo:rule2"]
        }
        """);
      underTest = new FixSuggestionsApi(mockServer.serverApiHelper("orgKey"));

      var response = underTest.getSupportedRules(new SonarLintCancelMonitor());

      assertThat(response)
        .isEqualTo(new SupportedRulesResponseDto(Set.of("repo:rule1", "repo:rule2")));
    }

    @Test
    void it_should_return_the_list_of_supported_rules_for_sonarqube_server() {
      mockServer.addStringResponse("/api/v2/fix-suggestions/supported-rules", """
        {
          "rules": ["repo:rule1", "repo:rule2"]
        }
        """);

      var response = underTest.getSupportedRules(new SonarLintCancelMonitor());

      assertThat(response)
        .isEqualTo(new SupportedRulesResponseDto(Set.of("repo:rule1", "repo:rule2")));
    }
  }

  @Nested
  class GetOrganizationConfigs {

    @Test
    void it_should_throw_an_exception_if_the_body_is_malformed() {
      mockServer.addStringResponse("/fix-suggestions/organization-configs/orgId", """
        [
        """);

      var throwable = catchThrowable(() -> underTest.getOrganizationConfigs("orgId", new SonarLintCancelMonitor()));

      assertThat(throwable).isInstanceOf(UnexpectedBodyException.class);
    }

    @Test
    void it_should_return_the_organization_config() {
      mockServer.addStringResponse("/fix-suggestions/organization-configs/orgId", """
        {
          "organizationId": "orgId",
          "enablement": "DISABLED",
          "organizationEligible": true,
          "aiCodeFix": {
            "enablement": "DISABLED",
            "organizationEligible": true
          }
        }
        """);

      var response = underTest.getOrganizationConfigs("orgId", new SonarLintCancelMonitor());

      assertThat(response)
        .isEqualTo(new OrganizationConfigsResponseDto("orgId",
          new AiCodeFixConfiguration(SuggestionFeatureEnablement.DISABLED, null, true)));
    }
  }

}
