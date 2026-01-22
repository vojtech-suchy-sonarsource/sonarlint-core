/*
ACR-b31b5f331b2a4435b3d2db859a4fde62
ACR-1484470a7d954f9bb91a27add579bada
ACR-c98b69e499ef4671b7c53fe36767a5a1
ACR-d0fd42ac9a0b4b4f8e858248d3a18c1b
ACR-35331fcb0c6f494e8218c38e87bf52e5
ACR-8c1a81ac54214c349e4c0ebd021eeea4
ACR-9e5c7af223094b2798df1214cf2bccb5
ACR-1b7138fe2e1a4191a8ed2c85201c1228
ACR-c418d8032d784428b01887b3ea4284cf
ACR-af6e28c2acd14dd8b7ffd62140c20fe0
ACR-f3bd5b1924e64d52a2e46f54edbb80ae
ACR-2d92ba2a046643a0a741c08f18f50386
ACR-67ec2be023d141368dc2aad834fd71e0
ACR-a72f2e42aab94a0cabbb4f4de894dee8
ACR-aeb3b680b4034225b6b22b8fdf62a899
ACR-c4c555162d9b4469b4242468ee0857ad
ACR-4d38abef4f474973805f990fdd095bb4
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
