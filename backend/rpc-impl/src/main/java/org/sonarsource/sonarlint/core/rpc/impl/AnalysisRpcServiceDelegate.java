/*
ACR-ecaa9c53d0534a8f9013a71932dcc724
ACR-3ea440a5a3f44d75a839ddae301faed1
ACR-6c83553723d64c389bdff8e973757d3f
ACR-c87ccca577714d6db7a94c6ae99d92a5
ACR-8cbeec3adaf240128b9f11fa94f620a8
ACR-764b1790e39d46b1822447eb927c4021
ACR-45f0bcd3fb0242149dfb5035bedcb186
ACR-3a732701380b48c49006050056776798
ACR-1c1e1e3344894cde996633928bd8ed8d
ACR-f4078b43df7749d8b1fd5aab0d4ca59b
ACR-0c24649dc9734fdaacd801ef9ad08527
ACR-d8f17f9ee3f84e708c8acf68c227ed4a
ACR-1b57c165216d405691779100ba9b25bd
ACR-8d26f84de1f74b48b7c90683cd4fca70
ACR-2f27f81ed37548f9a169b85c674774fc
ACR-bf5701f3429d47a9bf1e52db336fcbb7
ACR-e4909b91af3f49ac9185b696b7c1524f
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.analysis.AnalysisResult;
import org.sonarsource.sonarlint.core.analysis.AnalysisService;
import org.sonarsource.sonarlint.core.analysis.NodeJsService;
import org.sonarsource.sonarlint.core.analysis.RawIssue;
import org.sonarsource.sonarlint.core.analysis.api.TriggerType;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalysisRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFileListParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFullProjectParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeOpenFilesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeVCSChangedFilesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.DidChangeAnalysisPropertiesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.DidChangeAutomaticAnalysisSettingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.DidChangeClientNodeJsPathParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.DidChangePathToCompileCommandsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.ForceAnalyzeResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.GetAutoDetectedNodeJsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.GetForcedNodeJsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.GetSupportedFilePatternsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.GetSupportedFilePatternsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.NodeJsDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.ShouldUseEnterpriseCSharpAnalyzerParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.ShouldUseEnterpriseCSharpAnalyzerResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.FileEditDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.QuickFixDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.RawIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.RawIssueFlowDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.RawIssueLocationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.TextEditDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
import org.sonarsource.sonarlint.core.rules.RuleDetailsAdapter;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

class AnalysisRpcServiceDelegate extends AbstractRpcServiceDelegate implements AnalysisRpcService {

  public AnalysisRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<GetSupportedFilePatternsResponse> getSupportedFilePatterns(GetSupportedFilePatternsParams params) {
    return requestAsync(
      cancelChecker -> new GetSupportedFilePatternsResponse(getBean(AnalysisService.class).getSupportedFilePatterns(params.getConfigScopeId())),
      params.getConfigScopeId());
  }

  @Override
  public CompletableFuture<GetForcedNodeJsResponse> didChangeClientNodeJsPath(DidChangeClientNodeJsPathParams params) {
    return requestAsync(cancelChecker -> {
      var forcedNodeJs = getBean(NodeJsService.class).didChangeClientNodeJsPath(params.getClientNodeJsPath());
      var dto = forcedNodeJs == null ? null : new NodeJsDetailsDto(forcedNodeJs.getPath(), forcedNodeJs.getVersion().toString());
      return new GetForcedNodeJsResponse(dto);
    });
  }

  @Override
  public CompletableFuture<GetAutoDetectedNodeJsResponse> getAutoDetectedNodeJs() {
    return requestAsync(cancelChecker -> {
      var autoDetectedNodeJs = getBean(AnalysisService.class).getAutoDetectedNodeJs();
      var dto = autoDetectedNodeJs == null ? null : new NodeJsDetailsDto(autoDetectedNodeJs.getPath(), autoDetectedNodeJs.getVersion().toString());
      return new GetAutoDetectedNodeJsResponse(dto);
    });
  }

  @Override
  public CompletableFuture<AnalyzeFilesResponse> analyzeFilesAndTrack(AnalyzeFilesAndTrackParams params) {
    var configurationScopeId = params.getConfigurationScopeId();
    return requestFutureAsync(cancelChecker -> getBean(AnalysisService.class)
      .scheduleAnalysis(params.getConfigurationScopeId(), params.getAnalysisId(), Set.copyOf(params.getFilesToAnalyze()),
        params.getExtraProperties(), params.isShouldFetchServerIssues(), TriggerType.FORCED_WITH_EXCLUSIONS, cancelChecker)
      .thenApply(AnalysisRpcServiceDelegate::generateAnalyzeFilesResponse), configurationScopeId);
  }

  @Override
  public void didSetUserAnalysisProperties(DidChangeAnalysisPropertiesParams params) {
    notify(() -> getBean(AnalysisService.class).setUserAnalysisProperties(params.getConfigurationScopeId(), params.getProperties()));
  }

  @Override
  public void didChangePathToCompileCommands(DidChangePathToCompileCommandsParams params) {
    notify(() -> getBean(AnalysisService.class).didChangePathToCompileCommands(params.getConfigurationScopeId(), params.getPathToCompileCommands()));
  }

  @Override
  public void didChangeAutomaticAnalysisSetting(DidChangeAutomaticAnalysisSettingParams params) {
    notify(() -> getBean(AnalysisService.class).didChangeAutomaticAnalysisSetting(params.isEnabled()));
  }

  @Override
  public CompletableFuture<ForceAnalyzeResponse> analyzeFullProject(AnalyzeFullProjectParams params) {
    return requestAsync(
      cancelChecker -> new ForceAnalyzeResponse(getBean(AnalysisService.class)
        .analyzeFullProject(params.getConfigScopeId(), params.isHotspotsOnly())));
  }

  @Override
  public CompletableFuture<ForceAnalyzeResponse> analyzeFileList(AnalyzeFileListParams params) {
    return requestAsync(
      cancelChecker -> new ForceAnalyzeResponse(getBean(AnalysisService.class)
        .analyzeFileList(params.getConfigScopeId(), params.getFilesToAnalyze())));
  }

  @Override
  public CompletableFuture<ForceAnalyzeResponse> analyzeOpenFiles(AnalyzeOpenFilesParams params) {
    return requestAsync(
      cancelChecker -> new ForceAnalyzeResponse(getBean(AnalysisService.class).forceAnalyzeOpenFiles(params.getConfigScopeId())));
  }

  @Override
  public CompletableFuture<ForceAnalyzeResponse> analyzeVCSChangedFiles(AnalyzeVCSChangedFilesParams params) {
    return requestAsync(
      cancelChecker -> new ForceAnalyzeResponse(getBean(AnalysisService.class).analyzeVCSChangedFiles(params.getConfigScopeId())));
  }

  @Override
  public CompletableFuture<ShouldUseEnterpriseCSharpAnalyzerResponse> shouldUseEnterpriseCSharpAnalyzer(ShouldUseEnterpriseCSharpAnalyzerParams params) {
    return requestAsync(
      cancelChecker -> new ShouldUseEnterpriseCSharpAnalyzerResponse(getBean(AnalysisService.class)
        .shouldUseEnterpriseCSharpAnalyzer(params.getConfigurationScopeId())));
  }

  private static AnalyzeFilesResponse generateAnalyzeFilesResponse(AnalysisResult analysisResults) {
    return new AnalyzeFilesResponse(analysisResults.failedAnalysisFiles(), analysisResults.rawIssues().stream().map(AnalysisRpcServiceDelegate::toDto).toList());
  }

  static RawIssueDto toDto(RawIssue issue) {
    var range = issue.getTextRange();
    var textRange = range != null ? adapt(range) : null;
    var fileUri = issue.getFileUri();
    var flows = issue.getFlows().stream().map(flow -> {
      var locations = flow.locations().stream().map(location -> {
        var locationTextRange = location.getTextRange();
        var locationTextRangeDto = locationTextRange == null ? null : adapt(locationTextRange);
        var locationInputFile = location.getInputFile();
        var locationFileUri = locationInputFile == null ? null : locationInputFile.uri();
        return new RawIssueLocationDto(locationTextRangeDto, location.getMessage(), locationFileUri);
      }).toList();
      return new RawIssueFlowDto(locations);
    }).toList();
    return new RawIssueDto(
      RuleDetailsAdapter.adapt(issue.getSeverity()),
      RuleDetailsAdapter.adapt(issue.getRuleType()),
      RuleDetailsAdapter.adapt(issue.getCleanCodeAttribute()),
      issue.getImpacts().entrySet().stream().map(entry -> Map.entry(SoftwareQuality.valueOf(entry.getKey().name()), ImpactSeverity.valueOf(entry.getValue().name())))
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)),
      issue.getRuleKey(),
      requireNonNull(issue.getMessage()),
      fileUri,
      flows,
      issue.getQuickFixes().stream()
        .map(quickFix -> new QuickFixDto(
          quickFix.inputFileEdits().stream()
            .map(fileEdit -> new FileEditDto(fileEdit.target().uri(),
              fileEdit.textEdits().stream().map(textEdit -> new TextEditDto(adapt(textEdit.range()), textEdit.newText())).toList()))
            .toList(),
          quickFix.message()))
        .toList(),
      textRange,
      issue.getRuleDescriptionContextKey(),
      RuleDetailsAdapter.adapt(issue.getVulnerabilityProbability()));
  }

  private static TextRangeDto adapt(TextRange textRange) {
    return new TextRangeDto(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(), textRange.getEndLineOffset());
  }
}
