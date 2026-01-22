/*
ACR-edbaed94f23944bca2828ef2687c576e
ACR-aa4c642cf2874dbe83671ba07fb83069
ACR-9bb89414f5a24193972b4d12be98ba34
ACR-955e251c501b43a98cfbac81f93f6ab8
ACR-ddf506dd40cb4f06bd1dc449c5984244
ACR-986f84d58e4b4b84a06c77b631652013
ACR-c7ceaf885fe04569b81cdaa2c0ffed85
ACR-69b6c5a3d2c345b9aa2d3314faa3a942
ACR-a8a29d9fec104b74958474c62967d9d8
ACR-97b1c86ca32d4febb7e0bdecf31aba07
ACR-641a5f140b8144378c83418ad3bd0c29
ACR-65969b4f912c4f39940496f4b5026f71
ACR-38bb6718a06341e2a6237ecb3bc92979
ACR-4e21e31b4bee482e81807851d2481fe1
ACR-18e643aa78f94a4d87f13939da5ba472
ACR-f675c68972e54e568125724de2f58b01
ACR-5fbad3ac95834e04a9c86380f19ab474
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.labs;

import javax.annotation.Nullable;

public class JoinIdeLabsProgramResponse {
  private final boolean success;
  @Nullable
  private final String message;

  public JoinIdeLabsProgramResponse(boolean success, @Nullable String message) {
    this.success = success;
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  @Nullable
  public String getMessage() {
    return message;
  }
}
