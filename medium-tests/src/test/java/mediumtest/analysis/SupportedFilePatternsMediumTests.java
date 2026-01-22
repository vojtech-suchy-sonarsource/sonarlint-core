/*
ACR-7599ff0f44844230942a9df413ef1467
ACR-51bd576036024cf9b9f44036db000ce8
ACR-f6b592c2defd4a92ba055a0e077bb3e6
ACR-df1bcf6fbf4e4c40801b8ac780cd24a4
ACR-4fe5fe6cf835499faf29c3116e606a28
ACR-69cc84c6560148209b9d5b2c3e2f41da
ACR-6f262545bbca4ebd9ab4c2c2a7351cc5
ACR-59b552be0a0540dcbd42fd28bd294913
ACR-07a4de23974e4d17b4b7bbccae6097cf
ACR-b407b3164add47fea70a833f57190cd9
ACR-c14a33d8d53e4413a3fb6440a9435427
ACR-ffed15dc45da4014968a120ba0aa0c41
ACR-2a76b84e8eab46afa85751d323784186
ACR-cc950e5f4397436cafd700094dbf8f7c
ACR-bc84987ff7e0466bb1cb5bd8ffcd1bff
ACR-1b7647b6df3742d391df65875152e7bb
ACR-d290415f7a0f4867b365a22928b93239
 */
package mediumtest.analysis;

import java.util.concurrent.ExecutionException;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.GetSupportedFilePatternsParams;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;

class SupportedFilePatternsMediumTests {

  @SonarLintTest
  void it_should_return_default_supported_file_patterns_in_standalone_mode(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(JAVA)
      .start();

    var patterns = backend.getAnalysisService().getSupportedFilePatterns(new GetSupportedFilePatternsParams("configScopeId")).get().getPatterns();
    assertThat(patterns).containsOnly("**/*.java", "**/*.jav");
  }

  @SonarLintTest
  void it_should_return_default_supported_file_patterns_in_connected_mode_when_not_override_on_server(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var client = harness.newFakeClient().withMatchedBranch("configScopeId", "branchName").build();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", storage -> storage.withPlugin(TestPlugin.JAVA)
        .withProject("projectKey"))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withExtraEnabledLanguagesInConnectedMode(JAVA)
      .start(client);

    var patterns = backend.getAnalysisService().getSupportedFilePatterns(new GetSupportedFilePatternsParams("configScopeId")).get().getPatterns();
    assertThat(patterns).containsOnly("**/*.java", "**/*.jav");
  }

  @SonarLintTest
  void it_should_return_supported_file_patterns_with_server_defined_file_suffixes(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var client = harness.newFakeClient().withMatchedBranch("configScopeId", "branchName").build();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", storage -> storage.withPlugin(TestPlugin.JAVA)
        .withProject("projectKey", project -> project.withSetting("sonar.java.file.suffixes", ".foo, .bar")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withEnabledLanguageInStandaloneMode(JAVA)
      .start(client);

    var patterns = backend.getAnalysisService().getSupportedFilePatterns(new GetSupportedFilePatternsParams("configScopeId")).get().getPatterns();
    assertThat(patterns).containsOnly("**/*.foo", "**/*.bar");
  }

}
