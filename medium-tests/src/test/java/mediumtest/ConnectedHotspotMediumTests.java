/*
ACR-449d7eb217424a8eb14d1227ed50b698
ACR-e9081cc7606845238b1cc81ea39397d8
ACR-a8249459605746aaa06d27cf7bd0343b
ACR-443e9ff6ef4147358f534edfaced9d0b
ACR-955f3eaa1d884b7a8fd9944af9853a64
ACR-51d1101c3baa44a1acc0dbd487b06a71
ACR-beba3d1268a54616ab7ac1469b1f7f38
ACR-cca239e6a48f45d38f91b33f7c4e7692
ACR-6cb24b5f84b14aaa9125d5186ad6afaf
ACR-4cfb9b60ebd34f5e921dac51e2e083bb
ACR-0424b9f923a24dda80a7ed50fe971f6e
ACR-6c38bb1735ce4fa38e30c1f015889dfd
ACR-fb6b8b4a853344dea55944a2e19d332f
ACR-c3cc53338ddf4092b772a62f1e48fe2d
ACR-8ea79e2e5ea64412baa006cbae19ea80
ACR-9982a430eeac435da180b246f6fb3bf1
ACR-813b25d7dbf04b92aded02dd6ef5ee9b
 */
package mediumtest;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static utils.AnalysisUtils.createFile;

class ConnectedHotspotMediumTests {

  @SonarLintTest
  void should_locally_detect_hotspots_when_connected_to_sonarqube(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "Foo.java", """
      public class Foo {
        void foo() {
          String password = "blue";
        }
      }
      """);
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var projectKey = "projectKey";
    var branchName = "main";
    var server = harness.newFakeSonarQubeServer("9.9")
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile
        .withLanguage("java").withActiveRule("java:S2068", activeRule -> activeRule
          .withSeverity(org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity.BLOCKER)))
      .withProject(projectKey,
        project -> project
          .withQualityProfile("qpKey")
          .withBranch(branchName, branch -> branch.withHotspot("key", hotspot -> hotspot.withFilePath("Foo.java"))))
      .withPlugin(TestPlugin.JAVA)
      .start();
    var backend = harness.newBackend()
      .withBackendCapability(FULL_SYNCHRONIZATION, SECURITY_HOTSPOTS)
      .withSonarQubeConnection(CONNECTION_ID, server)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, projectKey)
      .withExtraEnabledLanguagesInConnectedMode(JAVA)
      .start(client);
    client.waitForSynchronization();

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedHotspotsForScopeIdAsList(CONFIG_SCOPE_ID)).isNotEmpty());

    var hotspot = client.getRaisedHotspotsForScopeIdAsList(CONFIG_SCOPE_ID).get(0);
    assertThat(hotspot.getRuleKey()).isEqualTo("java:S2068");
    assertThat(hotspot.getSeverityMode().isLeft()).isTrue();
    assertThat(hotspot.getSeverityMode().getLeft().getSeverity()).isEqualTo(IssueSeverity.BLOCKER);
  }

  private static final String CONNECTION_ID = StringUtils.repeat("very-long-id", 20);
  private static final String CONFIG_SCOPE_ID = "configScopeId";

}
