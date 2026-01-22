/*
ACR-466857120fc14a7f919cfdaf63fb01e7
ACR-ecfa06b34fd4402dbce3fbe89ee24127
ACR-2dd386fc28894878b38f2b99068de670
ACR-c1b48cba87fc49fe93b0bbf162b7dba3
ACR-8c6e536938ed4a9c938077a36bf2e0f5
ACR-f4797ccc8ca1488f842af7f98ebbb6cd
ACR-a494cfee493a44e2a9acb28b788264f1
ACR-03e571c912504753b7b9e1e7bd37330c
ACR-4ac3e08e221a4aaf917dc6b257c11165
ACR-6c147c6841104712b5e432af5157cd9a
ACR-e047e5d9ac5b4d63a852a8013f1a7edc
ACR-dfcfc9f0093146d5b7e7fa9b1ece9ccb
ACR-637a5969c9c9435f959a42c74e699ef6
ACR-a007de66e9a64bb5a62cd53bc6941d92
ACR-408f21077cad4e5bbd72c49400e4538d
ACR-5e8f084b951747389d941fadc7a036f2
ACR-558320ff9d9842ba9c863e99ceb0f335
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

public class RuleNonContextualSectionDto {
  private final String htmlContent;

  public RuleNonContextualSectionDto(String htmlContent) {
    this.htmlContent = htmlContent;
  }

  public String getHtmlContent() {
    return htmlContent;
  }
}
