/*
ACR-4af191a3c7104865a03dc5dac3b9baab
ACR-9af79ace7da64690a742c3232d204763
ACR-1b42f8f2f3ed435b82b4f919782eb247
ACR-57f20816bf8647b9912a7c7fcb99de79
ACR-2b3d38b1df994436ba13747636d5c14b
ACR-f7332d93a8c046fca0b78513419bcc0c
ACR-33d96d697afd402aa32a6093b72f7170
ACR-d40adcb094954203838c2a96ec431591
ACR-cc3d6acd1e604322b8596a2cf5377a01
ACR-5f21d2e01e2e4c5e862b1394b67f595a
ACR-15147a29530045a48878399c3921be95
ACR-5f8fec380d8f4cb09d3ba3ad7b5bf33d
ACR-e22f5c8024264e1b8eefa4aae486d5ac
ACR-42a0634c7aef4fd19b7bd323921c6657
ACR-c6dc3b2af441488698fcea6df525ce51
ACR-16c1264bfdbe47578ac908f1035d4f2b
ACR-bc2d478926064c508be218b69cda9692
 */
package org.sonarsource.sonarlint.core.issue;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.active.rules.ActiveRulesService;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.commons.Transition;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.LocalOnlyIssueStatusChangedEvent;
import org.sonarsource.sonarlint.core.event.ServerIssueStatusChangedEvent;
import org.sonarsource.sonarlint.core.event.SonarServerEventReceivedEvent;
import org.sonarsource.sonarlint.core.local.only.XodusLocalOnlyIssueStorageService;
import org.sonarsource.sonarlint.core.mode.SeverityModeService;
import org.sonarsource.sonarlint.core.newcode.NewCodeService;
import org.sonarsource.sonarlint.core.remediation.aicodefix.AiCodeFixService;
import org.sonarsource.sonarlint.core.reporting.FindingReportingService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.CheckStatusChangePermittedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.EffectiveIssueDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ReopenAllIssuesForFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ResolutionStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ImpactDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedFindingDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.RuleType;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality;
import org.sonarsource.sonarlint.core.rules.RuleDetails;
import org.sonarsource.sonarlint.core.rules.RuleDetailsAdapter;
import org.sonarsource.sonarlint.core.rules.RuleNotFoundException;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.exception.NotFoundException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues;
import org.sonarsource.sonarlint.core.serverapi.push.IssueChangedEvent;
import org.sonarsource.sonarlint.core.serverconnection.ServerInfoSynchronizer;
import org.sonarsource.sonarlint.core.serverconnection.issues.LocalOnlyIssuesRepository;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProjectServerIssueStore;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.tracking.LocalOnlyIssueRepository;
import org.sonarsource.sonarlint.core.tracking.TaintVulnerabilityTrackingService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

