/*
ACR-84198bac2a09431e96e8364d5c596a22
ACR-8d203574354648b8be11907a37baf62f
ACR-0df7d67248da4e8f83425df325b658cd
ACR-a254f2b315d244b1982305faac541970
ACR-b7636048407543a38d2f734ba5d8a473
ACR-df6800fa9f62435e8c211430167a3324
ACR-d88e692f61b64a5ea11a4150935cbd8b
ACR-8122450865264d8782e6326012ebf7b1
ACR-383cb4e06b4c479ea196ee36ed42028a
ACR-39fd9855c0ae4320a94938094d9a3ae4
ACR-f48957cccc3548c083b9cac066d60dfa
ACR-d3c9507b133b4b118cabc5fb029efe72
ACR-02a27762c12843688b83d264f7cdba1f
ACR-0d4307b3b2d54ebb80c673c154db0c9d
ACR-3177d1c292de4098ad54719ecdf6ce04
ACR-a0b6e96077914435bb5e8f86e3f13f99
ACR-81d129d365fe45bf9752e9436db339ff
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.List;

public class RuleContextualSectionWithDefaultContextKeyDto {

  private final String defaultContextKey;
  private final List<RuleContextualSectionDto> contextualSections;

  public RuleContextualSectionWithDefaultContextKeyDto(String defaultContextKey, List<RuleContextualSectionDto> contextualSections) {
    this.defaultContextKey = defaultContextKey;
    this.contextualSections = contextualSections;
  }

  public String getDefaultContextKey() {
    return defaultContextKey;
  }

  public List<RuleContextualSectionDto> getContextualSections() {
    return contextualSections;
  }

}
