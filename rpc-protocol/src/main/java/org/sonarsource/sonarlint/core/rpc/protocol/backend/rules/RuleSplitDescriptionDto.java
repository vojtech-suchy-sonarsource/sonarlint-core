/*
ACR-9b8256ab806842cb8f0175e9dcac5c0a
ACR-2afc00dc744c497cb036ae1d26799166
ACR-bdcd3c01da3f4f7595d8dda60253c25a
ACR-8e986764fcac47a6b7b179ce78ae6266
ACR-03a72a92e16a4dc2bc926b554d11fe23
ACR-68c60ffa7faf4ba481a546fb932816c6
ACR-6ac0310fd68548319721eb350874eef8
ACR-06e8c107664042a4aedf3cd111defcc1
ACR-0d93cfd4fe0040a5a7b9c01bbe089f8c
ACR-4b970a6e1b624a0095c3af668575a9c6
ACR-231ff0e6f81b4a41be5c612e11401bca
ACR-004d20372a0a48e688cff7dba1f09cb1
ACR-8e8af66379bd4f928cce7e323f281998
ACR-3c06d5fdd4364b5999467899dc710268
ACR-62392c3914fe4677ace71eeb671e6def
ACR-7f170b28934f429aacbc745150bf84f3
ACR-a00ce1d6996f44b2b544180bd3287705
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class RuleSplitDescriptionDto {
  private final String introductionHtmlContent;
  private final List<RuleDescriptionTabDto> tabs;

  public RuleSplitDescriptionDto(@Nullable String introductionHtmlContent, List<RuleDescriptionTabDto> tabs) {
    this.introductionHtmlContent = introductionHtmlContent;
    this.tabs = tabs;
  }

  @CheckForNull
  public String getIntroductionHtmlContent() {
    return introductionHtmlContent;
  }

  public List<RuleDescriptionTabDto> getTabs() {
    return tabs;
  }
}
