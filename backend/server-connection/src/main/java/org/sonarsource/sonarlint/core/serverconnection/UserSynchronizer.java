/*
ACR-763811499743443ca1a645aef2a943ed
ACR-ebc46e41337944f78e0d2c89f580ff63
ACR-9d9dbbdd8a5f4c89ab1405b93e566c77
ACR-b514d501dd4341e7b72238a6bbcb87d0
ACR-02bc0faaef844dc6b2384f8f223e3837
ACR-7f920644767a4a13a2664cf88146449f
ACR-b034ba4af44d42cd89300b7029aaa84b
ACR-3f8b902ad8ca44378f2ac5dd74364529
ACR-aacfeef9ea6f407b971a99ff6101d4aa
ACR-6d1ae2bb35cb4158aeb168abcdd31189
ACR-87c9c74ce37141c4a6fdcf8ae54d09a8
ACR-130c2249b77d4ff3a5fffb5ac99e2ba5
ACR-8b15410cc7bb435c87cb8f83cb582718
ACR-db14f17ec627449bafd39ad79d877248
ACR-5bcc6d255584446fa2ca5334f05c06b8
ACR-eb0b6a65226749ccac0dd0c50957226e
ACR-c65344e655fb4b4691e7421d91504961
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

  /*ACR-7dba722e322c4abba2334e43a73f1f57
ACR-99799a78a7be44e9b92ff1b7fd8225f7
ACR-0e3835bae26241be97bf9fb4a833aaaa
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


