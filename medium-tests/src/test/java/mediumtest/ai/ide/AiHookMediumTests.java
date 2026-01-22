/*
ACR-7f76edab021f47c1937ac1cb44331f0c
ACR-c86e9eafe5a349afafec59d66e766f8a
ACR-04e6c6e150674aa7a1d7e8187662558b
ACR-f87406b1cda74f7b8a29515a2fb7bfff
ACR-8f60aa1ebc9a4200895c63d19511ad69
ACR-79a9d4c444d841098efeb791970ae42d
ACR-43b6e4450ad148728ae2b181298be2be
ACR-bc79f6104f604a849dc2bc8664aba98c
ACR-da480d8f5e8140989bdefe6e17889ed2
ACR-d1ce9290579f44e79a5cbf0a579fc225
ACR-13d53b1747274453a2713822e65abdd2
ACR-462ca233f2dc4cd7bf16326680d3ad63
ACR-656ea55983384e9dbeabea48b43de9dd
ACR-fa2e39ab061042a29688dd12e4dc424c
ACR-2508ea1f07c74355bdb56a776e6308c1
ACR-b6cb7b5d0553439f87436b43d9b839b2
ACR-c4698921b2964566881f1245dba30573
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

    //ACR-1d9a1096a5a643b39475c6bd0142ce28
    await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(backend.getEmbeddedServerPort()).isGreaterThan(0));

    var response = backend.getAiAgentService()
      .getHookScriptContent(new GetHookScriptContentParams(AiAgent.WINDSURF))
      .join();

    //ACR-895e95e08f54408882607f6d49f74d96
    assertThat(response.getScriptFileName()).matches("sonarqube_analysis_hook\\.(js|py|sh)");
    assertThat(response.getScriptContent())
      .contains("SonarQube for IDE Windsurf Hook")
      .contains("sonarqube_analysis_hook")
      .contains("/sonarlint/api/analysis/files")
      .contains("/sonarlint/api/status")
      .contains("STARTING_PORT")
      .contains("ENDING_PORT")
      .containsAnyOf(
        "EXPECTED_IDE_NAME = 'Windsurf'",  //ACR-73e6b23c9ea7472095a6ba8ea0f9e091
        "EXPECTED_IDE_NAME=\"Windsurf\""   //ACR-a46c53547e1a41a8a78014c905991dff
      );

    //ACR-334c44bbf56943029ebb4a2c047a2128
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

    //ACR-77d384d39f2440e1abd95741c790b605
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

