/*
ACR-630d47ecf8e143c3b9390dcfa4bc5d08
ACR-7575ff3fc38344dab27ff569aee2110e
ACR-187a6fd240f243788300df18d617a9f3
ACR-681c2a02a7b04a0b82dff00a880fde52
ACR-8620be2facf746bbb29cefb1cd388e3b
ACR-acdb4aa7036f42198bc9ba3d41ccf306
ACR-cdc870ad20514648bf9ac6f456e565a1
ACR-67541f021a124694bc48cc1c08d3e3d8
ACR-b7a3470e7e0641c98fe70fd61c5238a8
ACR-896e92fa0d3f404e8341ccd619213811
ACR-c3c0bd2d93cf483da2a6b92bd9647379
ACR-b3a664db4bf242e5b78962eda05c68f7
ACR-7f9445335b984b4e832ad9e208eb862d
ACR-b6e4d6934e354fcfa382bb1695598d9f
ACR-3403d49e420549be9288ed0ec3005df7
ACR-215b2de94b984915ba05908d5505089f
ACR-4ed8542968ae46999f0e3d49a38c972e
 */
package mediumtest.ai.ide;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgent;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.GetRuleFileContentParams;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;

class AiAgentMediumTests {

  @SonarLintTest
  void it_should_return_the_rule_file_content_for_cursor(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .start();

    var response = backend.getAiAgentService().getRuleFileContent(new GetRuleFileContentParams(AiAgent.CURSOR)).join();

    assertThat(response.getContent()).contains("alwaysApply: true");
    assertThat(response.getContent()).contains("IMPORTANT");
    assertThat(response.getContent()).contains("analyze_file_list");
    assertThat(response.getContent()).contains("Important Tool Guidelines");
  }

  @SonarLintTest
  void it_should_return_the_rule_file_content_for_github_copilot(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .start();

    var response = backend.getAiAgentService().getRuleFileContent(new GetRuleFileContentParams(AiAgent.GITHUB_COPILOT)).join();

    assertThat(response.getContent()).doesNotContain("alwaysApply: true");
    assertThat(response.getContent()).contains("applyTo: \"**/*\"");
    assertThat(response.getContent()).contains("IMPORTANT");
    assertThat(response.getContent()).contains("analyze_file_list");
    assertThat(response.getContent()).contains("Important Tool Guidelines");
  }

  @SonarLintTest
  void it_should_return_the_rule_file_content_for_windsurf(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .start();

    var response = backend.getAiAgentService().getRuleFileContent(new GetRuleFileContentParams(AiAgent.WINDSURF)).join();

    assertThat(response.getContent()).contains("alwaysApply: true");
    assertThat(response.getContent()).contains("IMPORTANT");
    assertThat(response.getContent()).contains("analyze_file_list");
    assertThat(response.getContent()).contains("Important Tool Guidelines");
  }

  @SonarLintTest
  void it_should_return_the_rule_file_content_for_kiro(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .start();

    var response = backend.getAiAgentService().getRuleFileContent(new GetRuleFileContentParams(AiAgent.KIRO)).join();

    assertThat(response.getContent()).contains("inclusion: always");
    assertThat(response.getContent()).contains("IMPORTANT");
    assertThat(response.getContent()).contains("analyze_file_list");
    assertThat(response.getContent()).contains("Important Tool Guidelines");
  }

}
