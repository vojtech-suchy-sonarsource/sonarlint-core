/*
ACR-938294d4bffe4efd845f01b34f513257
ACR-b875fca657f84831a0d31b8d175550c6
ACR-f49bd295c4244cc3aa9a833a42037587
ACR-2e21700cf5d44e63a152abc1b3cf396d
ACR-704b84103ae84c21b7ab326271be522f
ACR-c1d2bb27f1ba402cbe8db46999cf9e8e
ACR-a5095bfdab76495cb4d8360275a58cf5
ACR-ad03fc78b73c4bda96e57c9ae9799859
ACR-5df54abe814844fd96308379f19df7df
ACR-487780323e8745f0b43ccb4ae3068bb6
ACR-51c63face9c7465c9da71bf5e3392b24
ACR-f493374cf1f849938ea14e3043be5100
ACR-40441794b42241c89259f859c9ac5d1e
ACR-a5040e90b5994d9d892e201156908730
ACR-adbe5ec6d59c49c1b0a5764044cf9e2c
ACR-327c2f76a6894d3289e84d507e926f35
ACR-9b58cadb3f5c425cb3e8286d9525b539
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
