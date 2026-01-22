/*
ACR-8bf13f79b6c24ee7ae8245cb63e837d9
ACR-5532631954b14110965303caef51e6f7
ACR-783cd06ea9994096bf1054cbb41ed0b6
ACR-cd36ff83917a4d969de8abfedc251585
ACR-b2f92deb004a4a05bd8f887733ac4ccf
ACR-403a9e77caac476186382175251c99bb
ACR-1354b072852c4cfda53aa2723bcfb7ec
ACR-8f1e8d66733b4680a0a1deffdbb85a0e
ACR-08ec1ba5c23440dfb38af22e63542e33
ACR-044540ed21ad4c269d06d5f033970e31
ACR-b98298c7710b449b9f88d3b6398d6729
ACR-0d6aacb5a572431a8980a75ec428f09d
ACR-90cdbe2f43f44bdba8ef5e0e20e770a1
ACR-560f533fac7c4fc7aa62e24810f3670c
ACR-ba003fa850c54f51a42685274c19fb72
ACR-14d380ea33934d919d44d10cb80b000f
ACR-d2ce00f0b66a46f994259de692aaa229
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class RuleDefinitionDto {
  private final String key;
  private final String name;
  private final CleanCodeAttribute cleanCodeAttribute;
  private final List<ImpactDto> softwareImpacts;
  private final Language language;
  private final Map<String, RuleParamDefinitionDto> paramsByKey;
  private final boolean isActiveByDefault;

  public RuleDefinitionDto(String key, String name, CleanCodeAttribute cleanCodeAttribute, List<ImpactDto> softwareImpacts,
    Map<String, RuleParamDefinitionDto> paramsByKey, boolean isActiveByDefault,
    Language language) {
    this.key = key;
    this.name = name;
    this.cleanCodeAttribute = cleanCodeAttribute;
    this.softwareImpacts = softwareImpacts;
    this.language = language;
    this.paramsByKey = paramsByKey;
    this.isActiveByDefault = isActiveByDefault;
  }

  public Map<String, RuleParamDefinitionDto> getParamsByKey() {
    return paramsByKey;
  }

  public boolean isActiveByDefault() {
    return isActiveByDefault;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public CleanCodeAttribute getCleanCodeAttribute() {
    return cleanCodeAttribute;
  }

  public List<ImpactDto> getSoftwareImpacts() {
    return softwareImpacts;
  }

  public Language getLanguage() {
    return language;
  }
}
