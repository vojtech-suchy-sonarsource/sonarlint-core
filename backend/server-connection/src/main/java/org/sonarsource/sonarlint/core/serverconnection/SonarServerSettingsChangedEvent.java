/*
ACR-32396a2c75c346fe85dd9678f803b68e
ACR-03139a2377354ce1ba56daab6c8d0641
ACR-20dd2c985e984d90b3816754b1ac60f2
ACR-aea6e04f1cef468dbb052ccd101418b4
ACR-c900a8cfb4e24e86b31d9d62114c8ee8
ACR-345f4bbbed5b45a1aefd07ad48ede531
ACR-f83939afe690445abb6aa6fe78789246
ACR-6b396dd0dfe545de861531b308a582ec
ACR-dd074ac164dd4f028873ac3df84005bb
ACR-71e3f5fc004e4a69b4d62f90fc1d4dca
ACR-bd471d1aadda457fb10db54a50c76eb9
ACR-0191ab30bef54d41986effbc223924ff
ACR-bdfbc6e50bfa43fe868d9753435dbadf
ACR-e1d3f2770a704f0fa0d9c89864c30d3c
ACR-f7f996e987fa45a5a6bd8d55cf15ad32
ACR-c825a3152ee4499bbbbc03071ea9231d
ACR-ace957dee74e4e059cd1bb89d145b23a
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Map;
import java.util.Set;

public class SonarServerSettingsChangedEvent {

  private final Set<String> configScopeIds;
  private final Map<String, String> updatedSettingsValueByKey;

  public SonarServerSettingsChangedEvent(Set<String> configScopeIds, Map<String, String> updatedSettingsValueByKey) {
    this.configScopeIds = configScopeIds;
    this.updatedSettingsValueByKey = updatedSettingsValueByKey;
  }

  public Set<String> getConfigScopeIds() {
    return configScopeIds;
  }

  public Map<String, String> getUpdatedSettingsValueByKey() {
    return updatedSettingsValueByKey;
  }
}
