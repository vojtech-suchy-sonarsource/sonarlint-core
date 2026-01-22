/*
ACR-c1f5c537712d43b48b36cf7d99924ece
ACR-8384abfd636540da901d5603c33ac365
ACR-d663cc96f67e48c8b42b91482bf26c5c
ACR-da7eaba789194b938434777b878738a5
ACR-a8c539c944934ba7b2b762f914e95445
ACR-69d02a0d9ec94a6aaff6297eed74b34c
ACR-4f7aaca7cdd44fa2a7187846b7ffed88
ACR-595bad1f2091406194472f21ad29496f
ACR-1e7f8a577db04603a966112864a73190
ACR-0390514c50d14470836190b7d8e0ac75
ACR-d3bc9490cb744b41b2781a00169cabdb
ACR-f184f4afe3fc49dba519e50f9bd97101
ACR-7ddfc35840044cdb8d6b395ea96e95d2
ACR-4decbdbcfb3e476d8481af5d26b086b3
ACR-dbb2b73b2a0340f58f63923a95f73e9f
ACR-d1b28e2da6ce429b93f1bc18e18c7358
ACR-8948ee37b28c4ac2b43f06dc66b735ae
 */
package org.sonarsource.sonarlint.core.embedded.server;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.analysis.AnalysisResult;
import org.sonarsource.sonarlint.core.analysis.AnalysisService;
import org.sonarsource.sonarlint.core.analysis.RawIssue;
import org.sonarsource.sonarlint.core.analysis.api.TriggerType;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.tracking.TaintVulnerabilityTrackingService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class AnalyzeFileListRequestHandlerTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester(true);
  private AnalyzeFileListRequestHandler analyzeFileListRequestHandler;
  private AnalysisService analysisService;
  private ClientFileSystemService clientFileSystemService;

  @BeforeEach
  void setup() {
    analysisService = mock(AnalysisService.class);
    clientFileSystemService = mock(ClientFileSystemService.class);
    var taintVulnerabilityTrackingService = mock(TaintVulnerabilityTrackingService.class);
    analyzeFileListRequestHandler = spy(new AnalyzeFileListRequestHandler(analysisService, clientFileSystemService, taintVulnerabilityTrackingService));
  }

  @Test
  void should_reject_non_post_requests() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest(Method.GET, "/analyze");
    var response = new BasicClassicHttpResponse(200);
    var context = mock(HttpContext.class);

    analyzeFileListRequestHandler.handle(request, response, context);

    assertThat(response.getCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void should_reject_invalid_json_request() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest(Method.POST, "/analyze");
    request.setEntity(new StringEntity("invalid json", StandardCharsets.UTF_8));
    var response = new BasicClassicHttpResponse(200);
    var context = mock(HttpContext.class);

    analyzeFileListRequestHandler.handle(request, response, context);

    assertThat(response.getCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    assertThat(logTester.logs()).contains("Failed to parse analyze file list request");
  }

  @Test
  void should_reject_null_request_body() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest(Method.POST, "/analyze");
    request.setEntity(new StringEntity("null", StandardCharsets.UTF_8));
    var response = new BasicClassicHttpResponse(200);
    var context = mock(HttpContext.class);

    analyzeFileListRequestHandler.handle(request, response, context);

    assertThat(response.getCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void should_reject_empty_file_list() throws HttpException, IOException {
    var requestJson = new Gson().toJson(new AnalyzeFileListRequestHandler.AnalyzeFileListRequest(Collections.emptyList()));
    var request = new BasicClassicHttpRequest(Method.POST, "/analyze");
    request.setEntity(new StringEntity(requestJson, StandardCharsets.UTF_8));
    var response = new BasicClassicHttpResponse(200);
    var context = mock(HttpContext.class);

    analyzeFileListRequestHandler.handle(request, response, context);

    assertThat(response.getCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void should_handle_issues_with_null_severity_and_file_path() throws HttpException, IOException {
    var analysisRequest = new AnalyzeFileListRequestHandler.AnalyzeFileListRequest(List.of("/path/to/file.java"));
    var requestJson = new Gson().toJson(analysisRequest);
    var request = new BasicClassicHttpRequest(Method.POST, "/analyze");
    request.setEntity(new StringEntity(requestJson, StandardCharsets.UTF_8));
    var response = new BasicClassicHttpResponse(200);
    var context = mock(HttpContext.class);
    var filesByScope = Map.of("scope1", Set.of(URI.create("file:///path/to/file.java")));
    when(clientFileSystemService.groupFilesByConfigScope(anySet())).thenReturn(filesByScope);
    var mockIssue = createMockRawIssue();
    var scanResults = mock(AnalysisResult.class);
    when(scanResults.rawIssues()).thenReturn(List.of(mockIssue));
    when(analysisService.scheduleAnalysis(anyString(), any(UUID.class), anySet(), anyMap(), 
      anyBoolean(), eq(TriggerType.FORCED), any())).thenReturn(CompletableFuture.completedFuture(scanResults));

    analyzeFileListRequestHandler.handle(request, response, context);

    assertThat(response.getCode()).isEqualTo(200);
    var responseContent = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
    var analysisResult = new Gson().fromJson(responseContent, AnalyzeFileListRequestHandler.AnalyzeFileListResult.class);
    assertThat(analysisResult.findings()).hasSize(1);
    var issue = analysisResult.findings().get(0);
    assertThat(issue.ruleKey()).isEqualTo("java:S123");
    assertThat(issue.severity()).isNull();
    assertThat(issue.filePath()).isNull();
    assertThat(issue.textRange()).isNull();
  }

  private RawIssue createMockRawIssue() {
    var mockIssue = mock(RawIssue.class);
    when(mockIssue.getRuleKey()).thenReturn("java:S123");
    when(mockIssue.getMessage()).thenReturn("Test message");
    when(mockIssue.getSeverity()).thenReturn(null);
    when(mockIssue.getFileUri()).thenReturn(null);
    when(mockIssue.getTextRange()).thenReturn(null);
    return mockIssue;
  }

}
