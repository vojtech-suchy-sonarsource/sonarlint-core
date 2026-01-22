/*
ACR-c7c46f8a597246d6a7ce068ed41f99ac
ACR-92e4aaec489a4cf090fce01542511167
ACR-28c6493df20c4d4fa6ac797b845e1c18
ACR-851f8112b2074587a37030a2a037d0d8
ACR-047585699be7410c843bd6d7f7966bc6
ACR-b0070cca4c5c4bb6b3674d2e3f1b5508
ACR-6d15713eb02b4858bd2fcf0a1e96ac1c
ACR-9ba1917f7e9248919ffb3efa295dafd8
ACR-36f80ae01df84101b92d2ed87438ccf5
ACR-9dec49cce7374b17a6d4de7bfe9251e8
ACR-f7ad18b9e8924ae2aa28a2476fc5e9ae
ACR-52630b4e289f4971a3d836e54b7df433
ACR-afd8c9624d234379bd56d3c3c08c4917
ACR-6adc19d087a840cb91a6e03b3bc2198f
ACR-53682acaaf1a4dc2b4022833fd423794
ACR-b6aa2ac49e4148a4a538c3a8168f5551
ACR-be0f65852f414c76905feee13868b313
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
