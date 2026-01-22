/*
ACR-990f2fe75c594f928002add005bdf9b3
ACR-ad1b16aececa45e7ad3cac25ff42e13b
ACR-4b277cfee87649b88ad163b89e6847a0
ACR-dd75863f35d742b9a1325d2814fbb1f7
ACR-02690d309b374ee2aeb09bc51a455c7f
ACR-b0e1d92530b9491a931813ead4859a23
ACR-cd187a4aa28c48bcaa920095599e4053
ACR-c64900d883c648a18e6918e4add5842a
ACR-380cb0406700438d824434d438aa48e4
ACR-98324434294e4dae9dd64d3ed302dd04
ACR-185034499fb945568d0b3805b8a8a896
ACR-113e8a99ad8947b9a9b19c4e63fd19d9
ACR-45948e22765f435f802683f594edb15b
ACR-1b96394380e4494a828fe19879b9502e
ACR-e64df3ae6f174a3e8839f5aca392926b
ACR-51dbb77849494dca9f68c1438f9dc0cf
ACR-c64937c80f7b44d2b3e4695b36cb4824
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetAutoDetectedNodeJsResponse {
  private final NodeJsDetailsDto details;

  public GetAutoDetectedNodeJsResponse(@Nullable NodeJsDetailsDto details) {
    this.details = details;
  }

  @CheckForNull
  public NodeJsDetailsDto getDetails() {
    return details;
  }
}
