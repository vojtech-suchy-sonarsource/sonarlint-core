/*
ACR-e2d37b8e0b404744bd7116b42c8f6452
ACR-ab0019fa71be4f8b9899d1e71d5fcea3
ACR-94000c28e8d849999fdb2a40e2f24a27
ACR-921b4d89024d43b9a30df5fcdbf51295
ACR-7c95f97d242b4ca48db4eda63cdcfc4f
ACR-e72c2b55b439424da8681ff1930b8ea5
ACR-5970cc9d7d494f16a649b3aa1f38f5e0
ACR-67434a3a6bca4ab5b8dbf65ffa719b06
ACR-c888430612af422eafcc479fe73d7e83
ACR-b36d50534bac4c519117d45af2ea4f4b
ACR-34fc184d21534109a52e427e60c71e75
ACR-cb78c6482cc14f018d1ae41d306ffab6
ACR-a16114b70a2e4a3d89e2eaef1a026768
ACR-0f5a941b449a4c249a0daf9549084afc
ACR-c71ecc191abc4e75afe7bca126496417
ACR-83b9da03bf9b4078a4bc42a24e7b5d8c
ACR-b92ccde2cf024fa4a0ceb41bdcd8c4e9
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

  /*ACR-29cd7c0971654d0fb53d2cf053d18732
ACR-5093f17166024befb02eddf7e04e203f
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
