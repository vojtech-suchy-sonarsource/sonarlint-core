/*
ACR-7929b53a5ff74bf7afd44e297bc01c6c
ACR-626d4dc874d644f791fd0947bae479d1
ACR-cc4722e32a1c42d19cb0096e6cd42003
ACR-866961f841d044c18903f3ad9975981e
ACR-11e977fa70994ea988264651a0889a20
ACR-2c005fb5bc2244c5bd3cc8f941e80c02
ACR-119d5f8f5d8b46bb9a7780dbc983354d
ACR-9c3f9804dbf04298a0cc7ebb972570ea
ACR-6400fe7245724689bc1df0b559f45019
ACR-87c45c261ff04573a1be8cab0677c84c
ACR-cd1b637610f548d182708c84c041e5c6
ACR-dfe9bce4b7e0448cbac3d83dd7508169
ACR-b475df3d08154a559312c9dad9200197
ACR-ad3ebd48df5948ad880579a86f6bd8e1
ACR-b2da466955194721a5b7aa6b10d0a0cb
ACR-3774690c63d2466faca40829a217706e
ACR-bb3572453050457cac4eff9b48fc7ec7
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
