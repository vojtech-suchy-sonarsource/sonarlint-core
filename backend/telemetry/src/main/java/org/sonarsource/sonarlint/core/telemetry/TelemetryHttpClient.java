/*
ACR-762fb4cd5d6f456c8d674ca3913b828b
ACR-d4133d0035024733b60d4da13f1c229b
ACR-ec48b566c79e4ff99b8c0d23e7662e8c
ACR-ade4f7e2cf6c4426a0819536a2ca6539
ACR-a803f7814a804a18885e94240ccd690c
ACR-c71074afbd6c432d9c16b2f7756a1037
ACR-1fc18cd4309a4532aa2fe6d04768becb
ACR-315994d41b5e4c688837cc0ed3ab4c65
ACR-0366c377a00d44ffbe2a833eeb34bcc5
ACR-a7805a1ec34c4a808c657bf6e6fc1a76
ACR-d72ea70e30b84956ba3bd3967c317d9b
ACR-1b7e949a4e954b9793622d70b05363c4
ACR-bab57708a7174df9abdd777bff8440e2
ACR-6312f123179745be8d0008fe73ff171d
ACR-e9a88b049bf343bfb1380a0f9d1650cc
ACR-63c21cc3a8c54ffdbd4629f0dad23e87
ACR-cf280baf910d4cdd992f883abb7ba6f7
 */
package org.sonarsource.sonarlint.core.telemetry;

import com.google.common.annotations.VisibleForTesting;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.SystemUtils;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;
import org.sonarsource.sonarlint.core.telemetry.measures.payload.TelemetryMeasuresBuilder;
import org.sonarsource.sonarlint.core.telemetry.measures.payload.TelemetryMeasuresPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.HotspotPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.IssuePayload;
import org.sonarsource.sonarlint.core.telemetry.payload.ShareConnectedModePayload;
import org.sonarsource.sonarlint.core.telemetry.payload.ShowHotspotPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.ShowIssuePayload;
import org.sonarsource.sonarlint.core.telemetry.payload.TaintVulnerabilitiesPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.TelemetryHelpAndFeedbackPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.TelemetryPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.TelemetryRulesPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.cayc.CleanAsYouCodePayload;
import org.sonarsource.sonarlint.core.telemetry.payload.cayc.NewCodeFocusPayload;
import org.springframework.beans.factory.annotation.Qualifier;

public class TelemetryHttpClient {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final String product;
  private final String version;
  private final String ideVersion;
  private final String platform;
  private final String architecture;
  private final HttpClient client;
  private final String endpoint;
  private final Map<String, Object> additionalAttributes;

  public TelemetryHttpClient(InitializeParams initializeParams, HttpClientProvider httpClientProvider, @Qualifier("telemetryEndpoint") String telemetryEndpoint) {
    TelemetryClientConstantAttributesDto attributes = initializeParams.getTelemetryConstantAttributes();
    this.product = attributes.getProductName();
    this.version = attributes.getProductVersion();
    this.ideVersion = attributes.getIdeVersion();
    this.platform = SystemUtils.OS_NAME;
    this.architecture = SystemUtils.OS_ARCH;
    this.client = httpClientProvider.getHttpClient();
    this.endpoint = telemetryEndpoint;
    this.additionalAttributes = attributes.getAdditionalAttributes();
  }

  void upload(TelemetryLocalStorage data, TelemetryLiveAttributes telemetryLiveAttributes) {
    try {
      sendPost(createPayload(data, telemetryLiveAttributes));
    } catch (Throwable catchEmAll) {
      if (InternalDebug.isEnabled()) {
        LOG.error("Failed to upload telemetry data", catchEmAll);
      }
    }
    try {
      sendMetricsPostIfNeeded(new TelemetryMeasuresBuilder(platform, product, data, telemetryLiveAttributes).build());
    } catch (Throwable catchEmAll) {
      if (InternalDebug.isEnabled()) {
        LOG.error("Failed to upload telemetry metrics data", catchEmAll);
      }
    }
  }

  void optOut(TelemetryLocalStorage data, TelemetryLiveAttributes telemetryLiveAttributes) {
    try {
      sendDelete(createPayload(data, telemetryLiveAttributes));
    } catch (Throwable catchEmAll) {
      if (InternalDebug.isEnabled()) {
        LOG.error("Failed to upload telemetry opt-out", catchEmAll);
      }
    }
  }

