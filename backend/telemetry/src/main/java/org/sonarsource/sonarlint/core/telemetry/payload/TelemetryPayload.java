/*
ACR-c05f8432521d412fbe973c9757271594
ACR-8befceec59164e02892f21f14606ed6c
ACR-91954e6f8a3347a2a5d8195b43c7331e
ACR-1c882e8b07df403885c75118bfbd2fab
ACR-dd4e49edd9e849df86bf9248fdf19fa9
ACR-67186d3479dd49798f72d8bed11a04df
ACR-c8b982da518d4c59bbfe60036349ef23
ACR-df7eac38636b459987bba73bf020f99d
ACR-c8f4f350b184427bb54d3686f0748bf1
ACR-88c550eda80b462b84c8372ac13f0583
ACR-3d2fe676233847e1813b025fc2de245d
ACR-9e4f0db05da24601a4207529f79e4c33
ACR-1efae134e22944ed895ebdd0736809c5
ACR-dc5334158b0a4b72840706bd98fb64c9
ACR-aeda229062c84246bac78efc901b5d18
ACR-49911537f1bf4cb2ae25d3d1021c2f78
ACR-a4977e42c9884cfcaf7869410670a6fb
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

/*ACR-67852b792e7840deadab8232b4d7561c
ACR-a3923381d6dc446383977e7b9d3a1e42
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
        //ACR-51b14cccf64a4552b67df04cbbf9e3eb
        target.add(entry.getKey(), value);
      } else if (value.isJsonObject()) {
        //ACR-9c83d26d356b4511941f0d7715ea138c
        var valueJson = (JsonObject) value;
        mergeObjects(valueJson, target.getAsJsonObject(entry.getKey()));
      }
      //ACR-5a8287d3da1a4d0db9a0c0e17a8669ca
    }
    return target;
  }

}
