/*
ACR-e1b17412554045058f18de1cfa2c9888
ACR-e69ff40ddc3845f2a6f452e29cb27121
ACR-b01102838e954841b297f2911ee20851
ACR-6868bad0efa241c4b55929fb9e6db26a
ACR-7ab1745702bf4b659c0f23fb1c69a74e
ACR-c59e4951fa714c99b21870f8c7da9a76
ACR-90fca901330c451a9c686dc094c77180
ACR-d4768fec2dc946e6abbcbf7ecc5b45d0
ACR-c18dcf5eb5cb4247bc7009546acefc37
ACR-1aec9ec9648443379aa2d85ca0110004
ACR-e2b36344dc4e4f23b28ce91982dac829
ACR-1d4ed65cee24408894d534ecc10d2cb1
ACR-1ceee7d56b5b45af97ad062ce8ddc384
ACR-8580e03cfb1943c1b58c03d87d3a47ef
ACR-a9e842fa73994c59a2463cfce5bd7192
ACR-b3bdcc8061a74d459a866d0ddd35bb1c
ACR-ec40b91402ee4fbba7619b44f8e23639
 */
package utils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class AnalysisUtils {

  private AnalysisUtils() {
    //ACR-d70786619cdb4a51b93040b20878bf60
  }

  public static Path createFile(Path folderPath, String fileName, String content) {
    var filePath = folderPath.resolve(fileName);
    try {
      Files.writeString(filePath, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return filePath;
  }

  public static RaisedIssueDto analyzeFileAndGetIssue(URI fileUri, SonarLintBackendFixture.FakeSonarLintRpcClient client, SonarLintTestRpcServer backend, String scopeId) {
    var raisedIssues = analyzeFileAndGetIssues(fileUri, client, backend, scopeId);
    return raisedIssues.get(0);
  }

  public static List<RaisedIssueDto> analyzeFileAndGetIssues(URI fileUri, SonarLintBackendFixture.FakeSonarLintRpcClient client, SonarLintTestRpcServer backend, String scopeId) {
    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
        new AnalyzeFilesAndTrackParams(scopeId, analysisId, List.of(fileUri), Map.of(), false, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(scopeId)).isNotEmpty());
    return client.getRaisedIssuesForScopeId(scopeId).get(fileUri);
  }

  public static Map<URI, List<RaisedIssueDto>> analyzeFilesAndGetIssuesAsMap(List<URI> files, SonarLintBackendFixture.FakeSonarLintRpcClient client, SonarLintTestRpcServer backend, String scopeId) {
    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
        new AnalyzeFilesAndTrackParams(scopeId, analysisId, files, Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(scopeId)).isNotEmpty());
    return client.getRaisedIssuesForScopeId(scopeId);
  }

  public static void analyzeFilesAndVerifyNoIssues(List<URI> files, SonarLintBackendFixture.FakeSonarLintRpcClient client, SonarLintTestRpcServer backend, String scopeId) {
    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
        new AnalyzeFilesAndTrackParams(scopeId, analysisId, files, Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().during(1, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(scopeId)).isEmpty());
  }

  public static void analyzeFileAndGetHotspots(URI fileUri, SonarLintBackendFixture.FakeSonarLintRpcClient client, SonarLintTestRpcServer backend, String scopeId) {
    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
        new AnalyzeFilesAndTrackParams(scopeId, analysisId, List.of(fileUri), Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(40, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedHotspotsForScopeIdAsList(scopeId)).isNotEmpty());
    assertThat(client.getRaisedHotspotsForScopeId(scopeId)).containsOnlyKeys(fileUri);
  }

  public static Map<URI, List<RaisedIssueDto>> getPublishedIssues(SonarLintBackendFixture.FakeSonarLintRpcClient client, String scopeId) {
    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(scopeId)).isNotEmpty());
    return client.getRaisedIssuesForScopeId(scopeId);
  }

  public static void waitForRaisedIssues(SonarLintBackendFixture.FakeSonarLintRpcClient client, String scopeId) {
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(scopeId)).isNotEmpty());
  }

  public static void waitForAnalysisReady(SonarLintBackendFixture.FakeSonarLintRpcClient client, String scopeId) {
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.isAnalysisReadyForScope(scopeId)).isTrue());
  }

  public static void editFile(Path folderPath, String fileName, String content) {
    var filePath = folderPath.resolve(fileName);
    try {
      Files.writeString(filePath, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void removeFile(Path folderPath, String fileName) {
    var filePath = folderPath.resolve(fileName);
    try {
      Files.deleteIfExists(filePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<RaisedIssueDto> awaitRaisedIssuesNotification(SonarLintBackendFixture.FakeSonarLintRpcClient client, String configScopeId) {
    waitForRaisedIssues(client, configScopeId);
    return client.getRaisedIssuesForScopeIdAsList(configScopeId);
  }
}
