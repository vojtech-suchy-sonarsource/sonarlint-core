/*
ACR-21031559d2654fa1a15706466e9d329e
ACR-dc9b3b2b9ccd439ba913fb2b6c603f9b
ACR-df8d775f29874c8ebe972e1f37f1ac37
ACR-2ba03f663c414f129dc19053fb713a66
ACR-48e56694ac3d4641897a6d753f23d805
ACR-73554b9e150e4a4f98f8c638797bd1ae
ACR-88bb1a52ca99404c90b517c7dfb802d3
ACR-ad6300f5c49b4896bfae269fc90c4deb
ACR-253767476f0f45fea3c9594c1d00ff9a
ACR-1601028104654e5a9da0d3738e5c9f83
ACR-9469745322874b759953ca810e09cecf
ACR-ea7b7ce8a5a347c58abca9be533596ed
ACR-520a07ee843f40a4be4e80aea63ffa89
ACR-2551154a9c04407a88f7de228f7fc519
ACR-15d55ad5d7e44aa786080e4a24acfa26
ACR-eb335bed9a3c40d894ec2806eab01dd4
ACR-02129f3bfb634d008a18d4108911072d
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
