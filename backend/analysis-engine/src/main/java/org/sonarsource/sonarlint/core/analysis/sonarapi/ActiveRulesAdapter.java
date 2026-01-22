/*
ACR-57278c43a13f43e79dac25fa2026e468
ACR-b0bca0502e98498485a0882f1110746b
ACR-8e3d17fbbeb343b0ab91b948b5d95a75
ACR-e46e406b244a4c8aa3c525d8b5aac806
ACR-7f1bf04b15974d11a18f1bb5e066c408
ACR-3e00327a337e4837a704dc277bfefd7e
ACR-ae0654c902b64f64bb60acebc28fe48b
ACR-9028eb27725241ed910fa43deccc0ca2
ACR-c0420c1c77d74541a3386d6ffb29c196
ACR-acf7688714b641b2badd88b2c2291715
ACR-47e4df7b30df47e694d8418294376a45
ACR-c5a4c224683b4d2ead1b0a66c313e90d
ACR-1869d8a9dc19496ca67239891be035b6
ACR-e2d53ba1a0234326ad68dbf252838bb2
ACR-9929a92b4a2b4d9aa20b01681773c367
ACR-e720bf202e204a44ba4e7a0c1db74875
ACR-8df3b31c8b6040e4aaa649261cbb7cf7
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
