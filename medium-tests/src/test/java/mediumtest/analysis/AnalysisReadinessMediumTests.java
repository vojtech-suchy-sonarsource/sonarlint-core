/*
ACR-9a1681b79a124a63bba2e4762d32d586
ACR-8092eee18cbf4f0e8a3b21f08773ab7d
ACR-62ccc01abe78467dbc19fc2068fd6234
ACR-ccd218f8c5ee4140b289afe05a8fe4fe
ACR-4e767f1dda70479ab0832a011f60f526
ACR-865b97900bc3484e9d5dc7c42c49e66a
ACR-4dd17db6667840098dc535434730f0fc
ACR-5f65e3cb4c524ca7871a33490af0d3a6
ACR-98cf41e2688d4aab81b31aa53b407bde
ACR-b046dc04620245188b3ed69f074339cb
ACR-092c40904dc84747bd354bb60af8072e
ACR-7505d48d8964472a8bc810eb55c6a0c5
ACR-1cf3a430d5fa4cf6b2538e7e8fa857de
ACR-c166d1aaa6d3480a857ad5d8a7a90d35
ACR-c9e4b5b4c2dd4e97a9722c37caa69739
ACR-1679d26dc8c5483183febfc52053cdd0
ACR-36a029eb163a4d2586aed6762daa4536
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

    //ACR-45b0d44c282a472fb23f149ab2b7a41a
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    verify(client, never()).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any());

    client.waitForSynchronization();

    //ACR-3c80122e579849ebaad9d4d7b666d19e
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

    //ACR-b65d562cefc6409e8d6859cff0319d22
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    verify(client, never()).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any());

    client.waitForSynchronization();

    //ACR-1a67a9f6f3a8417bb25d1b4eca4af1e5
    await().atMost(1, TimeUnit.SECONDS)
      .untilAsserted(() -> verify(client).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), true));
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isNotEmpty());

    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues).containsOnlyKeys(fileUri);
    clearInvocations(client);

    //ACR-fd1a7cca8fb6455d8fea31b6a7a61859
    backend.getConfigurationService()
      .didUpdateBinding(new DidUpdateBindingParams(CONFIG_SCOPE_ID, new BindingConfigurationDto("connectionId", "projectKey2", true), BindingMode.MANUAL, null));

    //ACR-2295f49d87b5419b9f67176cc9358b39
    verify(client, timeout(200)).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), false);

    client.waitForSynchronization();

    //ACR-753a5a5b3ff3416ab5a145b1027af00c
    await().atMost(1, TimeUnit.SECONDS)
      .untilAsserted(() -> verify(client).didChangeAnalysisReadiness(Set.of(CONFIG_SCOPE_ID), true));
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty());

    //ACR-b0fb10aae47b4ff7b21e0e28a9c8b506
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
