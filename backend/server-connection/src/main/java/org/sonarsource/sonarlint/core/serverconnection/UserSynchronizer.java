/*
ACR-5eda471f23684485813922ed2510df42
ACR-f80214dabd5e413b8f1abc4a22dfd8ed
ACR-09b6879a7c304b7fa883769394b7da65
ACR-5dd85c2fef294995a73e6ffbcf45a15f
ACR-b7dfaf6c332441ce998305aefc88fa80
ACR-55ee5fdf15bc416ea411af069227a7e8
ACR-4afa9dad0494476faca644f3fd0949b8
ACR-cb6991d83f51426a81cde302e4f68d1a
ACR-7b29ccaaa93d4000b8ca068cc8ffb535
ACR-b404c4520e1342b9b7dc238660ec63bb
ACR-55bc19baecc347db8082127a9ceab873
ACR-bfcbffb16b724fa8a40af859721b9b27
ACR-92e92f5b44f949238b0433c11327bb0b
ACR-225d7c6954a745199a17ab6d3e345ddf
ACR-800c1b687c3648e5bdea0693f4c119ed
ACR-a49428b127cf4578ae4f50079c720278
ACR-5a14d7dd04d14bb9bb8d4366f6dc7547
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;

public class UserSynchronizer {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final ConnectionStorage storage;

  public UserSynchronizer(ConnectionStorage storage) {
    this.storage = storage;
  }

  /*ACR-4230286d643c4db9bc3f57f113f9597a
ACR-5b45cc14d946472ca495409e17ce1e36
ACR-a2bd95ef31304586832a990aa965339a
   */
  public void synchronize(ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    try {
      var userId = serverApi.users().getCurrentUserId(cancelMonitor);
      if (userId != null && !userId.trim().isEmpty()) {
        storage.user().store(userId.trim());
      }
    } catch (Exception e) {
      LOG.warn("Failed to synchronize user id from server: {}", e.getMessage());
    }
  }
}


