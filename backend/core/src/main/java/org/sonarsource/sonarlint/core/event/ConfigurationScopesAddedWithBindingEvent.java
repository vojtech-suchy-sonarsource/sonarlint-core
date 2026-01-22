/*
ACR-1bf1d15d28da45378ca1dbe3ebdb5b45
ACR-86de5e1a4830422fb97bbc5871ebd7bb
ACR-0beaa4a3fee94131a8322cb4f5a44281
ACR-ccc7b866580a4e0db411bb7d25f7caef
ACR-2e37c90e82e84e709a3c2bf8224e0ce7
ACR-a48e2685d7a543c7b7f621eda84c9996
ACR-8b94229bdae84451b6520017fa7e4cdd
ACR-b6b4964b662d407f8216a5dbc6beb0fd
ACR-d0f9f6435da14859857fce3a8211b667
ACR-f362e07633bf4864af2aa77e3ddf6e4b
ACR-01a616fcf2534078a67388f8b2a320fc
ACR-8740be6a5aca45bcaeb375e62cc0f42d
ACR-a0df749414a14ea292aa450c78479f61
ACR-a3ac608ab89847079c166bf2c4a02fa2
ACR-b86cc566b2d8491d8b42aa9e2358e6b7
ACR-315cfaf4590e4a6cb43b471dde01be25
ACR-20e9b2d0fcc34b719b035c43b299aa67
 */
package org.sonarsource.sonarlint.core.event;

import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScopeWithBinding;

public record ConfigurationScopesAddedWithBindingEvent(Set<ConfigurationScopeWithBinding> addedConfigurationScopes) {

  public Set<String> getConfigScopeIds() {
    return addedConfigurationScopes.stream()
      .map(configScope -> configScope.scope().id())
      .collect(Collectors.toSet());
  }

}
