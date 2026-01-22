/*
ACR-955cf55ba80340f7a8d23341a3a5e67a
ACR-fda642cec1c0471bb9bf9f294151077a
ACR-b9e1b92e90a546f989460f0716a919ef
ACR-a16f4604caa9489ca0bacb987883d502
ACR-7ec31839d5b64591a1f4acf44ce76e76
ACR-d80f29d90c664f82b81cabf16cfdf532
ACR-7051e80915444a5095af5f452b636a26
ACR-2d17f59766f940c99764336f9bc88582
ACR-82c79caeda6d412bb4d22999d4453a59
ACR-7917d2ac32f74e4383c25dfe0d55243e
ACR-2e6a089a3260446a92d6834f120c5e24
ACR-7d794bb7c771459395ce0a1e07a39324
ACR-010f245caca14e7e80d65dc0c8812cf3
ACR-991fb6ad2f494d40a1fbd97beac3d6c3
ACR-1e1c6aae352e456fa22871a85ad31a21
ACR-edf490dd1d32426fb83507549dd23c87
ACR-6b85d852a90b4093b054e9b3ed65f3e8
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonDelegate;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgentRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalysisRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.BindingRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.SonarProjectBranchRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.ConfigurationRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.ConnectionRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.dogfooding.DogfoodingRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.FileRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.flightrecorder.FlightRecordingRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.IssueRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.labs.IdeLabsRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.LogRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode.NewCodeRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.progress.TaskProgressRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.AiCodeFixRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RulesRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.DependencyRiskRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.telemetry.TelemetryRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityTrackingRpcService;

public interface SonarLintRpcServer {

  /*ACR-05c0578e76da4e0f87d64024f20e6cba
ACR-685fd6c828b04d748ea27b1b2c84af14
   */
  @JsonRequest
  CompletableFuture<Void> initialize(InitializeParams params);

  @JsonDelegate
  ConnectionRpcService getConnectionService();

  @JsonDelegate
  ConfigurationRpcService getConfigurationService();

  @JsonDelegate
  FileRpcService getFileService();

  @JsonDelegate
  RulesRpcService getRulesService();

  @JsonDelegate
  BindingRpcService getBindingService();

  @JsonDelegate
  HotspotRpcService getHotspotService();

  @JsonDelegate
  TelemetryRpcService getTelemetryService();

  @JsonDelegate
  AnalysisRpcService getAnalysisService();

  @JsonDelegate
  SonarProjectBranchRpcService getSonarProjectBranchService();

  @JsonDelegate
  IssueRpcService getIssueService();

  @JsonDelegate
  NewCodeRpcService getNewCodeService();

  @JsonDelegate
  TaintVulnerabilityTrackingRpcService getTaintVulnerabilityTrackingService();

  @JsonDelegate
  DogfoodingRpcService getDogfoodingService();

  @JsonDelegate
  AiCodeFixRpcService getAiCodeFixRpcService();

  @JsonDelegate
  TaskProgressRpcService getTaskProgressRpcService();

  @JsonDelegate
  DependencyRiskRpcService getDependencyRiskService();

  @JsonDelegate
  FlightRecordingRpcService getFlightRecordingService();

  @JsonDelegate
  AiAgentRpcService getAiAgentService();

  @JsonDelegate
  LogRpcService getLogService();

  @JsonDelegate
  IdeLabsRpcService getIdeLabsService();

  @JsonRequest
  CompletableFuture<Void> shutdown();

}
