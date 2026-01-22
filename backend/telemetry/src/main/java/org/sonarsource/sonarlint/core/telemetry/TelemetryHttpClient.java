/*
ACR-0c67adad43844ca295dd14bbe64a3922
ACR-5146c5dba635418eae77ea782dda3437
ACR-9c3b7d79f244435aace8390c95457d81
ACR-671908cd58564e57a8428b4086cfb322
ACR-7a12dd7e16064de288fb3f79c4c495a4
ACR-998e951d062847048162004bb57348b1
ACR-30dd6ec2b6244db7afbb6cab05a184f0
ACR-8d4df847526f4b90b7e2e0514953d7fe
ACR-7cb83f15452a41f88c9dddcea2752051
ACR-2fbc5363630240299b453e4266742232
ACR-5caab1ab43074c45be67ac30c220b8f2
ACR-5ef66d363a8043d7ae4453d596ba03e6
ACR-89cf7cabca9d4c8bbe8abae04bf5163e
ACR-c2c00ef91efe42ba959aefefa8e898f6
ACR-2354db9aa0da4110a498910c91d70b7f
ACR-58b24ae8f7a94957a64f61762174a35f
ACR-a50134a92f0b4c81b8860cded72704f4
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
      //ACR-a75901bc47f045e6b01aae8c671e8f1f
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
