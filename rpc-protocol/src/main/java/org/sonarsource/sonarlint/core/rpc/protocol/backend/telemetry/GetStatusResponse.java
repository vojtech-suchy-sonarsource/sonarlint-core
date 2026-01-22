/*
ACR-bb15d9df451f43b1a9d345daf66bad36
ACR-9339d438b31444569949be303b8121eb
ACR-0d39a5a78b7944999c37e559b87a02dc
ACR-e566d9c4053444afbe7df2893d3b408e
ACR-55040a92377b4b5db3de3928a63b89cd
ACR-bf5d46756c354cdb80c2190203970959
ACR-885bbd4cbc7a4007933e0664af9a0f3c
ACR-54ed46f2614b4fa9acb3bdebd63c06cb
ACR-abf45579aadd4a5c9bb1add484c3d00b
ACR-b3a0e0a337cf4ab3be20e3f87e32a883
ACR-22ac069eeab041319b0afd4a540f3875
ACR-8070cb649c2e4d7b846b46d9fb074b94
ACR-ce86eafbce7e4c14ae7362ab7507ace2
ACR-e88a120bde8947749b05817e3e7b633c
ACR-42b7818620074d88b39a3063689b203b
ACR-a7e07be95a52442792f1892a87d9859b
ACR-5075623307ce4b2893b4a4ea1a102028
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.telemetry;

public class GetStatusResponse {

  private final boolean enabled;


  public GetStatusResponse(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
