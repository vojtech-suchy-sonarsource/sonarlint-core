/*
ACR-14cf859ce7b4479ab384c42d7514515b
ACR-1b5b617bc47b4c198d4701794bc030d7
ACR-ca1b3c3c80314a9f95ff869f1cdac811
ACR-999d3f8d81dd455d8dc52cd8820042d8
ACR-12efb245d8334f228e7dd0b1a22ee500
ACR-844212c2e82a46c49b49e97ecc887ba0
ACR-ba09715942124aeeb9734b228c24346a
ACR-ccf474581c1f4ec594e50c1882a57651
ACR-0a6aa8f22dcc440aac6894b9f9061fe4
ACR-166f9bd291574b45a9c0486ef70d41fa
ACR-809cc9f710c14396ad9ace7ca1d49612
ACR-df1804b737504776968299381bf62f5e
ACR-f3d414c48a1c468ba2d492858cf713f1
ACR-987628d0bbd242268e762be80a4ccdae
ACR-0feb841c9090409d8b1972f940996f56
ACR-e78e7a27a5a749be91c3565d4ea741c3
ACR-d52d2d7860734dd0a808240c93c62457
 */
package org.sonarsource.sonarlint.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.connection.SonarQubeClient;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationUpdatedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionCredentialsChangedEvent;
import org.sonarsource.sonarlint.core.http.ConnectionAwareHttpClientProvider;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverconnection.ServerVersionAndStatusChecker;
import org.springframework.context.event.EventListener;

public class SonarQubeClientManager {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final ConnectionConfigurationRepository connectionRepository;
  private final ConnectionAwareHttpClientProvider awareHttpClientProvider;
  private final HttpClientProvider httpClientProvider;
  private final SonarLintRpcClient client;
  private final SonarCloudActiveEnvironment sonarCloudActiveEnvironment;
  private final Map<String, SonarQubeClient> clientsByConnectionId = new ConcurrentHashMap<>();

  public SonarQubeClientManager(ConnectionConfigurationRepository connectionRepository, ConnectionAwareHttpClientProvider awareHttpClientProvider,
    HttpClientProvider httpClientProvider, SonarCloudActiveEnvironment sonarCloudActiveEnvironment, SonarLintRpcClient client) {
    this.connectionRepository = connectionRepository;
    this.awareHttpClientProvider = awareHttpClientProvider;
    this.httpClientProvider = httpClientProvider;
    this.client = client;
    this.sonarCloudActiveEnvironment = sonarCloudActiveEnvironment;
  }

  /*ACR-17b662e4a7c54207b7b92f01583c050c
ACR-067b38a19d084f6a8a031284a560b4bc
   */
  public SonarQubeClient getClientOrThrow(String connectionId) {
    return clientsByConnectionId.computeIfAbsent(connectionId, connId ->
      Optional.ofNullable(getSonarQubeClient(connId))
        .orElseThrow(() -> new ResponseErrorException(new ResponseError(SonarLintRpcErrorCode.CONNECTION_NOT_FOUND, "Connection '" + connectionId + "' is gone", connectionId))));
  }

  public void withActiveClient(String connectionId, Consumer<ServerApi> serverApiConsumer) {
    getValidClient(connectionId).ifPresent(connection -> connection.withClientApi(serverApiConsumer));
  }

  public <T> Optional<T> withActiveClientAndReturn(String connectionId, Function<ServerApi, T> serverApiConsumer) {
    return getValidClient(connectionId).map(connection -> connection.withClientApiAndReturn(serverApiConsumer));
  }

  public <T> Optional<T> withActiveClientFlatMapOptionalAndReturn(String connectionId, Function<ServerApi, Optional<T>> serverApiConsumer) {
    return getValidClient(connectionId).map(connection -> connection.withClientApiAndReturn(serverApiConsumer)).flatMap(Function.identity());
  }

  private Optional<SonarQubeClient> getValidClient(String connectionId) {
    return Optional.ofNullable(clientsByConnectionId.computeIfAbsent(connectionId, this::getSonarQubeClient))
      .filter(connection -> isConnectionActive(connectionId, connection));
  }

  @Nullable
  private SonarQubeClient getSonarQubeClient(String connectionId) {
    var connection = connectionRepository.getConnectionById(connectionId);
    if (connection == null) {
      LOG.debug("Connection '{}' is gone", connectionId);
      return null;
    }
    var endpointParams = connection.getEndpointParams();
    var isBearerSupported = checkIfBearerIsSupported(endpointParams);
    var serverApi = getServerApi(connectionId, endpointParams, isBearerSupported);
    return new SonarQubeClient(connectionId, serverApi, client);
  }

  private static boolean isConnectionActive(String connectionId, SonarQubeClient connection) {
    var isValid = connection.isActive();
    if (!isValid) {
      LOG.debug("Connection '{}' is invalid", connectionId);
    }
    return isValid;
  }

  @Nullable
  private ServerApi getServerApi(String connectionId, EndpointParams endpointParams, boolean isBearerSupported) {
    try {
      return new ServerApi(endpointParams, awareHttpClientProvider.getHttpClient(connectionId, isBearerSupported));
    } catch (IllegalStateException e) {
      return null;
    }
  }

  public ServerApi getForTransientConnection(Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection) {
    var endpointParams = transientConnection.map(
      sq -> new EndpointParams(sq.getServerUrl(), null, false, null),
      sc -> {
        var region = SonarCloudRegion.valueOf(sc.getRegion().toString());
        return new EndpointParams(sonarCloudActiveEnvironment.getUri(region).toString(), sonarCloudActiveEnvironment.getApiUri(region).toString(), true, sc.getOrganization());
      });
    var httpClient = transientConnection
      .map(TransientSonarQubeConnectionDto::getCredentials, TransientSonarCloudConnectionDto::getCredentials)
      .map(
        tokenDto -> {
          var isBearerSupported = checkIfBearerIsSupported(endpointParams);
          return httpClientProvider.getHttpClientWithPreemptiveAuth(tokenDto.getToken(), isBearerSupported);
        },
        userPass -> httpClientProvider.getHttpClientWithPreemptiveAuth(userPass.getUsername(), userPass.getPassword()));
    return new ServerApi(new ServerApiHelper(endpointParams, httpClient));
  }

  private boolean checkIfBearerIsSupported(EndpointParams params) {
    if (params.isSonarCloud()) {
      return true;
    }
    var httpClient = awareHttpClientProvider.getHttpClient();
    var cancelMonitor = new SonarLintCancelMonitor();
    var serverApi = new ServerApi(params, httpClient);
    var status = serverApi.system().getStatus(cancelMonitor);
    var serverChecker = new ServerVersionAndStatusChecker(serverApi);
    return serverChecker.isSupportingBearer(status);
  }

  @EventListener
  public void onConnectionRemoved(ConnectionConfigurationRemovedEvent event) {
    clientsByConnectionId.remove(event.getRemovedConnectionId());
  }

  @EventListener
  public void onConnectionUpdated(ConnectionConfigurationUpdatedEvent event) {
    clientsByConnectionId.remove(event.updatedConnectionId());
  }

  @EventListener
  public void onCredentialsChanged(ConnectionCredentialsChangedEvent event) {
    clientsByConnectionId.remove(event.getConnectionId());
  }
}
