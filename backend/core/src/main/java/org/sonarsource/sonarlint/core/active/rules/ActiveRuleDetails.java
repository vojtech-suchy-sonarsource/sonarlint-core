/*
ACR-92df959f75dc47d0af2a49e4f9b9969e
ACR-c1368eafa86f4faabd7558682190c4b5
ACR-132c94e8ddca47c0b160815ebad88324
ACR-9b6627d1fe194d19b9112a7ede8d1330
ACR-b379e9e4cf594991b3cde10a3a3118e4
ACR-1b00cdc13dd94feaa2db8014355de735
ACR-60ed54c447744fb9b50ebe920849e732
ACR-9344eedddb834926b97505de1ea08f89
ACR-57a714843dac4dafb391a8f78d0f4a6b
ACR-69b60452b09c4956b176e0404310147f
ACR-640eb7c21deb42f7ab349f6c53b81559
ACR-64570d07b9db404f91b27c2dc7b1f9fb
ACR-d00bb829a3c849b4a5de2b3fb9b3619f
ACR-95a0c90ed13a4b0a93137f9c6714d0c0
ACR-c0d48d11c2da48458b2ee9b031dbbf31
ACR-6aacef8a0839446c98b494b777e441d0
ACR-f1d3a995f5b34f5a877ca7ea879fc2fe
 */
package org.sonarsource.sonarlint.core.active.rules;

import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;

public record ActiveRuleDetails(
  String ruleKeyString,
  String languageKey,
  @Nullable Map<String, String> params,
  @Nullable String fullTemplateRuleKey,
  IssueSeverity issueSeverity,
  RuleType type,
  CleanCodeAttribute cleanCodeAttribute,
  Map<SoftwareQuality, ImpactSeverity> impacts,
  @Nullable VulnerabilityProbability vulnerabilityProbability) implements ActiveRule {

  public ActiveRuleDetails {
    if (params == null) {
      params = Map.of();
    }
  }

  @Override
  public RuleKey ruleKey() {
    return RuleKey.parse(ruleKeyString);
  }

  @Override
  public String severity() {
    throw new UnsupportedOperationException("severity not supported in SonarLint");
  }

  @Override
  public String language() {
    return languageKey();
  }

  @Override
  public String param(String key) {
    return params().get(key);
  }

  @Override
  public String internalKey() {
    //ACR-82f2ec343fa04a99bbc58bc2e7668b13
    return ruleKey().rule();
  }

  @Override
  public String templateRuleKey() {
    if (!StringUtils.isEmpty(fullTemplateRuleKey)) {
      //ACR-301095bc97ec43838b806699870a9775
      var ruleKey = RuleKey.parse(fullTemplateRuleKey);
      return ruleKey.rule();
    }
    return null;
  }

  @Override
  public String qpKey() {
    throw new UnsupportedOperationException("qpKey not supported in SonarLint");
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(ruleKeyString());
    if (!params.isEmpty()) {
      sb.append(params);
    }
    return sb.toString();
  }
}
