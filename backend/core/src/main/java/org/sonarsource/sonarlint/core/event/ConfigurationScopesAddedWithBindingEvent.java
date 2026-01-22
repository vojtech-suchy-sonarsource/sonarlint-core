/*
ACR-60ff7c387fcd4f159cfcdb248005cf88
ACR-0426309bfea9455e8f63cb13b3bfa0e0
ACR-4bb2b5e40b2e4627a35271ceea3b2ef0
ACR-5e7332325b0e4b7aaf814c564cb03430
ACR-5274821aaadf4eaa877f63d6e3b5c4d6
ACR-867036464702457b8d704a0b18a5e5ba
ACR-ad66f40d01424bf4ae6e24e53b811469
ACR-209890dbb4e54bedb31fd1b30de9a0f1
ACR-d1d9005ed1f348629d223cdf9ef1bd66
ACR-e030c953f70842e89e287bea0bf8eb96
ACR-152ea2a005e74a18b01c1ed40c1e85a5
ACR-5034c7b9e5d841f29686d2f2ef32408b
ACR-a41940e6e7cb42b7892714f1d3360bb0
ACR-ff04f111a91140f2b192de3d6742ed15
ACR-a05f71b53ddc4264af491a2eb91a0310
ACR-6d0b045d76b249c78baca30e4019e9e8
ACR-55c4c1f916cc4557bce3b7417b019459
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
