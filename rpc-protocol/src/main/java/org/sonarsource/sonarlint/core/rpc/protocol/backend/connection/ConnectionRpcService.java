/*
ACR-572db0eff50543259bc282eda9756fc7
ACR-425b7700cddd4474ba90d59f6fe26007
ACR-aaa2ce0979b640b2b69f43cc24196f31
ACR-53f7019495184ae78e9dd06d94fdaa45
ACR-194630837baa48c0a9c559746544b75e
ACR-6a1442ae7b6a4c36bddb61fc63aabc6a
ACR-80016a3d199646c0b1aadb38b450f65b
ACR-5d85f176013242fca2ac965fef7f2623
ACR-c49a3c86d861456085d2985328ba6c0b
ACR-ec468ec42ccf4cfca634b18f3cbe394d
ACR-36669984c80045f588763718401feb8c
ACR-39b3490966c046f1a7b5743e4199b0d4
ACR-aba860dd21754869a2d5ee250b262a59
ACR-4a0bff2d116947f79f2cdc9fc6c7555d
ACR-6a32df98362047d7b983e11cc963efe8
ACR-fca4e139be7245c699ad14c42a1e4537
ACR-fff5edd8694d447dbbe62dff41fe6c4d
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

/*ACR-12069e63d9734813a3e027452bc2fdeb
ACR-74d6e698e9814aad8785dd8ee44a2a86
ACR-10ee83a09bd04748b79e16c6b5cf07a0
ACR-79173026f6334b59bad65f5d7618af6f
ACR-7ddc1085af0c4a71b4f2a6e7603315b3
ACR-3782d4b49f804d0f9f4ce2111a5e1c41
ACR-e6f8cff2a17d4ed5815fbe7bccf7de56
ACR-fa9d6f2f188b4cd4aa07fe3247fee128
ACR-75d56f7a9c7d437f8f20a4cad35c9bad
 */
@JsonSegment("connection")
public interface ConnectionRpcService {

  /*ACR-b52eeef5645d4119a8b0a59adc188831
ACR-2cbe09a3bc174e969c27cc91eda6b17c
   */
  @JsonNotification
  void didUpdateConnections(DidUpdateConnectionsParams params);

  /*ACR-e488b5c3cb4046db885c3a10d7bf6818
ACR-749f20bd2529451cb256a5888f98f273
ACR-e7a2c81993994d23af7bb7b14ba3eac4
   */
  @JsonNotification
  void didChangeCredentials(DidChangeCredentialsParams params);

  /*ACR-3bbb763ccb28454d96fb951079331a36
ACR-ed1eb92ff1a048bdbadde2ba407201dc
ACR-b31bf1c99cf84912a531f7d6e0b0569c
ACR-434add4e976647fca46d07ee4911f0a2
   */
  @JsonRequest
  CompletableFuture<HelpGenerateUserTokenResponse> helpGenerateUserToken(HelpGenerateUserTokenParams params);

  /*ACR-568634741b9e46559eb76928d1bdb40c
ACR-4d21799f7845463c804d932a06939c36
ACR-266e5702e0f34210a5aae5bab9e201e8
ACR-d22fa69ae9be4b1084bf74a49dadaa6e
ACR-9af89fc723894a04b1724d6c79963374
ACR-af8faa299c704fa5a512c12e608328fa
ACR-dfd0afb6db9d4afdb3940b08f997ee03
ACR-de925219c66241329dd69350b33a1e05
   */
  @JsonRequest
  CompletableFuture<ValidateConnectionResponse> validateConnection(ValidateConnectionParams params);

  @JsonRequest
  CompletableFuture<ListUserOrganizationsResponse> listUserOrganizations(ListUserOrganizationsParams params);

  /*ACR-207496d003374524999f82cd2cfcbb73
ACR-e7f842aeabd1427a8649d3e9945d9670
   */
  @JsonRequest
  CompletableFuture<GetOrganizationResponse> getOrganization(GetOrganizationParams params);

  /*ACR-102d1af2a8524a439b523521a4dd07d4
ACR-7057889440404863a86b57e3b2ca6253
   */
  @JsonRequest
  CompletableFuture<FuzzySearchUserOrganizationsResponse> fuzzySearchUserOrganizations(FuzzySearchUserOrganizationsParams params);

  /*ACR-817df263225b49c3b951524254959c18
ACR-a8a9e064e09f4ae8a9d1ad3150100d52
ACR-f977e0831ea846c590a8608b945f0d46
ACR-eb18dcf71af54ff08e75df0c63dd4a03
   */
  @JsonRequest
  CompletableFuture<GetAllProjectsResponse> getAllProjects(GetAllProjectsParams params);

  /*ACR-4677cd2552604799879701d81884bd1e
ACR-65c61fdae2e14e7cb89ee7d3c1327106
   */
  @JsonRequest
  CompletableFuture<FuzzySearchProjectsResponse> fuzzySearchProjects(FuzzySearchProjectsParams params);

  /*ACR-a2de308d9b474781bafaa55e1e941d7e
ACR-7f9de5948176494bbd817007ccffa43e
   */
  @JsonRequest
  CompletableFuture<GetProjectNamesByKeyResponse> getProjectNamesByKey(GetProjectNamesByKeyParams params);

  /*
ACR-c5f51517cb7e4b14a0c51c7d461e5967
   */
  @JsonRequest
  CompletableFuture<GetConnectionSuggestionsResponse> getConnectionSuggestions(GetConnectionSuggestionsParams params);

  /*ACR-9c224ffc688f48e9a851d3dcc6eb2f83
ACR-de4a1f41a8c24afe8e7699c01b56210d
   */
  @JsonRequest
  CompletableFuture<GetMCPServerConfigurationResponse> getMCPServerConfiguration(GetMCPServerConfigurationParams params);
}
