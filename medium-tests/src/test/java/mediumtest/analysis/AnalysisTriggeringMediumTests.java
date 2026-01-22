/*
ACR-823a1b79e69442da8e809b9ae0ae85bf
ACR-a6456f150a9e492b9eb1b7d9bfc1e2c6
ACR-ee095d969e5045ce87888c94e7ffc34c
ACR-e86d7b257c9f4ccfb5edbf0e424b418a
ACR-1352f66ea65f4dbba13f199d0fc7e556
ACR-7e014e3c811440feb77a38aea304e31d
ACR-5120819244024e8fbca9c627ae2cd429
ACR-2c5de346035b47ed9fc1cbd07e8f2e7c
ACR-75af380b2b404887a2bf3861206582d5
ACR-7199698bb05c4728a201748b1b8c472d
ACR-584b326e1fd64adf85112e3018c7055e
ACR-a12df917aafc401fa527d523e1ad4cbe
ACR-f2431e11c0ee4633a2b17560a5d4275a
ACR-9288194bba1c4f45a5d7cd0dbf8a60e1
ACR-ece5227e30494c2bbc0668d253660c96
ACR-875afd95be244723a4162dae15f9eaae
ACR-3c5588c17a204b05904530bb5f688ad2
 */
package mediumtest.analysis;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.DidChangeAnalysisPropertiesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.DidChangeAutomaticAnalysisSettingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.DidChangePathToCompileCommandsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidCloseFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidOpenFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.UpdateStandaloneRulesConfigurationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedFindingDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.telemetry.TelemetryLocalStorage;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static utils.AnalysisUtils.awaitRaisedIssuesNotification;
import static utils.AnalysisUtils.createFile;
import static utils.AnalysisUtils.getPublishedIssues;
import static utils.AnalysisUtils.waitForRaisedIssues;

