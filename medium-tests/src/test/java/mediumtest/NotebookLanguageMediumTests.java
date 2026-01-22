/*
ACR-f4fe4f42e52d461bb755e334c66490c3
ACR-4372f92576d94aa2af86d52336514d7d
ACR-aed1943d5a514325a69bf95808e6631e
ACR-384495d8b7024805a6ed3cb29bc330a9
ACR-a123a80af9c2419b94765bf080ccc4ad
ACR-d65825c530d4429fb703449ef83f79ea
ACR-b3ddd400428c4ef2bfac958a647ace14
ACR-f673550188304d2a9d18d11f200348d3
ACR-0fb383c20f004feea3f9960ca942ddec
ACR-6ba60068b9bb476ba159665a841a8101
ACR-81d7f563ecbb4033933b6336fdbd5540
ACR-a28321fd7cee43cc8523ef501ec98cd7
ACR-8ca997f8d49646ba94756a86aa19d388
ACR-7b3a3eda9d1d4774ba6316337abd1e63
ACR-910f40a81ae743659e19f32b73e72d5c
ACR-3b2138e3223347dab9bc92ee416699ed
ACR-1e3fb8904ccf45ab83b1e177bec00ff1
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
