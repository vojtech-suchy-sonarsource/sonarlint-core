/*
ACR-bd6a0ecf72cc44e8828b956004b2dea1
ACR-22ad126c7e1341f4a2cabd2f31dbfa1b
ACR-89f02759435c41d9aadccd03bc0dfadc
ACR-1f4b1c80058c4c6885ecddc94fb3635b
ACR-9eedc5152bbf4df180754f5cc0a91ae3
ACR-0485c1ce7c6f4549940ca532e15c0bb0
ACR-3d05f37f9d184187ac7769d8d71469cf
ACR-fbcaf3736e5843d380203d8aedd52c7c
ACR-28a1abe332e846fb9c69067adf4fe811
ACR-77ef2a3e8fb7421d84497dd4e2456bb4
ACR-d363686158e44d69b407ee01c26768da
ACR-80b2ea6dc7b74c1abbb33de68f13a431
ACR-86941bd580f249e68063b600edf86ba2
ACR-bdad813cf42242baadec14abb9bd8516
ACR-7547b6b5c519453da3c753d92da49c8f
ACR-cf4dc401c0b24ca8bce0ddbf687b660d
ACR-32cb6779ef92499dbb1f10ba5bf2f62d
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
