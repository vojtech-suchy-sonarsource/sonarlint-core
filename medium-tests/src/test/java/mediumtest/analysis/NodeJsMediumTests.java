/*
ACR-3ac752b178834169bc715d39dbaa2349
ACR-6b399b2b6ee34d3791eb85702c559e0f
ACR-b8b774dfccf74313b18bdb08da3af7d5
ACR-e816d5a208eb4ceea3a0b6d3b556c9a6
ACR-d6ca1843cc514aadb0a7f3f7a35f8004
ACR-4e58cd4e36f04570bf65786957ceed70
ACR-b958fec14b464d1ca957913e313c63ae
ACR-2b7ccecb597f4a1c9c92bea96794b1b1
ACR-7edf38637a6d4effa9d138c3b117dd08
ACR-f8c0af4acec24936b69c194a01ae3caa
ACR-20a98e565ff74c6892fa393e5e0858dc
ACR-6e5e41e1ec014523a8f5c40a770ea7bf
ACR-40e5d3ade6644f07bdbc9c3ec4f4e211
ACR-982662f3a8bc475191cb22b45ec41ef9
ACR-218535e00fe84a54b46a861589e025cc
ACR-648db923460b4e5db6269519b07aec3c
ACR-9b0f258ca16546cba12d76ecbcf2ad79
 */
package mediumtest.analysis;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.DidChangeClientNodeJsPathParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetStandaloneRuleDescriptionParams;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;

class NodeJsMediumTests {

  private static final String JAVASCRIPT_S1481 = "javascript:S1481";

  @SonarLintTest
  void wrong_node_path_prevents_loading_sonar_js_rules(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)
      .withClientNodeJsPath(Paths.get("wrong"))
      .start(client);

    var futureRuleDetails = backend.getRulesService().getStandaloneRuleDetails(new GetStandaloneRuleDescriptionParams(JAVASCRIPT_S1481));

    assertThat(futureRuleDetails).failsWithin(Duration.ofSeconds(1))
      .withThrowableOfType(ExecutionException.class)
      .havingCause()
      .isInstanceOf(ResponseErrorException.class)
      .withMessage("Could not find rule 'javascript:S1481' in embedded rules");
    assertThat(client.getLogMessages()).contains("Unable to query node version");
  }

  @SonarLintTest
  void can_retrieve_auto_detected_node_js(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)
      .start(client);

    var nodeJsDetails = backend.getAnalysisService().getAutoDetectedNodeJs().join().getDetails();

    assertThat(nodeJsDetails).isNotNull();
    assertThat(nodeJsDetails.getPath()).isNotNull();
    assertThat(nodeJsDetails.getVersion()).isNotNull();
  }

  @SonarLintTest
  void can_retrieve_forced_node_js(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)
      .start(client);

    var nodeJsDetails = backend.getAnalysisService().didChangeClientNodeJsPath(new DidChangeClientNodeJsPathParams(null)).join().getDetails();

    assertThat(nodeJsDetails).isNotNull();
    assertThat(nodeJsDetails.getPath()).isNotNull();
    assertThat(nodeJsDetails.getVersion()).isNotNull();
  }

}
