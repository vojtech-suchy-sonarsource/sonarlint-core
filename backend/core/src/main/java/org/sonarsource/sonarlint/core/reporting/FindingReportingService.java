/*
ACR-61f963e934a041cb95836586ed3e19ac
ACR-afb2461c14ab4d8ba85035a60d7321f6
ACR-4c7347dde2164a9f885eae0d2b5c6f2c
ACR-14e6c0df8c9a4da79ca4f75e1fab5bf9
ACR-27ca7166fad246bdb8586a022e206941
ACR-68301c1a94f24a2bbd94cb224653227e
ACR-098ffe2722c547e9b06963f942fb8363
ACR-60d808caea5d4be79571eef9000d8c5e
ACR-e31bf2773a934cf0bb5f81e0dfb38772
ACR-f9c2c6afa7ce4f0d94418449ee6a9284
ACR-6789e74d529b45e49cd5c683f0c7e5b7
ACR-a2e4cd90abde465db014b3602bf4e965
ACR-cd9c899dc453420aa8609519a9d9f8ca
ACR-eab3ac4c926743149acfefd3aaed9258
ACR-23e77c71a18e4f3f8d2683ba94264339
ACR-0bfb00b3c9b34391bbe296fdcd5db170
ACR-1a571dad4fbd494cac31cad21165baa9
 */
package org.sonarsource.sonarlint.core.reporting;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.active.rules.ServerActiveRulesChanged;
import org.sonarsource.sonarlint.core.active.rules.StandaloneRulesConfigurationChanged;
import org.sonarsource.sonarlint.core.analysis.IssuesRaisedEvent;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.mode.SeverityModeService;
import org.sonarsource.sonarlint.core.newcode.NewCodeService;
import org.sonarsource.sonarlint.core.remediation.aicodefix.AiCodeFixFeature;
import org.sonarsource.sonarlint.core.remediation.aicodefix.AiCodeFixService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.reporting.PreviouslyRaisedFindingsRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaiseHotspotsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaiseIssuesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedFindingDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.tracking.TrackedIssue;
import org.sonarsource.sonarlint.core.tracking.streaming.Alarm;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.sonarsource.sonarlint.core.DtoMapper.toRaisedHotspotDto;
import static org.sonarsource.sonarlint.core.DtoMapper.toRaisedIssueDto;

public class FindingReportingService {
  public static final Duration STREAMING_INTERVAL = Duration.ofMillis(300);
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final SonarLintRpcClient client;
  private final ConfigurationRepository configurationRepository;
  private final NewCodeService newCodeService;
  private final SeverityModeService severityModeService;
  private final PreviouslyRaisedFindingsRepository previouslyRaisedFindingsRepository;
  private final Map<URI, Collection<TrackedIssue>> issuesPerFileUri = new ConcurrentHashMap<>();
  private final Map<URI, Collection<TrackedIssue>> securityHotspotsPerFileUri = new ConcurrentHashMap<>();
  private final Map<String, Alarm> streamingTriggeringAlarmByConfigScopeId = new ConcurrentHashMap<>();
  private final Map<UUID, Set<URI>> filesPerAnalysis = new ConcurrentHashMap<>();
  private final ApplicationEventPublisher eventPublisher;
  private final boolean isStreamingEnabled;
  private final AiCodeFixService aiCodeFixService;

  public FindingReportingService(SonarLintRpcClient client, ConfigurationRepository configurationRepository, NewCodeService newCodeService, SeverityModeService severityModeService,
    PreviouslyRaisedFindingsRepository previouslyRaisedFindingsRepository, ApplicationEventPublisher eventPublisher, InitializeParams initializeParams,
    AiCodeFixService aiCodeFixService) {
    this.client = client;
    this.configurationRepository = configurationRepository;
    this.newCodeService = newCodeService;
    this.severityModeService = severityModeService;
    this.previouslyRaisedFindingsRepository = previouslyRaisedFindingsRepository;
    this.eventPublisher = eventPublisher;
    this.isStreamingEnabled = initializeParams.getBackendCapabilities().contains(BackendCapability.ISSUE_STREAMING);
    this.aiCodeFixService = aiCodeFixService;
  }

