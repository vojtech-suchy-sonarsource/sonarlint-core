/*
ACR-c5415e13cc944215871087f8a7c70643
ACR-a93f9d0377f7431ca56a091b1e6d8a37
ACR-3ee002c91cfd458c89a267e8aaf67911
ACR-9da9be2ea13247a08f57189e0cf0a302
ACR-cc64ec1ab96f4b2e99710e10f83a9130
ACR-ecc81a49e0ec4892b1ee4989e9293908
ACR-82eba955b2a74d119787c63d0e64ddaa
ACR-a8085d10ab864a69bf14b8db7dfa71ec
ACR-b16fdc1b45cc4c6cbed0d23ef78d02d9
ACR-8f99cb37dc224283b7b2dd9ae488180c
ACR-a15b75253443467d9dd3ccb532f10548
ACR-f3bfbcb5e3cd409493c0d9abd6d419a9
ACR-0f80c87bf06b497b9f829d1d3490b43a
ACR-47fe0e0b0761446e9ef88956c0024c7e
ACR-c0f69bc376164ff3af657862dd2885fb
ACR-902398bd03194e2b9eef8bb33ba5c81c
ACR-4a2724fd4c1947c9bf15e802dcea1f09
 */
package mediumtest;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;
import static utils.AnalysisUtils.createFile;

class StandaloneNoPluginMediumTests {

  private static final String CONFIG_SCOPE_ID = "configScopeId";

  @SonarLintTest
  void dont_fail_and_detect_language_even_if_no_plugin(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "Foo.java", """
      public class Foo {
      
        void foo() {
          String password = "blue";
        }
      }
      """);
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)
      ))
      .build();
    var backend = harness.newBackend()
      .withBackendCapability(SECURITY_HOTSPOTS)
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .start(client);

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
        new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().during(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty());
  }

}
