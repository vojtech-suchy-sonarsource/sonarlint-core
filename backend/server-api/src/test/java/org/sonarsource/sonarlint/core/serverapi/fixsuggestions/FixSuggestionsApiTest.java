/*
ACR-ba43a417c6d7471d9993e9f7c138187c
ACR-179e3c985360436690ad07771e7a6463
ACR-8980097eb926416382e019811cc5d387
ACR-ec9b3d3273184d76ad76261406d077ca
ACR-07ab9a27bba0499ca22f654b5ba60949
ACR-bd7f75ac03c848439186d50405ce4837
ACR-04f2e0784cd34d78bf0a0b224c551ef8
ACR-411de4c3966142bd82dd67110bd78cb3
ACR-f43d1a8de74e45b5a879d91c1223c970
ACR-7cb9108b30a04b7ca4742eb09b88c31c
ACR-6dd4643edf2749d49871c047b3d3fa81
ACR-88aa609d6cf24116b911aeb2d49234d2
ACR-d9820e2441ee475a99aba456ee98f422
ACR-09ab044fdae24096a2d96082895b6bc6
ACR-41609e1940eb4cac8012bd9ae1993d55
ACR-373735af5c4a448e8986fc673d6817e6
ACR-0b308a74fa154fc3a76d863335659ce0
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
