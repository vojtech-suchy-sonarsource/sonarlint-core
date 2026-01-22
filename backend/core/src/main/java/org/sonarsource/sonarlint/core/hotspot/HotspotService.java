/*
ACR-27f44c358a5d48eab4b1a2f55874b5d1
ACR-7db86a2ecdb24513a346f5acbc6651dd
ACR-e6cd619eec4646a5bdbfb3326e9d926f
ACR-031eaa857e174a7e8273c4716ad0d1e7
ACR-9649986f4f9a49e88b369838ff2f2ee0
ACR-b19d1d622f464f03b3e5dc3dfc761721
ACR-c09253f22a814286a62c24afb36e7699
ACR-6dc21232693549f38e05ff23136ba1eb
ACR-b9da49c710bd4b73ba514f54241bffee
ACR-1363eb2f83e84ce48c3da3a54ffac1bd
ACR-c763113979e847a9a41a0e47a70868b7
ACR-60114b6e73d6413f87fc81913146c0bd
ACR-236f1af709ad42f59d652dccdb272c75
ACR-adc7a2c664cd4ed8a1a93dd4b94cfc42
ACR-e1188127ca9a44c28d1a6bb95362ca4e
ACR-72935ed2bb6a4389ba6a7e88c571f508
ACR-18d03a8350a24877bcfe58f61ab54198
 */
package org.sonarsource.sonarlint.core.hotspot;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.branch.SonarProjectBranchTrackingService;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.SonarServerEventReceivedEvent;
import org.sonarsource.sonarlint.core.reporting.FindingReportingService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckLocalDetectionSupportedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckStatusChangePermittedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.client.OpenUrlInBrowserParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;
import org.sonarsource.sonarlint.core.serverapi.push.SecurityHotspotChangedEvent;
import org.sonarsource.sonarlint.core.serverapi.push.SecurityHotspotClosedEvent;
import org.sonarsource.sonarlint.core.serverapi.push.SecurityHotspotRaisedEvent;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;
import org.sonarsource.sonarlint.core.tracking.TaintVulnerabilityTrackingService;
import org.springframework.context.event.EventListener;

