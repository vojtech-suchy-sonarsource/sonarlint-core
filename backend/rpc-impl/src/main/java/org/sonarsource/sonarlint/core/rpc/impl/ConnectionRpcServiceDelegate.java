/*
ACR-b76a3fdfb1804110a5bbab431be41236
ACR-3633599d3fa3473da8d2a41f93d3e70b
ACR-03297aaa8ec44f978911f7394aef1445
ACR-ef91be8b6fd440649bd0cd40361799b9
ACR-c77298ad84914286b4de85840593d7d0
ACR-68a61a606c3c4b0cae53d2f2c906a664
ACR-9528af49cced45d4a82b018f3a06a341
ACR-64699528767240de9fa0149bc3c6a5b3
ACR-4b9a0b261ae941529e4092e9d39e5345
ACR-6e1a25e2d9ab4509a7f5d8feefafde64
ACR-f7bdde1798e74055955d5b076ec0b804
ACR-db9fd716949d45f7a0576f76a329007f
ACR-e4ee17f46447498bbc90a74bc118c674
ACR-1a14775e806d4626ae68f745218088b1
ACR-2b83ac4e4e5b49f69fe55ce790532040
ACR-745c68539c554187ae0f8b13e9ec7036
ACR-9937e21b715546df8fee6f8803965821
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
