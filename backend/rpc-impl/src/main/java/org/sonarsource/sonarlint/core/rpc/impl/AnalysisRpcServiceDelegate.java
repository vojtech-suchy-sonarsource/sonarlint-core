/*
ACR-eff1eb70b2a140d6add4294521f8b726
ACR-4467fa60c7e442888a2e59a3bc115406
ACR-24f5de2ed8744c09a9ec01cd489abb78
ACR-e6dbeef9976f469ca6ee0f2457296fc0
ACR-ab1fd1d2cdb747fa82d5e6f0d310a6f0
ACR-d0594ef063bd4d7da81bdf85e2f8f244
ACR-fc4b5104757f42bf9c69b69937150f50
ACR-844d7ce1d7264bcab0cdeb90690ce792
ACR-90b4fcf3ad29436da4a5fe33c6aca930
ACR-29b9932bf7f74f0a96a3502c562e51ae
ACR-8ca2f64c20bb4b7fb61d000e34ebba76
ACR-ad29d4e52801488c9407b35d34afc15e
ACR-489034ed19d2479bb27a43a114ed4966
ACR-fef6fd10f70b4de89410c9acce743f42
ACR-001348bf1b9d4b7ab8ccbfa542ebaa13
ACR-60924b75fb154a70b8eb6b93b92b4e13
ACR-20696736b571408796377f14f25a7e9e
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
