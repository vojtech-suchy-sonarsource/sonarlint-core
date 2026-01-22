/*
ACR-8491049c014440be8db764b1d39b4622
ACR-048dee12ffd647f9ad38f81d1f266057
ACR-5879884a4a8247078089094731e929ad
ACR-e956d7f703524c2685bf5cfce14acc94
ACR-df934c9076b44c89ba165082551d1dd8
ACR-d11391da1982420f90f87b9a1d27b2ff
ACR-2593d1920b4249a5ba9b4800aac11678
ACR-6d6fd4db010847b1ac5ddfebdf733927
ACR-c1ea6c3029d24ac0952b085f80d22b45
ACR-8f520cd85c0643cb978cc812c2b1f62d
ACR-1f39b7f33bae4c20907945f7e3f28fe8
ACR-c128e2274de046abaf663bad38c8e389
ACR-871564dc2c1242dbb0281e6051ff376e
ACR-3f222aa8d7b54ffa8cd9f2a31e0c1931
ACR-23531cc442654285997ae81cb4c8c26c
ACR-202e338813a84c1cba1ecf6820f08fc2
ACR-090d76ee5638425a8d0c1ac53e8dd9ff
 */
package org.sonarsource.sonarlint.core.sca;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.CheckForNull;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.branch.SonarProjectBranchTrackingService;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.DependencyRisksSynchronizedEvent;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.CheckDependencyRiskSupportedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.DependencyRiskTransition;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.OpenUrlInBrowserParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sca.DidChangeDependencyRisksParams;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.sync.ScaSynchronizationService;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;
import org.springframework.context.event.EventListener;

