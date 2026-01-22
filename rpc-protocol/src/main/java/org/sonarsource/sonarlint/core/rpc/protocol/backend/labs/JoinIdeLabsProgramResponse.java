/*
ACR-df5f27c4450b4ff6b9e76000474d7ee5
ACR-dad924847d1d48ae8a2a7e2e943bdef5
ACR-9b8f716dad1e49d5bbf0c3478f09f524
ACR-19184dea61f34a879f67e0ffefb8bdbb
ACR-23bf197618c24d3cb2bf544f8ddfee81
ACR-2c706dace85d45d69afb9a0fdbf1a858
ACR-c5d5a4867cec4f40a82156d5c2881064
ACR-527563086ee447be9c19a4005a3bcd57
ACR-690733e2e75c44e59f21ee80624c21ab
ACR-5cdf8d8edef8446b9a2477b29286da8d
ACR-aceb3fa5d6654e5f87c8771c629f6126
ACR-e45979f9519944aeade095c496e6f6e0
ACR-4aead4fa94224c85bc03593e13eac5b6
ACR-f4c7895e3bc84e80bf2a4c0ab7c2e687
ACR-1b23fd205786404d8668f1be776f59d4
ACR-bac9b7afa2b84976ae116b86106ab71c
ACR-ee6c986d296d49be94dfc3e1cc4913cb
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
