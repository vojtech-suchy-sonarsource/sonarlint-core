/*
ACR-8ebc4a3bff79495689cf61eb84edc809
ACR-ea66e83dabe444f3976adb698adf9d55
ACR-ae61ad38a88142b480c964c377422f88
ACR-43eaa9675d4b4387b9c490f6dac67524
ACR-8ece9299dd884e8db5a3a6c62954e2f3
ACR-c0da57471ae14d9488eab6aafd1bce0a
ACR-0b6e44edd09f454489aee339f6e0c4ba
ACR-4dfe191b530d403c887bbaa3c737f744
ACR-9a5f0154ef144e918ab0df244ba96338
ACR-451381bfb52649f4b2eac4349e77dc70
ACR-241a8c24cfc34b108df3f0d164b70cb9
ACR-2fef87ea6224407ba2dc394d09b050d9
ACR-c8ff775db68f40e18d6cb40427a571b0
ACR-0c80e9e505e746f49ae26f2258cf16bc
ACR-cb4fc163a2ed4e9099e9294948130f7c
ACR-69c24f71708c49bbaed9ea187676891a
ACR-31501759e3d1411797ce78d73ba812ab
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

  /*ACR-c3b7488d3c094ae4a3ecbb325b6cda1f
ACR-c87e52c67a9d4be78b06b0b4fc7adfc1
ACR-80752c5f2c2249a2af79c332864474bf
   */
  @JsonNotification
  @Deprecated(since = "10.1")
  void analysisDoneOnSingleLanguage(AnalysisDoneOnSingleLanguageParams params);

  /*ACR-a811fc278df44eda8f58c9b97137a1d7
ACR-d6f5cdf307cb460b9e95c487910918f4
ACR-df1a013264834b5aa8482958409084a5
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

  /*ACR-7d0b586543a84450a18ced8ae26d0564
ACR-eaba0059469f4aa8ba7a3b57b6d81a43
ACR-69b0afe4632042d3aed42a6c5d9e6d27
   */
  @JsonNotification
  @Deprecated(since = "10.1")
  void addReportedRules(AddReportedRulesParams params);

  @JsonNotification
  void addQuickFixAppliedForRule(AddQuickFixAppliedForRuleParams params);

  @JsonNotification
  void helpAndFeedbackLinkClicked(HelpAndFeedbackClickedParams params);

  /*ACR-d78ed7fe21954c37b308eea400a789e3
ACR-9cc86df1fb534cb49046be5686291b2e
ACR-0d975c9279c341eea191e9adc9f9c485
   */
  @JsonNotification
  void mcpIntegrationEnabled();

  /*ACR-fd13a14364e548618273fed614f8f9ef
ACR-bfb3f126fa8140c6a58b09843b9ac63c
ACR-ae0d773ea8ca426daa0324df1d19bb58
   */
  @JsonNotification
  void mcpTransportModeUsed(McpTransportModeUsedParams params);

  @JsonNotification
  void toolCalled(ToolCalledParams params);

  /*ACR-f38b54d430f74f7b9072829db248be6c
ACR-edb9ef5429f94e4fafe5c8aa24a8f2bf
ACR-f3e80e164da54f1ba6840696d04d56a5
   */
  @JsonNotification
  void analysisReportingTriggered(AnalysisReportingTriggeredParams params);

  @JsonNotification
  void fixSuggestionResolved(FixSuggestionResolvedParams params);

  /*ACR-2d0afbe162d64ecc94118415bebd0cb7
ACR-eb15b470953a4094babbd21257b060e3
   */
  @JsonNotification
  void addedManualBindings();

  /*ACR-fa059bfc18694e2ebbb799bafbfe4283
ACR-6f6cb3247d774110b61d8ef12b34e1ef
ACR-eb314f74387b40a1a059a49146f8b17d
   */
  @JsonNotification
  void acceptedBindingSuggestion(AcceptedBindingSuggestionParams origin);

  /*ACR-23abc26160a1429282bbf0a4296f9f63
ACR-3d17bbf79cb34ba59a98a7c814f22bab
ACR-001dac04f4434e059334174c179a70c4
ACR-4b0fce29b2ee4a7891c088200b029b7c
ACR-ff6d930d1d444b4db314852368c8bcc4
ACR-0fce421fe4b44686a2efcaca353aae3b
   */
  @Deprecated(forRemoval = true)
  @JsonNotification
  void addedImportedBindings();

  /*ACR-a48fac69802741259e429b48be10ba4f
ACR-a8bb2797f50b46a3bc1ae43ac84ce8a5
ACR-0b7e55c5f77b47f19e3d093edacf1aaf
ACR-554b5a80d620434c893d9ea9fa6ad262
ACR-087ea352537f49399d2bb4f993574021
ACR-bce8df3861fe4468ac1457639b4165bc
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
