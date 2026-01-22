/*
ACR-c5e4c4628266481eaf64869ff199fb65
ACR-db62b620ee924fefb62b02bfb09ac7ab
ACR-3c904461e6b7439e95c8fe0a3c54cef4
ACR-7d79973def674aa9b93af3f769c4c50b
ACR-1673a8c3ee5043aba3ab8967e33817c8
ACR-59288f9ab19e45a08a1ee65d18043011
ACR-5a7ce6dd1b724f0a8bdfdfb777c5dec2
ACR-88b0e2cf2fe24eca91fa9646114a63ec
ACR-6f2bdc5f8e034e83bdcb75b7a3355522
ACR-2a4ad1c467b34a7b8c6b99ecaf3c9e59
ACR-b59ff774d77749b395bf0ab7036d9fc1
ACR-49f1c56e80944e91ae022b8b84242b03
ACR-7ea7b41ea8674005b5c82d049fa0206f
ACR-ac487016594b4ed29558fb9c9fc560ad
ACR-c959bc5ba44647b4871a6921243040e3
ACR-6ae759d9b804453d81cdb680a31a97fd
ACR-05bf2e27b20a49f38452fae46c4245ba
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
