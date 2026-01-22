/*
ACR-b743a045e0114f2f99966fc1a9ba652d
ACR-0dfaa2d017d641448acadaa6645c8e9a
ACR-ced0c96d51aa49cbabd3d9a24c12f53e
ACR-c91926543f3a46e1a6e4354ae0e0381c
ACR-2e5852bac2be4a9e95d30a5c2d3d5a1f
ACR-d0f4a203f69f4e1484ccadb26752cd30
ACR-563d2bacaaaf4ad28c50db1be005cb0c
ACR-3e309acbb8e8436fa05955f847733dbf
ACR-f5ccef4a5f68479695ea011f070706bc
ACR-c9148ffae6844caeb5709baf29d6d547
ACR-491161b4bdf440f8bfc44e2a33a609c6
ACR-7a34eaca8f894b3ca1c12696a2db4395
ACR-f9bb34e286654d74be80242fbb2eaee0
ACR-c4154029489c4a32885faac8def197cb
ACR-c1c353ed12cd473eba27921366293379
ACR-3d815dd304be4e15b254b7409e1db9a4
ACR-a1728693765c4861a181f1e42232ec16
 */
package mediumtest.promotion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.EMBEDDED_SERVER;

class ExtraEnabledLanguagesInConnectedModePromotionMediumTests {
  @RegisterExtension
  SonarLintLogTester logTester = new SonarLintLogTester(true);

  @SonarLintTest
  void it_should_notify_clients_for_a_detected_language_that_is_enabled_only_in_connected_mode(SonarLintTestHarness harness, @TempDir Path tempDir) throws IOException {
    var abapFile = tempDir.resolve("file.abap");
    Files.createFile(abapFile);
    var fakeClient = harness.newFakeClient()
      .withInitialFs("configScopeId", tempDir, List.of(new ClientFileDto(abapFile.toUri(), tempDir.relativize(abapFile), "configScopeId", false, null, abapFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withExtraEnabledLanguagesInConnectedMode(Language.ABAP)
      .withUnboundConfigScope("configScopeId")
      .withBackendCapability(EMBEDDED_SERVER)
      .withTelemetryEnabled()
      .start(fakeClient);

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams("configScopeId", UUID.randomUUID(),
        List.of(abapFile.toUri()), Map.of(), false,0)).join();

    verify(fakeClient).promoteExtraEnabledLanguagesInConnectedMode("configScopeId", Set.of(Language.ABAP));
  }

  @SonarLintTest
  void it_should_not_notify_clients_when_already_in_connected_mode(SonarLintTestHarness harness, @TempDir Path tempDir) throws IOException {
    var abapFile = tempDir.resolve("file.abap");
    Files.createFile(abapFile);
    var fakeClient = harness.newFakeClient()
      .withInitialFs("configScopeId", tempDir, List.of(new ClientFileDto(abapFile.toUri(), tempDir.relativize(abapFile), "configScopeId", false, null, abapFile, null, null, true)))
      .build();
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey")
      .start();
    var backend = harness.newBackend()
      .withExtraEnabledLanguagesInConnectedMode(Language.ABAP)
      .withSonarQubeConnection("connectionId", server, storage -> storage.withProject("projectKey", project -> project.withRuleSet("abap", ruleSet -> ruleSet.withActiveRule("abap:S100", "MAJOR")).withMainBranch("main")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBackendCapability(EMBEDDED_SERVER)
      .withTelemetryEnabled()
      .start(fakeClient);

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams("configScopeId", UUID.randomUUID(),
        List.of(abapFile.toUri()), Map.of(), false, 0)).join();

    verify(fakeClient, after(200).never()).promoteExtraEnabledLanguagesInConnectedMode("configScopeId", Set.of(Language.ABAP));
  }

  @SonarLintTest
  void it_should_not_notify_clients_when_detected_language_is_not_an_extra_language(SonarLintTestHarness harness, @TempDir Path tempDir) throws IOException {
    var abapFile = tempDir.resolve("file.abap");
    Files.createFile(abapFile);
    var fakeClient = harness.newFakeClient()
      .withInitialFs("configScopeId", tempDir, List.of(new ClientFileDto(abapFile.toUri(), tempDir.relativize(abapFile), "configScopeId", false, null, abapFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.ABAP)
      .withUnboundConfigScope("configScopeId")
      .withBackendCapability(EMBEDDED_SERVER)
      .withTelemetryEnabled()
      .start(fakeClient);

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams("configScopeId", UUID.randomUUID(),
        List.of(abapFile.toUri()), Map.of(), false, 0)).join();

    verify(fakeClient, after(200).never()).promoteExtraEnabledLanguagesInConnectedMode("configScopeId", Set.of(Language.ABAP));
  }

  @SonarLintTest
  void it_should_not_notify_clients_when_no_language_was_detected_during_analysis(SonarLintTestHarness harness, @TempDir Path tempDir) throws IOException {
    var randomFile = tempDir.resolve("file.abc");
    Files.createFile(randomFile);
    var fakeClient = harness.newFakeClient()
      .withInitialFs("configScopeId", tempDir,
        List.of(new ClientFileDto(randomFile.toUri(), tempDir.relativize(randomFile), "configScopeId", false, null, randomFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope("configScopeId")
      .withBackendCapability(EMBEDDED_SERVER)
      .withTelemetryEnabled()
      .start(fakeClient);

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams("configScopeId", UUID.randomUUID(),
        List.of(randomFile.toUri()), Map.of(), false, 0)).join();

    verify(fakeClient, after(200).never()).promoteExtraEnabledLanguagesInConnectedMode(eq("configScopeId"), anySet());
    verify(fakeClient, never()).log(argThat(logParams -> logParams.getLevel().equals(LogLevel.ERROR)));
  }
}