public class DependencyRiskService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final Version SCA_MIN_SQ_VERSION = Version.create("2025.4");

  private final ConfigurationRepository configurationRepository;
  private final ConnectionConfigurationRepository connectionRepository;
  private final StorageService storageService;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final SonarProjectBranchTrackingService branchTrackingService;
  private final ScaSynchronizationService scaSynchronizationService;
  private final SonarLintRpcClient client;
  private final TelemetryService telemetryService;

  public DependencyRiskService(ConfigurationRepository configurationRepository, ConnectionConfigurationRepository connectionRepository, StorageService storageService,
    SonarQubeClientManager sonarQubeClientManager, SonarProjectBranchTrackingService branchTrackingService, ScaSynchronizationService scaSynchronizationService,
    SonarLintRpcClient client, TelemetryService telemetryService) {
    this.configurationRepository = configurationRepository;
    this.connectionRepository = connectionRepository;
    this.storageService = storageService;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.branchTrackingService = branchTrackingService;
    this.scaSynchronizationService = scaSynchronizationService;
    this.client = client;
    this.telemetryService = telemetryService;
  }

  public List<DependencyRiskDto> listAll(String configurationScopeId, boolean shouldRefresh, SonarLintCancelMonitor cancelMonitor) {
    return configurationRepository.getEffectiveBinding(configurationScopeId)
      .map(binding -> loadDependencyRisks(configurationScopeId, binding, shouldRefresh, cancelMonitor))
      .orElseGet(Collections::emptyList);
  }

  @EventListener
  public void onDependencyRisksSynchronized(DependencyRisksSynchronizedEvent event) {
    var summary = event.summary();
    var connectionId = event.connectionId();
    var sonarProjectKey = event.sonarProjectKey();
    configurationRepository.getBoundScopesToConnectionAndSonarProject(connectionId, sonarProjectKey)
      .forEach(boundScope -> client.didChangeDependencyRisks(new DidChangeDependencyRisksParams(boundScope.getConfigScopeId(), summary.deletedItemIds(),
        summary.addedItems().stream()
          .map(DependencyRiskService::toDto)
          .toList(),
        summary.updatedItems().stream()
          .map(DependencyRiskService::toDto)
          .toList())));
  }

  public CheckDependencyRiskSupportedResponse checkSupported(String configurationScopeId) {
    var configScope = configurationRepository.getConfigurationScope(configurationScopeId);
    if (configScope == null) {
      var error = new ResponseError(SonarLintRpcErrorCode.CONFIG_SCOPE_NOT_FOUND, "The provided configuration scope does not exist: " + configurationScopeId, configurationScopeId);
      throw new ResponseErrorException(error);
    }
    var effectiveBinding = configurationRepository.getEffectiveBinding(configurationScopeId);
    if (effectiveBinding.isEmpty()) {
      return new CheckDependencyRiskSupportedResponse(false, "The project is not bound, please bind it to SonarQube Server Enterprise 2025.4 or higher");
    }
    var connectionId = effectiveBinding.get().connectionId();
    var connection = connectionRepository.getConnectionById(connectionId);
    if (connection == null) {
      var error = new ResponseError(SonarLintRpcErrorCode.CONNECTION_NOT_FOUND, "The provided configuration scope is bound to an unknown connection: " + connectionId,
        connectionId);
      throw new ResponseErrorException(error);
    }
    var optServerInfo = storageService.connection(connectionId).serverInfo().read();
    if (optServerInfo.isEmpty()) {
      var error = new ResponseError(SonarLintRpcErrorCode.CONNECTION_NOT_FOUND, "Could not retrieve server information for connection",
        connectionId);
      throw new ResponseErrorException(error);
    }
    var serverInfo = optServerInfo.get();
    if (!connection.getEndpointParams().isSonarCloud() && !serverInfo.version().satisfiesMinRequirement(SCA_MIN_SQ_VERSION)) {
      return new CheckDependencyRiskSupportedResponse(false, "The connected SonarQube Server version is lower than the minimum supported version 2025.4");
    }
    if (!serverInfo.hasFeature(Feature.SCA)) {
      return new CheckDependencyRiskSupportedResponse(false, "The connected SonarQube Server does not have Advanced Security enabled (requires Enterprise edition or higher)");
    }
    return new CheckDependencyRiskSupportedResponse(true, null);
  }

  private List<DependencyRiskDto> loadDependencyRisks(String configurationScopeId, Binding binding, boolean shouldRefresh, SonarLintCancelMonitor cancelMonitor) {
    return branchTrackingService.awaitEffectiveSonarProjectBranch(configurationScopeId)
      .map(matchedBranch -> {
        if (shouldRefresh) {
          sonarQubeClientManager.withActiveClient(binding.connectionId(),
            serverApi -> scaSynchronizationService.synchronize(serverApi, binding.connectionId(), binding.sonarProjectKey(), matchedBranch, cancelMonitor));
        }
        var projectStorage = storageService.binding(binding);
        return projectStorage.findings().loadDependencyRisks(matchedBranch)
          .stream().map(DependencyRiskService::toDto)
          .toList();
      }).orElseGet(Collections::emptyList);
  }

  private static DependencyRiskDto toDto(ServerDependencyRisk serverDependencyRisk) {
    return new DependencyRiskDto(
      serverDependencyRisk.key(),
      DependencyRiskDto.Type.valueOf(serverDependencyRisk.type().name()),
      DependencyRiskDto.Severity.valueOf(serverDependencyRisk.severity().name()),
      DependencyRiskDto.SoftwareQuality.valueOf(serverDependencyRisk.quality().name()),
      DependencyRiskDto.Status.valueOf(serverDependencyRisk.status().name()),
      serverDependencyRisk.packageName(),
      serverDependencyRisk.packageVersion(),
      serverDependencyRisk.vulnerabilityId(),
      serverDependencyRisk.cvssScore(),
      serverDependencyRisk.transitions().stream()
        .map(transition -> DependencyRiskDto.Transition.valueOf(transition.name()))
        .toList());
  }

  public void changeStatus(String configurationScopeId, UUID dependencyRiskKey, DependencyRiskTransition transition, @CheckForNull String comment,
    SonarLintCancelMonitor cancelMonitor) {
    var binding = configurationRepository.getEffectiveBindingOrThrow(configurationScopeId);
    var serverConnection = sonarQubeClientManager.getClientOrThrow(binding.connectionId());
    var projectServerIssueStore = storageService.binding(binding).findings();
    var branchName = branchTrackingService.awaitEffectiveSonarProjectBranch(configurationScopeId);

    if (branchName.isEmpty()) {
      throw new IllegalArgumentException("Could not determine matched branch for configuration scope " + configurationScopeId);
    }

    var dependencyRisks = projectServerIssueStore.loadDependencyRisks(branchName.get());
    var dependencyRiskOpt = dependencyRisks.stream().filter(risk -> risk.key().equals(dependencyRiskKey)).findFirst();

    if (dependencyRiskOpt.isEmpty()) {
      throw new DependencyRiskNotFoundException("Dependency Risk with key " + dependencyRiskKey + " was not found", dependencyRiskKey.toString());
    }

    var dependencyRisk = dependencyRiskOpt.get();

    if (!dependencyRisk.transitions().contains(adaptTransition(transition))) {
      throw new IllegalArgumentException("Transition " + transition + " is not allowed for this dependency risk");
    }

    if ((transition == DependencyRiskTransition.ACCEPT || transition == DependencyRiskTransition.SAFE)
      && (comment == null || comment.isBlank())) {
      throw new IllegalArgumentException("Comment is required for ACCEPT and SAFE transitions");
    }

    LOG.info("Changing status for dependency risk {} to {} with comment: {}", dependencyRiskKey, transition, comment);

    var newStatus = switch (transition) {
      case ACCEPT -> ServerDependencyRisk.Status.ACCEPT;
      case SAFE -> ServerDependencyRisk.Status.SAFE;
      case REOPEN -> ServerDependencyRisk.Status.OPEN;
      case CONFIRM -> ServerDependencyRisk.Status.CONFIRM;
    };
    var updatedDependencyRisk = dependencyRisk.withStatus(newStatus);

    serverConnection.withClientApi(serverApi -> {
      serverApi.sca().changeStatus(dependencyRiskKey, transition.name(), comment, cancelMonitor);
      projectServerIssueStore.updateDependencyRiskStatus(dependencyRiskKey, newStatus, updatedDependencyRisk.transitions());
      client.didChangeDependencyRisks(new DidChangeDependencyRisksParams(configurationScopeId, Set.of(), List.of(), List.of(toDto(updatedDependencyRisk))));
    });
  }

  private static ServerDependencyRisk.Transition adaptTransition(DependencyRiskTransition transition) {
    return switch (transition) {
      case REOPEN -> ServerDependencyRisk.Transition.REOPEN;
      case CONFIRM -> ServerDependencyRisk.Transition.CONFIRM;
      case ACCEPT -> ServerDependencyRisk.Transition.ACCEPT;
      case SAFE -> ServerDependencyRisk.Transition.SAFE;
    };
  }

  public void openDependencyRiskInBrowser(String configurationScopeId, UUID dependencyKey) {
    var effectiveBinding = configurationRepository.getEffectiveBinding(configurationScopeId);
    var endpointParams = effectiveBinding.flatMap(binding -> connectionRepository.getEndpointParams(binding.connectionId()));
    if (effectiveBinding.isEmpty() || endpointParams.isEmpty()) {
      throw new IllegalArgumentException(String.format("Configuration scope '%s' is not bound properly, unable to open dependency risk", configurationScopeId));
    }
    var branchName = branchTrackingService.awaitEffectiveSonarProjectBranch(configurationScopeId);
    if (branchName.isEmpty()) {
      throw new IllegalArgumentException(String.format("Configuration scope %s has no matching branch, unable to open dependency risk", configurationScopeId));
    }

    var url = buildDependencyRiskBrowseUrl(effectiveBinding.get().sonarProjectKey(), branchName.get(), dependencyKey, endpointParams.get());

    client.openUrlInBrowser(new OpenUrlInBrowserParams(url));

    telemetryService.dependencyRiskInvestigatedRemotely();
  }

  static String buildDependencyRiskBrowseUrl(String projectKey, String branch, UUID dependencyKey, EndpointParams endpointParams) {
    var relativePath = new StringBuilder("/dependency-risks/")
      .append(UrlUtils.urlEncode(dependencyKey.toString()))
      .append("/what?id=")
      .append(UrlUtils.urlEncode(projectKey))
      .append("&branch=")
      .append(UrlUtils.urlEncode(branch))
      .toString();

    return ServerApiHelper.concat(endpointParams.getBaseUrl(), relativePath);
  }

  public static class DependencyRiskNotFoundException extends RuntimeException {
    private final String key;

    public DependencyRiskNotFoundException(String message, String key) {
      super(message);
      this.key = key;
    }

    public String getKey() {
      return key;
    }
  }
}
