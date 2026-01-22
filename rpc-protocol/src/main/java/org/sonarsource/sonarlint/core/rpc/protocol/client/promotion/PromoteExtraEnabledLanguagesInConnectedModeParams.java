/*
ACR-724c673217c04eb7aee17285c56a6f2c
ACR-735cb3c2eb28431d8e33cbcff5704c92
ACR-f6c596fed12d42c6b6d070781a2beb9a
ACR-df7d4871f5b4417cbf4cce562d67acc5
ACR-f030746f6bff48dfa4080b193002b461
ACR-5e7098715210459d839aed8726e8bd64
ACR-cbd6966212844b3998b7f59feda0f621
ACR-246f1f894fae42dc97707ab2e7e95011
ACR-d88cea523a0e43dea5215c381cf51343
ACR-01172688b00742259b15c3adc8a40fd3
ACR-a51bb884dcfc4537816e473e85938c42
ACR-85656a2d169147308c4f9685b6e511c1
ACR-91e00ad67bf248e998d8eec41363002d
ACR-b541b61f1d394ccd896dcf33bddf2244
ACR-06aba1fadfe44a7b95b166a8317b9cef
ACR-0d9cfa9333de4c7ea56baed215abbcbb
ACR-0c73797b638b4481be7e991d21c9875a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.promotion;

import java.util.Set;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class PromoteExtraEnabledLanguagesInConnectedModeParams {

  private final String configurationScopeId;
  private final Set<Language> languagesToPromote;

  public PromoteExtraEnabledLanguagesInConnectedModeParams(String configurationScopeId, Set<Language> languagesToPromote) {
    this.configurationScopeId = configurationScopeId;
    this.languagesToPromote = languagesToPromote;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Set<Language> getLanguagesToPromote() {
    return languagesToPromote;
  }
}
