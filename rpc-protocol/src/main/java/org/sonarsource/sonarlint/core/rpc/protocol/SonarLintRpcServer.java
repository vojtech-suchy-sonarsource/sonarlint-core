/*
ACR-7f54ac3f528145aa8c05205c78ee642f
ACR-ef0fc480aa004a17be6b4938f3cbc1f0
ACR-a3d1201902a7434a906ccb1990d47978
ACR-b54f18923a894f8496a357889e4b3bdc
ACR-79ee8c24f2f44106a52a94aa8ea77e11
ACR-ef553d49ad254d528959da61cc20eece
ACR-4bde3f65bbb14afa99ef02c81e0eb6ef
ACR-1b14e3a3c5e94050a189c57f39f6f82e
ACR-bfeed9bcee4545fd93250ff5761fce84
ACR-10ae4624cd7c4e2f871a320fcf0007e3
ACR-097be42683c84221891e5e31df823ad0
ACR-408b91042d6848d18d9ff90c0025f9a2
ACR-0ecc1dca94c24417bb5f103daece6293
ACR-df5e1c316d6b4321b216fb62ee5132eb
ACR-54dd04c445dd41d1a1a7bd0c4d5f108a
ACR-5d53d8bba39549a18c0be7bd62f40b17
ACR-d70156db897b4b8880604e6a834ebb07
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

  /*ACR-70231b798345408485d42b3854ed136c
ACR-3e2f4de13c1044ae885845709b99e9ba
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
