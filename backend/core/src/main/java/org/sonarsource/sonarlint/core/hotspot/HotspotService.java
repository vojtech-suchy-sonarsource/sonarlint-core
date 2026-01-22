/*
ACR-cce1001887ea47859a4b28122ade9db0
ACR-fd758160ff7b48d29659b78dd0434a43
ACR-5e97b381acec4c22a2a71f030e9b8169
ACR-9b9d8eb67e614c8fb9152b368cb106d7
ACR-4400a523b45146ea88ad3c014d5168b5
ACR-47d28563f0c949a9a341a00f439f804f
ACR-d8531977bbf54872b47ed00369c6b08d
ACR-e423b756d8ca4066b68f12fa11569464
ACR-fd67419b2e7c442a9317169ffb0206a2
ACR-395bf8502d974202bdf1282bf1ab6fee
ACR-82e57802d1d74e36849249bd04b52c0c
ACR-cd3d6f3413fd4d7b80e531c61a89f41a
ACR-c21023b0c9a64dbeb00d3da3dd046deb
ACR-39614d708f9549ea9876394542a7b0d1
ACR-bdf1aff34b33461d940acc77facc2c27
ACR-9de52424cfa54c9faa1e49aaeb93ce7c
ACR-4aec55ff346247bea50a0d6aa1463944
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
    //ACR-7398cbef73a24ac1a01910983621be30
    var connection = connectionRepository.getConnectionById(connectionId);
    var r = sonarQubeClientManager.getClientOrThrow(connectionId)
      .withClientApiAndReturn(serverApi -> serverApi.hotspot().show(hotspotKey, cancelMonitor));
    var allowedStatuses = HotspotReviewStatus.allowedStatusesOn(connection.getKind());
    //ACR-a4b03b55fcfa404496888d9a4dc4d125
    //ACR-3d72fba2ab0b4dc6bd5c0c17647adc9d
    return toResponse(r.canChangeStatus, allowedStatuses);
  }

  private static CheckStatusChangePermittedResponse toResponse(boolean canChangeStatus, List<HotspotReviewStatus> coreStatuses) {
    return new CheckStatusChangePermittedResponse(canChangeStatus,
      canChangeStatus ? null : REVIEW_STATUS_UPDATE_PERMISSION_MISSING_REASON,
      coreStatuses.stream().map(s -> HotspotStatus.valueOf(s.name()))
        //ACR-9034f2426ad4440fadf5e247c4772efc
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
      //ACR-253453bc6351444dbd36159d9f07eafd
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
