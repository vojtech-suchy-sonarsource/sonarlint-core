/*
ACR-7fe3cd0b2150439099fa4dc9fdf83a04
ACR-e113b60a876742cea380ceca21c938b1
ACR-073f13b3f14a49358520fca1acd32caa
ACR-f6cd2fe90b354407b9b02533ea4b1f32
ACR-09dec85b0ca54416aecfd3fda66a5fba
ACR-d8a7cc7d2f8d49ae80b70d86267d28c3
ACR-8de6d61ebe2d4b9abd4fcc485495b946
ACR-75334801f0654c7599c65b39992cbab4
ACR-d307936c802b426c9c813b7270f74d21
ACR-13f510cb72a8466982056124b5676299
ACR-1817de47cf3048828bb4abb725f5638d
ACR-378cd4adf7dc41b99ed7c4f68513bc92
ACR-e1fff281aea042eeb12d2ace2741746d
ACR-b30e93992a0344c299978f925c1c9b4a
ACR-fa8545f8f0d04a1eadc75422529dd4a9
ACR-82db93c555e446e0a2866d05385a6f80
ACR-4baeae61c9544da4b604013aa24e9d59
 */
package org.sonarsource.sonarlint.core.labs;

import com.google.gson.Gson;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.labs.JoinIdeLabsProgramResponse;

public class IdeLabsService {

  private final IdeLabsHttpClient labsHttpClient;
  private final Gson gson = new Gson();

  public IdeLabsService(IdeLabsHttpClient labsHttpClient) {
    this.labsHttpClient = labsHttpClient;
  }

  public JoinIdeLabsProgramResponse joinIdeLabsProgram(String email, String ideName) {
    try (var response = labsHttpClient.join(email, ideName)) {
      if (!response.isSuccessful()) {  
        return new JoinIdeLabsProgramResponse(false, "An unexpected error occurred. Server responded with status code: " + response.code());  
      }

      var responseBody = gson.fromJson(response.bodyAsString(), IdeLabsSubscriptionResponseBody.class);
      if (!responseBody.validEmail()) {
        return new JoinIdeLabsProgramResponse(false, "The provided email address is not valid. Please enter a valid email address.");
      }

      return new JoinIdeLabsProgramResponse(true, null);
    } catch (Exception e) {
      return new JoinIdeLabsProgramResponse(false, "An unexpected error occurred: " + e.getMessage());
    }
  }
}
