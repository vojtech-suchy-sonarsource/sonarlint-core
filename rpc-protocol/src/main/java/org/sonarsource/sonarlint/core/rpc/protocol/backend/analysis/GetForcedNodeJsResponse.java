/*
ACR-f2e65db392c745d0b73bea569e1f551a
ACR-e9d7315428344c26ac67e50157790909
ACR-4f4650feb1a045c7882f2edf9047d5b8
ACR-057b357ef1084e3bad1ae532dd1bff21
ACR-e3014770dfe444409c80802ca65bf204
ACR-24257e3917a4471e9eda61a165d8ce5d
ACR-2335082a625b457a9c6e7fc7348a1d1e
ACR-643fbe4850184124a697e3cbdac7ca2c
ACR-a17bf39abadf4471b761c28f6cdf09b3
ACR-7f7deaa192374356b842f265cfdfd468
ACR-228f96b8a9744ce0a2186911ddfab0f8
ACR-113f398908434aafa4e4ed0d1fca356d
ACR-6c7773f6e416415995490e393806f08c
ACR-b824b21350a840a095dc57bc3796df40
ACR-bf67fea9dd4e44a8872da75b1ec09611
ACR-81e6b2b17f7641348b6e45a22d16ada6
ACR-1d1a6b6920054e79be5450013086a33b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetForcedNodeJsResponse {
  private final NodeJsDetailsDto details;

  public GetForcedNodeJsResponse(@Nullable NodeJsDetailsDto details) {
    this.details = details;
  }

  @CheckForNull
  public NodeJsDetailsDto getDetails() {
    return details;
  }
}
