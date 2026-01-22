/*
ACR-9da23bcf3c2a485fb78805fe2016ea44
ACR-f21365a1e8d74013aaf621052f7ec44d
ACR-35863c9be72e4db6b97701e97582142d
ACR-ca19189e3b914eccb5ea09ec035a37f4
ACR-d764101cc5944ca8be0e3fcde4e9c3c8
ACR-aa707b8f89a947a4b657e1e404aa8cfa
ACR-e205cfa997234b41b64453aed0bf33a9
ACR-8318c5b184764da2b1a39d845066d896
ACR-716ef8053f6d4fac8a50950f05461eda
ACR-8650cc629095472cb65e377fbba60a17
ACR-81d6ced7f2ab4b66afeeea85be8b4350
ACR-de620055df6f4770ae26aadfb6f73c41
ACR-5f97bde529ed4957af1c6fb3b9df2d23
ACR-1f2be3e8d3644ddd9e4a53aff0fdd512
ACR-62b4d884b70c4d7a9cf91bd466d4ee1b
ACR-d86a329792024775bad7e74b0e3905db
ACR-f0c30f808796488ba58388a91a5cb027
 */
package mediumtest.file;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.scanner.protocol.Constants;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidOpenFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.GetFilesStatusParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.AnalysisUtils;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static utils.AnalysisUtils.getPublishedIssues;

class ClientFileExclusionsMediumTests {
  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  @SonarLintTest
  void it_should_not_analyze_excluded_file_on_open(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createXmlFile(baseDir);
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .withFileExclusions(CONFIG_SCOPE_ID, Set.of("**/*.xml"))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    await().pollDelay(1, TimeUnit.SECONDS).atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> verify(client).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any()));
    assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isEmpty();
  }

  @SonarLintTest
  void it_should_analyze_not_excluded_file_on_open(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createXmlFile(baseDir);
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .withFileExclusions(CONFIG_SCOPE_ID, Set.of("**/*.java"))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues)
        .extracting(RaisedIssueDto::getPrimaryMessage)
        .containsExactly("Replace \"pom.version\" with \"project.version\"."));
  }

  @SonarLintTest
  void it_should_not_analyze_non_user_defined_file_on_open(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createXmlFile(baseDir);
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, false)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    await().pollDelay(1, TimeUnit.SECONDS).atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> verify(client).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any()));
    assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isEmpty();
  }

  @SonarLintTest
  void it_should_analyze_user_defined_file_on_open(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createXmlFile(baseDir);
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues)
        .extracting(RaisedIssueDto::getPrimaryMessage)
        .containsExactly("Replace \"pom.version\" with \"project.version\"."));
  }

  @SonarLintTest
  void it_should_not_exclude_client_defined_file_exclusion_in_connected_mode(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var ideFilePath = "Foo.java";
    var filePath = AnalysisUtils.createFile(baseDir, ideFilePath,
      """
        // FIXME foo bar
        public class Foo {
        }""");
    var projectKey = "projectKey";
    var connectionId = "connectionId";
    var branchName = "main";
    var ruleKey = "java:S1134";
    var message = "Take the required action to fix the issue indicated by this comment.";

    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .withFileExclusions(CONFIG_SCOPE_ID, Set.of("**/*.java"))
      .build();
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("main", branch -> branch
        .withIssue("uuid", "java:S1134", message, "author", ideFilePath, "395d7a96efa8afd1b66ab6b680d0e637", Constants.Severity.BLOCKER,
          org.sonarsource.sonarlint.core.commons.RuleType.BUG,
          "OPEN", null, Instant.ofEpochMilli(123456789L), new TextRange(2, 0, 2, 16))))
      .withQualityProfile("qp", qualityProfile -> qualityProfile.withLanguage("java")
        .withActiveRule(ruleKey, activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId, server,
        storage -> storage.withPlugin(TestPlugin.JAVA).withProject(projectKey,
          project -> project.withRuleSet("java", ruleSet -> ruleSet.withActiveRule(ruleKey, "MINOR"))
            .withMainBranch(branchName)))
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    backend.getConfigurationService()
      .didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
        new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
          new BindingConfigurationDto(connectionId, projectKey, true)))));

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues).isNotEmpty();
  }

  @SonarLintTest
  void it_should_exclude_non_user_defined_files_in_connected_mode(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var ideFilePath = "Foo.java";
    var filePath = AnalysisUtils.createFile(baseDir, ideFilePath,
      """
        // FIXME foo bar
        public class Foo {
        }""");
    var projectKey = "projectKey";
    var connectionId = "connectionId";
    var branchName = "main";
    var ruleKey = "java:S1134";
    var message = "Take the required action to fix the issue indicated by this comment.";

    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, false)))
      .build();
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("main", branch -> branch
        .withIssue("uuid", "java:S1134", message, "author", ideFilePath, "395d7a96efa8afd1b66ab6b680d0e637", Constants.Severity.BLOCKER,
          org.sonarsource.sonarlint.core.commons.RuleType.BUG,
          "OPEN", null, Instant.ofEpochMilli(123456789L), new TextRange(2, 0, 2, 16))))
      .withQualityProfile("qp", qualityProfile -> qualityProfile.withLanguage("java")
        .withActiveRule(ruleKey, activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId, server,
        storage -> storage.withPlugin(TestPlugin.JAVA).withProject(projectKey,
          project -> project.withRuleSet("java", ruleSet -> ruleSet.withActiveRule(ruleKey, "MINOR"))
            .withMainBranch(branchName)))
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    backend.getConfigurationService()
      .didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
        new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
          new BindingConfigurationDto(connectionId, projectKey, true)))));

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    await().pollDelay(1, TimeUnit.SECONDS).atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> verify(client).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any()));
    assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isEmpty();
  }

  @SonarLintTest
  void it_should_include_client_exclusions_when_getting_file_status(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createXmlFile(baseDir);
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .withFileExclusions(CONFIG_SCOPE_ID, Set.of("**/*.xml"))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    var future = backend.getFileService().getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of(fileUri))));

    assertThat(future).succeedsWithin(5, TimeUnit.SECONDS);
    assertThat(future.join().getFileStatuses().entrySet())
      .extracting(Map.Entry::getKey, e -> e.getValue().isExcluded())
      .containsExactlyInAnyOrder(
        tuple(fileUri, true));
  }

  private static Path createXmlFile(Path baseDir) {
    return AnalysisUtils.createFile(baseDir, "pom.xml",
      """
        <?xml version="1.0" encoding="UTF-8"?>
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>com.foo</groupId>
          <artifactId>bar</artifactId>
          <version>${pom.version}</version>
        </project>""");
  }
}
