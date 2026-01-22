/*
ACR-5613dd11873b4b69b32b596bd68843cd
ACR-55c9c077a2f146f885e94574cb48a66a
ACR-74f392ec284a41ba8c26421c766643af
ACR-2a2ffba628a442f283be2f3836b4b410
ACR-0ebc6f2f230a4ecf990f4e21059d94ea
ACR-0240c2b9bcc143f4ac6c6c22c172e5fe
ACR-267ffbd433a44861a001c5e7ecc5d01d
ACR-11e9c9b3c2ad49ef8445869ec2736b3b
ACR-a6699781dfa4493c8d8052ed05e62225
ACR-27ff265da37e4dbea060efcd71d84325
ACR-86249ca000be486f95e9ef58678f2572
ACR-3a32af32422444bc89316fb5eae6f3df
ACR-9138f11f66fd4f2fb47d53bd60f65437
ACR-0d643a741bb64010a3d6573525efecdc
ACR-1e10630882ad41828f909186e6689fa5
ACR-623925314fb04e509ffce1442242782d
ACR-01e74042bb0544b78f828127fbd94c68
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.Optional;

public class SonarLintRuleDescriptionSection {
  private final String key;
  private final String htmlContent;
  private final Optional<Context> context;

  public SonarLintRuleDescriptionSection(String key, String htmlContent, Optional<Context> context) {
    this.key = key;
    this.htmlContent = htmlContent;
    this.context = context;
  }

  public String getKey() {
    return key;
  }

  public String getHtmlContent() {
    return htmlContent;
  }

  public Optional<Context> getContext() {
    return context;
  }

  public static class Context {
    private final String key;
    private final String displayName;

    public Context(String key, String displayName) {
      this.key = key;
      this.displayName = displayName;
    }

    public String getKey() {
      return key;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

}