  @EventListener
  public void onStandaloneRulesConfigurationChanged(StandaloneRulesConfigurationChanged event) {
    if (event.isOnlyDeactivated()) {
      //ACR-1ffef23cc5a54c70aeee24481e2ca2ce
      configurationRepository.getConfigScopeIds().stream()
        .filter(configScopeId -> configurationRepository.getEffectiveBinding(configScopeId).isEmpty())
        .forEach(configScopeId -> {
          var deactivatedRules = event.getDeactivatedRules();
          updateAndReportFindings(configScopeId,
            hotspot -> raisedFindingUpdater(hotspot, deactivatedRules),
            issue -> raisedFindingUpdater(issue, deactivatedRules));
        });
    }
  }

  @CheckForNull
  private static <T extends RaisedFindingDto> T raisedFindingUpdater(T raisedFinding, List<String> deactivatedRules) {
    if (deactivatedRules.contains(raisedFinding.getRuleKey())) {
      return null;
    }
    return raisedFinding;
  }

  @EventListener
  private void onServerActiveRulesChanged(ServerActiveRulesChanged event) {
    var deactivatedRules = event.deactivatedRules();
    //ACR-b46e1d0e39eb40f89b2b88d23cbca65f
    if (event.activatedRules().isEmpty() && !deactivatedRules.isEmpty()) {
      var changedProjectKeys = event.projectKeys();
      configurationRepository.getAllBoundScopes().stream()
        .filter(scope -> event.connectionId().equals(scope.getConnectionId()) && changedProjectKeys.contains(scope.getSonarProjectKey()))
        .map(BoundScope::getConfigScopeId)
        .forEach(scopeId -> updateAndReportFindings(scopeId,
          hotspot -> raisedFindingUpdater(hotspot, deactivatedRules),
          issue -> raisedFindingUpdater(issue, deactivatedRules)));
    }
  }

  public void resetFindingsForFiles(String configurationScopeId, Set<URI> files) {
    files.forEach(fileUri -> {
      resetFindingsForFile(issuesPerFileUri, fileUri);
      resetFindingsForFile(securityHotspotsPerFileUri, fileUri);
    });
    previouslyRaisedFindingsRepository.resetFindingsCache(configurationScopeId, files);
  }

  public void initFilesToAnalyze(UUID analysisId, Set<URI> files) {
    filesPerAnalysis.computeIfAbsent(analysisId, k -> new HashSet<>()).addAll(files);
  }

  private static void resetFindingsForFile(Map<URI, Collection<TrackedIssue>> findingsMap, URI fileUri) {
    findingsMap.computeIfPresent(fileUri, (k, v) -> List.of());
  }

  public void streamIssue(String configurationScopeId, UUID analysisId, TrackedIssue trackedIssue) {
    //ACR-4c3d03703f7b4f67bb53661f736bd67b
    //ACR-8c3fc9ec2f6949fd8f752a19bffaeb58
    //ACR-50aef0fef3714081ab5ca6815d2f7227
    //ACR-4623bf856db64ddfb884eb030c7cbddf
    if (trackedIssue.isSecurityHotspot()) {
      insertTrackedIssue(securityHotspotsPerFileUri, trackedIssue);
    } else {
      insertTrackedIssue(issuesPerFileUri, trackedIssue);
    }
    if (isStreamingEnabled) {
      getStreamingDebounceAlarm(configurationScopeId, analysisId).schedule();
    }
  }

  private static void insertTrackedIssue(Map<URI, Collection<TrackedIssue>> map, TrackedIssue trackedIssue) {
    map.compute(trackedIssue.getFileUri(), (fileUri, fileFindings) -> {
      //ACR-b468aa6419eb40aea7dad3b41f826fe1
      if (fileFindings == null) {
        return List.of(trackedIssue);
      }
      var newIssues = new ArrayList<>(fileFindings);
      newIssues.removeIf(i -> i.getId().equals(trackedIssue.getId()));
      newIssues.add(trackedIssue);
      return List.copyOf(newIssues);
    });
  }

