/*
ACR-bd5a4680a94d4a6f8c608cb1cb90792b
ACR-9b85ac520a9e4381b45db9c300da4381
ACR-5ba418121ea6444d8a6ce8c7a9b6d57a
ACR-7d6a9d0e98104f918fa18185c3e484c6
ACR-4b1231fa1faf492592caa4d10370d07d
ACR-edb5fd8a0632496792390670d7886c3d
ACR-c9d37bc1160c46c6adde8098ff8f83df
ACR-e755f274b2ab4a71af6bd15d84dcc87e
ACR-e1306f809f794c5d9fab60bfcb220ee0
ACR-a7784446ade44fe48d87da0085727608
ACR-ef7ae47aa8a54efb9b0d3bd9851abb8c
ACR-5aa9643fa5d147deb0e5ca3c2efcb21a
ACR-e1f72ec87dde49b5be1c39770293317f
ACR-ba531d8b012d4407959cad1ea7cc23eb
ACR-0a00e90ac7ad4095926a1c29c56cace3
ACR-30dee312b2b74d47b780c5acaea14aa6
ACR-defe066d1d904f2981432ff9a1af5436
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
