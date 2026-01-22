/*
ACR-07915824460d40ffafb60fe7155be803
ACR-96644b410ffe438aa509f9b54fd6ceff
ACR-65b46a22ef774d89924e77989badfb88
ACR-a2c43c06b7354fdb8e926cbde8ddd7c4
ACR-953e17f1f2834af584b46ae69f4f9560
ACR-304bd9a52652453d8cfca9e519be2eb1
ACR-aa15d834c2c04030aba16f8feaebd833
ACR-b3c342c9aae94b09b73136e073d334ef
ACR-b13dbba83e694ac18bb7bc82cdf4d97e
ACR-7636703fcc3949e6bf2491b7700968f6
ACR-7e4f723786d74f8fb50936376cd82050
ACR-d770e8ae06cf4bba9f7ca60436ca3fb0
ACR-5ab5f8ed5d9c4dfb9dcd597bc57b54ed
ACR-60203b23de0248b98914f3a37e12a10f
ACR-93627bdb0d3042b0bf3135a34ab6ff63
ACR-43de493391144ee4b3550a96e43bdb04
ACR-b16342ab4f9e444d9c60ad23c24322f6
 */
package mediumtest.tracking;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.RuleType;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;

class SecurityHotspotTrackingMediumTests {

  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  @SonarLintTest
  void it_should_track_server_hotspot(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var ideFilePath = "Foo.java";
    var filePath = createFile(baseDir, ideFilePath,
      """
        package sonar;
        
        public class Foo {
          public void run() {
            String username = "steve";
            String password = "blue";
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?" +
              "user=" + username + "&password=" + password); // Sensitive
          }
        }""");
    var projectKey = "projectKey";
    var connectionId = "connectionId";
    var branchName = "main";
    var ruleKey = "java:S2068";
    var message = "'password' detected in this expression, review this potentially hard-coded password.";

    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var server = harness.newFakeSonarQubeServer("10.0")
      .withProject(projectKey, project -> project.withBranch(branchName, branch -> branch
        .withHotspot("uuid", hotspot -> hotspot.withAuthor("author")
          .withCreationDate(Instant.ofEpochSecond(123456789L))
          .withFilePath(ideFilePath)
          .withMessage(message)
          .withRuleKey(ruleKey)
          .withTextRange(new TextRange(6, 11, 6, 12))
          .withStatus(HotspotReviewStatus.TO_REVIEW)
          .withVulnerabilityProbability(VulnerabilityProbability.HIGH))))
      .withQualityProfile("qp", qualityProfile -> qualityProfile.withLanguage("java")
        .withActiveRule(ruleKey, activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId, server,
        storage -> storage.withPlugin(TestPlugin.JAVA).withProject(projectKey,
          project -> project.withRuleSet("java", ruleSet -> ruleSet.withActiveRule(ruleKey, "MINOR"))
            .withMainBranch(branchName)))
      .withBackendCapability(SECURITY_HOTSPOTS)
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    backend.getConfigurationService()
      .didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
        new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
          new BindingConfigurationDto(connectionId, projectKey, true)))));

    var firstPublishedHotspot = analyzeFileAndGetHotspot(backend, fileUri, client);

    assertThat(firstPublishedHotspot)
      .extracting("ruleKey", "primaryMessage", "severityMode.left.severity", "severityMode.left.type", "serverKey", "status", "introductionDate",
        "textRange.startLine", "textRange.startLineOffset", "textRange.endLine", "textRange.endLineOffset")
      .containsExactly(ruleKey, message, IssueSeverity.MINOR, RuleType.SECURITY_HOTSPOT, "uuid", HotspotStatus.TO_REVIEW, Instant.ofEpochSecond(123456789L), 6, 11, 6, 19);
  }

  @SonarLintTest
  void it_should_track_known_server_hotspots(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var ideFilePath = "Foo.java";
    var filePath = createFile(baseDir, ideFilePath,
      """
        package sonar;
        
        public class Foo {
          public void run() {
            String username = "steve";
            String password = "blue";
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?" +
              "user=" + username + "&password=" + password); // Sensitive
          }
        }""");
    var projectKey = "projectKey";
    var connectionId = "connectionId";
    var branchName = "main";
    var ruleKey = "java:S2068";
    var message = "'password' detected in this expression, review this potentially hard-coded password.";

    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var server = harness.newFakeSonarQubeServer("10.0")
      .withProject(projectKey, project -> project.withBranch(branchName, branch -> branch
        .withHotspot("uuid", hotspot -> hotspot.withAuthor("author")
          .withCreationDate(Instant.ofEpochSecond(123456789L))
          .withFilePath(ideFilePath)
          .withMessage(message)
          .withRuleKey(ruleKey)
          .withTextRange(new TextRange(6, 11, 6, 12))
          .withStatus(HotspotReviewStatus.TO_REVIEW)
          .withVulnerabilityProbability(VulnerabilityProbability.HIGH))))
      .withQualityProfile("qp", qualityProfile -> qualityProfile.withLanguage("java")
        .withActiveRule(ruleKey, activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId, server,
        storage -> storage.withPlugin(TestPlugin.JAVA).withProject(projectKey,
          project -> project.withRuleSet("java", ruleSet -> ruleSet.withActiveRule(ruleKey, "MINOR"))
            .withMainBranch(branchName)))
      .withBackendCapability(SECURITY_HOTSPOTS)
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    backend.getConfigurationService()
      .didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
        new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
          new BindingConfigurationDto(connectionId, projectKey, true)))));

    var firstPublishedHotspot = analyzeFileAndGetHotspot(backend, fileUri, client);
    var secondPublishedHotspot = analyzeFileAndGetHotspot(backend, fileUri, client);

    assertThat(secondPublishedHotspot)
      .extracting("id", "ruleKey", "primaryMessage", "severityMode.left.severity", "severityMode.left.type", "serverKey", "introductionDate",
        "textRange.startLine", "textRange.startLineOffset", "textRange.endLine", "textRange.endLineOffset")
      .containsExactly(firstPublishedHotspot.getId(), ruleKey, message, IssueSeverity.MINOR, RuleType.SECURITY_HOTSPOT, "uuid", Instant.ofEpochSecond(123456789L), 6, 11, 6, 19);
  }

  @SonarLintTest
  void it_should_not_track_server_hotspots_in_standalone_mode(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var ideFilePath = "Foo.java";
    var filePath = createFile(baseDir, ideFilePath,
      """
        package sonar;
        
        public class Foo {
          public void run() {
            String username = "steve";
            String password = "blue";
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?" +
              "user=" + username + "&password=" + password); // Sensitive
          }
        }""");
    var projectKey = "projectKey";
    var connectionId = "connectionId";
    var branchName = "main";
    var ruleKey = "java:S2068";

    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId,
        storage -> storage.withPlugin(TestPlugin.JAVA).withProject(projectKey,
          project -> project.withRuleSet("java", ruleSet -> ruleSet.withActiveRule(ruleKey, "MINOR"))
            .withMainBranch(branchName)))
      .withBackendCapability(SECURITY_HOTSPOTS)
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    analyzeFileAndAssertNoHotspotsRaised(backend, fileUri, client);
  }

  private RaisedHotspotDto analyzeFileAndGetHotspot(SonarLintTestRpcServer backend, URI fileUri, SonarLintRpcClientDelegate client) {
    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(fileUri), Map.of(), true, System.currentTimeMillis()))
      .join();
    var publishedHotspotsByFile = getPublishedHotspots(client, analysisId);
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    assertThat(publishedHotspotsByFile).containsOnlyKeys(fileUri);
    var publishedHotspots = publishedHotspotsByFile.get(fileUri);
    assertThat(publishedHotspots).hasSize(1);
    return publishedHotspots.get(0);
  }

  private Map<URI, List<RaisedHotspotDto>> getPublishedHotspots(SonarLintRpcClientDelegate client, UUID analysisId) {
    ArgumentCaptor<Map<URI, List<RaisedHotspotDto>>> trackedIssuesCaptor = ArgumentCaptor.forClass(Map.class);
    verify(client, timeout(300)).raiseHotspots(eq(CONFIG_SCOPE_ID), trackedIssuesCaptor.capture(), eq(false), eq(analysisId));
    return trackedIssuesCaptor.getValue();
  }

  private void analyzeFileAndAssertNoHotspotsRaised(SonarLintTestRpcServer backend, URI fileUri, SonarLintRpcClientDelegate client) {
    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(fileUri), Map.of(), true, System.currentTimeMillis()))
      .join();
    ArgumentCaptor<Map<URI, List<RaisedHotspotDto>>> trackedIssuesCaptor = ArgumentCaptor.forClass(Map.class);
    verify(client, times(0)).raiseHotspots(eq(CONFIG_SCOPE_ID), trackedIssuesCaptor.capture(), eq(false), eq(analysisId));
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
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
