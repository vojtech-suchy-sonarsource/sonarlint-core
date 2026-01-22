/*
ACR-bad2da902b6f4757be53028166c88060
ACR-871ce374a85640e0868107ad7faf6679
ACR-ab840f9c56914235ba6b49d3211e297a
ACR-eb754fda47f0431ba1c8f0b0208175da
ACR-1ae686febc7f41418a9f1305a3fed59b
ACR-38901766e5d24df4b083dcd1db5bfa8c
ACR-defc05a138be4056b86c900a91fce2ca
ACR-8cb4bb17292744a9a20082e22d6d20af
ACR-78a13e90b11d43be896a41e1d3b429ba
ACR-2201f43e4dcb4719b571dcfbddc46948
ACR-dda9e245fd1e46d18e18286b0b74c0cb
ACR-83d5a7613c0c4779b9f50b7a1c428f57
ACR-cbd23f8afc59449fbf9a03d9c0ca9d9b
ACR-8bc1453731164be1a39dd8aff32cfbb6
ACR-f41e0897b6f0402ab92aaf0768418ec7
ACR-005632a473194bb592899451e994cfb5
ACR-4587eda94b2644a29f598a30827850ae
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
