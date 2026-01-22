/*
ACR-3fd9876194a143a49e28b22e91ee9667
ACR-fc822e3074fa48cca42b83ba055291ce
ACR-da62898d99fe43aaa71e06412e4e1d6d
ACR-26d35394b8a44e1db160bc8ebee3e3c7
ACR-4128424351264b7fb071898c9cfce6df
ACR-a945c208c16e4d72a8b478c552c9e841
ACR-112d6cab59d44844b68c783a2e9700e1
ACR-eaf9281f187b43b08b50d4dceaa773c0
ACR-7b60270ff64f47e691396c1c5a66b052
ACR-632024f10a244b4c838206d82e3a246c
ACR-43e72709dbd74bd9a375ed9413fc9daf
ACR-a22795e6dfb94ba3801e385b41a2c15b
ACR-8dd0f814970b4f1989cde8022c804f75
ACR-bbe1eca726be4b83a7cf3a08168f6af4
ACR-0a5db70293dd4a13a8c3fcb822b2f0bf
ACR-4a8ebd94ef23489fa01d65aad858551a
ACR-d325f0e0376c4b7a9f040a50dfe854be
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.rule.RuleKey;

public class ActiveRulesAdapter implements ActiveRules {
  private final Collection<ActiveRule> allActiveRules;
  private final Map<String, List<ActiveRule>> activeRulesByRepository = new HashMap<>();
  private final Map<String, List<ActiveRule>> activeRulesByLanguage = new HashMap<>();
  private final Map<String, Map<String, ActiveRule>> activeRulesByRepositoryAndKey = new HashMap<>();
  private final Map<String, Map<String, ActiveRule>> activeRulesByRepositoryAndInternalKey = new HashMap<>();

  public ActiveRulesAdapter(Collection<ActiveRule> activeRules) {
    allActiveRules = List.copyOf(activeRules);
    for (ActiveRule r : allActiveRules) {
      if (r.internalKey() != null) {
        activeRulesByRepositoryAndInternalKey.computeIfAbsent(r.ruleKey().repository(), x -> new HashMap<>()).put(r.internalKey(), r);
      }
      activeRulesByRepositoryAndKey.computeIfAbsent(r.ruleKey().repository(), x -> new HashMap<>()).put(r.ruleKey().rule(), r);
      activeRulesByRepository.computeIfAbsent(r.ruleKey().repository(), x -> new ArrayList<>()).add(r);
      activeRulesByLanguage.computeIfAbsent(r.language(), x -> new ArrayList<>()).add(r);
    }
  }

  @Override
  public ActiveRule find(RuleKey ruleKey) {
    return activeRulesByRepositoryAndKey.getOrDefault(ruleKey.repository(), Collections.emptyMap())
      .get(ruleKey.rule());
  }

  @Override
  public Collection<ActiveRule> findAll() {
    return allActiveRules;
  }

  @Override
  public Collection<ActiveRule> findByRepository(String repository) {
    return activeRulesByRepository.getOrDefault(repository, Collections.emptyList());
  }

  @Override
  public Collection<ActiveRule> findByLanguage(String language) {
    return activeRulesByLanguage.getOrDefault(language, Collections.emptyList());
  }

  @Override
  public ActiveRule findByInternalKey(String repository, String internalKey) {
    return activeRulesByRepositoryAndInternalKey.containsKey(repository) ? activeRulesByRepositoryAndInternalKey.get(repository).get(internalKey) : null;
  }

}
