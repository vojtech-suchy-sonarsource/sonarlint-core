/*
ACR-0e5b6d9c84e74907a07b3e9bf444ac9d
ACR-ab4c2078feb6479c81bc47b690f53bb1
ACR-ac7257938d8344a79025a47bdb42fc78
ACR-82b27e7e27294f4da6df42575871001b
ACR-ec6537ca8f6a4bbf8fd33527b44f33ef
ACR-d08ccb18ebf74233a2bd248215bb173d
ACR-23dfae875bc74ab388725ee3b2e42410
ACR-22eb73470a11476d929bdd995ca30b5b
ACR-d202dcf6138b4aae801bd1c0c2d09399
ACR-7a9efb7b4354477d944a89fdcf5018af
ACR-115fef813c5648cab467767952486319
ACR-5c3188e9239d4ff9a21cd9ee7afafeed
ACR-6fd60c5ee8334bb2abf133cd5b32dcf8
ACR-5b60550b953441a8886f8249acc48a55
ACR-28b780bd98bf4c68af5f60c7fcaaa531
ACR-99dcbd489da0436f87d3fe7a262b7066
ACR-e71bcdbd54ff4f1593243072c1703501
 */
package org.sonarsource.sonarlint.core.serverapi.users;

import com.google.gson.Gson;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;

public class UsersApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ServerApiHelper helper;

  public UsersApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  /*ACR-264488af0701410ea0640bdb2e60b4d0
ACR-79e9d27abd0943ba89ed466f123510c2
ACR-71648c8493044ea1ae5437aa5bc1f9b9
ACR-88fe9910fcb547eab1d248c124c532e7
   */
  @CheckForNull
  public String getCurrentUserId(SonarLintCancelMonitor cancelMonitor) {
    try (var response = helper.get("/api/users/current", cancelMonitor)) {
      var body = response.bodyAsString();
      try {
        var userResponse = new Gson().fromJson(body, CurrentUserResponse.class);
        return userResponse == null ? null : userResponse.id;
      } catch (Exception e) {
        LOG.error("Error while parsing /api/users/current response", e);
        return null;
      }
    }
  }

  private static class CurrentUserResponse {
    String id;
  }
}


