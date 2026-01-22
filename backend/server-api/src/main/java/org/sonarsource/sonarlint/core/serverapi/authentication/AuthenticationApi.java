/*
ACR-5641c74c13eb4b0eb6ae43051241950f
ACR-4298b0b0b2c5459883aab79972ef8695
ACR-9605f26985a04cbe8fcaa971e8280712
ACR-a9d4674fbe4b487fad17a9cc8e20b011
ACR-5c8d76e6fd354638928467e2c32e2038
ACR-10c812731ec44d63a0ca36158fe3f98b
ACR-e3e4239a7f88452eb12a51063e8654a2
ACR-84b2c34078e742228d631bfcdb0361da
ACR-cf97c23c7b6c46daa881033a0757e64b
ACR-d01ba98afdd54615ab6418adfaf61c10
ACR-fdb87b8d4dcd4013ad2a0b0ea1cda0cf
ACR-5f255355e1e042a1abe18fb1145996c2
ACR-ef3859fb15f643449e40396365a39e57
ACR-349360df641041d3b17b6c616277dc2c
ACR-99f10b421bae45a49c51685d82da4fe2
ACR-920b847daab7492badfe3e97b265ec08
ACR-91c6f87f0ae840f7a2b565373aa48ef0
 */
package org.sonarsource.sonarlint.core.serverapi.authentication;

import com.google.gson.Gson;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.system.ValidationResult;

public class AuthenticationApi {

  private final ServerApiHelper serverApiHelper;

  public AuthenticationApi(ServerApiHelper serverApiHelper) {
    this.serverApiHelper = serverApiHelper;
  }

  public ValidationResult validate(SonarLintCancelMonitor cancelMonitor) {
    try (var response = serverApiHelper.rawGet("api/authentication/validate?format=json", cancelMonitor)) {
      var code = response.code();
      if (response.isSuccessful()) {
        var responseStr = response.bodyAsString();
        var validateResponse = new Gson().fromJson(responseStr, ValidateResponse.class);
        return new ValidationResult(validateResponse.valid, validateResponse.valid ? "Authentication successful" : "Authentication failed");
      } else {
        return new ValidationResult(false, "HTTP Connection failed (" + code + "): " + response.bodyAsString());
      }
    }
  }

  private static class ValidateResponse {
    boolean valid;
  }

}
