/*
ACR-bdd763642f2c4635b9c20031755e045c
ACR-8ca609d0ad7a4e1f971a1aa7e0db17ed
ACR-b302b69281034e00a8f41979d6ad5b04
ACR-140ec9f5639d4cd49f60e39af577f7f9
ACR-91ed8b86901d49c6b11d65fcafc455f4
ACR-3bdb6936ecf64a918706abb9b3478de5
ACR-53bda2f24cc1435bbf7a3520be587f2e
ACR-5334d7a2a46641a9a8e4176e6fdb9d87
ACR-b5ad1dd53e884e02a2dacd8f36b5a5ff
ACR-deeff3f5bfd14fa7937c6a8ab350d8a1
ACR-2c521bcbea444ba1a1ae02d73ee49044
ACR-7a8e92c81ba24d9995aa8f047cad12ce
ACR-6d848ec40eeb4bc78e778ba4d4b61af6
ACR-5f920f4fa763424eaa09c80817e62d23
ACR-132ba30a1fd84911908fd048c9aaddf1
ACR-5f41d3ba78a94a938f787532eddc9f67
ACR-894a4f632a2c4fbca1540ab22a381bc0
 */
package mediumtest.analysis;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFileListParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFullProjectParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeOpenFilesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeVCSChangedFilesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidOpenFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.commit;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.createRepository;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.modifyFile;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static utils.AnalysisUtils.createFile;