class AnalysisTriggeringMediumTests {

  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  @SonarLintTest
  void it_should_analyze_file_on_open(SonarLintTestHarness harness, @TempDir Path baseDir) {
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
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
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
  void it_should_not_fail_an_analysis_of_windows_shortcut_file_and_skip_the_file_analysis(SonarLintTestHarness harness) {
    var baseDir = new File("src/test/projects/windows-shortcut").getAbsoluteFile().toPath();
    var actualFile = Paths.get(baseDir.toString(), "hello.py");
    var windowsShortcut = Paths.get(baseDir.toString(), "hello.py.lnk");
    var fakeWindowsShortcut = Paths.get(baseDir.toString(), "hello.py.fake.lnk");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(
        new ClientFileDto(actualFile.toUri(), baseDir.relativize(actualFile), CONFIG_SCOPE_ID, false, null, actualFile, null, null, true),
        new ClientFileDto(windowsShortcut.toUri(), baseDir.relativize(windowsShortcut), CONFIG_SCOPE_ID, false, null, windowsShortcut,
          null, null, true),
        new ClientFileDto(fakeWindowsShortcut.toUri(), baseDir.relativize(fakeWindowsShortcut), CONFIG_SCOPE_ID, false, null,
          fakeWindowsShortcut, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.TEXT)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, windowsShortcut.toUri()));
    await().during(5, TimeUnit.SECONDS).untilAsserted(() -> {
      assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isEmpty();
      assertThat(client.getLogMessages().stream()
        .filter(message -> message.startsWith("Filtered out URIs that are Windows shortcuts: "))
        .toList()).isNotEmpty();
    });

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fakeWindowsShortcut.toUri()));
    await().during(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isNotEmpty());
  }

  @SonarLintTest
  void it_should_not_fail_an_analysis_of_symlink_file_and_skip_the_file_analysis(SonarLintTestHarness harness, @TempDir Path baseDir) throws IOException {
    var filePath = createFile(baseDir, "pom.xml",
      """
        <?xml version="1.0" encoding="UTF-8"?>
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>com.foo</groupId>
          <artifactId>bar</artifactId>
          <version>${pom.version}</version>
        </project>""");
    var link = Paths.get(baseDir.toString(), "pom-link.xml");
    Files.createSymbolicLink(link, filePath);
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(
        new ClientFileDto(link.toUri(), baseDir.relativize(link), CONFIG_SCOPE_ID, false, null, link, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, link.toUri()));
    await().during(5, TimeUnit.SECONDS).untilAsserted(() -> {
      assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isEmpty();
      assertThat(client.getLogMessages().stream()
        .filter(message -> message.startsWith("Filtered out URIs that are symbolic links: "))
        .toList()).isNotEmpty();
    });
  }

  @SonarLintTest
  void it_should_analyze_open_file_on_content_change(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml", "");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues).isEmpty());
    reset(client);

    backend.getFileService()
      .didUpdateFileSystem(new DidUpdateFileSystemParams(
        Collections.emptyList(),
        List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, """
          <?xml version="1.0" encoding="UTF-8"?>
          <project>
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.foo</groupId>
            <artifactId>bar</artifactId>
            <version>${pom.version}</version>
          </project>""", null, true)),
        Collections.emptyList()));

    waitForRaisedIssues(client, CONFIG_SCOPE_ID);
    publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues)
        .extracting(RaisedIssueDto::getPrimaryMessage)
        .containsExactly("Replace \"pom.version\" with \"project.version\"."));
  }

  @SonarLintTest
  void it_should_analyze_closed_file_on_content_change(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml", "");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues).isEmpty());
    reset(client);
    backend.getFileService().didCloseFile(new DidCloseFileParams(CONFIG_SCOPE_ID, fileUri));

    backend.getFileService()
      .didUpdateFileSystem(new DidUpdateFileSystemParams(
        List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, """
          <?xml version="1.0" encoding="UTF-8"?>
          <project>
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.foo</groupId>
            <artifactId>bar</artifactId>
            <version>${pom.version}</version>
          </project>""", null, true)),
        Collections.emptyList(),
        Collections.emptyList()));

    verify(client, timeout(500).times(0)).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any());
  }

  @SonarLintTest
  void it_should_analyze_open_files_when_re_enabling_automatic_analysis(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml", """
      <?xml version="1.0" encoding="UTF-8"?>
      <project>
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.foo</groupId>
        <artifactId>bar</artifactId>
        <version>${pom.version}</version>
      </project>""");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .withAutomaticAnalysisEnabled(false)
      .start(client);
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    backend.getAnalysisService().didChangeAutomaticAnalysisSetting(new DidChangeAutomaticAnalysisSettingParams(true));

    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues)
        .extracting(RaisedIssueDto::getPrimaryMessage)
        .containsExactly("Replace \"pom.version\" with \"project.version\"."));
  }

  @SonarLintTest
  void it_should_analyze_open_files_when_re_enabling_automatic_analysis_when_same_file_opened_twice(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml", """
      <?xml version="1.0" encoding="UTF-8"?>
      <project>
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.foo</groupId>
        <artifactId>bar</artifactId>
        <version>${pom.version}</version>
      </project>""");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .withAutomaticAnalysisEnabled(false)
      .start(client);
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    backend.getAnalysisService().didChangeAutomaticAnalysisSetting(new DidChangeAutomaticAnalysisSettingParams(true));

    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues)
        .extracting(RaisedIssueDto::getPrimaryMessage)
        .containsExactly("Replace \"pom.version\" with \"project.version\"."));
  }

  @SonarLintTest
  void it_should_save_automatic_analysis_setting_and_trigger_telemetry_on_toggle(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withAutomaticAnalysisEnabled(false)
      .withTelemetryEnabled()
      .start();

    backend.getAnalysisService().didChangeAutomaticAnalysisSetting(new DidChangeAutomaticAnalysisSettingParams(true));

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent())
      .extracting(TelemetryLocalStorage::isAutomaticAnalysisEnabled, TelemetryLocalStorage::getAutomaticAnalysisToggledCount)
      .containsExactly(true, 1));

    backend.getAnalysisService().didChangeAutomaticAnalysisSetting(new DidChangeAutomaticAnalysisSettingParams(false));

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent())
      .extracting(TelemetryLocalStorage::isAutomaticAnalysisEnabled, TelemetryLocalStorage::getAutomaticAnalysisToggledCount)
      .containsExactly(false, 2));
  }

  @SonarLintTest
  void it_should_not_update_automatic_analysis_setting_if_not_changed(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withAutomaticAnalysisEnabled(true)
      .withTelemetryEnabled()
      .start();

    backend.getAnalysisService().didChangeAutomaticAnalysisSetting(new DidChangeAutomaticAnalysisSettingParams(true));

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent())
      .extracting(TelemetryLocalStorage::isAutomaticAnalysisEnabled, TelemetryLocalStorage::getAutomaticAnalysisToggledCount)
      .containsExactly(true, 0));
  }

  @SonarLintTest
  void it_should_not_analyze_opened_file_if_it_was_already_open(SonarLintTestHarness harness, @TempDir Path baseDir) {
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
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
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
    clearInvocations(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    //ACR-2f06536bd8554667aee7f1ba91655a43
    verify(client, timeout(500).times(0)).startProgress(any());
  }

  @SonarLintTest
  void it_should_analyze_open_files_when_enabling_rule(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml", """
      <?xml version="1.0" encoding="UTF-8"?>
      <project>
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.foo</groupId>
        <artifactId>My_Project</artifactId>
      </project>""");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues).isEmpty());
    reset(client);

    backend.getRulesService().updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("xml:S3420",
      new StandaloneRuleConfigDto(true, Map.of()))));

    waitForRaisedIssues(client, CONFIG_SCOPE_ID);
    publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues)
        .extracting(RaisedIssueDto::getPrimaryMessage)
        .containsExactly("Update this \"artifactId\" to match the provided regular expression: '[a-z][a-z-0-9]+'"));
  }

  @SonarLintTest
  void it_should_not_analyze_open_files_but_should_clear_and_report_issues_when_disabling_rule(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml", """
      <?xml version="1.0" encoding="UTF-8"?>
      <project>
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.foo</groupId>
        <artifactId>bar</artifactId>
        <version>${pom.version}</version>
      </project>""");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    var publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues).extracting(RaisedFindingDto::getRuleKey).containsOnly("xml:S3421"));
    reset(client);

    backend.getRulesService().updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("xml:S3421",
      new StandaloneRuleConfigDto(false, Map.of()))));

    //ACR-fed06e7ec71e4f75b674c4ab69397d29
    verify(client, never()).log(any());

    //ACR-d5cc745029b44246914d0411567735a3
    publishedIssues = getPublishedIssues(client, CONFIG_SCOPE_ID);
    assertThat(publishedIssues)
      .containsOnlyKeys(fileUri)
      .hasEntrySatisfying(fileUri, issues -> assertThat(issues).isEmpty());
  }

  @SonarLintTest
  void it_should_trigger_analysis_after_analysis_properties_change(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml",
      """
        <?xml version="1.0" encoding="UTF-8"?>
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>com.foo</groupId>
          <artifactId>bar</artifactId>
          <version>${pom.version}</version>
        </project>""");
    var fileUri = URI.create(filePath.toUri().toString());
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    var raisedIssueDto = awaitRaisedIssuesNotification(client, CONFIG_SCOPE_ID);
    assertThat(raisedIssueDto).isNotEmpty();

    client.cleanRaisedIssues();
    assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty();

    backend.getAnalysisService().didSetUserAnalysisProperties(new DidChangeAnalysisPropertiesParams(CONFIG_SCOPE_ID, Map.of("foo", "bar")));

    raisedIssueDto = awaitRaisedIssuesNotification(client, CONFIG_SCOPE_ID);
    assertThat(raisedIssueDto).isNotEmpty();

    client.cleanRaisedIssues();
    assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty();

    backend.getAnalysisService().didSetUserAnalysisProperties(new DidChangeAnalysisPropertiesParams(CONFIG_SCOPE_ID, Map.of("foo", "bar")));

    await().during(1, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty());
  }

  @SonarLintTest
  void it_should_trigger_analysis_after_path_to_compile_commands_change(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "pom.xml",
      """
        <?xml version="1.0" encoding="UTF-8"?>
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>com.foo</groupId>
          <artifactId>bar</artifactId>
          <version>${pom.version}</version>
        </project>""");
    var fileUri = URI.create(filePath.toUri().toString());
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
    var raisedIssueDto = awaitRaisedIssuesNotification(client, CONFIG_SCOPE_ID);
    assertThat(raisedIssueDto).isNotEmpty();

    client.cleanRaisedIssues();
    assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty();

    backend.getAnalysisService().didChangePathToCompileCommands(new DidChangePathToCompileCommandsParams(CONFIG_SCOPE_ID, "/path/to/compile_commands.json"));

    raisedIssueDto = awaitRaisedIssuesNotification(client, CONFIG_SCOPE_ID);
    assertThat(raisedIssueDto).isNotEmpty();

    client.cleanRaisedIssues();
    assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty();

    backend.getAnalysisService().didChangePathToCompileCommands(new DidChangePathToCompileCommandsParams(CONFIG_SCOPE_ID, "/path/to/compile_commands.json"));

    await().during(1, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty());
  }

}
