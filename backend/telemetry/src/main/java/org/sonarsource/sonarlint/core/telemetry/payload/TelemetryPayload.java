/*
ACR-8681998e5574452c96664357358de2e4
ACR-af26af1b552f42e0a3abd7bd4ae18412
ACR-a023768f7c67402ca861a32893dbddad
ACR-dfaf4530c36d499890e7a618839c74c3
ACR-d867098b6caa41479d1ed2e915f22bdb
ACR-b35a539ab0894bd6b034b6ab170d627a
ACR-5db58b03a44b4f7ca4985461636247ff
ACR-66d82b381d1747ba9ca2b4d51391bbc7
ACR-c8b9e53b266f481a9ab8b72f71c331da
ACR-f46bc4df967b48d1a12349a11ffb0dae
ACR-51d20f2eb9f94632b24b3532f0fa8610
ACR-92dab8976d8d4843a35aa4809221ff8c
ACR-1563eff5ac67457ba1ba2d0b9fe8b9b2
ACR-5c356fd6cdaf49468e39db9af64581bd
ACR-d64cd67758494e03bcd5ff972c64c9ed
ACR-9c9e136efeb84ba7a00868b740728ba8
ACR-99f1f4f078644135aa2df829e05c9f43
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.storage.adapter.OffsetDateTimeAdapter;
import org.sonarsource.sonarlint.core.telemetry.payload.cayc.CleanAsYouCodePayload;

/*ACR-d9cdc2d8e50b4bc6887220538a9dce86
ACR-0ac6b03926b84de3be5648f7ab89e406
 */
public class TelemetryPayload {
  @SerializedName("days_since_installation")
  private final long daysSinceInstallation;

  @SerializedName("days_of_use")
  private final long daysOfUse;

  @SerializedName("sonarlint_version")
  private final String version;

  @SerializedName("sonarlint_product")
  private final String product;

  @SerializedName("ide_version")
  private final String ideVersion;

  @SerializedName("platform")
  private final String platform;

  @SerializedName("architecture")
  private final String architecture;

  @SerializedName("connected_mode_used")
  private final boolean connectedMode;

  @SerializedName("connected_mode_sonarcloud")
  private final boolean connectedModeSonarcloud;

  @SerializedName("system_time")
  private final OffsetDateTime systemTime;

  @SerializedName("install_time")
  private final OffsetDateTime installTime;

  @SerializedName("os")
  private final String os;

  @SerializedName("jre")
  private final String jre;

  @SerializedName("nodejs")
  private final String nodejs;

  @SerializedName("analyses")
  private final TelemetryAnalyzerPerformancePayload[] analyses;

  @SerializedName("server_notifications")
  private final TelemetryNotificationsPayload notifications;

  @SerializedName("show_hotspot")
  private final ShowHotspotPayload showHotspotPayload;

  @SerializedName("show_issue")
  private final ShowIssuePayload showIssuePayload;

  @SerializedName("taint_vulnerabilities")
  private final TaintVulnerabilitiesPayload taintVulnerabilitiesPayload;

  @SerializedName("rules")
  private final TelemetryRulesPayload telemetryRulesPayload;

  @SerializedName("hotspot")
  private final HotspotPayload hotspotPayload;

  @SerializedName("issue")
  private final IssuePayload issuePayload;

  @SerializedName("help_and_feedback")
  private final TelemetryHelpAndFeedbackPayload helpAndFeedbackPayload;

  @SerializedName("ai_fix_suggestions")
  private final TelemetryFixSuggestionPayload[] aiFixSuggestionsPayload;

  @SerializedName("count_issues_with_possible_ai_fix_from_ide")
  private final int countIssuesWithPossibleAiFixFromIde;

  @SerializedName("cayc")
  private final CleanAsYouCodePayload cleanAsYouCodePayload;

  @SerializedName("shared_connected_mode")
  private final ShareConnectedModePayload shareConnectedModePayload;

  private final transient Map<String, Object> additionalAttributes;

