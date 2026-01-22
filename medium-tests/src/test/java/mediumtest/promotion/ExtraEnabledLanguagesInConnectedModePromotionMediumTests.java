/*
ACR-17e1013e934c41d4bcbc364f186fd312
ACR-4520adf5e97343cda41843b168b89ef5
ACR-8f8df64aafec45caaf36c6fdecd9193e
ACR-1b5ff623aeb24d3c84306900270c96c8
ACR-98f454017db04b48993c85b82ff679ef
ACR-b9fd858d368a450d8b56ba945257fca1
ACR-e9d19812108140bcae3d27a77817993f
ACR-beb4cdc33af046e49111bf54bcdd27a2
ACR-b507d54941b5498a941cc4a86a8014a8
ACR-c51a5fa1726c4cd2b388228b0e232e36
ACR-823c42f0f1b346b2bf441967c006d3c9
ACR-1992feb41b4741efa597500525682732
ACR-9d0320a324114ed2a2b4b137e650605b
ACR-d57b4cbf9d6541e4bcf3c50e085abc7c
ACR-b7e16b65887f4a40a1cf17235256f83b
ACR-716e14784cbc47d5870de40d9217bb08
ACR-90c4b16f2ce1485089d7ed96b9a99c39
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
