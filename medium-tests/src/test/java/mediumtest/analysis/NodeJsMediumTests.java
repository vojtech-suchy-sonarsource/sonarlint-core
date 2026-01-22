/*
ACR-615928614aa34635956c630134f08663
ACR-b621a4958af34c0fb28fbf1cd7d2e0c0
ACR-4bbef63a3316486d8bc06e646acfcf9c
ACR-16f973131d744bedba9a7617999b54bb
ACR-c13e6a35a9ac4a40a29cda22e361ea2b
ACR-9bba9ca1d8af4f2682cc4c38040c0f03
ACR-9c1276f821a94f6db9d3c62fe4a7c829
ACR-8a1b33b71e2b40afb877f5abf6dab63d
ACR-115dbcf690c9439db870e62a40563833
ACR-0ad5489011ac4417930c570411fa5150
ACR-71085d00d32e4f759ba394d291cd6f15
ACR-5a8cc63992a041f89963dd6e969d9c36
ACR-ca3f1976769b4f23b08c6e3240fe4e20
ACR-32499c8482dc4b609be5c77606c42071
ACR-436499e5f7f44c80935a609ff917998a
ACR-ddcc62e13422451dacb774e824cf81b7
ACR-453d88e2c103482fa2f6224eaadd6b30
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
