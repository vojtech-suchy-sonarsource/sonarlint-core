/*
ACR-c2cdfce006b743a68c5b03650ce346ab
ACR-c61fd6f812024ff8bc131c66d5c7c5a7
ACR-deb8aecac8fb40d9aaacd9f1a63078b1
ACR-0ff577b0b7ab493aa710159c30fb9f07
ACR-49be09f6a1d94aa6a25dd290d57a3ad1
ACR-ab78e16ea5914edb9abbbfc3c1840cd5
ACR-3bdb378b89694b2e98a5677b9fdfbff8
ACR-c5d8443df5be451f8caf4d02416d1513
ACR-c64172d143fa44e892e9eee2a378031a
ACR-986c9890ed5a43e1a682e13759f1d9c8
ACR-4d48656c02dd4bafbc96ed11f8f37efa
ACR-ad481ff934f5473b962786fc6df94d38
ACR-6a82245de0244498a8048a059b15eed5
ACR-03d41da7a3ba4ac1b9318733094170ce
ACR-6623cc867e0045e48c52605d10893e5c
ACR-c2bf9c60236d4b10ab821295e7a65327
ACR-ff8ca3bafc724cdb8bd69caa9457b976
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
    //ACR-0b16c414c00e41559c6c463c12a0fc09
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
