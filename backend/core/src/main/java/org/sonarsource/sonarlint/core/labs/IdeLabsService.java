/*
ACR-af111f9c84634b88b96c9b8730b5fba3
ACR-84add2c8c18d4a3295996403757ecfef
ACR-36df7e51aa3b425f9bb3b577af5de090
ACR-09ce135e610c4c85a0e774eb9a1aa4c0
ACR-d3afad44c63e4f82884f9ac25ebe0061
ACR-bb07e80969d4420cb97aed1529554661
ACR-dd37890b2f3348cebeb9419a62c21d5d
ACR-15374f10cc38462480d401be4669e352
ACR-8a419f9aa7e64c47819fb1059074e3ed
ACR-296becf9663a430a83e537278f34f3c9
ACR-aa69a4accd0b4c65b563562d9bf946b7
ACR-6e29540d4abf420ab53f68b8aeca27f2
ACR-7695fe764a40413eb537dd37dfa46d74
ACR-9213ec9e737d438493c031a8d93f9e93
ACR-fe4c116b67fb4352b4bc6a39984e6735
ACR-33ebda904c4341538ec49a09c5a77d66
ACR-2347eea82940402fb649721f7dd690c8
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
