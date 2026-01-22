/*
ACR-cba8ec31619d4f739b2047a1c6da6bb0
ACR-f3f48ab966c645f6a69885d44634d032
ACR-3e949d78f10e44b5aeb898393d0ce7df
ACR-8f2714e24a1d4eb5b2588c3fd5074648
ACR-35f9b492b83946c79fbbd3485824c82d
ACR-58432831d8104609be7aa4d94b461841
ACR-3d4f0626b50248d6a367dabe44e39718
ACR-cd71d1d0bc564c15a6c1f3adc04bebfb
ACR-e8cc7d5450e944b0958c70fb61717c0c
ACR-18a1d2ac06cc43bd95695bd1ddcf6c25
ACR-469b712dad504500b43a72d8fec67297
ACR-da7e550096bd4d48895e42c96ec5e5aa
ACR-b91c045fad074f8f90da3e12ca6d22e9
ACR-f8e48841da9c4988855d0c2f9cbcf01e
ACR-4e25b4618d064b4babcf4ba92c983791
ACR-ff0049bfe7ec4d1a9ba1056144f53464
ACR-278c0e64ce674616ad9842937a3f7419
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
