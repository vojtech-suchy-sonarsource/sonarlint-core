/*
ACR-b6d6de8f26ef4885bfef949d38c04751
ACR-9b57bc0f1498487188b02738b387b5c5
ACR-89664ad63e89449ca6203b7a02ec00ad
ACR-20527a84b9cc4fe79804a53f97464ff9
ACR-1f1e794566654b0a8196a3fd426d7622
ACR-1d137e590ca7400db0bbbc84fe943e96
ACR-f68b1af1ff1c467b85958c6af3e39051
ACR-d5e90d82c9f74b4c90009bafd23f6021
ACR-af489be06edc461ea96cced818203e91
ACR-d9abf918216f49f3a9b15680a6267468
ACR-f393e448c37740af8dec122c25fec75c
ACR-ba7449634c954e1f905911a08fb00ec5
ACR-d9c2a420900a47ec876218a5ad351c1e
ACR-c84a738c19964e8d90a26d6c5579cb20
ACR-0bab9c7e3fed4c679e4318b542aceefa
ACR-cf8acc8da2d44a54a9c7f6761596437d
ACR-8b6a1b7491694fc99111d4d046649f1b
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
    //ACR-dfb7b8a1a73d4931a88ffb3ed84455a2
    return ruleKey().rule();
  }

  @Override
  public String templateRuleKey() {
    if (!StringUtils.isEmpty(fullTemplateRuleKey)) {
      //ACR-61b96a61cf3f4d61a65d319689661eb4
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
