/*
ACR-a3d2cc80a5684e65a9779cdd985fdff6
ACR-b8b7c025e48844cc8047a41d24656cab
ACR-094cad1562e94268a783302c57e85571
ACR-1e896befc23c4c678638934d5167d3cc
ACR-701cc33dcbdc4cc0bc560a6ca35da55d
ACR-cc6ff2f08af24781930affffda2d8204
ACR-2abba3de53964806973a6b4461a49832
ACR-7a5371b1d5ab49b79f97774a4c4fd0d7
ACR-fd836af5aeac4a269dde502a7d8fc9cd
ACR-860f528df9d74830ba030efffc707f90
ACR-542b7828db3c4efeb629d13b3c61730b
ACR-2387be6660524c7ea1b8330c6ac23f09
ACR-07f7a2cbf06d4a61828d7feb157b3f51
ACR-f8ed08f6d3e9453fbe2042ccc16c844c
ACR-975a8d668c2947cb8ed7b2a614bdb728
ACR-de71ad41074a4f5ba4ac91f4151d55f4
ACR-5e3e346b3e7a4a99adecc7accc4e2d89
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class CheckAnticipatedStatusChangeSupportedResponse {
  private final boolean supported;

  public CheckAnticipatedStatusChangeSupportedResponse(boolean supported) {
    this.supported = supported;
  }

  public boolean isSupported() {
    return supported;
  }
}
