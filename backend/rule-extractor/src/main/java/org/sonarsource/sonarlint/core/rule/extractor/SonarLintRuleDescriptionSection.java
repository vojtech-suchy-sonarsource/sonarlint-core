/*
ACR-e76aced68e6f40bcb0ede9ef78b178c4
ACR-2a063595db944e3a987a0f49e13248fa
ACR-51d02f5007e6478f996cf44f5d72e74b
ACR-68bcc4fc17ed474cb0674bf46e98c254
ACR-93ef43a53064448f8be0d4f3ec99c3f9
ACR-0ae291bed9964bcab1f30af435532dbe
ACR-f48a00eef53d4a9882e1f2ec017322e0
ACR-982e0cdb1c4a419998170426a251fdab
ACR-b6c36464cdea4cb4ad8909becc959b0e
ACR-4d83366dd52940b4939e0bc794948170
ACR-81de1df0a236495a8b984a14aeac1e0d
ACR-4fb326c6e8514cd5b5b2ad2a5255af25
ACR-d3c149986f7b4f0193e665a142f22d74
ACR-9d50007b263f40ddae89ce1710f84069
ACR-59d31c1d673b437a9945bdec6620ad17
ACR-a4453bff861d4f3199758130a5b1cd8a
ACR-7042e9ee4df147b6bad1a545db0d4ae6
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
