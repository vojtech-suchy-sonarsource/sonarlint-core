/*
ACR-dd42c56faea74d97804d8a9082d4fdcc
ACR-d0e27ae5ce9f4f2ab791a8c5c2ea755a
ACR-502d18f9720146578007d95248f2599e
ACR-9e67dfd42c6343a4926910713192314e
ACR-4dd1db9021d44afb8bc0fc39bbaf2ade
ACR-2bed27cb42194d489466cca91fa2abaa
ACR-9dda4e74fa1244d49742fef190aa35f9
ACR-01579bc807284fe28b5bfe2ed4a7290c
ACR-241a3d5a732c41e0986db913634a4e2e
ACR-699d6eecf084429b8c86f100e6724059
ACR-ada938847cc94bbd808908777721138f
ACR-2de4bbcd4855453dbce47edd26117329
ACR-bf42b901bea44d989191514a518ae84a
ACR-4fd75acc75184b8385263c0a738f8f19
ACR-12d229d30c164a49808369c39df8340e
ACR-81ba3ba2b9f2469fb840bbc5dd6319c0
ACR-3d6dc77c18a64e7e977c6b8733e22606
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

  /*ACR-f4fad4e9703b40848ae6a02cb1c3d327
ACR-79219cf731e048b8aa17418fc526fd9b
ACR-04ceb884a7ef4cf78b0e576f634dde5f
ACR-d3cb7acbe4cc4c349436ca2024b863d1
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


