/*
ACR-5c09951ff83145c6aa0fc332afdb303a
ACR-dc0ec8fca1d14b19889a7709f6d59011
ACR-05fb59e0ee6943dfa96e4449832e9d2e
ACR-b0a89061fee841d2a0035314f43dbde0
ACR-3ffe9692af274404a952f2f499862c39
ACR-055c982feb0f41408f36d9d0ad55e9c7
ACR-1d79aa7818494d77818121e967d707b3
ACR-3673d9dac8554e8690a5b9aacf514471
ACR-45d25387f4654b1ab8c2c0101f8a3e06
ACR-15513ec23a2b4e47b74344a9454b1c14
ACR-cac7a45180e44bf88c697328133d0a5f
ACR-0612e31d7e044de79f92edc32c2d772f
ACR-4c3bcfef1c904d9185b347bbdae6fecf
ACR-c09040ec9cf84cb191399090a54f04f6
ACR-21fd31e48e0243098462077e113bbc63
ACR-f873bb16bf344b8bb80c3bfd2ab9a291
ACR-434cbe7be9b94fbdb1d2f3ab1cf2837b
 */
package org.sonarsource.sonarlint.core;

import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;

public class ConfigurationScopeSharedContext {

  private final ConfigurationScope configurationScope;
  private final BindingSuggestionOrigin origin;

  ConfigurationScopeSharedContext(ConfigurationScope configurationScope, BindingSuggestionOrigin origin) {
    this.configurationScope = configurationScope;
    this.origin = origin;
  }

  public ConfigurationScope getConfigurationScope() {
    return configurationScope;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }

}
