/*
ACR-5ca0688b963848ac949a4633ab047708
ACR-3a121c1858ce43d98784bb240662871c
ACR-2f2c17cf84c241c39a211881b3aa23f0
ACR-39b033d896e9492cb005b694b5afca30
ACR-ef6cda444a1d459a97c67bdf2d2aa197
ACR-06526f8db8d74afb8ef157da6d38f31e
ACR-078b28d528f64bbe937cdb28a9d0090b
ACR-6b221a66f9f84cef8e2dbde18ac918a4
ACR-8ee3ff41bd9b4dc2923136886ae01292
ACR-8a3432c363cd4205853e9f70179c0587
ACR-0160d13c32974af2b1e69eef90116a59
ACR-9ea3b83b3e2c409da4c43fd86f5423fc
ACR-2c2231b63502409b861bde3a1f8abd78
ACR-4fbd3caf67a54ec39aa53ecdec1a885c
ACR-15ff1f3671104286bfe690ed1b67c90a
ACR-25825b51ef07425d9145825816e86efe
ACR-c6a522f7fef84b89b39a5a690988b1d4
 */
package org.sonarsource.sonarlint.core.event;

import org.sonarsource.sonarlint.core.repository.config.BindingConfiguration;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;

public class ConfigurationScopeRemovedEvent {
  private final ConfigurationScope removedConfigurationScope;
  private final BindingConfiguration removedBindingConfiguration;

  public ConfigurationScopeRemovedEvent(ConfigurationScope removedConfigurationScope, BindingConfiguration removedBindingConfiguration) {
    this.removedConfigurationScope = removedConfigurationScope;
    this.removedBindingConfiguration = removedBindingConfiguration;
  }

  public String getRemovedConfigurationScopeId() {
    return removedConfigurationScope.id();
  }

  public ConfigurationScope getRemovedConfigurationScope() {
    return removedConfigurationScope;
  }

  public BindingConfiguration getRemovedBindingConfiguration() {
    return removedBindingConfiguration;
  }
}
