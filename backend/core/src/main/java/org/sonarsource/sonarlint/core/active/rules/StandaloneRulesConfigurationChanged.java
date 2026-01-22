/*
ACR-0fb099045ba14af0a1bfe2b5d8bac2cc
ACR-9a4e7e6ae0804271aea520fc9bfec14a
ACR-28276470d78c432998039130cda22ba1
ACR-165360c7ccc34f97828e3ff8d9b37ad9
ACR-969aa4746b074c9badd47cb5e88a01e3
ACR-de1ed1387052428ea2b16cc3ce087ecd
ACR-aa44a14fdd4443558f2e142964e23705
ACR-ee31aa0ca9384c1daab39946064cf52f
ACR-f9b1323440424ffea5d48b11625cfdda
ACR-d03eacdbede849128b4faac5cd610443
ACR-aa771368fec44ac5bce3b9b6a7280f09
ACR-2a3496ffd7464c298263d4fb9b634ccb
ACR-2e948e5ca456445b989ddb9ccce4e9cb
ACR-a8d37a4af2484de08879de713f2bdd29
ACR-f60d4f3725504bf2bb5f1a18278053b4
ACR-b97cc13d27f8458780b5a4b6156e7253
ACR-7aeb85f3fbee4b15aea3e7b83d38a000
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
