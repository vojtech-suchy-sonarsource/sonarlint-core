/*
 * SonarLint Core - Telemetry
 * Copyright (C) 2016-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

/**
 * Models the usage data uploaded
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

  private TelemetryPayload(Builder builder) {
    this.daysSinceInstallation = builder.daysSinceInstallation;
    this.daysOfUse = builder.daysOfUse;
    this.product = builder.product;
    this.version = builder.version;
    this.ideVersion = builder.ideVersion;
    this.platform = builder.platform;
    this.architecture = builder.architecture;
    this.connectedMode = builder.connectedMode;
    this.connectedModeSonarcloud = builder.connectedModeSonarcloud;
    this.systemTime = builder.systemTime;
    this.installTime = builder.installTime;
    this.os = builder.os;
    this.jre = builder.jre;
    this.nodejs = builder.nodejs;
    this.analyses = builder.analyses;
    this.notifications = builder.notifications;
    this.showHotspotPayload = builder.showHotspotPayload;
    this.showIssuePayload = builder.showIssuePayload;
    this.taintVulnerabilitiesPayload = builder.taintVulnerabilitiesPayload;
    this.telemetryRulesPayload = builder.telemetryRulesPayload;
    this.hotspotPayload = builder.hotspotPayload;
    this.issuePayload = builder.issuePayload;
    this.helpAndFeedbackPayload = builder.helpAndFeedbackPayload;
    this.aiFixSuggestionsPayload = builder.aiFixSuggestionsPayload;
    this.countIssuesWithPossibleAiFixFromIde = builder.countIssuesWithPossibleAiFixFromIde;
    this.cleanAsYouCodePayload = builder.cleanAsYouCodePayload;
    this.shareConnectedModePayload = builder.shareConnectedModePayload;
    this.additionalAttributes = builder.additionalAttributes;
  }

  public static Builder builder() {
    return new Builder();
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
        // new value for "key":
        target.add(entry.getKey(), value);
      } else if (value.isJsonObject()) {
        // existing value for "key" - recursively deep merge:
        var valueJson = (JsonObject) value;
        mergeObjects(valueJson, target.getAsJsonObject(entry.getKey()));
      }
      // Don't override value if it already exists in the target
    }
    return target;
  }

  public static class Builder {
    private long daysSinceInstallation;
    private long daysOfUse;
    private String version;
    private String product;
    private String ideVersion;
    private String platform;
    private String architecture;
    private boolean connectedMode;
    private boolean connectedModeSonarcloud;
    private OffsetDateTime systemTime;
    private OffsetDateTime installTime;
    private String os;
    private String jre;
    private String nodejs;
    private TelemetryAnalyzerPerformancePayload[] analyses;
    private TelemetryNotificationsPayload notifications;
    private ShowHotspotPayload showHotspotPayload;
    private ShowIssuePayload showIssuePayload;
    private TaintVulnerabilitiesPayload taintVulnerabilitiesPayload;
    private TelemetryRulesPayload telemetryRulesPayload;
    private HotspotPayload hotspotPayload;
    private IssuePayload issuePayload;
    private TelemetryHelpAndFeedbackPayload helpAndFeedbackPayload;
    private TelemetryFixSuggestionPayload[] aiFixSuggestionsPayload;
    private int countIssuesWithPossibleAiFixFromIde;
    private CleanAsYouCodePayload cleanAsYouCodePayload;
    private ShareConnectedModePayload shareConnectedModePayload;
    private Map<String, Object> additionalAttributes;

    public Builder setDaysSinceInstallation(long daysSinceInstallation) {
      this.daysSinceInstallation = daysSinceInstallation;
      return this;
    }

    public Builder setDaysOfUse(long daysOfUse) {
      this.daysOfUse = daysOfUse;
      return this;
    }

    public Builder setVersion(String version) {
      this.version = version;
      return this;
    }

    public Builder setProduct(String product) {
      this.product = product;
      return this;
    }

    public Builder setIdeVersion(String ideVersion) {
      this.ideVersion = ideVersion;
      return this;
    }

    public Builder setPlatform(@Nullable String platform) {
      this.platform = platform;
      return this;
    }

    public Builder setArchitecture(@Nullable String architecture) {
      this.architecture = architecture;
      return this;
    }

    public Builder setConnectedMode(boolean connectedMode) {
      this.connectedMode = connectedMode;
      return this;
    }

    public Builder setConnectedModeSonarcloud(boolean connectedModeSonarcloud) {
      this.connectedModeSonarcloud = connectedModeSonarcloud;
      return this;
    }

    public Builder setSystemTime(OffsetDateTime systemTime) {
      this.systemTime = systemTime;
      return this;
    }

    public Builder setInstallTime(OffsetDateTime installTime) {
      this.installTime = installTime;
      return this;
    }

    public Builder setOs(String os) {
      this.os = os;
      return this;
    }

    public Builder setJre(String jre) {
      this.jre = jre;
      return this;
    }

    public Builder setNodejs(@Nullable String nodejs) {
      this.nodejs = nodejs;
      return this;
    }

    public Builder setAnalyses(TelemetryAnalyzerPerformancePayload[] analyses) {
      this.analyses = analyses;
      return this;
    }

    public Builder setNotifications(TelemetryNotificationsPayload notifications) {
      this.notifications = notifications;
      return this;
    }

    public Builder setShowHotspotPayload(ShowHotspotPayload showHotspotPayload) {
      this.showHotspotPayload = showHotspotPayload;
      return this;
    }

    public Builder setShowIssuePayload(ShowIssuePayload showIssuePayload) {
      this.showIssuePayload = showIssuePayload;
      return this;
    }

    public Builder setTaintVulnerabilitiesPayload(TaintVulnerabilitiesPayload taintVulnerabilitiesPayload) {
      this.taintVulnerabilitiesPayload = taintVulnerabilitiesPayload;
      return this;
    }

    public Builder setTelemetryRulesPayload(TelemetryRulesPayload telemetryRulesPayload) {
      this.telemetryRulesPayload = telemetryRulesPayload;
      return this;
    }

    public Builder setHotspotPayload(HotspotPayload hotspotPayload) {
      this.hotspotPayload = hotspotPayload;
      return this;
    }

    public Builder setIssuePayload(IssuePayload issuePayload) {
      this.issuePayload = issuePayload;
      return this;
    }

    public Builder setHelpAndFeedbackPayload(TelemetryHelpAndFeedbackPayload helpAndFeedbackPayload) {
      this.helpAndFeedbackPayload = helpAndFeedbackPayload;
      return this;
    }

    public Builder setAiFixSuggestionsPayload(TelemetryFixSuggestionPayload[] aiFixSuggestionsPayload) {
      this.aiFixSuggestionsPayload = aiFixSuggestionsPayload;
      return this;
    }

    public Builder setCountIssuesWithPossibleAiFixFromIde(int countIssuesWithPossibleAiFixFromIde) {
      this.countIssuesWithPossibleAiFixFromIde = countIssuesWithPossibleAiFixFromIde;
      return this;
    }

    public Builder setCleanAsYouCodePayload(CleanAsYouCodePayload cleanAsYouCodePayload) {
      this.cleanAsYouCodePayload = cleanAsYouCodePayload;
      return this;
    }

    public Builder setShareConnectedModePayload(ShareConnectedModePayload shareConnectedModePayload) {
      this.shareConnectedModePayload = shareConnectedModePayload;
      return this;
    }

    public Builder setAdditionalAttributes(Map<String, Object> additionalAttributes) {
      this.additionalAttributes = additionalAttributes;
      return this;
    }

    public TelemetryPayload build() {
      return new TelemetryPayload(this);
    }
  }

}
