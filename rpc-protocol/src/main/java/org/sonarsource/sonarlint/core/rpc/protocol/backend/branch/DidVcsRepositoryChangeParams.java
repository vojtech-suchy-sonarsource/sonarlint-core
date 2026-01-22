/*
ACR-b5742a448a944f05a51a0d52eb3c3c26
ACR-13e1a8ccc6ca406788487fc40a670291
ACR-b1f51dfb41a04d719a87049385250be9
ACR-462e8cf4f4a24c5b869524bd689662d2
ACR-bcf1146271c64174a026b415e89c674e
ACR-c3388a7029014a8b96cb47005ace56ff
ACR-49bb55b4bfb24ce3b29b778bdf64f36a
ACR-37909fff92ea4c0683a60cee05a40f76
ACR-5c0fe387f7654cf493dfd84cf0abeb18
ACR-dcf200b1c3644bafb05daa6f02665c00
ACR-cbd1f9d4d3764a47a01f4a5a2e713e0d
ACR-d46ef95ac104446ca846d836eb8c9b8e
ACR-0372e39cd85e48b4acfaacaa3bcff362
ACR-4b9d33d6409346428c3cf60df4e2ef3f
ACR-c2ae6f02b493477e8047e888231fd07e
ACR-281307a0b9a547f0b1745d07713811f6
ACR-dea0e4253b7c426caa07e1e3d957c2cf
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
