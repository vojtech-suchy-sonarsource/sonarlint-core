/*
ACR-7ad510b6698c408ebb48bfa0c9515ef7
ACR-63e4b1894b0a4698ab5fb8cb4fb41cae
ACR-bde743c24d864cb38e1a13667c8ebcb0
ACR-2b4124ed8f524e058736d2792bf2cb66
ACR-7e61c7a0dd4646548f4213425863b3b3
ACR-8af7a3b9498a4080b82baab07c04cd19
ACR-ee2caa771d7348a2827819b62c137feb
ACR-89c2731d583649f194b2219b06dd57d9
ACR-8ba88ca1c8684bd6aacfd4cd218a47de
ACR-c2c200b827f045de9bb3a32997157ba6
ACR-06ea102fa47b4d3e91b6c3848856383f
ACR-80b76ee8f5e94b3abb46207661befa4d
ACR-566180a74e66464da0d9f333761461e3
ACR-d4f12e29061b4034b7a8aab1b2b4bd0e
ACR-8677a356902c4815be91fa2b2d9f559a
ACR-0d129980091342f6bbe6d53af032e06a
ACR-a0d58934af154e57a6052590ecbd84a1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;


public class RuleContextualSectionDto {
  private final String htmlContent;
  private final String contextKey;
  private final String displayName;

  public RuleContextualSectionDto(String htmlContent, String contextKey, String displayName) {
    this.htmlContent = htmlContent;
    this.contextKey = contextKey;
    this.displayName = displayName;
  }

  public String getHtmlContent() {
    return htmlContent;
  }

  public String getContextKey() {
    return contextKey;
  }

  public String getDisplayName() {
    return displayName;
  }
}
