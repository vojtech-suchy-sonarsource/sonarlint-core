/*
ACR-5b9c119fb7334dd2a017c1de8e6e7720
ACR-90b4c418ac664734b3ab2aee25590c02
ACR-24bfc3165dbe4cb99c1a29d5244bf60a
ACR-7d969d70bfd54542a902a4b95f4595c2
ACR-8f584441a8024385a90a7fa3db793717
ACR-9edd92af1c0540a9ab2aba341c4629cb
ACR-7d00302949054ff29f361288716a074c
ACR-a492a5edab8243c3b9b03222b80643aa
ACR-c721e0bd7d1142a9817976a0a286319a
ACR-5b34a3d15ad9490f830442b4e351fed8
ACR-6cfd55d0653444258a97f014fa96fb48
ACR-8516082bd4e74f35bb7711777d21b067
ACR-11947f49bc2b42fda3209faced8a5ab6
ACR-90a75a2ab3a3455b9a82c333b815e2b8
ACR-6b4d2503c5714eaf92bdd7f67fb8a3ff
ACR-37eed77612b44b3b8c97214bc4beed9f
ACR-ad964d3051f546aa9f7fa6d22f4cb318
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
