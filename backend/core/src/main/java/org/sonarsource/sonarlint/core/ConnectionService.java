/*
ACR-d4a2baeaf7654737a7574bd164682b22
ACR-86c4a973f78847bda79bb30988e0e986
ACR-77824bfd1c414debb1c5e983562c587a
ACR-5afd86cad6e946238cb4cefd9e587bce
ACR-0e705503ae004757b30e65f13cd59c0a
ACR-053aa069487f465aa084102093aa5be8
ACR-8d443a0556d54373aa3889b7b5939979
ACR-2daca89108a44558aab96474db5969d1
ACR-d735e688390a4d47a356863b943ce18c
ACR-28f309650e6743099c586e7a91cb6a65
ACR-b214dd4a8cfa49e19c018929cf25c53e
ACR-579f40b77040412cbb915f5dbd7b427e
ACR-61fa29d03c1a433db7da14c0e20c077b
ACR-a7ba395879f94c1fa9881f48e52a2d34
ACR-0dda8948953747ae805eadb5017583c2
ACR-7418957fd3314cbe98c3f54a6ba4074f
ACR-cb7f64d35e4b451ca5d40e070025f402
 */
package org.sonarsource.sonarlint.core;

import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.validation.InvalidFields;
import org.sonarsource.sonarlint.core.commons.validation.RegexpValidator;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationAddedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationUpdatedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionCredentialsChangedEvent;
import org.sonarsource.sonarlint.core.repository.connection.AbstractConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.SonarQubeConnectionConfiguration;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarCloudConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.SonarProjectDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.validate.ValidateConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.serverapi.component.ServerProject;
import org.sonarsource.sonarlint.core.serverapi.exception.UnauthorizedException;
import org.sonarsource.sonarlint.core.serverconnection.ServerVersionAndStatusChecker;
import org.springframework.context.ApplicationEventPublisher;

import static java.util.stream.Collectors.toMap;
import static org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode.UNAUTHORIZED;

