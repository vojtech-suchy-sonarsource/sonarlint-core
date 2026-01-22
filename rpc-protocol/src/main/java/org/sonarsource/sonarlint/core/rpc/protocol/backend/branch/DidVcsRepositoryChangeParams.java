/*
ACR-7a3b7025ef86481bbffda196e7b2cd74
ACR-c4bf654592ef433a9c270a59598e5bbf
ACR-2fbb65f9e3dd4189812789b1dfee17f4
ACR-6cd7db01dc264c829b7f7054a464f722
ACR-714faba5b4f84bd19c0e90223e71f962
ACR-f49b6620f9ea4832af893576753ec033
ACR-d8fa88e87b22442792ef3797987f734b
ACR-e788228e1863447b9b1d08b7428adae1
ACR-7ef40cdf56f4453cb0cf95d8988f286e
ACR-6945f66709224385af36e74c027acd49
ACR-f148e0ce68ce4c9e9c7be541940438d4
ACR-979e0e860a464b6b99cdab1c9d0406b4
ACR-fd594609e70f458db2cc753e8b50489c
ACR-abc99330dec640b9bb3c688f96ed128f
ACR-f845016064cb4432b87e51afa1ec4e15
ACR-70854cb1c5b1412b9d2e4ef8052ea4b5
ACR-53cf298141bb4269a179a550c0748cc7
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.branch;

public class DidVcsRepositoryChangeParams {
  private final String configurationScopeId;

  public DidVcsRepositoryChangeParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
