/*
ACR-39d6d5d5c28440cab0ea4d8b459fbe46
ACR-ef66b407f2ce4c03b0d2c6ff7ae4c0f3
ACR-4c0c19cf73c54aa2affa3f4c1eeba268
ACR-76ffa7cccd464612ba896f241f333c6e
ACR-45492dfe8695484e9218d510e68c0e74
ACR-d47d28db28474dde9c2b383f1eb550b6
ACR-06648fec50d348c7864e43af7bbea2dd
ACR-c45596e76be84d06851c4b49acd72436
ACR-68cd5587443a42b3b3c1243aa15b5a28
ACR-b58d06e3c0624022a3d105e5a770845d
ACR-6d16e435ad5d455cae533cbe2b832ec9
ACR-c564aef6c11843958b76248018800885
ACR-e283e439b5dc4bccb436dc5c5dddc6c1
ACR-6dc6a2fa71244579b9cbf44afc48700f
ACR-4c085d583cd148a99bf9187f47b65755
ACR-8f353b1f32a84825b55bd0a1a5a35355
ACR-e2ac1fb5c73b44b1a726334179354122
 */
package mediumtest;

import org.apache.commons.lang3.StringUtils;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidChangeCredentialsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.test.utils.server.ServerFixture.newSonarQubeServer;

class NotebookLanguageMediumTests {

  private static final String CONNECTION_ID = StringUtils.repeat("very-long-id", 30);
  private static final String JAVA_MODULE_KEY = "test-project-2";
  public static final String SCOPE_ID = "scopeId";

  @SonarLintTest
  void should_not_enable_sync_for_notebook_python_language(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .build();
    var server = newSonarQubeServer()
      .withProject(JAVA_MODULE_KEY, project -> project.withBranch("main"))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage
        .withPlugin(TestPlugin.JAVA)
        .withPlugin(TestPlugin.JAVASCRIPT)
        .withPlugin(TestPlugin.PYTHON)
        .withProject(JAVA_MODULE_KEY))
      .withBoundConfigScope(SCOPE_ID, CONNECTION_ID, JAVA_MODULE_KEY)
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withEnabledLanguageInStandaloneMode(Language.JS)
      .withEnabledLanguageInStandaloneMode(Language.IPYTHON)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(fakeClient);

    backend.getConnectionService().didChangeCredentials(new DidChangeCredentialsParams(CONNECTION_ID));

    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("[SYNC] Languages enabled for synchronization: [java, js]"));
  }

}