  private void triggerStreaming(String configurationScopeId, UUID analysisId) {
    var effectiveBinding = configurationRepository.getEffectiveBinding(configurationScopeId);
    var connectionId = effectiveBinding.map(Binding::connectionId).orElse(null);
    var newCodeDefinition = newCodeService.getFullNewCodeDefinition(configurationScopeId).orElseGet(NewCodeDefinition::withAlwaysNew);
    var isMQRMode = severityModeService.isMQRModeForConnection(connectionId);
    var aiCodeFixFeature = effectiveBinding.flatMap(aiCodeFixService::getFeature);
    var issuesToRaise = issuesPerFileUri.entrySet().stream()
      .filter(e -> filesPerAnalysis.get(analysisId).contains(e.getKey()))
      .map(e -> Map.entry(e.getKey(),
        e.getValue().stream().map(issue -> toRaisedIssueDto(issue, newCodeDefinition, isMQRMode, aiCodeFixFeature.map(feature -> feature.isFixable(issue)).orElse(false)))
          .toList()))
      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    var hotspotsToRaise = securityHotspotsPerFileUri.entrySet().stream()
      .filter(e -> filesPerAnalysis.get(analysisId).contains(e.getKey()))
      .map(e -> Map.entry(e.getKey(), e.getValue().stream().map(issue -> toRaisedHotspotDto(issue, newCodeDefinition, isMQRMode)).toList()))
      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    updateRaisedFindingsCacheAndNotifyClient(configurationScopeId, analysisId, issuesToRaise, hotspotsToRaise, true);
  }

  public void reportTrackedFindings(String configurationScopeId, UUID analysisId, Map<Path, List<TrackedIssue>> issuesToReport, Map<Path, List<TrackedIssue>> hotspotsToReport) {
    //ACR-20f2d22aff2044389e3469885047450a
    stopStreaming(configurationScopeId);
    var effectiveBinding = configurationRepository.getEffectiveBinding(configurationScopeId);
    var connectionId = effectiveBinding.map(Binding::connectionId).orElse(null);
    var newCodeDefinition = newCodeService.getFullNewCodeDefinition(configurationScopeId).orElseGet(NewCodeDefinition::withAlwaysNew);
    var isMQRMode = severityModeService.isMQRModeForConnection(connectionId);
    var aiCodeFixFeature = effectiveBinding.flatMap(aiCodeFixService::getFeature);
    var issuesToRaise = getIssuesToRaise(issuesToReport, newCodeDefinition, isMQRMode, aiCodeFixFeature);
    this.eventPublisher.publishEvent(new IssuesRaisedEvent(issuesToRaise.values().stream().flatMap(List::stream).toList()));
    var hotspotsToRaise = getHotspotsToRaise(hotspotsToReport, newCodeDefinition, isMQRMode);
    updateRaisedFindingsCacheAndNotifyClient(configurationScopeId, analysisId, issuesToRaise, hotspotsToRaise, false);
    filesPerAnalysis.remove(analysisId);
  }

  private synchronized void updateRaisedFindingsCacheAndNotifyClient(String configurationScopeId, @Nullable UUID analysisId, Map<URI, List<RaisedIssueDto>> updatedIssues,
    Map<URI, List<RaisedHotspotDto>> updatedHotspots, boolean isIntermediatePublication) {
    var fileIssues = previouslyRaisedFindingsRepository.replaceIssuesForFiles(configurationScopeId, updatedIssues);

    var totalIssues = fileIssues.values().stream().mapToInt(List::size).sum();
    LOG.debug("Reporting {} issues over {} files for configuration scope {}", totalIssues, fileIssues.size(), configurationScopeId);

    client.raiseIssues(new RaiseIssuesParams(configurationScopeId, fileIssues, isIntermediatePublication, analysisId));
    var effectiveBindingOpt = configurationRepository.getEffectiveBinding(configurationScopeId);
    if (effectiveBindingOpt.isPresent()) {
      //ACR-66b6ddc53458475d88f9599e4304fb1e
      var hotspotsToRaise = previouslyRaisedFindingsRepository.replaceHotspotsForFiles(configurationScopeId, updatedHotspots);
      client.raiseHotspots(new RaiseHotspotsParams(configurationScopeId, hotspotsToRaise, isIntermediatePublication, analysisId));
    }
  }

  private void stopStreaming(String configurationScopeId) {
    var alarm = removeStreamingDebounceAlarmIfExists(configurationScopeId);
    if (alarm != null) {
      alarm.shutdownNow();
    }
  }

  private Alarm getStreamingDebounceAlarm(String configurationScopeId, UUID analysisId) {
    return streamingTriggeringAlarmByConfigScopeId.computeIfAbsent(configurationScopeId,
      id -> new Alarm("sonarlint-finding-streamer", STREAMING_INTERVAL, () -> triggerStreaming(configurationScopeId, analysisId)));
  }

  private Alarm removeStreamingDebounceAlarmIfExists(String configurationScopeId) {
    return streamingTriggeringAlarmByConfigScopeId.remove(configurationScopeId);
  }

