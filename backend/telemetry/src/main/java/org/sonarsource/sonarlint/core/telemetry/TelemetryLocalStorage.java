/*
ACR-c3e9bd7d635b475cb62d78ffa94b1e45
ACR-c66c719fbb20427bbdd96d40f16dc41f
ACR-0c828c1f179245a0a508c48b5ecb2e79
ACR-2c4715db2729442d81d9147a0fec24a9
ACR-926e22a136b64ed88dd4bb50cbc116e0
ACR-876d6149c3d54716906e011f9962ee6c
ACR-ff41baebc16b4e96a0fae0d7398c28f5
ACR-1623bc1f73cf447f83c6583087daefed
ACR-37efcc0ea1fd40ddbb10685d1d72c86f
ACR-18f4d8a67c264cc6ae4f277367f91348
ACR-13d8094999c84f0ca79a047d602ae5fc
ACR-5d9dc2ac2ffb4850ad53908aa0db3ed2
ACR-1a5883c089dd45ba9b2e450a08f64d58
ACR-3c6db4b3293e4870b1703dbd0dd28659
ACR-859e4a322f6849b9a77bee12d3070c4c
ACR-d75f56e3912442be943f94cb1007a425
ACR-079e874e9cd4412394e8e0132f7051f5
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.storage.local.LocalStorage;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgent;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AnalysisReportingType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FixSuggestionStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.McpTransportMode;

import static java.time.temporal.ChronoUnit.DAYS;

public class TelemetryLocalStorage implements LocalStorage {
  @Deprecated
  private LocalDate installDate;
  private LocalDate lastUseDate;
  private LocalDateTime lastUploadDateTime;
  private OffsetDateTime installTime;
  private long numUseDays;
  private boolean enabled;
  private final Map<String, TelemetryAnalyzerPerformance> analyzers;
  private final Map<String, TelemetryNotificationsCounter> notificationsCountersByEventType;
  private int showHotspotRequestsCount;
  private int showIssueRequestsCount;
  private int openHotspotInBrowserCount;
  private int taintVulnerabilitiesInvestigatedLocallyCount;
  private int taintVulnerabilitiesInvestigatedRemotelyCount;
  private int hotspotStatusChangedCount;
  private final Set<String> issueStatusChangedRuleKeys;
  private int issueStatusChangedCount;
  private final Set<String> raisedIssuesRules;
  private final Set<String> quickFixesApplied;
  private final Map<String, Integer> quickFixCountByRuleKey;
  private final Map<String, TelemetryHelpAndFeedbackCounter> helpAndFeedbackLinkClickedCount;
  private final Map<AnalysisReportingType, TelemetryAnalysisReportingCounter> analysisReportingCountersByType;
  private final Map<String, TelemetryFindingsFilteredCounter> findingsFilteredCountersByType;
  private final Map<String, TelemetryFixSuggestionReceivedCounter> fixSuggestionReceivedCounter;
  private final Map<String, List<TelemetryFixSuggestionResolvedStatus>> fixSuggestionResolved;
  private final Map<String, ToolCallCounter> calledToolsByName;
  private final Set<UUID> issuesUuidAiFixableSeen;
  private boolean isFocusOnNewCode;
  private int codeFocusChangedCount;
  private int manualAddedBindingsCount;
  private int importedAddedBindingsCount;
  private int autoAddedBindingsCount;
  private int exportedConnectedModeCount;
  private int newBindingsPropertiesFileCount;
  private int newBindingsRemoteUrlCount;
  private int newBindingsProjectNameCount;
  private int newBindingsSharedConfigurationCount;
  private int suggestedRemoteBindingsCount;
  private long newIssuesFoundCount;
  private long issuesFixedCount;
  private int biggestNumberOfFilesInConfigScope;
  private long listingTimeForBiggestNumberConfigScopeFiles;
  private long longestListingTimeForConfigScopeFiles;
  private int numberOfFilesForLongestFilesListingTimeConfigScope;
  private int taintInvestigatedLocallyCount;
  private int taintInvestigatedRemotelyCount;
  private int hotspotInvestigatedLocallyCount;
  private int hotspotInvestigatedRemotelyCount;
  private int issueInvestigatedLocallyCount;
  private int dependencyRiskInvestigatedRemotelyCount;
  private int dependencyRiskInvestigatedLocallyCount;
  private boolean isAutomaticAnalysisEnabled;
  private int automaticAnalysisToggledCount;
  private int flightRecorderSessionsCount;
  private int mcpServerConfigurationRequestedCount;
  private int mcpRuleFileRequestedCount;
  private boolean isMcpIntegrationEnabled;
  @Nullable
  private McpTransportMode mcpTransportModeUsed;
  private final Map<String, Integer> labsLinkClickedCount;
  private final Map<String, Integer> labsFeedbackLinkClickedCount;
  private final Map<AiAgent, Integer> aiHooksInstalledCount;
  private final Map<String, Integer> campaignsShown;
  private final Map<String, String> campaignsResolutions;

  TelemetryLocalStorage() {
    enabled = true;
    installTime = OffsetDateTime.now();
    analyzers = new LinkedHashMap<>();
    notificationsCountersByEventType = new LinkedHashMap<>();
    issueStatusChangedRuleKeys = new HashSet<>();
    raisedIssuesRules = new HashSet<>();
    quickFixesApplied = new HashSet<>();
    quickFixCountByRuleKey = new LinkedHashMap<>();
    helpAndFeedbackLinkClickedCount = new LinkedHashMap<>();
    analysisReportingCountersByType = new LinkedHashMap<>();
    findingsFilteredCountersByType = new LinkedHashMap<>();
    fixSuggestionReceivedCounter = new LinkedHashMap<>();
    fixSuggestionResolved = new LinkedHashMap<>();
    issuesUuidAiFixableSeen = new HashSet<>();
    calledToolsByName = new HashMap<>();
    labsLinkClickedCount = new HashMap<>();
    labsFeedbackLinkClickedCount = new HashMap<>();
    aiHooksInstalledCount = new EnumMap<>(AiAgent.class);
    campaignsShown = new HashMap<>();
    campaignsResolutions = new HashMap<>();
  }

  public Collection<String> getRaisedIssuesRules() {
    return raisedIssuesRules;
  }

  public void addReportedRules(Set<String> reportedRuleKeys) {
    this.raisedIssuesRules.addAll(reportedRuleKeys);
  }

  public Collection<String> getQuickFixesApplied() {
    return quickFixesApplied;
  }

  public void addQuickFixAppliedForRule(String ruleKey) {
    markSonarLintAsUsedToday();
    this.quickFixesApplied.add(ruleKey);
    var currentCountForKey = this.quickFixCountByRuleKey.getOrDefault(ruleKey, 0);
    this.quickFixCountByRuleKey.put(ruleKey, currentCountForKey + 1);
  }

  public Map<String, Integer> getQuickFixCountByRuleKey() {
    return quickFixCountByRuleKey;
  }

  @Deprecated
  void setInstallDate(LocalDate date) {
    this.installDate = date;
  }

  @Deprecated
  public LocalDate installDate() {
    return installDate;
  }

  public OffsetDateTime installTime() {
    return installTime;
  }

  public void setInstallTime(OffsetDateTime installTime) {
    this.installTime = installTime;
  }

  void setLastUseDate(@Nullable LocalDate date) {
    this.lastUseDate = date;
  }

  @CheckForNull
  public LocalDate lastUseDate() {
    return lastUseDate;
  }

  public Map<String, TelemetryAnalyzerPerformance> analyzers() {
    return analyzers;
  }

  public Map<String, TelemetryNotificationsCounter> notifications() {
    return notificationsCountersByEventType;
  }

  public Map<String, TelemetryHelpAndFeedbackCounter> getHelpAndFeedbackLinkClickedCounter() {
    return helpAndFeedbackLinkClickedCount;
  }

  public Map<AnalysisReportingType, TelemetryAnalysisReportingCounter> getAnalysisReportingCountersByType() {
    return analysisReportingCountersByType;
  }

  public Map<String, TelemetryFindingsFilteredCounter> getFindingsFilteredCountersByType() {
    return findingsFilteredCountersByType;
  }

  public Map<String, TelemetryFixSuggestionReceivedCounter> getFixSuggestionReceivedCounter() {
    return fixSuggestionReceivedCounter;
  }

  public Map<String, List<TelemetryFixSuggestionResolvedStatus>> getFixSuggestionResolved() {
    return fixSuggestionResolved;
  }

  public int getCountIssuesWithPossibleAiFixFromIde() {
    return issuesUuidAiFixableSeen.size();
  }

  public boolean isFocusOnNewCode() {
    return isFocusOnNewCode;
  }

  public int getCodeFocusChangedCount() {
    return codeFocusChangedCount;
  }

  void setLastUploadTime() {
    setLastUploadTime(LocalDateTime.now());
  }

  void setLastUploadTime(@Nullable LocalDateTime dateTime) {
    this.lastUploadDateTime = dateTime;
  }

  @CheckForNull
  public LocalDateTime lastUploadTime() {
    return lastUploadDateTime;
  }

  void setNumUseDays(long numUseDays) {
    this.numUseDays = numUseDays;
  }

  void clearAfterPing() {
    analyzers.clear();
    notificationsCountersByEventType.clear();
    showHotspotRequestsCount = 0;
    showIssueRequestsCount = 0;
    openHotspotInBrowserCount = 0;
    taintVulnerabilitiesInvestigatedLocallyCount = 0;
    taintVulnerabilitiesInvestigatedRemotelyCount = 0;
    hotspotStatusChangedCount = 0;
    issueStatusChangedRuleKeys.clear();
    issueStatusChangedCount = 0;
    raisedIssuesRules.clear();
    quickFixesApplied.clear();
    quickFixCountByRuleKey.clear();
    helpAndFeedbackLinkClickedCount.clear();
    analysisReportingCountersByType.clear();
    findingsFilteredCountersByType.clear();
    fixSuggestionReceivedCounter.clear();
    fixSuggestionResolved.clear();
    issuesUuidAiFixableSeen.clear();
    codeFocusChangedCount = 0;
    manualAddedBindingsCount = 0;
    importedAddedBindingsCount = 0;
    autoAddedBindingsCount = 0;
    exportedConnectedModeCount = 0;
    newBindingsPropertiesFileCount = 0;
    newBindingsRemoteUrlCount = 0;
    newBindingsProjectNameCount = 0;
    newBindingsSharedConfigurationCount = 0;
    suggestedRemoteBindingsCount = 0;
    newIssuesFoundCount = 0;
    issuesFixedCount = 0;
    biggestNumberOfFilesInConfigScope = 0;
    calledToolsByName.clear();
    dependencyRiskInvestigatedLocallyCount = 0;
    dependencyRiskInvestigatedRemotelyCount = 0;
    automaticAnalysisToggledCount = 0;
    flightRecorderSessionsCount = 0;
    mcpServerConfigurationRequestedCount = 0;
    mcpRuleFileRequestedCount = 0;
    isMcpIntegrationEnabled = false;
    mcpTransportModeUsed = null;
    labsLinkClickedCount.clear();
    labsFeedbackLinkClickedCount.clear();
    aiHooksInstalledCount.clear();
    campaignsShown.clear();
    campaignsResolutions.clear();
  }

  public long numUseDays() {
    return numUseDays;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean enabled() {
    return enabled;
  }

  /*ACR-bf10638a20d844d2b546b49fbdf73aff
ACR-856d31dd3e7b435a8556a598fa3d69c5
ACR-6e797f133ee04c8d8bfa6f7f445a84e7
ACR-9983ea1eab5c4715a4c200c5d4b11954
ACR-676b959122fa44fb9dae5c52a6716e1e
   */
  void setUsedAnalysis() {
    markSonarLintAsUsedToday();
  }

  private void markSonarLintAsUsedToday() {
    var now = LocalDate.now();
    if (lastUseDate == null || !lastUseDate.equals(now)) {
      numUseDays++;
    }
    lastUseDate = now;
  }

  /*ACR-591dea3234c742d7a1e020165fe9869f
ACR-6ef23868f0d048b4b95715f5309c934c
   */
  void setUsedAnalysis(String language, int analysisTimeMs) {
    markSonarLintAsUsedToday();

    var analyzer = analyzers.computeIfAbsent(language, x -> new TelemetryAnalyzerPerformance());
    analyzer.registerAnalysis(analysisTimeMs);
  }

  static boolean isOlder(@Nullable LocalDate first, @Nullable LocalDate second) {
    return first == null || (second != null && first.isBefore(second));
  }

  static boolean isOlder(@Nullable LocalDateTime first, @Nullable LocalDateTime second) {
    return first == null || (second != null && first.isBefore(second));
  }

  @Override
  public void validateAndMigrate() {
    var today = LocalDate.now();

    //ACR-c49622e807f149c792582630439ee43e
    if (installDate != null && (installTime == null || installTime.toLocalDate().isAfter(installDate))) {
      setInstallTime(installDate.atTime(OffsetTime.now()));
    }

    //ACR-9a22af939d4140b0a306c252f6bda4d4
    if (installTime == null || installTime.isAfter(OffsetDateTime.now())) {
      setInstallTime(OffsetDateTime.now());
    }

    //ACR-ddb683ea78ae4ce5bbc1efebdf66c314
    if (lastUseDate == null) {
      numUseDays = 0;
      analyzers.clear();
      return;
    }

    if (lastUseDate.isBefore(installTime.toLocalDate())) {
      lastUseDate = installTime.toLocalDate();
    } else if (lastUseDate.isAfter(today)) {
      lastUseDate = today;
    }

    var maxUseDays = installTime.toLocalDate().until(lastUseDate, DAYS) + 1;
    if (numUseDays() > maxUseDays) {
      numUseDays = maxUseDays;
    }
  }

  public void incrementDevNotificationsCount(String eventType) {
    this.notificationsCountersByEventType.computeIfAbsent(eventType, k -> new TelemetryNotificationsCounter()).incrementDevNotificationsCount();
  }

  public void incrementDevNotificationsClicked(String eventType) {
    markSonarLintAsUsedToday();
    this.notificationsCountersByEventType.computeIfAbsent(eventType, k -> new TelemetryNotificationsCounter()).incrementDevNotificationsClicked();
  }

  public void incrementShowHotspotRequestCount() {
    markSonarLintAsUsedToday();
    showHotspotRequestsCount++;
  }

  public int showHotspotRequestsCount() {
    return showHotspotRequestsCount;
  }

  public void incrementShowIssueRequestCount() {
    markSonarLintAsUsedToday();
    showIssueRequestsCount++;
  }

  public void fixSuggestionReceived(String suggestionId, AiSuggestionSource aiSuggestionSource, int snippetsCount, boolean wasGeneratedFromIde) {
    markSonarLintAsUsedToday();
    this.fixSuggestionReceivedCounter.computeIfAbsent(suggestionId, k -> new TelemetryFixSuggestionReceivedCounter(aiSuggestionSource, snippetsCount, wasGeneratedFromIde));
  }

  public void fixSuggestionResolved(String suggestionId, FixSuggestionStatus status, @Nullable Integer snippetIndex) {
    markSonarLintAsUsedToday();
    var fixSuggestionSnippets = this.fixSuggestionResolved.computeIfAbsent(suggestionId, k -> new ArrayList<>());
    var existingSnippetStatus = fixSuggestionSnippets.stream()
      .filter(s -> {
        var previousIndex = s.getFixSuggestionResolvedSnippetIndex();
        return (snippetIndex == null && previousIndex == null) ||
          (previousIndex != null && previousIndex.equals(snippetIndex));
      })
      .findFirst();
    //ACR-cf809bf49fae42c585e99b73db717cdd
    existingSnippetStatus.ifPresentOrElse(telemetryFixSuggestionResolvedStatus -> telemetryFixSuggestionResolvedStatus.setFixSuggestionResolvedStatus(status),
      () -> fixSuggestionSnippets.add(new TelemetryFixSuggestionResolvedStatus(status, snippetIndex)));
  }

  public void addIssuesWithPossibleAiFixFromIde(Set<UUID> issues) {
    markSonarLintAsUsedToday();
    issuesUuidAiFixableSeen.addAll(issues);
  }

  public int getShowIssueRequestsCount() {
    return showIssueRequestsCount;
  }

  public void incrementOpenHotspotInBrowserCount() {
    markSonarLintAsUsedToday();
    openHotspotInBrowserCount++;
  }

  public int openHotspotInBrowserCount() {
    return openHotspotInBrowserCount;
  }

  public void incrementTaintVulnerabilitiesInvestigatedLocallyCount() {
    markSonarLintAsUsedToday();
    taintVulnerabilitiesInvestigatedLocallyCount++;
  }

  public int taintVulnerabilitiesInvestigatedLocallyCount() {
    return taintVulnerabilitiesInvestigatedLocallyCount;
  }

  public void incrementTaintVulnerabilitiesInvestigatedRemotelyCount() {
    markSonarLintAsUsedToday();
    taintVulnerabilitiesInvestigatedRemotelyCount++;
  }

  public int taintVulnerabilitiesInvestigatedRemotelyCount() {
    return taintVulnerabilitiesInvestigatedRemotelyCount;
  }

  public void helpAndFeedbackLinkClicked(String itemId) {
    this.helpAndFeedbackLinkClickedCount.computeIfAbsent(itemId, k -> new TelemetryHelpAndFeedbackCounter()).incrementHelpAndFeedbackLinkClickedCount();
  }

  public void analysisReportingTriggered(AnalysisReportingType analysisType) {
    this.analysisReportingCountersByType.computeIfAbsent(analysisType, k -> new TelemetryAnalysisReportingCounter()).incrementAnalysisReportingCount();
  }

  public void findingsFiltered(String filterType) {
    markSonarLintAsUsedToday();
    this.findingsFilteredCountersByType.computeIfAbsent(filterType, k -> new TelemetryFindingsFilteredCounter()).incrementFindingsFilteredCount();
  }

  public void incrementHotspotStatusChangedCount() {
    markSonarLintAsUsedToday();
    hotspotStatusChangedCount++;
  }

  public int hotspotStatusChangedCount() {
    return hotspotStatusChangedCount;
  }

  public void addIssueStatusChanged(String ruleKey) {
    markSonarLintAsUsedToday();
    issueStatusChangedRuleKeys.add(ruleKey);
    issueStatusChangedCount++;
  }

  public Set<String> issueStatusChangedRuleKeys() {
    return issueStatusChangedRuleKeys;
  }

  public int issueStatusChangedCount() {
    return issueStatusChangedCount;
  }

  public void setInitialNewCodeFocus(boolean focusOnNewCode) {
    markSonarLintAsUsedToday();
    this.isFocusOnNewCode = focusOnNewCode;
  }

  public void incrementNewCodeFocusChange() {
    markSonarLintAsUsedToday();
    this.isFocusOnNewCode = !this.isFocusOnNewCode;
    codeFocusChangedCount++;
  }

  public void incrementManualAddedBindingsCount() {
    markSonarLintAsUsedToday();
    manualAddedBindingsCount++;
  }

  public int getManualAddedBindingsCount() {
    return manualAddedBindingsCount;
  }

  public void incrementImportedAddedBindingsCount() {
    markSonarLintAsUsedToday();
    importedAddedBindingsCount++;
  }

  public int getImportedAddedBindingsCount() {
    return importedAddedBindingsCount;
  }

  public void incrementAutoAddedBindingsCount() {
    markSonarLintAsUsedToday();
    autoAddedBindingsCount++;
  }

  public int getAutoAddedBindingsCount() {
    return autoAddedBindingsCount;
  }

  public void incrementExportedConnectedModeCount() {
    markSonarLintAsUsedToday();
    exportedConnectedModeCount++;
  }

  public void incrementNewBindingsPropertiesFileCount() {
    markSonarLintAsUsedToday();
    newBindingsPropertiesFileCount++;
  }

  public void incrementNewBindingsRemoteUrlCount() {
    markSonarLintAsUsedToday();
    newBindingsRemoteUrlCount++;
  }

  public void incrementNewBindingsProjectNameCount() {
    markSonarLintAsUsedToday();
    newBindingsProjectNameCount++;
  }

  public void incrementNewBindingsSharedConfigurationCount() {
    markSonarLintAsUsedToday();
    newBindingsSharedConfigurationCount++;
  }

  public void incrementSuggestedRemoteBindingsCount() {
    suggestedRemoteBindingsCount++;
  }

  public int getExportedConnectedModeCount() {
    return exportedConnectedModeCount;
  }

  public int getNewBindingsPropertiesFileCount() {
    return newBindingsPropertiesFileCount;
  }

  public int getNewBindingsRemoteUrlCount() {
    return newBindingsRemoteUrlCount;
  }

  public int getNewBindingsProjectNameCount() {
    return newBindingsProjectNameCount;
  }

  public int getNewBindingsSharedConfigurationCount() {
    return newBindingsSharedConfigurationCount;
  }

  public int getSuggestedRemoteBindingsCount() {
    return suggestedRemoteBindingsCount;
  }

  public void addNewlyFoundIssues(long newIssues) {
    markSonarLintAsUsedToday();
    newIssuesFoundCount += newIssues;
  }

  public long getNewIssuesFoundCount() {
    return newIssuesFoundCount;
  }

  public void addFixedIssues(long fixedIssues) {
    markSonarLintAsUsedToday();
    issuesFixedCount += fixedIssues;
  }

  public long getIssuesFixedCount() {
    return issuesFixedCount;
  }

  public void setMcpIntegrationEnabled(boolean isMcpIntegrationEnabled) {
    this.isMcpIntegrationEnabled = isMcpIntegrationEnabled;
  }

  public boolean isMcpIntegrationEnabled() {
    return isMcpIntegrationEnabled;
  }

  public void setMcpTransportModeUsed(McpTransportMode mcpTransportMode) {
    this.mcpTransportModeUsed = mcpTransportMode;
  }

  @CheckForNull
  public McpTransportMode getMcpTransportModeUsed() {
    return mcpTransportModeUsed;
  }

  public void incrementToolCalledCount(String toolName, boolean succeeded) {
    markSonarLintAsUsedToday();
    calledToolsByName.computeIfAbsent(toolName, k -> new ToolCallCounter()).incrementCount(succeeded);
  }

  public Map<String, ToolCallCounter> getCalledToolsByName() {
    return calledToolsByName;
  }

  public void updateListFilesPerformance(int size, long timeMs) {
    if (size > biggestNumberOfFilesInConfigScope) {
      biggestNumberOfFilesInConfigScope = size;
      listingTimeForBiggestNumberConfigScopeFiles = timeMs;
    }
    if (timeMs > longestListingTimeForConfigScopeFiles) {
      longestListingTimeForConfigScopeFiles = timeMs;
      numberOfFilesForLongestFilesListingTimeConfigScope = size;
    }
  }

  public int getBiggestNumberOfFilesInConfigScope() {
    return biggestNumberOfFilesInConfigScope;
  }

  public long getListingTimeForBiggestNumberConfigScopeFiles() {
    return listingTimeForBiggestNumberConfigScopeFiles;
  }

  public int getNumberOfFilesForLongestFilesListingTimeConfigScope() {
    return numberOfFilesForLongestFilesListingTimeConfigScope;
  }

  public long getLongestListingTimeForConfigScopeFiles() {
    return longestListingTimeForConfigScopeFiles;
  }

  public void incrementHotspotInvestigatedLocallyCount() {
    markSonarLintAsUsedToday();
    hotspotInvestigatedLocallyCount++;
  }

  public void incrementHotspotInvestigatedRemotelyCount() {
    markSonarLintAsUsedToday();
    hotspotInvestigatedRemotelyCount++;
  }

  public void incrementTaintInvestigatedLocallyCount() {
    markSonarLintAsUsedToday();
    taintInvestigatedLocallyCount++;
  }

  public void incrementTaintInvestigatedRemotelyCount() {
    markSonarLintAsUsedToday();
    taintInvestigatedRemotelyCount++;
  }

  public void incrementIssueInvestigatedLocallyCount() {
    markSonarLintAsUsedToday();
    issueInvestigatedLocallyCount++;
  }

  public void incrementDependencyRiskInvestigatedRemotelyCount() {
    markSonarLintAsUsedToday();
    dependencyRiskInvestigatedRemotelyCount++;
  }

  public void incrementDependencyRiskInvestigatedLocallyCount() {
    markSonarLintAsUsedToday();
    dependencyRiskInvestigatedLocallyCount++;
  }

  public int getHotspotInvestigatedRemotelyCount() {
    return hotspotInvestigatedRemotelyCount;
  }

  public int getHotspotInvestigatedLocallyCount() {
    return hotspotInvestigatedLocallyCount;
  }

  public int getTaintInvestigatedRemotelyCount() {
    return taintInvestigatedRemotelyCount;
  }

  public int getTaintInvestigatedLocallyCount() {
    return taintInvestigatedLocallyCount;
  }

  public int getIssueInvestigatedLocallyCount() {
    return issueInvestigatedLocallyCount;
  }

  public int getDependencyRiskInvestigatedRemotelyCount() {
    return dependencyRiskInvestigatedRemotelyCount;
  }

  public int getDependencyRiskInvestigatedLocallyCount() {
    return dependencyRiskInvestigatedLocallyCount;
  }

  public boolean isAutomaticAnalysisEnabled() {
    return isAutomaticAnalysisEnabled;
  }

  public int getAutomaticAnalysisToggledCount() {
    return automaticAnalysisToggledCount;
  }

  public void setInitialAutomaticAnalysisEnablement(boolean automaticAnalysisEnabled) {
    markSonarLintAsUsedToday();
    this.isAutomaticAnalysisEnabled = automaticAnalysisEnabled;
  }

  public void incrementAutomaticAnalysisToggledCount() {
    markSonarLintAsUsedToday();
    this.isAutomaticAnalysisEnabled = !this.isAutomaticAnalysisEnabled;
    automaticAnalysisToggledCount++;
  }

  public void incrementFlightRecorderSessionsCount() {
    markSonarLintAsUsedToday();
    flightRecorderSessionsCount ++;
  }

  public int getFlightRecorderSessionsCount() {
    return flightRecorderSessionsCount;
  }

  public void incrementMcpServerConfigurationRequestedCount() {
    markSonarLintAsUsedToday();
    mcpServerConfigurationRequestedCount++;
  }

  public int getMcpServerConfigurationRequestedCount() {
    return mcpServerConfigurationRequestedCount;
  }

  public void incrementMcpRuleFileRequestedCount() {
    markSonarLintAsUsedToday();
    mcpRuleFileRequestedCount++;
  }

  public int getMcpRuleFileRequestedCount() {
    return mcpRuleFileRequestedCount;
  }

  public Map<String, Integer> getLabsFeedbackLinkClickedCount() {
    return labsFeedbackLinkClickedCount;
  }

  public Map<String, Integer> getLabsLinkClickedCount() {
    return labsLinkClickedCount;
  }

  public void ideLabsLinkClicked(String linkId) {
    this.labsLinkClickedCount.merge(linkId, 1, Integer::sum);
  }

  public void ideLabsFeedbackLinkClicked(String featureId) {
    this.labsFeedbackLinkClickedCount.merge(featureId, 1, Integer::sum);
  }

  public void aiHookInstalled(AiAgent aiAgent) {
    markSonarLintAsUsedToday();
    this.aiHooksInstalledCount.merge(aiAgent, 1, Integer::sum);
  }

  public Map<AiAgent, Integer> getAiHooksInstalledCount() {
    return aiHooksInstalledCount;
  }

  public void campaignShown(String campaignName) {
    campaignsShown.merge(campaignName, 1, Integer::sum);
  }

  public void campaignResolved(String campaignName, String campaignResolution) {
    campaignsResolutions.put(campaignName, campaignResolution);
  }

  public Map<String, Integer> getCampaignsShown() {
    return campaignsShown;
  }

  public Map<String, String> getCampaignsResolutions() {
    return campaignsResolutions;
  }
}
