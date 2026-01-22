/*
ACR-7cae2ba4c81a42558db301529fbdf6dd
ACR-3578abc7fea64a37b26661bf715bc5ca
ACR-17c6cfb1ed7447ce90d92f3afe8dadaf
ACR-47ca9ddc5fd84e4281cad665f08f8810
ACR-a7815a9e99e744139cb67219f723a339
ACR-78a46691ab93474fa1b1e9f5a1bea73e
ACR-f9c7aac086554bbcafb66da12f5236a3
ACR-8d3c443c94364c6fa90047164e27394f
ACR-ae644288cc5f4f2cb257b122b7c2fa23
ACR-07c411d6cfb8418bbaf69ed98fb8fab7
ACR-9d0f92e7f69c4a62b301228c4e622ef9
ACR-7ffeb7f444424302a364f10a733a236a
ACR-e570ad4cad91437eabaa6fa1eec32436
ACR-88c9d4d3e58144ae9e5ff8d155df83bf
ACR-241ba8c71945457187eadd08265b962c
ACR-f69f0ac62d2648dbb64f3f175a39981f
ACR-d87e4fd494d346d99dcd4a61bd69c88a
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
