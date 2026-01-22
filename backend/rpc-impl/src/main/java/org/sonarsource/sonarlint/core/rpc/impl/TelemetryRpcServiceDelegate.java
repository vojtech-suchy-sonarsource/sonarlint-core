/*
ACR-00988f83183c418cba90f1a592d23230
ACR-6d121127a4464fe0a89194aef9613284
ACR-19023d6bdc4a4490a3176b72439d39f4
ACR-66a3d836b08e4378b67642bd3d8af77d
ACR-0bad4689ab12427daf3cbeaf04ebaade
ACR-5bff7246447740da837e07ac918047bb
ACR-aae8f4ee9cfa4167b031d31207a4eb38
ACR-612bf470f3ed4669b9cbbc2b53fd333c
ACR-01611618a6b741f79b967613a3ece1a3
ACR-a1ae22993d0d49caa12f689125291c90
ACR-aa8c9934c1064044b28b8952a3c897db
ACR-c3b83a24d73047e1a8b854719cf06c3a
ACR-a5a2c69bc8ca4cd19a581ef4827a474a
ACR-5af1b1ba4fed493fbe04cd0daaa50171
ACR-b182a4301b3d4aa98d23c2c21de6d8df
ACR-3e5e98bd2e9f4d7bb9242699db86a315
ACR-04e0db5e462e4958858fe8e9afbcd468
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.telemetry.GetStatusResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.telemetry.TelemetryRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AcceptedBindingSuggestionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AddQuickFixAppliedForRuleParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AddReportedRulesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AnalysisDoneOnSingleLanguageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AnalysisReportingTriggeredParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.DevNotificationsClickedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FindingsFilteredParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FixSuggestionResolvedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.IdeLabsExternalLinkClickedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.IdeLabsFeedbackLinkClickedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.HelpAndFeedbackClickedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.McpTransportModeUsedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.ToolCalledParams;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

class TelemetryRpcServiceDelegate extends AbstractRpcServiceDelegate implements TelemetryRpcService {

  public TelemetryRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<GetStatusResponse> getStatus() {
    return requestAsync(cancelMonitor -> getBean(TelemetryService.class).getStatus());
  }

  @Override
  public void enableTelemetry() {
    notify(() -> getBean(TelemetryService.class).enableTelemetry());
  }

  @Override
  public void disableTelemetry() {
    notify(() -> getBean(TelemetryService.class).disableTelemetry());
  }

  @Override
  public void analysisDoneOnSingleLanguage(AnalysisDoneOnSingleLanguageParams params) {
    notify(() -> getBean(TelemetryService.class).analysisDoneOnSingleLanguage(params.getLanguage(), params.getAnalysisTimeMs()));
  }

  @Override
  public void analysisDoneOnMultipleFiles() {
    notify(() -> getBean(TelemetryService.class).analysisDoneOnMultipleFiles());
  }

  @Override
  public void devNotificationsClicked(DevNotificationsClickedParams params) {
    notify(() -> getBean(TelemetryService.class).smartNotificationsClicked(params.getEventType()));
  }

  @Override
  public void taintVulnerabilitiesInvestigatedLocally() {
    notify(() -> getBean(TelemetryService.class).taintVulnerabilitiesInvestigatedLocally());
  }

  @Override
  public void taintVulnerabilitiesInvestigatedRemotely() {
    notify(() -> getBean(TelemetryService.class).taintVulnerabilitiesInvestigatedRemotely());
  }

  @Override
  public void addReportedRules(AddReportedRulesParams params) {
    notify(() -> getBean(TelemetryService.class).addReportedRules(params.getRuleKeys()));
  }

  @Override
  public void addQuickFixAppliedForRule(AddQuickFixAppliedForRuleParams params) {
    notify(() -> getBean(TelemetryService.class).addQuickFixAppliedForRule(params.getRuleKey()));
  }

  @Override
  public void helpAndFeedbackLinkClicked(HelpAndFeedbackClickedParams params) {
    notify(() -> getBean(TelemetryService.class).helpAndFeedbackLinkClicked(params));
  }

  @Override
  public void mcpIntegrationEnabled() {
    notify(() -> getBean(TelemetryService.class).mcpIntegrationEnabled());
  }

  @Override
  public void mcpTransportModeUsed(McpTransportModeUsedParams params) {
    notify(() -> getBean(TelemetryService.class).mcpTransportModeUsed(params.getMcpTransportMode()));
  }

  @Override
  public void toolCalled(ToolCalledParams params) {
    notify(() -> getBean(TelemetryService.class).toolCalled(params));
  }

  @Override
  public void analysisReportingTriggered(AnalysisReportingTriggeredParams params) {
    notify(() -> getBean(TelemetryService.class).analysisReportingTriggered(params));
  }

  @Override
  public void fixSuggestionResolved(FixSuggestionResolvedParams params) {
    notify(() -> getBean(TelemetryService.class).fixSuggestionResolved(params));
  }

  @Override
  public void addedManualBindings() {
    notify(() -> getBean(TelemetryService.class).addedManualBindings());
  }

  @Override
  public void acceptedBindingSuggestion(AcceptedBindingSuggestionParams params) {
    notify(() -> getBean(TelemetryService.class).acceptedBindingSuggestion(params.getOrigin()));
  }

  @Override
  public void addedImportedBindings() {
    notify(() -> getBean(TelemetryService.class).addedImportedBindings());
  }

  @Override
  public void addedAutomaticBindings() {
    notify(() -> getBean(TelemetryService.class).addedAutomaticBindings());
  }

  @Override
  public void taintInvestigatedLocally() {
    notify(() -> getBean(TelemetryService.class).taintInvestigatedLocally());
  }

  @Override
  public void taintInvestigatedRemotely() {
    notify(() -> getBean(TelemetryService.class).taintInvestigatedRemotely());
  }

  @Override
  public void hotspotInvestigatedLocally() {
    notify(() -> getBean(TelemetryService.class).hotspotInvestigatedLocally());
  }

  @Override
  public void hotspotInvestigatedRemotely() {
    notify(() -> getBean(TelemetryService.class).hotspotInvestigatedRemotely());
  }

  @Override
  public void issueInvestigatedLocally() {
    notify(() -> getBean(TelemetryService.class).issueInvestigatedLocally());
  }

  @Override
  public void dependencyRiskInvestigatedLocally() {
    notify(() -> getBean(TelemetryService.class).dependencyRiskInvestigatedLocally());
  }

  @Override
  public void findingsFiltered(FindingsFilteredParams params) {
    notify(() -> getBean(TelemetryService.class).findingsFiltered(params.getFilterType()));
  }

  @Override
  public void ideLabsExternalLinkClicked(IdeLabsExternalLinkClickedParams params) {
    notify(() -> getBean(TelemetryService.class).ideLabsLinkClicked(params.getLinkId()));
  }

  @Override
  public void ideLabsFeedbackLinkClicked(IdeLabsFeedbackLinkClickedParams params) {
    notify(() -> getBean(TelemetryService.class).ideLabsFeedbackLinkClicked(params.getFeatureId()));
  }
}
