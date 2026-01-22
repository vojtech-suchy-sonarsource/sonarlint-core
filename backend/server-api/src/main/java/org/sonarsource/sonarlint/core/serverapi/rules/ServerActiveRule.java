/*
ACR-bbc29ab3961443f2ad755499b0826070
ACR-48ad26457e094ff0b5476dfbd87fc92e
ACR-f3ef5ec9b3cc4cc1a6dab9cf1e1d1bf0
ACR-9b13ea19921948179604f759b7206a74
ACR-3d98a40650d842f2bfffd8d96e18b854
ACR-a5ef68ef5223433ea7b9512284f34849
ACR-eff6d17a44114c74831c45b445690da6
ACR-5348039547ff4127af5849f0acfba168
ACR-aaf170ec9285475ca27a1e2f8692acbe
ACR-03a5b5327ccb4dfba5818afe22c510bf
ACR-32ba4af5043a4d97acd830ec9b5e5523
ACR-be3d4683243e4363ab2a4cbe249a1e0a
ACR-c24917e78b6b4e349e84d6b2d5c0c90c
ACR-4f35d84ffbe143c6ac673f75498120f8
ACR-111e29bcbf0246cdbb30aa11c4768eb2
ACR-86798c4cf7514366817730ab989b1c93
ACR-d164eb0083334acd9b98bd1eabb639a7
 */
package org.sonarsource.sonarlint.core.serverapi.rules;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;

public class ServerActiveRule {
  private final String ruleKey;
  private final IssueSeverity severity;
  private final Map<String, String> params;
  private final String templateKey;
  private final List<ImpactPayload> overriddenImpacts;

  public ServerActiveRule(String ruleKey, IssueSeverity severity, Map<String, String> params, @Nullable String templateKey, List<ImpactPayload> overriddenImpacts) {
    this.ruleKey = ruleKey;
    this.severity = severity;
    this.params = params;
    this.templateKey = templateKey;
    this.overriddenImpacts = overriddenImpacts;
  }

  public List<ImpactPayload> getOverriddenImpacts() {
    return overriddenImpacts;
  }

  public IssueSeverity getSeverity() {
    return severity;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getTemplateKey() {
    return templateKey;
  }
}
