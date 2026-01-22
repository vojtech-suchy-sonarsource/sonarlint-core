/*
ACR-a994d27558674f72acc2478b468156c9
ACR-725f3ae69019473ca1f948317ccdc902
ACR-0175501494444242a31365228a01b1c7
ACR-20a1e8c4147f4ea08f18b8436895efee
ACR-a1d46dbda84f433eae1cdcac6c02e16b
ACR-8179762a4f9b400f95e909b641bd6d37
ACR-2a8259944ab34cd7b31ef8a3f8fc1dec
ACR-6dff6135dd4e4daf80bba6b21f6f0044
ACR-970b5dd7941d425ca3231fc8e606f8d7
ACR-c7d6190637144b35b917c2ac33dc076e
ACR-666fd270121340e8af4d86b55168a934
ACR-52dfe910a26e4d03a6e4bdfc1ba73f10
ACR-5038ac6f94df414a94f71545b092d5ba
ACR-ac2d718a6e7c4f21b2f7b05316c60307
ACR-a6c12db5ffb84b479088d84cdbd14d09
ACR-064927da4496436686a721e73a3ab7f8
ACR-15c2aba6dfb241b9a13ca4f6a106efaf
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
