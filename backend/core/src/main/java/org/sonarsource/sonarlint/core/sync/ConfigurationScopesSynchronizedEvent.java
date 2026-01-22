/*
ACR-50ccab6347db43c088210712e2811e57
ACR-53a8ed1d643c4617b2a204bcdf003881
ACR-d0b4ce8e0159451092416b6118a59eb3
ACR-debf6337298346d1ad13425498a79b17
ACR-59ab7e795933402bbb64478ad8df69c5
ACR-d556f1a64afe4ee9ba52888b8f6ecc88
ACR-f6f959e637f444ddb121655bca026243
ACR-f4084e43247e4bc08985017dbb711e49
ACR-d9b01bf6352344f0bbbe081031fc6e58
ACR-fc6f0a0e9f7b4bd7a2a6f414a827e649
ACR-8b217645c34d4025ba0fc3671953f24f
ACR-42eab1d09f454463bd4300bdb568acb1
ACR-bd1cf02cd0974fafbb0c588d7f21ebeb
ACR-536f0ee4efbd4a2fa44d626bacfc61e4
ACR-27d27a8a8d5c4a65be365110c727911b
ACR-5ff1ed540cf746f0a6d48f642f806146
ACR-f095a606994e4a2584ffd93a47475ab2
 */
package org.sonarsource.sonarlint.core.sync;

import java.util.Set;

public class ConfigurationScopesSynchronizedEvent {
  private final Set<String> configScopeIds;

  public ConfigurationScopesSynchronizedEvent(Set<String> configScopeIds) {
    this.configScopeIds = configScopeIds;
  }

  public Set<String> getConfigScopeIds() {
    return configScopeIds;
  }
}
