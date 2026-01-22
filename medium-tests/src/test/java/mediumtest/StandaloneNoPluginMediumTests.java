/*
ACR-1dc8ccfafe9546a79f524163524fa089
ACR-fda4e291dcb6478caa9e158a2aa09d31
ACR-9a65756e0b2044209781f7ac07330972
ACR-e77dbefe06604d6d8448906676561447
ACR-4f9e9262c1334f50ba54ae754619a299
ACR-2d7020b0c5644289bdbebacc12843593
ACR-d761c3c7793c41af9f16fb402a9e52be
ACR-bb7daddcb54a44d99a45e73ef7f7a6fd
ACR-fd58307a162a4ffbb4f47e95e1b43efa
ACR-300f0dff9edf4815b07f4c00d43e4fec
ACR-2f903f70b8a14533a10006656d3f1ba7
ACR-9e8589c3a4cd40ad837c40dc70ded774
ACR-e6d78954e11e41b49f6b2d0ccfc05aac
ACR-70ecc618f7cf4af7ab8ca8758ae87f48
ACR-cd0b27eaf23d4996aa2c19b98197a636
ACR-48a31a75ae3e4f8493c14376828984c3
ACR-03ad66e3c1654209bd90cf35a50ded19
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
