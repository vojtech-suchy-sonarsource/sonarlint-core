/*
ACR-f973026820e24d5ebe26f4517a78f168
ACR-dfe58934b35c41f7bf481d5f500d7418
ACR-4c4c579102ab488aa9b9c2bf99699641
ACR-d41bfeb7273e4e2bb5076d369064dbea
ACR-7bcbb5473e9a4373a1f9e53ed3b9fbe7
ACR-eb94ed94cf1742219a24f3ba9c27c1fc
ACR-70d9d84dd9244c9dbe5cba6039502bfd
ACR-295322f05243486e88297ce1e499596a
ACR-e3a363a0337f4c4fbfb2f51434d01805
ACR-76f75a39a24149b29b524fbee750072c
ACR-2068d9021e7b4e55b40507b2e93d4a16
ACR-833202ab3a844a70afd9b0732676dcbf
ACR-b3570b3ba7794c0e81722d8f532e08a2
ACR-4f8d4eacd0a749e8ae4724f01fb37fd3
ACR-b91c91337b6d4cdf9aa0dd419a206195
ACR-8ec17897bcb14a0d985e205ee3efd0e1
ACR-4c4106b36b3b43548db6ea9a318891f2
 */
package mediumtest.ai.ide;

import java.time.Duration;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgent;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.GetHookScriptContentParams;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.EMBEDDED_SERVER;

class AiHookMediumTests {

  @SonarLintTest
  void it_should_return_hook_script_content_for_windsurf_with_embedded_port(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .withClientName("ClientName")
      .start(fakeClient);

    //ACR-4279a4111cd94cd0b6c0be96805e4a93
    await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(backend.getEmbeddedServerPort()).isGreaterThan(0));

    var response = backend.getAiAgentService()
      .getHookScriptContent(new GetHookScriptContentParams(AiAgent.WINDSURF))
      .join();

    //ACR-1031b84926a04079b1b6b7793cc4a1fa
    assertThat(response.getScriptFileName()).matches("sonarqube_analysis_hook\\.(js|py|sh)");
    assertThat(response.getScriptContent())
      .contains("SonarQube for IDE Windsurf Hook")
      .contains("sonarqube_analysis_hook")
      .contains("/sonarlint/api/analysis/files")
      .contains("/sonarlint/api/status")
      .contains("STARTING_PORT")
      .contains("ENDING_PORT")
      .containsAnyOf(
        "EXPECTED_IDE_NAME = 'Windsurf'",  //ACR-4eae48dbc11344d3b40c43d665b2aa81
        "EXPECTED_IDE_NAME=\"Windsurf\""   //ACR-80ab2681c3cf47dd8493725ca12dfaae
      );

    //ACR-a16ed1d5287045f6bc578115a103638f
    assertThat(response.getConfigFileName()).isEqualTo("hooks.json");
    assertThat(response.getConfigContent()).contains("\"post_write_code\"");
    assertThat(response.getConfigContent()).contains("{{SCRIPT_PATH}}");
    assertThat(response.getConfigContent()).contains("\"show_output\": true");
  }

  @SonarLintTest
  void it_should_throw_exception_for_cursor_not_yet_implemented(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .withClientName("ClientName")
      .start(fakeClient);

    //ACR-c605226c1e0543399964c2717a92a270
    await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(backend.getEmbeddedServerPort()).isGreaterThan(0));

    var futureResponse = backend.getAiAgentService()
      .getHookScriptContent(new GetHookScriptContentParams(AiAgent.CURSOR));

    assertThat(futureResponse)
      .failsWithin(Duration.ofSeconds(2))
      .withThrowableThat()
      .withCauseInstanceOf(UnsupportedOperationException.class)
      .withMessageContaining("hook configuration not yet implemented");
  }

}

