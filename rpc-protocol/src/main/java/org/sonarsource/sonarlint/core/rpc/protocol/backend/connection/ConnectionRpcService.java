/*
ACR-74384ac75f824d509c6893278b1e2f8d
ACR-2afa3c9e80ba4e429d2a6490b131ecf1
ACR-e30f7f10f45d40e3b39764dc0c5cacba
ACR-d45329bc6ac24646ad170e36c4f35f5c
ACR-86e715d8f7b0465faac2cf61f95bd262
ACR-6656e92ffa9e4264b6e8b368a359798a
ACR-40ba8359d4594ed291a181640ae8adb1
ACR-21e253b465304aa59668f84a34660ab6
ACR-f6f1a9a080104833b1845958bca42770
ACR-1c9cd604412240328d3491173f5c2d35
ACR-22232ba3a14f442ea7d5f3ce8c7e8c71
ACR-5d34532a58884b609e24ff73c2b0de71
ACR-597a710cd14c4ac8afc218d4acb4a214
ACR-de3cd2c4982643718e76043e6956ceaf
ACR-6f7d3b31017b4d919680760157bae459
ACR-58ddd31f290e40fd8991472d7dccd261
ACR-04f035bfc6fc492a999c93f33f7e8e85
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenResponse;
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
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetConnectionSuggestionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetCredentialsParams;

/*ACR-60f07131efe847d6a0bdb650c0faac83
ACR-b50f8573c9ea4d9c9ce068fbe109d206
ACR-04ee4d0a87f748d1805b0fbdb4f85d9e
ACR-395b12e5772d4e189bbc17dc77dbd382
ACR-11ba2077f8d34526aba50330c113a429
ACR-b0d598b864234c0a8b81fa63f623ef64
ACR-9ffaf57f87b14862b1114c5a0a4eed37
ACR-f1b31595575346e190bc9149181bebb8
ACR-74705c463b404fcba12eb99404093c27
 */
@JsonSegment("connection")
public interface ConnectionRpcService {

  /*ACR-6d087a8bec9d4e15b156a344dec4a268
ACR-e4be653f207446dda5ccad248bfc243f
   */
  @JsonNotification
  void didUpdateConnections(DidUpdateConnectionsParams params);

  /*ACR-88a1956e07a4418f8c0981b41f147d71
ACR-4c71a4fdb8d548bab2b36411e71440d0
ACR-0f0f19b3db054ed0935fdfbf2614da70
   */
  @JsonNotification
  void didChangeCredentials(DidChangeCredentialsParams params);

  /*ACR-135cf67ef4254fce951a29be7b5d9acd
ACR-1d4c221bc2ed4bbb9281b48586c20bb0
ACR-a9747c810d9547b0875f3b87ea2b120b
ACR-8fcae4504d4347bcaa01bdd1aa9feeaf
   */
  @JsonRequest
  CompletableFuture<HelpGenerateUserTokenResponse> helpGenerateUserToken(HelpGenerateUserTokenParams params);

  /*ACR-de6d666d41344263973ef4f7bf4a7ea3
ACR-9abd1d9b4e1b4e42aeadc9a3a66e4834
ACR-6b3118c7ad44442fbfed74636992ca33
ACR-9a43e23aa7624f429a18cd606288b281
ACR-9f6410979acb44c8a1eb7602b254cba8
ACR-1e827d9d6ae94d4b96d09b14aae99fa0
ACR-cea63a69cfac4b5cb7972268c4844144
ACR-9fa2f594b0154491809b714cf222e129
   */
  @JsonRequest
  CompletableFuture<ValidateConnectionResponse> validateConnection(ValidateConnectionParams params);

  @JsonRequest
  CompletableFuture<ListUserOrganizationsResponse> listUserOrganizations(ListUserOrganizationsParams params);

  /*ACR-be2c46a3ec6f49cbba9d6c594198ea01
ACR-012e319bbad344de94240598058eab29
   */
  @JsonRequest
  CompletableFuture<GetOrganizationResponse> getOrganization(GetOrganizationParams params);

  /*ACR-f3078539ff6843d5be2b7b71cdb7d295
ACR-373c06186ec84080a2e81c50b6a85217
   */
  @JsonRequest
  CompletableFuture<FuzzySearchUserOrganizationsResponse> fuzzySearchUserOrganizations(FuzzySearchUserOrganizationsParams params);

  /*ACR-2b38b7d4ec24457c8282b468ac9dba14
ACR-a316c00d510d46f587de5d4bb26ac11e
ACR-4a63a10a4ff448fab811e62f107ba8e7
ACR-37d42848f09f465496496d7c13670daa
   */
  @JsonRequest
  CompletableFuture<GetAllProjectsResponse> getAllProjects(GetAllProjectsParams params);

  /*ACR-0fee778c683b46318dc38be309cb7cd6
ACR-3540094efe344e018ecace0c8c3e586e
   */
  @JsonRequest
  CompletableFuture<FuzzySearchProjectsResponse> fuzzySearchProjects(FuzzySearchProjectsParams params);

  /*ACR-f560dfb68c4a490fb2336a588a17181e
ACR-0856e40cac7640b9acdfab0aabd41dde
   */
  @JsonRequest
  CompletableFuture<GetProjectNamesByKeyResponse> getProjectNamesByKey(GetProjectNamesByKeyParams params);

  /*
ACR-51e7445d00df42b29f73e609b68ef2a7
   */
  @JsonRequest
  CompletableFuture<GetConnectionSuggestionsResponse> getConnectionSuggestions(GetConnectionSuggestionsParams params);

  /*ACR-3dfc284459974466af4f1bb80fbc89d7
ACR-ef19c20114f8469b8e8958a667b3278b
   */
  @JsonRequest
  CompletableFuture<GetMCPServerConfigurationResponse> getMCPServerConfiguration(GetMCPServerConfigurationParams params);
}