public class ConnectionService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  /*ACR-73e778062710429aad44ec603422d610
ACR-b899293e8f88480aac6450bab5076742
   */
  private static final RegexpValidator REGEXP_VALIDATOR = new RegexpValidator("[a-z0-9\\-]+");

  private final ApplicationEventPublisher applicationEventPublisher;
  private final ConnectionConfigurationRepository repository;
  private final SonarCloudActiveEnvironment sonarCloudActiveEnvironment;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final TokenGeneratorHelper tokenGeneratorHelper;

  @Inject
  public ConnectionService(ApplicationEventPublisher applicationEventPublisher, ConnectionConfigurationRepository repository, InitializeParams params,
    SonarCloudActiveEnvironment sonarCloudActiveEnvironment, TokenGeneratorHelper tokenGeneratorHelper, SonarQubeClientManager sonarQubeClientManager) {
    this(applicationEventPublisher, repository, params.getSonarQubeConnections(), params.getSonarCloudConnections(), sonarCloudActiveEnvironment, sonarQubeClientManager,
      tokenGeneratorHelper);
  }

  ConnectionService(ApplicationEventPublisher applicationEventPublisher, ConnectionConfigurationRepository repository,
    @Nullable List<SonarQubeConnectionConfigurationDto> initSonarQubeConnections, @Nullable List<SonarCloudConnectionConfigurationDto> initSonarCloudConnections,
    SonarCloudActiveEnvironment sonarCloudActiveEnvironment, SonarQubeClientManager sonarQubeClientManager, TokenGeneratorHelper tokenGeneratorHelper) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.repository = repository;
    this.sonarCloudActiveEnvironment = sonarCloudActiveEnvironment;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.tokenGeneratorHelper = tokenGeneratorHelper;
    if (initSonarQubeConnections != null) {
      initSonarQubeConnections.forEach(c -> repository.addOrReplace(adapt(c)));
    }
    if (initSonarCloudConnections != null) {
      initSonarCloudConnections.forEach(c -> repository.addOrReplace(adapt(c)));
    }
  }

  private static SonarQubeConnectionConfiguration adapt(SonarQubeConnectionConfigurationDto sqDto) {
    return new SonarQubeConnectionConfiguration(sqDto.getConnectionId(), sqDto.getServerUrl(), sqDto.getDisableNotifications());
  }

  private SonarCloudConnectionConfiguration adapt(SonarCloudConnectionConfigurationDto scDto) {
    var region = SonarCloudRegion.valueOf(scDto.getRegion().toString());
    return new SonarCloudConnectionConfiguration(sonarCloudActiveEnvironment.getUri(region), sonarCloudActiveEnvironment.getApiUri(region), scDto.getConnectionId(),
      scDto.getOrganization(), region, scDto.isDisableNotifications());
  }

  private static void putAndLogIfDuplicateId(Map<String, AbstractConnectionConfiguration> map, AbstractConnectionConfiguration config) {
    if (map.put(config.getConnectionId(), config) != null) {
      LOG.error("Duplicate connection registered: {}", config.getConnectionId());
    }
  }

  public void didUpdateConnections(List<SonarQubeConnectionConfigurationDto> sonarQubeConnections,
    List<SonarCloudConnectionConfigurationDto> sonarCloudConnections) {
    var newConnectionsById = new HashMap<String, AbstractConnectionConfiguration>();
    sonarQubeConnections.forEach(config -> putAndLogIfDuplicateId(newConnectionsById, adapt(config)));
    sonarCloudConnections.forEach(config -> putAndLogIfDuplicateId(newConnectionsById, adapt(config)));

    var previousConnectionsById = repository.getConnectionsById();

    var updatedConnections = newConnectionsById.entrySet().stream()
      .filter(e -> previousConnectionsById.containsKey(e.getKey()))
      .filter(e -> !previousConnectionsById.get(e.getKey()).equals(e.getValue()))
      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    var addedConnections = newConnectionsById.entrySet().stream()
      .filter(e -> !previousConnectionsById.containsKey(e.getKey()))
      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    var removedConnectionIds = new HashSet<>(previousConnectionsById.keySet());
    removedConnectionIds.removeAll(newConnectionsById.keySet());

    updatedConnections.values().forEach(this::updateConnection);
    addedConnections.values().forEach(this::addConnection);
    removedConnectionIds.forEach(this::removeConnection);
  }

  public void didChangeCredentials(String connectionId) {
    applicationEventPublisher.publishEvent(new ConnectionCredentialsChangedEvent(connectionId));
  }

  private void addConnection(AbstractConnectionConfiguration connectionConfiguration) {
    repository.addOrReplace(connectionConfiguration);
    LOG.debug("Connection '{}' added", connectionConfiguration.getConnectionId());
    applicationEventPublisher.publishEvent(new ConnectionConfigurationAddedEvent(connectionConfiguration.getConnectionId(), connectionConfiguration.getKind()));
  }

  private void removeConnection(String removedConnectionId) {
    var removed = repository.remove(removedConnectionId);
    if (removed == null) {
      LOG.debug("Attempt to remove connection '{}' that was not registered. Possibly a race condition?", removedConnectionId);
    } else {
      LOG.debug("Connection '{}' removed", removedConnectionId);
      applicationEventPublisher.publishEvent(new ConnectionConfigurationRemovedEvent(removedConnectionId));
    }
  }

  private void updateConnection(AbstractConnectionConfiguration connectionConfiguration) {
    var connectionId = connectionConfiguration.getConnectionId();
    var previous = repository.addOrReplace(connectionConfiguration);
    if (previous == null) {
      LOG.debug("Attempt to update connection '{}' that was not registered. Possibly a race condition?", connectionId);
      applicationEventPublisher.publishEvent(new ConnectionConfigurationAddedEvent(connectionConfiguration.getConnectionId(), connectionConfiguration.getKind()));
    } else {
      LOG.debug("Connection '{}' updated", previous.getConnectionId());
      applicationEventPublisher.publishEvent(new ConnectionConfigurationUpdatedEvent(connectionConfiguration.getConnectionId()));
    }
  }

  public ValidateConnectionResponse validateConnection(Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection,
    SonarLintCancelMonitor cancelMonitor) {
    try {
      var serverApi = sonarQubeClientManager.getForTransientConnection(transientConnection);
      var serverChecker = new ServerVersionAndStatusChecker(serverApi);
      serverChecker.checkVersionAndStatus(cancelMonitor);
      var validateCredentials = serverApi.authentication().validate(cancelMonitor);
      if (validateCredentials.success() && transientConnection.isRight()) {
        var organizationKey = transientConnection.getRight().getOrganization();
        if (organizationKey != null) {
          var organization = serverApi.organization().searchOrganization(organizationKey, cancelMonitor);
          if (organization.isEmpty()) {
            return new ValidateConnectionResponse(false, "No organizations found for key: " + organizationKey);
          }
        }
      }
      return new ValidateConnectionResponse(validateCredentials.success(), validateCredentials.message());
    } catch (Exception e) {
      return new ValidateConnectionResponse(false, e.getMessage());
    }
  }

  public HelpGenerateUserTokenResponse helpGenerateUserToken(String serverUrl, @Nullable HelpGenerateUserTokenParams.Utm utm, SonarLintCancelMonitor cancelMonitor) {
    if (utm != null) {
      var invalidFields = validateUtm(utm);
      if (invalidFields.hasInvalidFields()) {
        throw new ResponseErrorException(new ResponseError(ResponseErrorCode.InvalidParams,
          "UTM parameters should match regular expression: [a-z0-9\\-]+",
          invalidFields.getNames()));
      }
    }

    return tokenGeneratorHelper.helpGenerateUserToken(serverUrl, utm, cancelMonitor);
  }

  private static InvalidFields validateUtm(HelpGenerateUserTokenParams.Utm utm) {
    return REGEXP_VALIDATOR.validateAll(Map.of(
      "utm_medium", utm.getMedium(),
      "utm_source", utm.getSource(),
      "utm_content", utm.getContent(),
      "utm_term", utm.getTerm()));
  }

  public List<SonarProjectDto> getAllProjects(Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection, SonarLintCancelMonitor cancelMonitor) {
    var serverApi = sonarQubeClientManager.getForTransientConnection(transientConnection);

    try {
      return serverApi.component().getAllProjects(cancelMonitor)
        .stream().map(serverProject -> new SonarProjectDto(serverProject.key(), serverProject.name()))
        .toList();
    } catch (UnauthorizedException e) {
      throw new ResponseErrorException(new ResponseError(UNAUTHORIZED, "The authorization has failed. Please check your credentials.", null));
    }
  }

  public Map<String, String> getProjectNamesByKey(Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection,
    List<String> projectKeys, SonarLintCancelMonitor cancelMonitor) {
    var serverApi = sonarQubeClientManager.getForTransientConnection(transientConnection);
    var projectNamesByKey = new HashMap<String, String>();
    projectKeys.forEach(key -> {
      var projectName = serverApi.component().getProject(key, cancelMonitor).map(ServerProject::name).orElse(null);
      projectNamesByKey.put(key, projectName);
    });
    return projectNamesByKey;
  }
}
