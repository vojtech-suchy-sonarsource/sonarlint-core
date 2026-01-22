/*
ACR-8c3137fc863a4a7f9c596ce0af12ce45
ACR-c3bf9d3faecd4918a6d18dfbfd722caa
ACR-c3897f79a4b0415082771c9d49de22e2
ACR-7bc89256841d47559b6cf5d0efcf7570
ACR-7f696b6223034065952e9dbc3869f68d
ACR-4866c72ec2754f788312cb4cf0a94282
ACR-a038d19991254d0f8b6e1afae22cdef0
ACR-1034e33b44f244b3be64bb4f9e4b01d5
ACR-db423c1201ee444caddc2b18f763c349
ACR-b9dd9a7bf3224f229f7e65a4462ccbe5
ACR-d99d05cfb0d14dbdb7622a25df2b3993
ACR-60bdb878b98142afb3535d0707b02a71
ACR-eaa1f636bc6e409896da05a67cca2b7b
ACR-ea1999ae54e846a1a8cf015e3ece039b
ACR-0455288b5f6a4cad80ba92122ed10710
ACR-19c5a028e01a42e397c8f5886c4492c9
ACR-de8cf622b01d4beaadc114616afaa886
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