  private TelemetryPayload createPayload(TelemetryLocalStorage data, TelemetryLiveAttributes telemetryLiveAttrs) {
    var systemTime = OffsetDateTime.now();
    var daysSinceInstallation = data.installTime().until(systemTime, ChronoUnit.DAYS);
    var analyzers = TelemetryUtils.toPayload(data.analyzers());
    var notifications = TelemetryUtils.toPayload(telemetryLiveAttrs.isDevNotificationsDisabled(), data.notifications());
    var showHotspotPayload = new ShowHotspotPayload(data.showHotspotRequestsCount());
    var showIssuePayload = new ShowIssuePayload(data.getShowIssueRequestsCount());
    var hotspotPayload = new HotspotPayload(data.openHotspotInBrowserCount(), data.hotspotStatusChangedCount());
    var taintVulnerabilitiesPayload = new TaintVulnerabilitiesPayload(data.taintVulnerabilitiesInvestigatedLocallyCount(),
      data.taintVulnerabilitiesInvestigatedRemotelyCount());
    var issuePayload = new IssuePayload(data.issueStatusChangedRuleKeys(), data.issueStatusChangedCount());
    var jre = System.getProperty("java.version");
    var telemetryRulesPayload = new TelemetryRulesPayload(telemetryLiveAttrs.getNonDefaultEnabledRules(),
      telemetryLiveAttrs.getDefaultDisabledRules(), data.getRaisedIssuesRules(), data.getQuickFixesApplied());
    var helpAndFeedbackPayload = new TelemetryHelpAndFeedbackPayload(data.getHelpAndFeedbackLinkClickedCounter());
    var fixSuggestionPayload = TelemetryUtils.toFixSuggestionResolvedPayload(
      data.getFixSuggestionReceivedCounter(),
      data.getFixSuggestionResolved()
    );
    var countIssuesWithPossibleAiFixFromIde = data.getCountIssuesWithPossibleAiFixFromIde();
    var cleanAsYouCodePayload = new CleanAsYouCodePayload(new NewCodeFocusPayload(data.isFocusOnNewCode(), data.getCodeFocusChangedCount()));

    ShareConnectedModePayload shareConnectedModePayload;
    if (telemetryLiveAttrs.usesConnectedMode()) {
      shareConnectedModePayload = new ShareConnectedModePayload(data.getManualAddedBindingsCount(), data.getImportedAddedBindingsCount(),
        data.getAutoAddedBindingsCount(), data.getExportedConnectedModeCount());
    } else {
      shareConnectedModePayload = new ShareConnectedModePayload(null, null, null, null);
    }

    var mergedAdditionalAttributes = new HashMap<>(telemetryLiveAttrs.getAdditionalAttributes());
    mergedAdditionalAttributes.putAll(additionalAttributes);

    return new TelemetryPayload(daysSinceInstallation, data.numUseDays(), product, version, ideVersion, platform, architecture,
      telemetryLiveAttrs.usesConnectedMode(), telemetryLiveAttrs.usesSonarCloud(), systemTime, data.installTime(), platform, jre,
      telemetryLiveAttrs.getNodeVersion(), analyzers, notifications, showHotspotPayload, showIssuePayload,
      taintVulnerabilitiesPayload, telemetryRulesPayload, hotspotPayload, issuePayload, helpAndFeedbackPayload,
      fixSuggestionPayload, countIssuesWithPossibleAiFixFromIde, cleanAsYouCodePayload, shareConnectedModePayload, mergedAdditionalAttributes);
  }

  private void sendPost(TelemetryPayload payload) {
    logTelemetryPayload(payload);
    var responseCompletableFuture = client.postAsync(endpoint, HttpClient.JSON_CONTENT_TYPE, payload.toJson());
    handleTelemetryResponse(responseCompletableFuture, "data");
  }

  private void sendMetricsPostIfNeeded(TelemetryMeasuresPayload payload) {
    if (!payload.hasMetrics()) {
      //ACR-dfe86c2dde7c45e2aebe2729c7233036
      if (isTelemetryLogEnabled()) {
        LOG.info("Not sending empty telemetry metrics payload.");
      }
      return;
    }

    logTelemetryMetricsPayload(payload);
    var responseCompletableFuture = client.postAsync(endpoint + "/metrics", HttpClient.JSON_CONTENT_TYPE, payload.toJson());
    handleTelemetryResponse(responseCompletableFuture, "data");
  }

  private void logTelemetryPayload(TelemetryPayload payload) {
    if (isTelemetryLogEnabled()) {
      LOG.info("Sending telemetry payload.");
      LOG.info(payload.toJson());
    }
  }

  private void logTelemetryMetricsPayload(TelemetryMeasuresPayload payload) {
    if (isTelemetryLogEnabled()) {
      LOG.info("Sending telemetry metrics payload.");
      LOG.info(payload.toJson());
    }
  }

  private void sendDelete(TelemetryPayload payload) {
    var responseCompletableFuture = client.deleteAsync(endpoint, HttpClient.JSON_CONTENT_TYPE, payload.toJson());
    handleTelemetryResponse(responseCompletableFuture, "opt-out");
  }

  private static void handleTelemetryResponse(CompletableFuture<HttpClient.Response> responseCompletableFuture, String uploadType) {
    responseCompletableFuture.thenAccept(response -> {
      if (!response.isSuccessful() && InternalDebug.isEnabled()) {
        LOG.error("Failed to upload telemetry {}: {}", uploadType, response.toString());
      }
    }).exceptionally(exception -> {
      if (InternalDebug.isEnabled()) {
        LOG.error(String.format("Failed to upload telemetry %s", uploadType), exception);
      }
      return null;
    });
  }

  @VisibleForTesting
  boolean isTelemetryLogEnabled(){
    return Boolean.parseBoolean(System.getenv("SONARLINT_TELEMETRY_LOG"));
  }
}