public class IssueService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final String STATUS_CHANGE_PERMISSION_MISSING_REASON = "Marking an issue as resolved requires the 'Administer Issues' permission";
  private static final String UNSUPPORTED_SQ_VERSION_REASON = "Marking a local-only issue as resolved requires SonarQube Server 10.2+";
  private static final Version SQ_ANTICIPATED_TRANSITIONS_MIN_VERSION = Version.create("10.2");

  /*ACR-9140918d21f74fffafc0ffe81e2537fb
ACR-bd0a527571aa4232b94dd856130bb94e
   */
  private static final Version SQ_ACCEPTED_TRANSITION_MIN_VERSION = Version.create("10.4");
  private static final List<ResolutionStatus> NEW_RESOLUTION_STATUSES = List.of(ResolutionStatus.ACCEPT, ResolutionStatus.FALSE_POSITIVE);
  private static final List<ResolutionStatus> OLD_RESOLUTION_STATUSES = List.of(ResolutionStatus.WONT_FIX, ResolutionStatus.FALSE_POSITIVE);
  private static final Map<ResolutionStatus, Transition> transitionByResolutionStatus = Map.of(
    ResolutionStatus.ACCEPT, Transition.ACCEPT,
    ResolutionStatus.WONT_FIX, Transition.WONT_FIX,
    ResolutionStatus.FALSE_POSITIVE, Transition.FALSE_POSITIVE);

  private final ConfigurationRepository configurationRepository;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final StorageService storageService;
  private final XodusLocalOnlyIssueStorageService localOnlyIssueStorageService;
  private final LocalOnlyIssueRepository localOnlyIssueRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final FindingReportingService findingReportingService;
  private final SeverityModeService severityModeService;
  private final NewCodeService newCodeService;
  private final ActiveRulesService activeRulesService;
  private final TaintVulnerabilityTrackingService taintVulnerabilityTrackingService;
  private final AiCodeFixService aiCodeFixService;
  private final LocalOnlyIssuesRepository localOnlyIssuesRepository;

  public IssueService(ConfigurationRepository configurationRepository, SonarQubeClientManager sonarQubeClientManager, StorageService storageService,
    XodusLocalOnlyIssueStorageService localOnlyIssueStorageService, LocalOnlyIssueRepository localOnlyIssueRepository, ApplicationEventPublisher eventPublisher,
    FindingReportingService findingReportingService, SeverityModeService severityModeService, NewCodeService newCodeService, ActiveRulesService activeRulesService,
    TaintVulnerabilityTrackingService taintVulnerabilityTrackingService, AiCodeFixService aiCodeFixService, LocalOnlyIssuesRepository localOnlyIssuesRepository) {
    this.configurationRepository = configurationRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.storageService = storageService;
    this.localOnlyIssueStorageService = localOnlyIssueStorageService;
    this.localOnlyIssueRepository = localOnlyIssueRepository;
    this.eventPublisher = eventPublisher;
    this.findingReportingService = findingReportingService;
    this.severityModeService = severityModeService;
    this.newCodeService = newCodeService;
    this.activeRulesService = activeRulesService;
    this.taintVulnerabilityTrackingService = taintVulnerabilityTrackingService;
    this.aiCodeFixService = aiCodeFixService;
    this.localOnlyIssuesRepository = localOnlyIssuesRepository;
  }

  @PostConstruct
  public void migrateData() {
    if (localOnlyIssueStorageService.exists()) {
      try {
        LOG.info("Migrating the Xodus local-only issues to H2");
        var migrationStart = System.currentTimeMillis();
        var xodusLocalOnlyIssueStore = localOnlyIssueStorageService.get();
        var issuesPerConfigScope = xodusLocalOnlyIssueStore.loadAll();
        localOnlyIssuesRepository.storeIssues(issuesPerConfigScope);
        LOG.info("Migrated Xodus local-only issues to H2, took {}ms", System.currentTimeMillis() - migrationStart);
      } catch (Exception e) {
        LOG.error("Unable to migrate local-only findings, will use fresh DB", e);
      }
    }
    //ACR-f601be6f31784698b7c27dbf8b8cc940
    localOnlyIssueStorageService.delete();
  }

  public void changeStatus(String configurationScopeId, String issueKey, ResolutionStatus newStatus, boolean isTaintIssue, SonarLintCancelMonitor cancelMonitor) {
    var binding = configurationRepository.getEffectiveBindingOrThrow(configurationScopeId);
    var serverConnection = sonarQubeClientManager.getClientOrThrow(binding.connectionId());
    var reviewStatus = transitionByResolutionStatus.get(newStatus);
    var projectServerIssueStore = storageService.binding(binding).findings();
    boolean isServerIssue = projectServerIssueStore.containsIssue(issueKey);
    if (isServerIssue) {
      serverConnection.withClientApi(serverApi -> serverApi.issue().changeStatus(issueKey, reviewStatus, cancelMonitor));
      projectServerIssueStore.updateIssueResolutionStatus(issueKey, isTaintIssue, true)
        .ifPresent(issue -> eventPublisher.publishEvent(new ServerIssueStatusChangedEvent(binding.connectionId(), binding.sonarProjectKey(), issue)));
    } else {
      var localIssueOpt = asUUID(issueKey).flatMap(localOnlyIssueRepository::findByKey);
      if (localIssueOpt.isEmpty()) {
        //ACR-a7068ae98c01478aa763850896888007
        //ACR-55392b9c02d5441cbd89eb873787c203
        try {
          serverConnection.withClientApi(serverApi -> serverApi.issue().changeStatus(issueKey, reviewStatus, cancelMonitor));
          return;
        } catch (NotFoundException ex) {
          throw issueNotFoundException(issueKey);
        }
      }
      var coreStatus = org.sonarsource.sonarlint.core.commons.IssueStatus.valueOf(newStatus.name());
      var issue = localIssueOpt.get();
      issue.resolve(coreStatus);
      var allIssues = localOnlyIssuesRepository.loadAll(configurationScopeId);
      serverConnection.withClientApi(serverApi -> serverApi.issue()
        .anticipatedTransitions(binding.sonarProjectKey(), concat(allIssues, issue), cancelMonitor));
      localOnlyIssuesRepository.storeLocalOnlyIssue(configurationScopeId, issue);
      eventPublisher.publishEvent(new LocalOnlyIssueStatusChangedEvent(issue));
    }
  }

  private static List<LocalOnlyIssue> concat(List<LocalOnlyIssue> issues, LocalOnlyIssue issue) {
    return Stream.concat(issues.stream(), Stream.of(issue)).toList();
  }

  private static List<LocalOnlyIssue> subtract(List<LocalOnlyIssue> allIssues, List<LocalOnlyIssue> issueToSubtract) {
    return allIssues.stream()
      .filter(it -> issueToSubtract.stream().noneMatch(issue -> issue.getId().equals(it.getId())))
      .toList();
  }

  public boolean checkAnticipatedStatusChangeSupported(String configScopeId) {
    var binding = configurationRepository.getEffectiveBindingOrThrow(configScopeId);
    var connectionId = binding.connectionId();
    return sonarQubeClientManager.getClientOrThrow(binding.connectionId())
      .withClientApiAndReturn(serverApi -> checkAnticipatedStatusChangeSupported(serverApi, connectionId));
  }

  /*ACR-045ace3bf7b8463e87bf2fcf76403fad
ACR-7d9304e41d744a25b4c32899d2c69d74
ACR-5c159a82c45b437791866cc5e1a0c36d
ACR-7773405b22904ff9848af8f229f4fcbe
ACR-da7f1e26e54c4257a8504ff654bb4b67
ACR-3b304b0fe8cc4412b09607669e7f9bbf
   */
  private boolean checkAnticipatedStatusChangeSupported(ServerApi api, String connectionId) {
    return !api.isSonarCloud() && storageService.connection(connectionId).serverInfo().read()
      .map(version -> version.version().satisfiesMinRequirement(SQ_ANTICIPATED_TRANSITIONS_MIN_VERSION))
      .orElse(false);
  }

  public CheckStatusChangePermittedResponse checkStatusChangePermitted(String connectionId, String issueKey, SonarLintCancelMonitor cancelMonitor) {
    return sonarQubeClientManager.getClientOrThrow(connectionId).withClientApiAndReturn(serverApi -> asUUID(issueKey)
      .flatMap(localOnlyIssueRepository::findByKey)
      .map(r -> {
        //ACR-a48332190836495a8fcaf4054f1e2dfe
        //ACR-20fe6477fc214c5ca001559452cf500a
        //ACR-7b756ee4cc7442fbbc43b16b438c1dd1
        List<ResolutionStatus> statuses = List.of();
        if (checkAnticipatedStatusChangeSupported(serverApi, connectionId)) {
          var is104orNewer = !serverApi.isSonarCloud() && is104orNewer(connectionId, serverApi, cancelMonitor);
          statuses = is104orNewer ? NEW_RESOLUTION_STATUSES : OLD_RESOLUTION_STATUSES;
        }

        return toResponse(statuses, UNSUPPORTED_SQ_VERSION_REASON);
      })
      .orElseGet(() -> {
        var issue = serverApi.issue().searchByKey(issueKey, cancelMonitor);
        return toResponse(getAdministerIssueTransitions(issue), STATUS_CHANGE_PERMISSION_MISSING_REASON);
      }));
  }

  /*ACR-5e46c5a57f664f8c8da00110a5d81cc7
ACR-422603d2c3744395838097a909b81aa3
   */
  private boolean is104orNewer(String connectionId, ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    var serverVersionSynchronizer = new ServerInfoSynchronizer(storageService.connection(connectionId));
    var serverVersion = serverVersionSynchronizer.readOrSynchronizeServerInfo(serverApi, cancelMonitor);
    return serverVersion.version().compareToIgnoreQualifier(SQ_ACCEPTED_TRANSITION_MIN_VERSION) >= 0;
  }

  private static CheckStatusChangePermittedResponse toResponse(List<ResolutionStatus> statuses, String reason) {
    var permitted = !statuses.isEmpty();

    //ACR-1ac51ca6836a497cb60eba12c2de6d06
    return new CheckStatusChangePermittedResponse(permitted, permitted ? null : reason, statuses);
  }

  private static List<ResolutionStatus> getAdministerIssueTransitions(Issues.Issue issue) {
    //ACR-ce1e3450877b4429b0ce2dbda894331c
    //ACR-55abe2a611524ae9a840023596757d86
    var possibleTransitions = new HashSet<>(issue.getTransitions().getTransitionsList());

    if (possibleTransitions.containsAll(toTransitionStatus(NEW_RESOLUTION_STATUSES))) {
      return NEW_RESOLUTION_STATUSES;
    }

    //ACR-334ee4c3765549ddbe49693d44616231
    return possibleTransitions.containsAll(toTransitionStatus(OLD_RESOLUTION_STATUSES))
      ? OLD_RESOLUTION_STATUSES
      : List.of();
  }

  private static Set<String> toTransitionStatus(List<ResolutionStatus> resolutions) {
    return resolutions.stream()
      .map(resolution -> transitionByResolutionStatus.get(resolution).getStatus())
      .collect(Collectors.toSet());
  }

  public void addComment(String configurationScopeId, String issueKey, String text, SonarLintCancelMonitor cancelMonitor) {
    var binding = configurationRepository.getEffectiveBindingOrThrow(configurationScopeId);
    var projectServerIssueStore = storageService.binding(binding).findings();
    boolean isServerIssue = projectServerIssueStore.containsIssue(issueKey);
    if (isServerIssue) {
      addCommentOnServerIssue(configurationScopeId, issueKey, text, cancelMonitor);
    } else {
      var optionalId = asUUID(issueKey);
      if (optionalId.isPresent()) {
        setCommentOnLocalOnlyIssue(configurationScopeId, optionalId.get(), text, cancelMonitor);
      } else {
        //ACR-3fcfc248b89a4396baad448ceca51cb7
        //ACR-7fedd2235d454263966be4b9172ab507
        try {
          addCommentOnServerIssue(configurationScopeId, issueKey, text, cancelMonitor);
        } catch (NotFoundException ex) {
          throw issueNotFoundException(issueKey);
        }
      }
    }
  }

  public boolean reopenIssue(String configurationScopeId, String issueId, boolean isTaintIssue, SonarLintCancelMonitor cancelMonitor) {
    var binding = configurationRepository.getEffectiveBindingOrThrow(configurationScopeId);
    var projectServerIssueStore = storageService.binding(binding).findings();
    boolean isServerIssue = projectServerIssueStore.containsIssue(issueId);
    if (isServerIssue) {
      return sonarQubeClientManager.getClientOrThrow(binding.connectionId())
        .withClientApiAndReturn(serverApi -> reopenServerIssue(serverApi, binding, issueId, projectServerIssueStore, isTaintIssue, cancelMonitor));
    } else {
      return reopenLocalIssue(issueId, configurationScopeId, cancelMonitor);
    }
  }

  public boolean reopenAllIssuesForFile(ReopenAllIssuesForFileParams params, SonarLintCancelMonitor cancelMonitor) {
    var configurationScopeId = params.getConfigurationScopeId();
    var ideRelativePath = params.getIdeRelativePath();
    var allIssues = localOnlyIssuesRepository.loadAll(configurationScopeId);
    var issuesForFile = localOnlyIssuesRepository.loadForFile(configurationScopeId, ideRelativePath);
    var issuesToSync = subtract(allIssues, issuesForFile);
    var binding = configurationRepository.getEffectiveBindingOrThrow(configurationScopeId);
    sonarQubeClientManager.getClientOrThrow(binding.connectionId())
      .withClientApi(serverApi -> serverApi.issue().anticipatedTransitions(binding.sonarProjectKey(), issuesToSync, cancelMonitor));
    return localOnlyIssuesRepository.removeAllIssuesForFile(configurationScopeId, ideRelativePath);
  }

  private void removeIssueOnServer(String configurationScopeId, UUID issueId, SonarLintCancelMonitor cancelMonitor) {
    var allIssues = localOnlyIssuesRepository.loadAll(configurationScopeId);
    var issuesToSync = allIssues.stream().filter(it -> !it.getId().equals(issueId)).toList();
    var binding = configurationRepository.getEffectiveBindingOrThrow(configurationScopeId);
    sonarQubeClientManager.getClientOrThrow(binding.connectionId())
      .withClientApi(serverApi -> serverApi.issue().anticipatedTransitions(binding.sonarProjectKey(), issuesToSync, cancelMonitor));
  }

  private void setCommentOnLocalOnlyIssue(String configurationScopeId, UUID issueId, String comment, SonarLintCancelMonitor cancelMonitor) {
    var optionalLocalOnlyIssue = localOnlyIssuesRepository.find(issueId);
    if (optionalLocalOnlyIssue.isPresent()) {
      var commentedIssue = optionalLocalOnlyIssue.get();
      var resolution = commentedIssue.getResolution();
      if (resolution != null) {
        resolution.setComment(comment);
        var issuesToSync = new ArrayList<>(localOnlyIssuesRepository.loadAll(configurationScopeId));
        issuesToSync.replaceAll(issue -> issue.getId().equals(issueId) ? commentedIssue : issue);
        var binding = configurationRepository.getEffectiveBindingOrThrow(configurationScopeId);
        sonarQubeClientManager.getClientOrThrow(binding.connectionId())
          .withClientApi(serverApi -> serverApi.issue().anticipatedTransitions(binding.sonarProjectKey(), issuesToSync, cancelMonitor));
        localOnlyIssuesRepository.storeLocalOnlyIssue(configurationScopeId, commentedIssue);
      }
    } else {
      throw issueNotFoundException(issueId.toString());
    }
  }

  private static ResponseErrorException issueNotFoundException(String issueId) {
    var error = new ResponseError(SonarLintRpcErrorCode.ISSUE_NOT_FOUND, "Issue key " + issueId + " was not found", issueId);
    throw new ResponseErrorException(error);
  }

  private void addCommentOnServerIssue(String configurationScopeId, String issueKey, String comment, SonarLintCancelMonitor cancelMonitor) {
    var binding = configurationRepository.getEffectiveBindingOrThrow(configurationScopeId);
    sonarQubeClientManager.getClientOrThrow(binding.connectionId())
      .withClientApi(serverApi -> serverApi.issue().addComment(issueKey, comment, cancelMonitor));
  }

  private boolean reopenServerIssue(ServerApi connection, Binding binding, String issueId, ProjectServerIssueStore projectServerIssueStore, boolean isTaintIssue,
    SonarLintCancelMonitor cancelMonitor) {
    connection.issue().changeStatus(issueId, Transition.REOPEN, cancelMonitor);
    var serverIssue = projectServerIssueStore.updateIssueResolutionStatus(issueId, isTaintIssue, false);
    serverIssue.ifPresent(issue -> eventPublisher.publishEvent(new ServerIssueStatusChangedEvent(binding.connectionId(), binding.sonarProjectKey(), issue)));
    return true;
  }

  private boolean reopenLocalIssue(String issueId, String configurationScopeId, SonarLintCancelMonitor cancelMonitor) {
    var issueUuidOptional = asUUID(issueId);
    if (issueUuidOptional.isEmpty()) {
      return false;
    }
    var issueUuid = issueUuidOptional.get();
    removeIssueOnServer(configurationScopeId, issueUuid, cancelMonitor);
    return localOnlyIssuesRepository.removeIssue(issueUuid);
  }

  public EffectiveIssueDetailsDto getEffectiveIssueDetails(String configurationScopeId, UUID findingId, SonarLintCancelMonitor cancelMonitor)
    throws IssueNotFoundException, RuleNotFoundException {
    var effectiveBinding = configurationRepository.getEffectiveBinding(configurationScopeId);
    String connectionId = null;
    if (effectiveBinding.isPresent()) {
      connectionId = effectiveBinding.get().connectionId();
    }
    var isMQRMode = severityModeService.isMQRModeForConnection(connectionId);
    var newCodeDefinition = newCodeService.getFullNewCodeDefinition(configurationScopeId).orElseGet(NewCodeDefinition::withAlwaysNew);
    var aiCodeFixFeature = effectiveBinding.flatMap(aiCodeFixService::getFeature);
    var maybeIssue = findingReportingService.findReportedIssue(findingId, newCodeDefinition, isMQRMode, aiCodeFixFeature);
    var maybeHotspot = findingReportingService.findReportedHotspot(findingId, newCodeDefinition, isMQRMode);
    var maybeTaint = taintVulnerabilityTrackingService.getTaintVulnerability(configurationScopeId, findingId, cancelMonitor);

    if (maybeIssue != null) {
      return getFindingDetails(maybeIssue, configurationScopeId, cancelMonitor);
    } else if (maybeHotspot != null) {
      return getFindingDetails(maybeHotspot, configurationScopeId, cancelMonitor);
    } else if (maybeTaint.isPresent()) {
      return getTaintDetails(maybeTaint.get(), configurationScopeId, cancelMonitor);
    }
    throw new IssueNotFoundException("Failed to retrieve finding details. Finding with key '"
      + findingId + "' not found.", findingId);
  }

  private EffectiveIssueDetailsDto getFindingDetails(RaisedFindingDto finding, String configurationScopeId, SonarLintCancelMonitor cancelMonitor) throws RuleNotFoundException {
    var ruleKey = finding.getRuleKey();
    var ruleDetails = activeRulesService.getActiveRuleDetails(configurationScopeId, ruleKey, cancelMonitor);
    var ruleDetailsEnrichedWithActualIssueSeverity = RuleDetails.merging(ruleDetails, finding);
    var effectiveRuleDetails = RuleDetailsAdapter.transform(ruleDetailsEnrichedWithActualIssueSeverity, finding.getRuleDescriptionContextKey());
    return new EffectiveIssueDetailsDto(ruleKey, effectiveRuleDetails.getName(), effectiveRuleDetails.getLanguage(),
      //ACR-20b89e3fe2a44dc596c78ad464856daa
      effectiveRuleDetails.getVulnerabilityProbability(),
      effectiveRuleDetails.getDescription(), effectiveRuleDetails.getParams(), finding.getSeverityMode(), finding.getRuleDescriptionContextKey());
  }

  private EffectiveIssueDetailsDto getTaintDetails(TaintVulnerabilityDto finding, String configurationScopeId, SonarLintCancelMonitor cancelMonitor) throws RuleNotFoundException {
    var ruleKey = finding.getRuleKey();
    var ruleDetails = activeRulesService.getActiveRuleDetails(configurationScopeId, ruleKey, cancelMonitor);
    var ruleDetailsEnrichedWithActualIssueSeverity = RuleDetails.merging(ruleDetails, finding);
    var effectiveRuleDetails = RuleDetailsAdapter.transform(ruleDetailsEnrichedWithActualIssueSeverity, finding.getRuleDescriptionContextKey());
    return new EffectiveIssueDetailsDto(ruleKey, effectiveRuleDetails.getName(), effectiveRuleDetails.getLanguage(),
      effectiveRuleDetails.getVulnerabilityProbability(),
      effectiveRuleDetails.getDescription(), effectiveRuleDetails.getParams(), finding.getSeverityMode(), finding.getRuleDescriptionContextKey());
  }

  @EventListener
  public void onServerEventReceived(SonarServerEventReceivedEvent eventReceived) {
    var connectionId = eventReceived.getConnectionId();
    var serverEvent = eventReceived.getEvent();
    if (serverEvent instanceof IssueChangedEvent issueChangedEvent) {
      handleEvent(connectionId, issueChangedEvent);
    }
  }

  private void handleEvent(String connectionId, IssueChangedEvent event) {
    updateProjectIssueStorage(connectionId, event);
    republishPreviouslyRaisedIssues(connectionId, event);
  }

  private void republishPreviouslyRaisedIssues(String connectionId, IssueChangedEvent event) {
    var isMQRMode = severityModeService.isMQRModeForConnection(connectionId);
    var boundScopes = configurationRepository.getBoundScopesToConnectionAndSonarProject(connectionId, event.getProjectKey());
    boundScopes.forEach(scope -> {
      var scopeId = scope.getConfigScopeId();
      findingReportingService.updateAndReportIssues(scopeId, previouslyRaisedIssue -> raisedIssueUpdater(previouslyRaisedIssue, isMQRMode, event));
    });
  }

  public static RaisedIssueDto raisedIssueUpdater(RaisedIssueDto previouslyRaisedIssue, boolean isMQRMode, IssueChangedEvent event) {
    var updatedIssue = previouslyRaisedIssue;
    var resolved = event.getResolved();
    var userSeverity = event.getUserSeverity();
    var userType = event.getUserType();
    var impactedIssueKeys = event.getImpactedIssues().stream().map(IssueChangedEvent.Issue::getIssueKey).collect(Collectors.toSet());
    if (resolved != null) {
      UnaryOperator<RaisedIssueDto> issueUpdater = it -> it.builder().withResolution(resolved).buildIssue();
      updatedIssue = updateIssue(updatedIssue, impactedIssueKeys, issueUpdater);
    }
    if (updatedIssue.getSeverityMode().isLeft()) {
      //ACR-360ac74ae0704e7096e98e93e7a4a79e
      //ACR-2380902aaef0412592b61a67f32cb911
      var standardModeDetails = updatedIssue.getSeverityMode().getLeft();
      if (userSeverity != null) {
        UnaryOperator<RaisedIssueDto> issueUpdater = it -> it.builder().withStandardModeDetails(IssueSeverity.valueOf(userSeverity.name()), standardModeDetails.getType())
          .buildIssue();
        updatedIssue = updateIssue(updatedIssue, impactedIssueKeys, issueUpdater);
      }
      if (userType != null) {
        UnaryOperator<RaisedIssueDto> issueUpdater = it -> it.builder().withStandardModeDetails(standardModeDetails.getSeverity(), RuleType.valueOf(userType.name()))
          .buildIssue();
        updatedIssue = updateIssue(updatedIssue, impactedIssueKeys, issueUpdater);
      }
    }
    for (var issue : event.getImpactedIssues()) {
      if (!issue.getImpacts().isEmpty() && isMQRMode && updatedIssue.getSeverityMode().isRight()) {
        var mqrModeDetails = updatedIssue.getSeverityMode().getRight();
        var impacts = issue.getImpacts().entrySet().stream()
          .map(impact -> new ImpactDto(
            SoftwareQuality.valueOf(impact.getKey().name()),
            org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity.valueOf(impact.getValue().name())))
          .toList();
        UnaryOperator<RaisedIssueDto> issueUpdater = it -> it.builder()
          .withMQRModeDetails(mqrModeDetails.getCleanCodeAttribute(), mergeImpacts(it.getSeverityMode().getRight().getImpacts(), impacts)).buildIssue();
        updatedIssue = updateIssue(updatedIssue, impactedIssueKeys, issueUpdater);
      }

    }
    return updatedIssue;
  }

  private static List<ImpactDto> mergeImpacts(List<ImpactDto> currentImpacts, List<ImpactDto> overriddenImpacts) {
    var mergedImpacts = new ArrayList<>(currentImpacts);
    for (var impact : overriddenImpacts) {
      mergedImpacts.removeIf(i -> i.getSoftwareQuality().equals(impact.getSoftwareQuality()));
      mergedImpacts.add(new ImpactDto(impact.getSoftwareQuality(), impact.getImpactSeverity()));
    }

    return mergedImpacts;
  }

  private static RaisedIssueDto updateIssue(RaisedIssueDto issue, Set<String> impactedIssueKeys, UnaryOperator<RaisedIssueDto> issueUpdater) {
    var serverKey = issue.getServerKey();
    if (serverKey != null && impactedIssueKeys.contains(serverKey)) {
      return issueUpdater.apply(issue);
    }
    return issue;
  }

  private void updateProjectIssueStorage(String connectionId, IssueChangedEvent event) {
    var findingsStorage = storageService.connection(connectionId).project(event.getProjectKey()).findings();
    event.getImpactedIssues().forEach(issue -> findingsStorage.updateIssue(issue.getIssueKey(), storedIssue -> {
      var userSeverity = event.getUserSeverity();
      if (userSeverity != null) {
        storedIssue.setUserSeverity(userSeverity);
      }
      var userType = event.getUserType();
      if (userType != null) {
        storedIssue.setType(userType);
      }
      var resolved = event.getResolved();
      if (resolved != null) {
        storedIssue.setResolved(resolved);
      }
      var impacts = issue.getImpacts();
      if (!impacts.isEmpty()) {
        storedIssue.setImpacts(mergeImpacts(storedIssue.getImpacts(), impacts));
      }
    }));
  }

  private static Map<org.sonarsource.sonarlint.core.commons.SoftwareQuality, ImpactSeverity> mergeImpacts(
    Map<org.sonarsource.sonarlint.core.commons.SoftwareQuality, ImpactSeverity> defaultImpacts,
    Map<org.sonarsource.sonarlint.core.commons.SoftwareQuality, ImpactSeverity> overriddenImpacts) {
    var mergedImpacts = new EnumMap<org.sonarsource.sonarlint.core.commons.SoftwareQuality, ImpactSeverity>(org.sonarsource.sonarlint.core.commons.SoftwareQuality.class);
    if (!defaultImpacts.isEmpty()) {
      mergedImpacts = new EnumMap<>(defaultImpacts);
    }

    for (var entry : overriddenImpacts.entrySet()) {
      var quality = org.sonarsource.sonarlint.core.commons.SoftwareQuality.valueOf(entry.getKey().name());
      var severity = ImpactSeverity.mapSeverity(entry.getValue().name());
      mergedImpacts.put(quality, severity);
    }

    return Collections.unmodifiableMap(mergedImpacts);
  }

  private static Optional<UUID> asUUID(String key) {
    try {
      return Optional.of(UUID.fromString(key));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
