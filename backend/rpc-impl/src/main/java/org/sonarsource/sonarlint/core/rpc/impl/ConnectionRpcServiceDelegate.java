/*
ACR-15969ded30fc49adb213170d1db59174
ACR-d2fe72308d804a5baf30bea2a2cfa5ec
ACR-75f695d6daf84edcab1a9a46e92f7e82
ACR-793aeebf26594fb08ae6313778b6ff43
ACR-5391e4e3705947a09e99389be09efe4c
ACR-bc3f847fa1f04d6d86b3d17a45701abd
ACR-e58e62d6b9e24dbfb0f17a01590d66b2
ACR-6254b0402efc46a7b5cef08d6cbef435
ACR-35431256dda24ffc9a623dfe2208918b
ACR-31815f1665044dca83c5dce90213c0b0
ACR-944702441cae4eb8a644b5b5f6ac2cab
ACR-f1f6c9052627429d9a9134a3ec90cc26
ACR-7e3b3c0578954097ba69dcdc9e325e9e
ACR-d1509f09142644b0948d85ef6915b48d
ACR-799cbdfc6816439e8ea627aeb2b74bdd
ACR-5571c40a33d248179fdab454472684c1
ACR-cdecf005bbc8483d98e9bcee3b783073
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.ConnectionService;
import org.sonarsource.sonarlint.core.ConnectionSuggestionProvider;
import org.sonarsource.sonarlint.core.MCPServerConfigurationProvider;
import org.sonarsource.sonarlint.core.OrganizationsCache;
import org.sonarsource.sonarlint.core.SonarProjectsCache;
import org.sonarsource.sonarlint.core.commons.SonarLintException;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.ConnectionRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.GetConnectionSuggestionsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.GetMCPServerConfigurationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.GetMCPServerConfigurationResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidChangeCredentialsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidUpdateConnectionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.FuzzySearchUserOrganizationsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.FuzzySearchUserOrganizationsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.GetOrganizationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.GetOrganizationResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.ListUserOrganizationsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.ListUserOrganizationsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.FuzzySearchProjectsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.FuzzySearchProjectsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.GetAllProjectsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.GetAllProjectsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.GetProjectNamesByKeyParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.GetProjectNamesByKeyResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.validate.ValidateConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.validate.ValidateConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetConnectionSuggestionsParams;

class ConnectionRpcServiceDelegate extends AbstractRpcServiceDelegate implements ConnectionRpcService {

  public ConnectionRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public void didUpdateConnections(DidUpdateConnectionsParams params) {
    notify(() -> getBean(ConnectionService.class).didUpdateConnections(params.getSonarQubeConnections(), params.getSonarCloudConnections()));
  }

  @Override
  public void didChangeCredentials(DidChangeCredentialsParams params) {
    notify(() -> getBean(ConnectionService.class).didChangeCredentials(params.getConnectionId()));
  }

  @Override
  public CompletableFuture<HelpGenerateUserTokenResponse> helpGenerateUserToken(HelpGenerateUserTokenParams params) {
    return requestAsync(cancelMonitor -> getBean(ConnectionService.class).helpGenerateUserToken(params.getServerUrl(), params.getUtm(), cancelMonitor));
  }

  @Override
  public CompletableFuture<ValidateConnectionResponse> validateConnection(ValidateConnectionParams params) {
    return requestAsync(cancelMonitor -> getBean(ConnectionService.class).validateConnection(params.getTransientConnection(), cancelMonitor));
  }

  @Override
  public CompletableFuture<ListUserOrganizationsResponse> listUserOrganizations(ListUserOrganizationsParams params) {
    return requestAsync(cancelMonitor -> new ListUserOrganizationsResponse(getBean(OrganizationsCache.class)
      .listUserOrganizations(new TransientSonarCloudConnectionDto(null, params.getCredentials(), params.getRegion()), cancelMonitor)));
  }

  @Override
  public CompletableFuture<GetOrganizationResponse> getOrganization(GetOrganizationParams params) {
    return requestAsync(cancelMonitor -> new GetOrganizationResponse(getBean(OrganizationsCache.class)
      .getOrganization(new TransientSonarCloudConnectionDto(params.getOrganizationKey(), params.getCredentials(), params.getRegion()), cancelMonitor)));
  }

  @Override
  public CompletableFuture<FuzzySearchUserOrganizationsResponse> fuzzySearchUserOrganizations(FuzzySearchUserOrganizationsParams params) {
    return requestAsync(cancelMonitor -> new FuzzySearchUserOrganizationsResponse(getBean(OrganizationsCache.class)
      .fuzzySearchOrganizations(new TransientSonarCloudConnectionDto(null, params.getCredentials(), params.getRegion()), params.getSearchText(), cancelMonitor)));
  }

  @Override
  public CompletableFuture<GetAllProjectsResponse> getAllProjects(GetAllProjectsParams params) {
    return requestAsync(cancelMonitor -> new GetAllProjectsResponse(getBean(ConnectionService.class).getAllProjects(params.getTransientConnection(), cancelMonitor)));
  }

  @Override
  public CompletableFuture<FuzzySearchProjectsResponse> fuzzySearchProjects(FuzzySearchProjectsParams params) {
    return requestAsync(cancelMonitor -> new FuzzySearchProjectsResponse(getBean(SonarProjectsCache.class)
      .fuzzySearchProjects(params.getConnectionId(), params.getSearchText(), cancelMonitor)));
  }

  @Override
  public CompletableFuture<GetProjectNamesByKeyResponse> getProjectNamesByKey(GetProjectNamesByKeyParams params) {
    return requestAsync(cancelMonitor -> new GetProjectNamesByKeyResponse(getBean(ConnectionService.class)
      .getProjectNamesByKey(params.getTransientConnection(), params.getProjectKeys(), cancelMonitor)));
  }

  @Override
  public CompletableFuture<GetConnectionSuggestionsResponse> getConnectionSuggestions(GetConnectionSuggestionsParams params) {
    return requestAsync(
      cancelMonitor -> new GetConnectionSuggestionsResponse(getBean(ConnectionSuggestionProvider.class)
        .getConnectionSuggestions(params.getConfigurationScopeId(), cancelMonitor)));
  }

  @Override
  public CompletableFuture<GetMCPServerConfigurationResponse> getMCPServerConfiguration(GetMCPServerConfigurationParams params) {
    return requestAsync(cancelMonitor -> {
      try {
        return new GetMCPServerConfigurationResponse(
          getBean(MCPServerConfigurationProvider.class).getMCPServerConfigurationJSON(params.getConnectionId(), params.getToken()));
      } catch (SonarLintException e) {
        var error = new ResponseError(SonarLintRpcErrorCode.CONNECTION_NOT_FOUND, e.getMessage(), params.getConnectionId());
        throw new ResponseErrorException(error);
      }
    });
  }

}
