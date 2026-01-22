/*
ACR-380119b32abb4004a401ce39c2ad200b
ACR-bb4e13224e3042baa55629d5978057dc
ACR-e2ba37d5dd4442d096b4056842eaf737
ACR-0281a2da05614f628d2ccc4b641df423
ACR-22499d9ab0614925a15232e1f48161e2
ACR-0751b8f113644e3096a822151ad0655b
ACR-bdc83432133e4668ac3113c2f7cd00a9
ACR-b358776bbbe84491a10eca0d8c8fb63f
ACR-ce7bfff1fc184f3b9440598de6c3bd98
ACR-36b8273fa30446bb9538168ec143929a
ACR-ebaf42fb28ed460088e0ac6a87ce5074
ACR-22c28023934d4719865ec3d5c99088a1
ACR-93733197995d496d80422778e0a35783
ACR-cccc6ea1fe6c4390b11f872fa0fe42a9
ACR-e3515b51aff14cd9a5aaedfd8b11b33a
ACR-f49a8497596442e4b8ed80af8dbab639
ACR-c2aead05f2f34270b56bef6bb35924ec
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
