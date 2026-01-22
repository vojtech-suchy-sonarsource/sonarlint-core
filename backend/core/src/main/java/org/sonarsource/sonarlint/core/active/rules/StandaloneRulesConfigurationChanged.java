/*
ACR-abddbf75fd8b441999d076f15ced87d0
ACR-653accd89a304eff9c9488a294b0afcb
ACR-d87b164690fe4b09ac667a1be7e7897b
ACR-d9c762726d794b7e9e754fa35846a695
ACR-7733662aa69e46e299b2185f6b2ed2d6
ACR-460c7dda6e5d45909bd45b6edb6455ee
ACR-46129d9c9bef44d089763965c91fc9c3
ACR-d3cf4c7d219f485ea16ca5286b5fadcb
ACR-0c216eed77f349289ce2c9bfb6bab9d4
ACR-6c3cbb4d9fb34cae990714eac4cf4fa6
ACR-c8fda99f91a54038bbcc7ea232b0dbbe
ACR-4873fcd86a7f40af862fcc7853802342
ACR-1d524c799f814c35adafd97db9aa565f
ACR-d4a5e5df4b714468975b857ced2a7c8f
ACR-a0e22410b5b34ce8b9c553cd150da900
ACR-2865f957b87d4c468bdc7a0ff309e712
ACR-df732d681b2d44b191940699f3aba101
 */
package org.sonarsource.sonarlint.core.active.rules;

import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;

public class StandaloneRulesConfigurationChanged {
  private final Map<String, StandaloneRuleConfigDto> standaloneRuleConfig;

  StandaloneRulesConfigurationChanged(Map<String, StandaloneRuleConfigDto> standaloneRuleConfig) {
    this.standaloneRuleConfig = standaloneRuleConfig;
  }

  public boolean isOnlyDeactivated() {
    return standaloneRuleConfig.values().stream()
      .noneMatch(StandaloneRuleConfigDto::isActive);
  }

  public List<String> getDeactivatedRules() {
    return standaloneRuleConfig.entrySet().stream()
      .filter(entry -> !entry.getValue().isActive())
      .map(Map.Entry::getKey)
      .toList();
  }
}
