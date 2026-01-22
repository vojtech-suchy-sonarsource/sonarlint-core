/*
ACR-2ef86fc8136145e68c67b0cac34108d4
ACR-1679788c80d64d1da3d380600a3ffb10
ACR-d088a0b0c75246ef942a22ffecc49910
ACR-df87d58865324ec8b66b963ed7ec6851
ACR-52d7b88d50f54467ae96c1014313896f
ACR-9f784463b80a4198a869cee9f6ea039f
ACR-6971363e72e143afa2d9fd193ad8255c
ACR-a0c2c46dc9e54686b0277f7fad496014
ACR-b983d789c4f34ac195a08ce4694bbf14
ACR-bc034c7825654724b60482fe39894fd3
ACR-bf45e2d13054463d97eac4764f710eed
ACR-471c7c2357d44bd0abc7e978b267f949
ACR-48aaf4ac21bd44d38ce20d66e28ff3a7
ACR-db949dde2c6a4e5685ced41d84a5fb2b
ACR-5a85aef007224109a81d6b4fd620682d
ACR-1e62960cebc74955a8ad0cf291dbf5fd
ACR-e9fa9c5c09dd430783d1c73870495a67
 */
package mediumtest.synchronization;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import org.sonarsource.sonarlint.core.test.utils.server.ServerFixture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.awaitility.Awaitility.waitAtMost;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;

class RuleSetSynchronizationMediumTests {

  @SonarLintTest
  void it_should_pull_active_ruleset_from_server(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("10.3")
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("ruleKey", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .withProject("projectKey", project -> project.withQualityProfile("qpKey").withBranch("main"))
      .start();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> assertThat(getAnalyzerConfigFile(backend, "connectionId", "projectKey"))
      .exists()
      .extracting(this::readRuleSets, as(InstanceOfAssertFactories.map(String.class, Sonarlint.RuleSet.class)))
      .hasSize(1)
      .extractingByKey("java")
      .extracting(Sonarlint.RuleSet::getRuleList, as(LIST))
      .containsExactly(Sonarlint.RuleSet.ActiveRule.newBuilder().setRuleKey("ruleKey").setSeverity("MAJOR").build()));
  }

  @SonarLintTest
  void it_should_not_pull_when_server_is_down(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("10.3")
      .withStatus(ServerFixture.ServerStatus.DOWN)
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("ruleKey", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .withProject("projectKey", project -> project.withQualityProfile("qpKey").withBranch("main"))
      .start();
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(client);

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> {
      assertThat(getAnalyzerConfigFile(backend, "connectionId", "projectKey")).doesNotExist();
      assertThat(client.getLogMessages()).contains("Error during synchronization");
    });
  }

  private void addConfigurationScope(SonarLintTestRpcServer backend, String configScopeId, String connectionId, String projectKey) {
    backend.getConfigurationService().didAddConfigurationScopes(
      new DidAddConfigurationScopesParams(List.of(new ConfigurationScopeDto(configScopeId, null, true, "name", new BindingConfigurationDto(connectionId, projectKey, true)))));
  }

  private Path getAnalyzerConfigFile(SonarLintTestRpcServer backend, String connectionId, String projectKey) {
    return backend.getStorageRoot().resolve(encodeForFs(connectionId)).resolve("projects").resolve(encodeForFs(projectKey)).resolve("analyzer_config.pb");
  }

  private Map<String, Sonarlint.RuleSet> readRuleSets(Path protoFilePath) {
    return ProtobufFileUtil.readFile(protoFilePath, Sonarlint.AnalyzerConfiguration.parser()).getRuleSetsByLanguageKeyMap();
  }
}
