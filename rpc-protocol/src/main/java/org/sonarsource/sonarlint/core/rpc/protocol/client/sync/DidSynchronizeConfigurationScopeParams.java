/*
ACR-77b41bbf8ce34405b24b625d938ca64b
ACR-860d4058f75d44f08689694040b71b5c
ACR-aab908d35b9347c196efdbdcf3a6cfed
ACR-52f455c86cdc494e869e53dd4fcde0e2
ACR-a76da5529a92402c8f8f2a11c869877e
ACR-16014ec218aa440c8008a16e76e68173
ACR-bacc6c4b3b4d4213969dd392fc4bf5b1
ACR-5c05c97dfe784d61bab6bac591f8b6d9
ACR-bfd4dc203d3348ab9cf225a94b591710
ACR-d8da461c068644b5834e9b1eb9dcd1bc
ACR-84c069ef069a46e4a77a53221ff17189
ACR-6709feab6cdb403ca1ed0ddea896a65a
ACR-77b217205c4c46d0b3ed3538044aca97
ACR-150f8b186bd54fd2912b8b549f0a46f6
ACR-c6039ecdfa594805b9c5a374d9ad0f52
ACR-390a3d2673cf494f8bd7c7558547c966
ACR-fbbf866a70a4494f863db99b67c97362
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.sync;

import java.util.Set;

public class DidSynchronizeConfigurationScopeParams {
  private final Set<String> configurationScopeIds;

  public DidSynchronizeConfigurationScopeParams(Set<String> configurationScopeIds) {
    this.configurationScopeIds = configurationScopeIds;
  }

  public Set<String> getConfigurationScopeIds() {
    return configurationScopeIds;
  }
}