class AnalysisForcedByClientMediumTests {

  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  @SonarLintTest
  void should_run_forced_analysis_for_list_of_files(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath1 = createFile(baseDir, "Foo.java",
      "public interface Foo {}");
    var filePath2 = createFile(baseDir, "Bar.java",
      "public interface Bar {}");
    var fileUri1 = filePath1.toUri();
    var fileUri2 = filePath2.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri1, baseDir.relativize(filePath1), CONFIG_SCOPE_ID, false, null, filePath1, null, null, true),
        new ClientFileDto(fileUri2, baseDir.relativize(filePath2), CONFIG_SCOPE_ID, false, null, filePath2, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    backend.getAnalysisService().analyzeFileList(
      new AnalyzeFileListParams(CONFIG_SCOPE_ID, List.of(fileUri1, fileUri2)));
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).hasSize(2));

    var raisedIssues = client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID);
    assertThat(raisedIssues).hasSize(2);
  }

  @SonarLintTest
  void should_run_forced_analysis_for_open_files(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath1 = createFile(baseDir, "Foo.java", "public interface Foo {}");
    var filePath2 = createFile(baseDir, "Bar.java", "public interface Bar {}");
    var filePath3 = createFile(baseDir, "Baz.java", "public interface Baz {}");
    var fileUri1 = filePath1.toUri();
    var fileUri2 = filePath2.toUri();
    var fileUri3 = filePath3.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(
        new ClientFileDto(fileUri1, baseDir.relativize(filePath1), CONFIG_SCOPE_ID, false, null, filePath1, null, null, true),
        new ClientFileDto(fileUri2, baseDir.relativize(filePath2), CONFIG_SCOPE_ID, false, null, filePath2, null, null, true),
        new ClientFileDto(fileUri3, baseDir.relativize(filePath3), CONFIG_SCOPE_ID, false, null, filePath3, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .withAutomaticAnalysisEnabled(false)
      .start(client);
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri1));
    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri2));

    backend.getAnalysisService().analyzeOpenFiles(new AnalyzeOpenFilesParams(CONFIG_SCOPE_ID));
    await().during(500, TimeUnit.MILLISECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).hasSize(2));

    var raisedIssues = client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID);
    assertThat(raisedIssues).hasSize(2);
  }

  @SonarLintTest
  void should_run_forced_analysis_vcs_changed_files(SonarLintTestHarness harness, @TempDir Path baseDir) throws IOException, GitAPIException {
    var git = createRepository(baseDir);

    var fileFoo = createFile(baseDir, "Foo.java", "public interface Foo {}");
    var fileBar = createFile(baseDir, "Bar.java", "");
    git.add().addFilepattern("Foo.java").call();
    git.add().addFilepattern("Bar.java").call();
    commit(git, "Foo.java");
    commit(git, "Bar.java");
    modifyFile(fileBar, "public interface Bar {}");
    var fileBaz = createFile(baseDir, "Baz.java", "public interface Baz {}");
    git.add().addFilepattern("Baz.java").call();
    var fileQux = createFile(baseDir, "Qux.java", "public interface Qux {}");
    var fileFooUri = fileFoo.toUri();
    var fileBarUri = fileBar.toUri();
    var fileBazUri = fileBaz.toUri();
    var fileQuxUri = fileQux.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(
        new ClientFileDto(fileFooUri, baseDir.relativize(fileFoo), CONFIG_SCOPE_ID, false, null, fileFoo, null, null, true),
        new ClientFileDto(fileBarUri, baseDir.relativize(fileBar), CONFIG_SCOPE_ID, false, null, fileBar, null, null, true),
        new ClientFileDto(fileBazUri, baseDir.relativize(fileBaz), CONFIG_SCOPE_ID, false, null, fileBaz, null, null, true),
        new ClientFileDto(fileQuxUri, baseDir.relativize(fileQux), CONFIG_SCOPE_ID, false, null, fileQux, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    backend.getAnalysisService().analyzeVCSChangedFiles(new AnalyzeVCSChangedFilesParams(CONFIG_SCOPE_ID));
    await().during(500, TimeUnit.MILLISECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).hasSize(3));

    var raisedIssues = client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID);
    assertThat(raisedIssues).hasSize(3);
  }

  @SonarLintTest
  @Disabled("Flaky tests")
  void should_run_forced_full_project_analysis_only_for_hotspots(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var fileFoo = createFile(baseDir, "Foo.java", """
      public class Foo {
      
        void foo() {
          String password = "blue";
        }
      }
      """);
    var fileBar = createFile(baseDir, "Bar.java", "");
    var fileFooUri = fileFoo.toUri();
    var fileBarUri = fileBar.toUri();

    var connectionId = "connectionId";
    var branchName = "branchName";
    var projectKey = "projectKey";
    var serverWithHotspots = harness.newFakeSonarQubeServer("10.4")
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java")
        .withActiveRule("java:S2068", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .withProject(projectKey,
        project -> project
          .withQualityProfile("qpKey")
          .withBranch(branchName))
      .withPlugin(TestPlugin.JAVA)
      .start();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(
        new ClientFileDto(fileFooUri, baseDir.relativize(fileFoo), CONFIG_SCOPE_ID, false, null, fileFoo, null, null, true),
        new ClientFileDto(fileBarUri, baseDir.relativize(fileBar), CONFIG_SCOPE_ID, false, null, fileBar, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withBackendCapability(FULL_SYNCHRONIZATION, SECURITY_HOTSPOTS)
      .withSonarQubeConnection(connectionId, serverWithHotspots)
      .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
      .withExtraEnabledLanguagesInConnectedMode(JAVA)
      .start(client);
    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));

    backend.getAnalysisService().analyzeFullProject(new AnalyzeFullProjectParams(CONFIG_SCOPE_ID, true));
    await().atMost(40, TimeUnit.SECONDS).untilAsserted(() ->
      assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty());
    await().atMost(40, TimeUnit.SECONDS).untilAsserted(() ->
      assertThat(client.getRaisedHotspotsForScopeIdAsList(CONFIG_SCOPE_ID)).hasSize(1));

    var raisedIssuesForFoo = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileFooUri);
    var raisedIssuesForBar = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileBarUri);
    var raisedHotspotsForFoo = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileFooUri);
    var raisedHotspotsForBar = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileBarUri);
    assertThat(raisedIssuesForFoo).isEmpty();
    assertThat(raisedIssuesForBar).isEmpty();
    assertThat(raisedHotspotsForFoo).hasSize(1);
    assertThat(raisedHotspotsForBar).isEmpty();
  }

  @SonarLintTest
  @Disabled("Flaky test")
  void should_run_forced_full_project_analysis_for_all_findings(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var fileFoo = createFile(baseDir, "Foo.java", """
      public class Foo {
      
        void foo() {
          String password = "blue";
        }
      }
      """);
    var fileBar = createFile(baseDir, "Bar.java", "");
    var fileFooUri = fileFoo.toUri();
    var fileBarUri = fileBar.toUri();

    var connectionId = "connectionId";
    var branchName = "branchName";
    var projectKey = "projectKey";
    var serverWithHotspots = harness.newFakeSonarQubeServer("10.4")
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java")
        .withActiveRule("java:S2068", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR))
        .withActiveRule("java:S1220", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
      .withProject(projectKey,
        project -> project
          .withQualityProfile("qpKey")
          .withBranch(branchName))
      .withPlugin(TestPlugin.JAVA)
      .start();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(
        new ClientFileDto(fileFooUri, baseDir.relativize(fileFoo), CONFIG_SCOPE_ID, false, null, fileFoo, null, null, true),
        new ClientFileDto(fileBarUri, baseDir.relativize(fileBar), CONFIG_SCOPE_ID, false, null, fileBar, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .withBackendCapability(SECURITY_HOTSPOTS)
      .withSonarQubeConnection(connectionId, serverWithHotspots)
      .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
      .withExtraEnabledLanguagesInConnectedMode(JAVA)
      .start(client);
    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));

    backend.getAnalysisService().analyzeFullProject(new AnalyzeFullProjectParams(CONFIG_SCOPE_ID, false));
    await().atMost(40, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).hasSize(2));
    await().atMost(40, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedHotspotsForScopeIdAsList(CONFIG_SCOPE_ID)).hasSize(1));

    var raisedIssuesForFoo = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileFooUri);
    var raisedIssuesForBar = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileBarUri);
    var raisedHotspotsForFoo = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileFooUri);
    var raisedHotspotsForBar = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileBarUri);
    assertThat(raisedIssuesForFoo).hasSize(1);
    assertThat(raisedIssuesForBar).hasSize(1);
    assertThat(raisedHotspotsForFoo).hasSize(1);
    assertThat(raisedHotspotsForBar).isEmpty();
  }

  @SonarLintTest
  void should_not_check_file_exclusions_for_forced_analysis(SonarLintTestHarness harness, @TempDir Path baseDir) {
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
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .withFileExclusions(CONFIG_SCOPE_ID, Set.of("**/*.xml"))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));

    verify(client, never()).raiseIssues(eq(CONFIG_SCOPE_ID), any(), eq(false), any());

    backend.getAnalysisService().analyzeOpenFiles(new AnalyzeOpenFilesParams(CONFIG_SCOPE_ID));

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isNotEmpty());

    var issues = client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID);
    assertThat(issues).hasSize(1);
  }

}