  public TelemetryPayload(long daysSinceInstallation, long daysOfUse, String product, String version, String ideVersion, @Nullable String platform, @Nullable String architecture,
    boolean connectedMode, boolean connectedModeSonarcloud, OffsetDateTime systemTime, OffsetDateTime installTime, String os, String jre, @Nullable String nodejs,
    TelemetryAnalyzerPerformancePayload[] analyses, TelemetryNotificationsPayload notifications, ShowHotspotPayload showHotspotPayload,
    ShowIssuePayload showIssuePayload, TaintVulnerabilitiesPayload taintVulnerabilitiesPayload, TelemetryRulesPayload telemetryRulesPayload, HotspotPayload hotspotPayload,
    IssuePayload issuePayload, TelemetryHelpAndFeedbackPayload helpAndFeedbackPayload, TelemetryFixSuggestionPayload[] aiFixSuggestionsPayload,
    int countIssuesWithPossibleAiFixFromIde, CleanAsYouCodePayload cleanAsYouCodePayload, ShareConnectedModePayload shareConnectedModePayload,
    Map<String, Object> additionalAttributes) {
    this.daysSinceInstallation = daysSinceInstallation;
    this.daysOfUse = daysOfUse;
    this.product = product;
    this.version = version;
    this.ideVersion = ideVersion;
    this.platform = platform;
    this.architecture = architecture;
    this.connectedMode = connectedMode;
    this.connectedModeSonarcloud = connectedModeSonarcloud;
    this.systemTime = systemTime;
    this.installTime = installTime;
    this.os = os;
    this.jre = jre;
    this.nodejs = nodejs;
    this.analyses = analyses;
    this.notifications = notifications;
    this.showHotspotPayload = showHotspotPayload;
    this.showIssuePayload = showIssuePayload;
    this.taintVulnerabilitiesPayload = taintVulnerabilitiesPayload;
    this.telemetryRulesPayload = telemetryRulesPayload;
    this.hotspotPayload = hotspotPayload;
    this.issuePayload = issuePayload;
    this.helpAndFeedbackPayload = helpAndFeedbackPayload;
    this.aiFixSuggestionsPayload = aiFixSuggestionsPayload;
    this.countIssuesWithPossibleAiFixFromIde = countIssuesWithPossibleAiFixFromIde;
    this.cleanAsYouCodePayload = cleanAsYouCodePayload;
    this.shareConnectedModePayload = shareConnectedModePayload;
    this.additionalAttributes = additionalAttributes;
  }

  public long daysSinceInstallation() {
    return daysSinceInstallation;
  }

  public long daysOfUse() {
    return daysOfUse;
  }

  public TelemetryAnalyzerPerformancePayload[] analyses() {
    return analyses;
  }

  public String version() {
    return version;
  }

  public String product() {
    return product;
  }

  public boolean connectedMode() {
    return connectedMode;
  }

  public boolean connectedModeSonarcloud() {
    return connectedModeSonarcloud;
  }

  public String os() {
    return os;
  }

  public String jre() {
    return jre;
  }

  public String nodejs() {
    return nodejs;
  }

  public OffsetDateTime systemTime() {
    return systemTime;
  }

  public TelemetryNotificationsPayload notifications() {
    return notifications;
  }

  public TelemetryHelpAndFeedbackPayload helpAndFeedbackPayload() {
    return helpAndFeedbackPayload;
  }

  public CleanAsYouCodePayload cleanAsYouCodePayload() {
    return cleanAsYouCodePayload;
  }

  public IssuePayload issuePayload() {
    return issuePayload;
  }

  public Map<String, Object> additionalAttributes() {
    return additionalAttributes;
  }

  public ShowHotspotPayload getShowHotspotPayload() {
    return showHotspotPayload;
  }

  public ShowIssuePayload getShowIssuePayload() {
    return showIssuePayload;
  }

  public TaintVulnerabilitiesPayload getTaintVulnerabilitiesPayload() {
    return taintVulnerabilitiesPayload;
  }

  public TelemetryRulesPayload getTelemetryRulesPayload() {
    return telemetryRulesPayload;
  }

  public HotspotPayload getHotspotPayload() {
    return hotspotPayload;
  }

  public ShareConnectedModePayload getShareConnectedModePayload() {
    return shareConnectedModePayload;
  }

  public TelemetryFixSuggestionPayload[] getAiFixSuggestionsPayload() {
    return aiFixSuggestionsPayload;
  }

  public String getIdeVersion() {
    return ideVersion;
  }

  public String getPlatform() {
    return platform;
  }

  public String getArchitecture() {
    return architecture;
  }

  public OffsetDateTime getInstallTime() {
    return installTime;
  }

  public int getCountIssuesWithPossibleAiFixFromIde() {
    return countIssuesWithPossibleAiFixFromIde;
  }

  public String toJson() {
    var gson = new GsonBuilder()
      .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
      .serializeNulls()
      .create();
    var jsonPayload = gson.toJsonTree(this).getAsJsonObject();
    var jsonAdditional = gson.toJsonTree(additionalAttributes, new TypeToken<Map<String, Object>>() {
    }.getType()).getAsJsonObject();
    return gson.toJson(mergeObjects(jsonAdditional, jsonPayload));
  }

  static JsonObject mergeObjects(JsonObject source, JsonObject target) {
    for (Entry<String, JsonElement> entry : source.entrySet()) {
      var value = entry.getValue();
      if (!target.has(entry.getKey())) {
        //ACR-999a6b1907ee47f288c5658574f699b5
        target.add(entry.getKey(), value);
      } else if (value.isJsonObject()) {
        //ACR-462a0d33dbdf472c9dda0363597d1fba
        var valueJson = (JsonObject) value;
        mergeObjects(valueJson, target.getAsJsonObject(entry.getKey()));
      }
      //ACR-43f6f92c0bcf4975b061ed9f940d961b
    }
    return target;
  }

}