public class HotspotService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String NO_BINDING_REASON = "The project is not bound, please bind it to SonarQube (Server, Cloud)";
  private static final String REVIEW_STATUS_UPDATE_PERMISSION_MISSING_REASON = "Changing a hotspot's status requires the 'Administer Security Hotspot' permission.";

  private final SonarLintRpcClient client;
  private final ConfigurationRepository configurationRepository;
  private final ConnectionConfigurationRepository connectionRepository;

  private final SonarQubeClientManager sonarQubeClientManager;
  private final TelemetryService telemetryService;
  private final SonarProjectBranchTrackingService branchTrackingService;
  private final FindingReportingService findingReportingService;
  private final StorageService storageService;

  public HotspotService(SonarLintRpcClient client, StorageService storageService, ConfigurationRepository configurationRepository,
    ConnectionConfigurationRepository connectionRepository, SonarQubeClientManager sonarQubeClientManager, TelemetryService telemetryService,
    SonarProjectBranchTrackingService branchTrackingService, FindingReportingService findingReportingService) {
    this.client = client;
    this.storageService = storageService;
    this.configurationRepository = configurationRepository;
    this.connectionRepository = connectionRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.telemetryService = telemetryService;
    this.branchTrackingService = branchTrackingService;
    this.findingReportingService = findingReportingService;
  }

  public void openHotspotInBrowser(String configScopeId, String hotspotKey) {
    var effectiveBinding = configurationRepository.getEffectiveBinding(configScopeId);
    var endpointParams = effectiveBinding.flatMap(binding -> connectionRepository.getEndpointParams(binding.connectionId()));
    if (effectiveBinding.isEmpty() || endpointParams.isEmpty()) {
      LOG.warn("Configuration scope {} is not bound properly, unable to open hotspot", configScopeId);
      return;
    }
    var branchName = branchTrackingService.awaitEffectiveSonarProjectBranch(configScopeId);
    if (branchName.isEmpty()) {
      LOG.warn("Configuration scope {} has no matching branch, unable to open hotspot", configScopeId);
      return;
    }

    var url = buildHotspotUrl(effectiveBinding.get().sonarProjectKey(), branchName.get(), hotspotKey, endpointParams.get());

    client.openUrlInBrowser(new OpenUrlInBrowserParams(url));

    telemetryService.hotspotOpenedInBrowser();
  }

  public CheckLocalDetectionSupportedResponse checkLocalDetectionSupported(String configScopeId) {
    var configScope = configurationRepository.getConfigurationScope(configScopeId);
    if (configScope == null) {
      var error = new ResponseError(SonarLintRpcErrorCode.CONFIG_SCOPE_NOT_FOUND, "The provided configuration scope does not exist: " + configScopeId, configScopeId);
      throw new ResponseErrorException(error);
    }
    var effectiveBinding = configurationRepository.getEffectiveBinding(configScopeId);
    if (effectiveBinding.isEmpty()) {
      return new CheckLocalDetectionSupportedResponse(false, NO_BINDING_REASON);
    }
    var connectionId = effectiveBinding.get().connectionId();
    if (connectionRepository.getConnectionById(connectionId) == null) {
      var error = new ResponseError(SonarLintRpcErrorCode.CONNECTION_NOT_FOUND, "The provided configuration scope is bound to an unknown connection: " + connectionId,
        connectionId);
      throw new ResponseErrorException(error);
    }

    return new CheckLocalDetectionSupportedResponse(true, null);
  }

  public CheckStatusChangePermittedResponse checkStatusChangePermitted(String connectionId, String hotspotKey, SonarLintCancelMonitor cancelMonitor) {
    //ACR-88759afbe81d4cbd850a4f7fe7927dc8
    var connection = connectionRepository.getConnectionById(connectionId);
    var r = sonarQubeClientManager.getClientOrThrow(connectionId)
      .withClientApiAndReturn(serverApi -> serverApi.hotspot().show(hotspotKey, cancelMonitor));
    var allowedStatuses = HotspotReviewStatus.allowedStatusesOn(connection.getKind());
    //ACR-7dbc23bc890e4ec183619d15d0d560e2
    //ACR-b7dd7503410947ceb22159421c15df83
    return toResponse(r.canChangeStatus, allowedStatuses);
  }

  private static CheckStatusChangePermittedResponse toResponse(boolean canChangeStatus, List<HotspotReviewStatus> coreStatuses) {
    return new CheckStatusChangePermittedResponse(canChangeStatus,
      canChangeStatus ? null : REVIEW_STATUS_UPDATE_PERMISSION_MISSING_REASON,
      coreStatuses.stream().map(s -> HotspotStatus.valueOf(s.name()))
        //ACR-664c30266e38449ca09ae3ba18120a2c
        .sorted()
        .toList());
  }

  public void changeStatus(String configurationScopeId, String hotspotKey, HotspotReviewStatus newStatus, SonarLintCancelMonitor cancelMonitor) {
    var effectiveBindingOpt = configurationRepository.getEffectiveBinding(configurationScopeId);
    if (effectiveBindingOpt.isEmpty()) {
      LOG.debug("No binding for config scope {}", configurationScopeId);
      return;
    }
    sonarQubeClientManager.withActiveClient(effectiveBindingOpt.get().connectionId(), serverApi -> {
      serverApi.hotspot().changeStatus(hotspotKey, newStatus, cancelMonitor);
      saveStatusInStorage(effectiveBindingOpt.get(), hotspotKey, newStatus);
      telemetryService.hotspotStatusChanged();
    });
  }

  private void saveStatusInStorage(Binding binding, String hotspotKey, HotspotReviewStatus newStatus) {
    storageService.binding(binding)
      .findings()
      .changeHotspotStatus(hotspotKey, newStatus);
  }

  static String buildHotspotUrl(String projectKey, String branch, String hotspotKey, EndpointParams endpointParams) {
    var relativePath = (endpointParams.isSonarCloud() ? "/project/security_hotspots?id=" : "/security_hotspots?id=")
      + UrlUtils.urlEncode(projectKey)
      + "&branch="
      + UrlUtils.urlEncode(branch)
      + "&hotspots="
      + UrlUtils.urlEncode(hotspotKey);

    return ServerApiHelper.concat(endpointParams.getBaseUrl(), relativePath);
  }

  @EventListener
  public void onServerEventReceived(SonarServerEventReceivedEvent event) {
    var connectionId = event.getConnectionId();
    var serverEvent = event.getEvent();
    if (serverEvent instanceof SecurityHotspotChangedEvent hotspotChangedEvent) {
      updateStorage(connectionId, hotspotChangedEvent);
      republishPreviouslyRaisedHotspots(connectionId, hotspotChangedEvent);
    } else if (serverEvent instanceof SecurityHotspotClosedEvent hotspotClosedEvent) {
      updateStorage(connectionId, hotspotClosedEvent);
      republishPreviouslyRaisedHotspots(connectionId, hotspotClosedEvent);
    } else if (serverEvent instanceof SecurityHotspotRaisedEvent hotspotRaisedEvent) {
      //ACR-a5b35e263a0c4f309989c30e3a024903
      updateStorage(connectionId, hotspotRaisedEvent);
    }
  }

  private void updateStorage(String connectionId, SecurityHotspotRaisedEvent event) {
    var hotspot = new ServerHotspot(
      event.getHotspotKey(),
      event.getRuleKey(),
      event.getMainLocation().getMessage(),
      event.getMainLocation().getFilePath(),
      TaintVulnerabilityTrackingService.adapt(event.getMainLocation().getTextRange()),
      event.getCreationDate(),
      event.getStatus(),
      event.getVulnerabilityProbability(),
      null);
    var projectKey = event.getProjectKey();
    storageService.connection(connectionId).project(projectKey).findings().insert(event.getBranch(), hotspot);
  }

  private void updateStorage(String connectionId, SecurityHotspotClosedEvent event) {
    var projectKey = event.getProjectKey();
    storageService.connection(connectionId).project(projectKey).findings().deleteHotspot(event.getHotspotKey());
  }

  private void updateStorage(String connectionId, SecurityHotspotChangedEvent event) {
    var projectKey = event.getProjectKey();
    storageService.connection(connectionId).project(projectKey).findings().updateHotspot(event.getHotspotKey(), hotspot -> {
      var status = event.getStatus();
      if (status != null) {
        hotspot.setStatus(status);
      }
      var assignee = event.getAssignee();
      if (assignee != null) {
        hotspot.setAssignee(assignee);
      }
    });
  }

  private void republishPreviouslyRaisedHotspots(String connectionId, SecurityHotspotChangedEvent event) {
    var boundScopes = configurationRepository.getBoundScopesToConnectionAndSonarProject(connectionId, event.getProjectKey());
    boundScopes.forEach(scope -> {
      var scopeId = scope.getConfigScopeId();
      findingReportingService.updateAndReportHotspots(scopeId,
        raisedHotspotDto -> changedHotspotUpdater(raisedHotspotDto, event));
    });
  }

  private static RaisedHotspotDto changedHotspotUpdater(RaisedHotspotDto raisedHotspotDto, SecurityHotspotChangedEvent event) {
    if (event.getHotspotKey().equals(raisedHotspotDto.getServerKey())) {
      return raisedHotspotDto.withHotspotStatusAndResolution(HotspotStatus.valueOf(event.getStatus().name()), event.getStatus().isResolved());
    }
    return raisedHotspotDto;
  }

  private void republishPreviouslyRaisedHotspots(String connectionId, SecurityHotspotClosedEvent event) {
    var boundScopes = configurationRepository.getBoundScopesToConnectionAndSonarProject(connectionId, event.getProjectKey());
    boundScopes.forEach(scope -> {
      var scopeId = scope.getConfigScopeId();
      findingReportingService.updateAndReportHotspots(scopeId,
        raisedHotspotDto -> closedHotspotUpdater(raisedHotspotDto, event));
    });
  }

  private static RaisedHotspotDto closedHotspotUpdater(RaisedHotspotDto raisedHotspotDto, SecurityHotspotClosedEvent event) {
    if (event.getHotspotKey().equals(raisedHotspotDto.getServerKey())) {
      return null;
    }
    return raisedHotspotDto;
  }
}
