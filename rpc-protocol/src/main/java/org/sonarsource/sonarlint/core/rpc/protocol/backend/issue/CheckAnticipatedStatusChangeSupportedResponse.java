/*
ACR-6a6e74244afb491aac6c4ab7b615b4c5
ACR-9a97a90c15af450db8eeefa7e99389fd
ACR-11ee2a2b0d6b45edb447eb209a7d35f6
ACR-efa264e898d9493d814656681c51aabb
ACR-0125c6e402a64ebcac15a242587b780f
ACR-9e3aa2b47f3b4458b8f4608676970efa
ACR-3cb6ae3947c7443595f712072e7dfa2e
ACR-bede70fbd825413ba40a990555deb3f9
ACR-b08c2247ca854e8cb777e24d2058c0b3
ACR-db4e696f1f3747bbb887f7a04c27ffab
ACR-4bb68d7da0b34fd5a2b459c0372cc605
ACR-604746b82b5c4a44a35707bc594a148e
ACR-d3cfbf5a082b447cb32b1b00b9b3dd67
ACR-32d37cd9d106410f8469dc8f83184f82
ACR-8f51944807f94e5a84ab4a5770e150a4
ACR-1cef95338a284e24b85bb09dcb0fd695
ACR-5efcdde558194843957abe93e3690f56
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