  private static Map<URI, List<RaisedIssueDto>> getIssuesToRaise(Map<Path, List<TrackedIssue>> updatedIssues, NewCodeDefinition newCodeDefinition, boolean isMQRMode,
    Optional<AiCodeFixFeature> aiCodeFixFeature) {
    LOG.debug("AiCodeFix optional is present: {}", aiCodeFixFeature.isPresent());
    return updatedIssues.values().stream().flatMap(Collection::stream)
      .collect(groupingBy(TrackedIssue::getFileUri,
        Collectors.mapping(issue -> toRaisedIssueDto(issue, newCodeDefinition, isMQRMode, aiCodeFixFeature.map(feature -> {
          LOG.debug("AiCodeFix is fixable: {}", aiCodeFixFeature.get().isFixable(issue));
          LOG.debug("Supported rules: {}", aiCodeFixFeature.get().settings().supportedRules());
          LOG.debug("Issue ruleKey {} and text range {}", issue.getRuleKey(), issue.getTextRangeWithHash());
          return feature.isFixable(issue);
        }).orElse(false)),
          Collectors.toList())));
  }

  private static Map<URI, List<RaisedHotspotDto>> getHotspotsToRaise(Map<Path, List<TrackedIssue>> hotspots, NewCodeDefinition newCodeDefinition, boolean isMQRMode) {
    return hotspots.values().stream().flatMap(Collection::stream)
      .collect(groupingBy(TrackedIssue::getFileUri, Collectors.mapping(hotspot -> toRaisedHotspotDto(hotspot, newCodeDefinition, isMQRMode), Collectors.toList())));
  }

  public void updateAndReportIssues(String configurationScopeId, UnaryOperator<RaisedIssueDto> issueUpdater) {
    updateAndReportFindings(configurationScopeId, UnaryOperator.identity(), issueUpdater);
  }

  public void updateAndReportHotspots(String configurationScopeId, UnaryOperator<RaisedHotspotDto> hotspotUpdater) {
    updateAndReportFindings(configurationScopeId, hotspotUpdater, UnaryOperator.identity());
  }

  public void updateAndReportFindings(String configurationScopeId, UnaryOperator<RaisedHotspotDto> hotspotUpdater, UnaryOperator<RaisedIssueDto> issueUpdater) {
    var updatedHotspots = updateFindings(hotspotUpdater, previouslyRaisedFindingsRepository.getRaisedHotspotsForScope(configurationScopeId));
    var updatedIssues = updateFindings(issueUpdater, previouslyRaisedFindingsRepository.getRaisedIssuesForScope(configurationScopeId));
    updateRaisedFindingsCacheAndNotifyClient(configurationScopeId, null, updatedIssues, updatedHotspots, false);
  }

  private static <F extends RaisedFindingDto> Map<URI, List<F>> updateFindings(UnaryOperator<F> findingUpdater, Map<URI, List<F>> previouslyRaisedFindings) {
    Map<URI, List<F>> updatedFindings = new HashMap<>();
    previouslyRaisedFindings.forEach((uri, finding) -> {
      var updatedFindingsForFile = finding.stream()
        .map(findingUpdater)
        .filter(Objects::nonNull)
        .toList();
      updatedFindings.put(uri, updatedFindingsForFile);
    });
    return updatedFindings;
  }

  @CheckForNull
  public RaisedIssueDto findReportedIssue(UUID issueId, NewCodeDefinition newCodeDefinition, boolean isMQRMode, Optional<AiCodeFixFeature> aiCodeFixFeature) {
    for (var findingsForFile : issuesPerFileUri.values()) {
      var optFinding = findingsForFile.stream().filter(issue -> issue.getId().equals(issueId)).findFirst();
      if (optFinding.isPresent()) {
        return toRaisedIssueDto(optFinding.get(), newCodeDefinition, isMQRMode, aiCodeFixFeature.map(feature -> feature.isFixable(optFinding.get())).orElse(false));
      }
    }
    return null;
  }

  @CheckForNull
  public RaisedHotspotDto findReportedHotspot(UUID hotspotId, NewCodeDefinition newCodeDefinition, boolean isMQRMode) {
    for (var findingsForFile : securityHotspotsPerFileUri.values()) {
      var optFinding = findingsForFile.stream().filter(hotspot -> hotspot.getId().equals(hotspotId)).findFirst();
      if (optFinding.isPresent()) {
        return toRaisedHotspotDto(optFinding.get(), newCodeDefinition, isMQRMode);
      }
    }
    return null;
  }
}
