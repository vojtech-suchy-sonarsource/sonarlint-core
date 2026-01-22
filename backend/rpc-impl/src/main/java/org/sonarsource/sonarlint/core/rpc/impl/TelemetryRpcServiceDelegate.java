/*
ACR-65ee1ffbff934d69bbb667c43a7258a1
ACR-c45f00dcfda94a2eaed586e35cd030a4
ACR-fba1bdc58e0342b48aaaeef9af9e4fb7
ACR-3445c08d96844c4cba2848f76f34a5b1
ACR-2935d9ad919040708e04f7c68f61780b
ACR-d096ef6a865d432b9ee6418bce800d22
ACR-30fe2f9d24714150a3560714706a71b5
ACR-0a6f32477bcd47e9ad5e935c867ce98f
ACR-84cd0d8db92f4a2d99683c6ba9b34347
ACR-202aa952e17143ec8a3caa7c608ccae2
ACR-eb095dda10424f6e8aefa1e6606aafaa
ACR-fe4ca618665d4d1bb2fdf9adecae774f
ACR-3a9f26914a9740039dc94b9eb355b2ca
ACR-d8e13bd670b34abf87167bff4ac93bb0
ACR-9a83134aed504f29b8a7b96cfd48de53
ACR-2c0b945239144f5085dedfd63b74eccd
ACR-fc1183443bb848af91c117f360a2c6e5
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
