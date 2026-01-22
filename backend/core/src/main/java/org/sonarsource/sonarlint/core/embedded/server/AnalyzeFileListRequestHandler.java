/*
ACR-28284791cf4d43d8838e10512f427108
ACR-00f18747f1e346af905742359e5ada8a
ACR-fbde93324d5c45d9a5c215e74e96106b
ACR-561ad43dec3248fbb190d61cabac0c1e
ACR-fc63906a59cc47d2a3d952f85b53e95a
ACR-ef7459e8060d4a4285a39effeefd628e
ACR-691b0b26c41148f7b27b760757d4202f
ACR-89c19ae13db041dc9c02374d978c21f5
ACR-0faa10a6dc0f487b805650c20c7b0b8e
ACR-c8e6e88d24ad4211bd161b324258bfd9
ACR-6f4a1bf8f165459eb5cf2e4255054982
ACR-649c827c94d649f6baeb1e30cf618b46
ACR-696b37bff28f4d36bd49fce4b4fd8a69
ACR-572c2ca363f64e80976ae5efcac584f7
ACR-a864c8bf641d4b00a9f6273cf314e7ca
ACR-c8179ff7d75341e394b4a68949b61ac2
ACR-b486ae30c5d34639b96add2f31ad596e
 */
package org.sonarsource.sonarlint.core.embedded.server;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.analysis.api.TriggerType;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.analysis.AnalysisService;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.tracking.TaintVulnerabilityTrackingService;

public class AnalyzeFileListRequestHandler implements HttpRequestHandler {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final AnalysisService analysisService;
  private final ClientFileSystemService clientFileSystemService;
  private final TaintVulnerabilityTrackingService taintService;
  private final Gson gson = new Gson();

  public AnalyzeFileListRequestHandler(AnalysisService analysisService, ClientFileSystemService clientFileSystemService,
    TaintVulnerabilityTrackingService taintService) {
    this.analysisService = analysisService;
    this.clientFileSystemService = clientFileSystemService;
    this.taintService = taintService;
  }

  @Override
  public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext httpContext) throws HttpException, IOException {
    LOG.debug("Received request for analyzing a list of files");

    if (!Method.POST.isSame(request.getMethod())) {
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      return;
    }

    AnalyzeFileListRequest analysisRequest;
    try {
      var requestBody = EntityUtils.toString(request.getEntity(), "UTF-8");
      analysisRequest = gson.fromJson(requestBody, AnalyzeFileListRequest.class);
    } catch (Exception e) {
      LOG.warn("Failed to parse analyze file list request", e);
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      response.setEntity(new StringEntity("Failed to parse analyze file list request", ContentType.APPLICATION_JSON));
      return;
    }

    if (analysisRequest == null || analysisRequest.fileAbsolutePaths == null || analysisRequest.fileAbsolutePaths.isEmpty()) {
      LOG.warn("Empty or invalid file list in analyze request");
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      response.setEntity(new StringEntity("Empty or invalid file list in analyze request", ContentType.APPLICATION_JSON));
      return;
    }

    try {
      response.setEntity(new StringEntity(new Gson().toJson(analyze(analysisRequest)), ContentType.APPLICATION_JSON));
    } catch (Exception e) {
      LOG.error("Failed to analyze files", e);
      response.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      response.setEntity(new StringEntity("Failed to analyze files, reason: " + e.getMessage(), ContentType.APPLICATION_JSON));
    }
  }

  private AnalyzeFileListResult analyze(AnalyzeFileListRequest request) {
    var cancelMonitor = new SonarLintCancelMonitor();
    var filePaths = request.fileAbsolutePaths.stream()
      .map(path -> Paths.get(path).toUri().normalize())
      .collect(Collectors.toSet());

    LOG.debug("Analyzing list of {} files: {}", filePaths.size(), filePaths);

    var filesByScope = clientFileSystemService.groupFilesByConfigScope(filePaths);

    if (filesByScope.isEmpty()) {
      LOG.warn("No files belong to any configured scope, skipping analysis");
      throw new IllegalStateException("No files were found to be indexed by SonarQube for IDE");
    }

    var allIssues = filesByScope.entrySet().stream().flatMap(entry -> {
      var configScopeId = entry.getKey();
      var files = entry.getValue();
      LOG.info("Analyzing list of {} files: {}", files.size(), files);
      try {
        var taints = getTaintsAsRawFindings(configScopeId, files, cancelMonitor);
        var issues = getIssuesAndHotspotsAsRawFindings(configScopeId, files, cancelMonitor);
        return Stream.concat(taints, issues);
      } catch (ExecutionException | InterruptedException e) {
        LOG.error("Failed to analyze files for config scope {}", configScopeId, e);
        throw new RuntimeException(e);
      }
    }).toList();

    return new AnalyzeFileListResult(allIssues);
  }

  private Stream<RawFindingResponse> getTaintsAsRawFindings(String configScopeId, Set<URI> files, SonarLintCancelMonitor cancelMonitor) {
    return taintService.listAll(configScopeId, true, cancelMonitor)
      .stream()
      .filter(taint -> files.contains(taint.getIdeFilePath().toUri()))
      .map(taint -> {
        var isMqrMode = taint.getSeverityMode().isRight();
        var textRange = taint.getTextRange();
        return new RawFindingResponse(
          taint.getRuleKey(),
          taint.getMessage(),
          isMqrMode ? taint.getSeverityMode().getRight().getImpacts().stream()
            .map(impact -> impact.getImpactSeverity().name())
            .collect(Collectors.joining(",")) : taint.getSeverityMode().getLeft().getSeverity().name(),
          taint.getIdeFilePath().toString(),
          textRange == null ? null : new TextRange(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(), textRange.getEndLineOffset())
        );
      });
  }

  private Stream<RawFindingResponse> getIssuesAndHotspotsAsRawFindings(String configScopeId, Set<URI> files, SonarLintCancelMonitor cancelMonitor)
    throws ExecutionException, InterruptedException {
    return analysisService.scheduleAnalysis(configScopeId, UUID.randomUUID(), files, Collections.emptyMap(),
        false, TriggerType.FORCED, cancelMonitor)
      .thenApplyAsync(results ->
        results.rawIssues().stream().map(i -> new RawFindingResponse(
          i.getRuleKey(),
          i.getMessage(),
          i.getSeverity() == null ? null : i.getSeverity().name(),
          i.getFileUri() == null ? null : i.getFileUri().getPath(),
          i.getTextRange()
        )))
      .get();
  }

  public record AnalyzeFileListRequest(List<String> fileAbsolutePaths) {
  }

  public record AnalyzeFileListResult(List<RawFindingResponse> findings) {
  }

  public record RawFindingResponse(String ruleKey, String message, @Nullable String severity, @Nullable String filePath, @Nullable TextRange textRange) {
  }

}
