/*
ACR-178f283c3bf74700845ddc54bb581fa8
ACR-91015e1d65a3472fb53860d21921e82d
ACR-bc5457b32756457484efda443f2bb141
ACR-36c1f923a62046c39984a8cc3b6f5580
ACR-db90ffb583d049a98cfca52d9bbfd57e
ACR-428fbf3555534ea8aed12696c8f44100
ACR-22db5ba6e7d242dfbc2d9303f944a158
ACR-42fb768e685e48098fdd22f925f64d5a
ACR-7a2609e594a741318feffe4766b0874d
ACR-c44c40bb393c4455bfae15b290c861d1
ACR-00a4fd7cd9744787853fef912fa90742
ACR-f04d8e4be9bf4113a9111d3fdbac834e
ACR-cffc0439522d45e0b03d058a8b576482
ACR-6f2b572173a0410389e1997ef4574fca
ACR-2585198f1a9e4488aa63c34b4399390f
ACR-a0ec2b60290f499c816205badce4b821
ACR-b8e37339c8574cc3ab8d52557a94eb73
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.telemetry;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AcceptedBindingSuggestionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AddQuickFixAppliedForRuleParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AddReportedRulesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AnalysisDoneOnSingleLanguageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AnalysisReportingTriggeredParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.DevNotificationsClickedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FindingsFilteredParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FixSuggestionResolvedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.HelpAndFeedbackClickedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.IdeLabsExternalLinkClickedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.IdeLabsFeedbackLinkClickedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.McpTransportModeUsedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.ToolCalledParams;

@JsonSegment("telemetry")
public interface TelemetryRpcService {

  @JsonRequest
  CompletableFuture<GetStatusResponse> getStatus();

  @JsonNotification
  void enableTelemetry();

  @JsonNotification
  void disableTelemetry();

  /*ACR-ad59a0ef12424d4f8637bb8b19e17ae1
ACR-297e4cb9723842e1bdf48014bc4023d8
ACR-233962cd10664c548ffcfe3f612143cd
   */
  @JsonNotification
  @Deprecated(since = "10.1")
  void analysisDoneOnSingleLanguage(AnalysisDoneOnSingleLanguageParams params);

  /*ACR-b1e1a739810e419cbcf2bb09dbc818a7
ACR-bbabb0b1b43e4bd9bab232fbd7225e62
ACR-df2542cd5d9c43d796b2dd302e8e2b1f
   */
  @JsonNotification
  @Deprecated(since = "10.1")
  void analysisDoneOnMultipleFiles();

  @JsonNotification
  void devNotificationsClicked(DevNotificationsClickedParams params);

  @JsonNotification
  void taintVulnerabilitiesInvestigatedLocally();

  @JsonNotification
  void taintVulnerabilitiesInvestigatedRemotely();

  /*ACR-f6f88bccd1404201aa2b9c463734c511
ACR-389181d4883c4179aea010f1268a6aa4
ACR-cfbbc5004c334e3bb9e30837dc09d718
   */
  @JsonNotification
  @Deprecated(since = "10.1")
  void addReportedRules(AddReportedRulesParams params);

  @JsonNotification
  void addQuickFixAppliedForRule(AddQuickFixAppliedForRuleParams params);

  @JsonNotification
  void helpAndFeedbackLinkClicked(HelpAndFeedbackClickedParams params);

  /*ACR-065402f912e549489861a8ebf5f7bd04
ACR-29a96339f2f84c8eb81bd0a1b84f5165
ACR-f446f12585e846c3b773526390f142b2
   */
  @JsonNotification
  void mcpIntegrationEnabled();

  /*ACR-1eaa97da6e8a45f2845bafd9abf8a4b5
ACR-d8206ca3b6944745a01dbeb8b63d4d0a
ACR-8eda7911377c4526ad78adbe34f9cd08
   */
  @JsonNotification
  void mcpTransportModeUsed(McpTransportModeUsedParams params);

  @JsonNotification
  void toolCalled(ToolCalledParams params);

  /*ACR-42827eafbd254740937a5d5f80b8f506
ACR-55f70ccf24ba403a9969f83ff7e43e05
ACR-350ee71ec686433aa5c142b6643f2c1d
   */
  @JsonNotification
  void analysisReportingTriggered(AnalysisReportingTriggeredParams params);

  @JsonNotification
  void fixSuggestionResolved(FixSuggestionResolvedParams params);

  /*ACR-ccd247a8e622495d9d6002b384a12c3c
ACR-e648397ef2aa4fcea6c1930f09947d0b
   */
  @JsonNotification
  void addedManualBindings();

  /*ACR-33f9bd3c050244e985bb5102f1badd58
ACR-fce0b1835dd14550833f6ea7b001df45
ACR-b811b0fed8c74ebb99329a2d7b28255e
   */
  @JsonNotification
  void acceptedBindingSuggestion(AcceptedBindingSuggestionParams origin);

  /*ACR-b40da2fe7e614703b023f7e26033a3e3
ACR-545a10d74a9d44aa84bd36e92a754b75
ACR-083b7bf15eb5455aa3f8a329cdd1bf27
ACR-d6b6bbbbf42746a48898addb25653929
ACR-d168d4e917f54fcf80ff6ab951a03fe9
ACR-ef278fc8c06b4554a8d404af8ec0ba5b
   */
  @Deprecated(forRemoval = true)
  @JsonNotification
  void addedImportedBindings();

  /*ACR-58028094f5724e2b8ce126b0cffc780f
ACR-569f181077fc4813aed4efa5e6658fd1
ACR-39003d10e5ed4f2bbaae880cda1c41c7
ACR-12812b1cc51a4379a612ea77097693ac
ACR-550a55f58920463d9e00a4cbd260cb24
ACR-1736c5fc91bb435490bac50d3d90bac9
   */
  @Deprecated(forRemoval = true)
  @JsonNotification
  void addedAutomaticBindings();

  @JsonNotification
  void taintInvestigatedLocally();

  @JsonNotification
  void taintInvestigatedRemotely();

  @JsonNotification
  void hotspotInvestigatedLocally();

  @JsonNotification
  void hotspotInvestigatedRemotely();

  @JsonNotification
  void issueInvestigatedLocally();

  @JsonNotification
  void dependencyRiskInvestigatedLocally();

  @JsonNotification
  void findingsFiltered(FindingsFilteredParams params);

  @JsonNotification
  void ideLabsExternalLinkClicked(IdeLabsExternalLinkClickedParams params);

  @JsonNotification
  void ideLabsFeedbackLinkClicked(IdeLabsFeedbackLinkClickedParams params);
}
