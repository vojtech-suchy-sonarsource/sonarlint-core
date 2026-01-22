/*
ACR-957eac6bd6b04a5fb492b2bde53f2079
ACR-2f6eaa550a15438e8615f4c891421534
ACR-d2b8869bf8d141a0bdb8c04bbb8d8cf6
ACR-02673c7ba52b40bea3b5cb101eab05de
ACR-304e6cfd1f9a46e5baec279c6dd935ec
ACR-bbf4ea0018e741969f1075ec2b14ed54
ACR-022052440fe84799a8a0fd733f7daa5a
ACR-e556d755a91b461a9f1602093297e7a3
ACR-7444395e6d2740e8835b75162e366e05
ACR-a3ab44d355874d19ac9905548105493a
ACR-ba01c578cf3b43d5ae884d66a2d8bcb8
ACR-e61171594301433b81e73b13b0dc7f3e
ACR-a0c989db37e94ad6884e7eb47cf06e23
ACR-60715f5b1e0c46e18900cc3caff67c92
ACR-e65d3b2a81e14e919b0298cc3eb2e065
ACR-3e2047abab6f4ae3b029a0c67873155d
ACR-0ef3391844f94f0fa6e520b8d8512c5d
 */
package mediumtest.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingMode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidOpenFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedFindingDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static utils.AnalysisUtils.getPublishedIssues;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AnalysisReadinessMediumTests {

  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  @SonarLintTest
  void it_should_be_immediately_consider_analysis_to_be_ready_when_adding_a_non_bound_configuration_scope(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .start(client);

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "name", null))));

    verify(client, timeout(1000)).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), true);
  }

  @SonarLintTest
  void it_should_change_readiness_and_analyze_xml_file_in_connected_mode(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml",
      """
        <?xml version="1.0" encoding="UTF-8"?>
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>com.foo</groupId>
          <artifactId>bar</artifactId>
          <version>${pom.version}</version>
        </project>""");
    var fileUri = filePath.toUri();
    var server = harness.newFakeSonarQubeServer()
      .withPlugin(TestPlugin.XML)
      .withProject("projectKey", project -> project.withQualityProfile("qp"))
      .withQualityProfile("qp", qualityProfile -> qualityProfile.withLanguage("xml").withActiveRule("xml:S3421", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .start();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server.baseUrl())
      .withBoundConfigScope(CONFIG_SCOPE_ID, "connectionId", "projectKey")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .withExtraEnabledLanguagesInConnectedMode(Language.XML)
      .start(client);

    verify(client, never()).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), true);

    //ACR-e6ae8d471f5847c0a0ce2f3c6a2fd105
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    verify(client, never()).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any());

    client.waitForSynchronization();

    //ACR-e73a64f5a22c4c2d8123187839b0a59f
    await().atMost(1, TimeUnit.SECONDS)
      .untilAsserted(() -> verify(client).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), true));
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isNotEmpty());

    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues).containsOnlyKeys(fileUri);
  }

  @SonarLintTest
  void it_should_reanalyse_open_files_after_unbinding(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml",
      """
        <?xml version="1.0" encoding="UTF-8"?>
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>com.foo</groupId>
          <artifactId>bar</artifactId>
          <version>${pom.version}</version>
        </project>""");
    var fileUri = filePath.toUri();
    var server = harness.newFakeSonarQubeServer()
      .withPlugin(TestPlugin.XML)
      .withProject("projectKey", project -> project.withQualityProfile("qp"))
      .withProject("projectKey2", project -> project.withQualityProfile("qp2"))
      .withQualityProfile("qp", qualityProfile -> qualityProfile.withLanguage("xml").withActiveRule("xml:S3421", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .withQualityProfile("qp2")
      .start();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server.baseUrl())
      .withBoundConfigScope(CONFIG_SCOPE_ID, "connectionId", "projectKey")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .withExtraEnabledLanguagesInConnectedMode(Language.XML)
      .start(client);

    verify(client, never()).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), true);

    //ACR-bae72c5b630d41dfbce85ce5b467b8ea
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    verify(client, never()).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any());

    client.waitForSynchronization();

    //ACR-7ef0d0b342034b6a821d7d643fd8fb65
    await().atMost(1, TimeUnit.SECONDS)
      .untilAsserted(() -> verify(client).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), true));
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isNotEmpty());

    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues).containsOnlyKeys(fileUri);
    clearInvocations(client);

    //ACR-2e61e908faee40dea08e5f4d37d79143
    backend.getConfigurationService()
      .didUpdateBinding(new DidUpdateBindingParams(CONFIG_SCOPE_ID, new BindingConfigurationDto("connectionId", "projectKey2", true), BindingMode.MANUAL, null));

    //ACR-2cb50010003342b0b918658050e7f921
    verify(client, timeout(200)).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), false);

    client.waitForSynchronization();

    //ACR-f34029a411b54d06ae9f1bfa3cc6b088
    await().atMost(1, TimeUnit.SECONDS)
      .untilAsserted(() -> verify(client).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), true));
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty());

    //ACR-5dde25d0465b4660bf957c42e395dde3
    backend.getConfigurationService()
      .didUpdateBinding(new DidUpdateBindingParams(CONFIG_SCOPE_ID, new BindingConfigurationDto("connectionId", "projectKey", true), BindingMode.MANUAL, null));

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID))
      .extracting(RaisedFindingDto::getRuleKey).containsExactly("xml:S3421"));
  }

  private static Path createFile(Path folderPath, String fileName, String content) {
    var filePath = folderPath.resolve(fileName);
    try {
      Files.writeString(filePath, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return filePath;
  }
}
