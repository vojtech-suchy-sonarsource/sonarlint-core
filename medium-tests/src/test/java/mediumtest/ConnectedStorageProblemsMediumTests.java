/*
ACR-82d5c2218cd94c67a0b55c5b186f2e11
ACR-3c42da5b886740ccbc2f313ae4898fcf
ACR-0667ad7400d846db9d480fcf97f8fd10
ACR-ff54da9856eb40329037d97e33d4f896
ACR-c0945ea3b9644ea38104c3ee0cd142c8
ACR-84e1dd0f92354f428343459724557c26
ACR-95ce6f4368754c57abb5aa65a8769ce2
ACR-7108505d50a74132961dae7728a6e539
ACR-9e86a9dba3d84a628dc3a30709ff0a7b
ACR-ba6b4afe891c42e5b00f0a0b0f341994
ACR-46ad6913075a4cb5ac11f02158719a61
ACR-a99d41c71fc34558a945bc5ba3da0a4e
ACR-0a73120883e349648d9484252effbbe2
ACR-7552ae9ab9504b559a16a961fe8d9b1e
ACR-a41f090dc5f94ae5bb0ed50218ec8f70
ACR-da793a6041e14803a8fccedba261a002
ACR-d3414ffd4b39449388e2eafaae7302c9
 */
package mediumtest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static utils.AnalysisUtils.createFile;

class ConnectedStorageProblemsMediumTests {
  private static final String CONNECTION_ID = "localhost";
  private static final String CONFIG_SCOPE_ID = "myProject";

  @SonarLintTest
  void corrupted_plugin_should_not_prevent_startup(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var inputFile = createFile(baseDir, "Foo.java", """
      public class Foo {
        public void foo() {
          int x;
          System.out.println("Foo");
          System.out.println("Foo"); //NOSONAR
        }
      }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)
      ))
      .build();
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage
        .withPlugin(TestPlugin.JAVA)
        .withPlugin(SonarLanguage.JS.getPluginKey(), createFakePlugin(), "hash")
        .withProject(CONFIG_SCOPE_ID,
          project -> project.withMainBranch("main").withRuleSet(SonarLanguage.JS.getSonarLanguageKey(),
            ruleSet -> ruleSet.withActiveRule("java:S106", "BLOCKER"))))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, CONFIG_SCOPE_ID)
      .withEnabledLanguageInStandaloneMode(org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA)
      .withEnabledLanguageInStandaloneMode(org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JS).start(client);

    backend.getAnalysisService().analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, UUID.randomUUID(),
      List.of(inputFile.toUri()), Map.of(), false, Instant.now().toEpochMilli())).get();

    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Execute Sensor: JavaSensor"));
  }

  private static Path createFakePlugin() {
    try {
      return Files.createTempFile("fakePlugin", "jar");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
