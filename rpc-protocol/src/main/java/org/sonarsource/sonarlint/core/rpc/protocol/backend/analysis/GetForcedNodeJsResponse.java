/*
ACR-d285993b1e7a451a999bc1ffd7021354
ACR-fbdec754a52e4382a3143f0a52eb3385
ACR-c3fa54796b8a4ebd85cdd08370802bab
ACR-f03f7c6f61ce4f5ba135a869da1d48fb
ACR-47ccdb0f695443dcb5a58b0bbc2fc96d
ACR-8b4d0f234ad74e2ab0b843b9c3075669
ACR-5afc927c0f87490ea7d1a368ad4815ba
ACR-ea2a023a803243c59623384fce8e1f40
ACR-ebb5133aa94544bb958d008c8ab39ed0
ACR-62a7b2d6492940dd9dafa47fc85355ac
ACR-bf94522d86834491b76a3278178e76ec
ACR-2f5c35b6882d46a99381332ae5f361f5
ACR-2b05f25a96f248ca87268dd6104892cd
ACR-aa241edb340543f3b922b8fd597966fc
ACR-d495ebeb12834439a5eefdbfe1b28c1e
ACR-0669c57aa4f1464a9deeca79d9e1f454
ACR-e5a579d769e14cd89fbcae5c1aad35d6
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
