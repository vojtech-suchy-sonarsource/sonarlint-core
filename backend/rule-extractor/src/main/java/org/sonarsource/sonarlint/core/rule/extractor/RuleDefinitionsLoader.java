/*
ACR-227a3058d33d4a5287e47c17dd616250
ACR-a9659d23b15544a99b3f682a99cf70b4
ACR-d83e8ce8167a4df5813746fe033bd6fb
ACR-8c6f921f2a39455486a7dc986325e76a
ACR-58fa862fb03e402a8478be736fcde03d
ACR-1912b37f3acf44028b8a2fc720c68698
ACR-f14eba80d45c43979e933c06d3f39e62
ACR-df5856a3d4d54d9a9900cb4ce07f6cbe
ACR-403e6efe3bb44feea61913188ec97093
ACR-9b1579e12c844983a791eb3807d85cc9
ACR-47d5bdad8ee14cbcbb5616d8c1e7343f
ACR-25747ea3cce74241a7d909fcbc00b842
ACR-0dbfa2f912724c9da776c46bab768403
ACR-e4d28ddcaf4f4fe8b59461129738d6ec
ACR-6e499dc2a7bf45d19aa089b0e6a6b60e
ACR-2b3e913fdee64b35870f61db47b93e70
ACR-c2cb35d9171a4eb09599070e5af27797
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.List;
import java.util.Optional;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-cea2cf3cfda144378e90c51889eba4a6
ACR-b35257504d494e8a8aaab5ce3de506b1
 */
public class RuleDefinitionsLoader {

  private final RulesDefinition.Context context;

  public RuleDefinitionsLoader(Optional<List<RulesDefinition>> pluginDefs) {
    context = new RulesDefinition.Context();
    for (var pluginDefinition : pluginDefs.orElse(List.of())) {
      try {
        pluginDefinition.define(context);
      } catch (Exception e) {
        SonarLintLogger.get().warn(String.format("Failed to load rule definitions for %s, associated rules will be skipped", pluginDefinition), e);
      }
    }
  }

  public RulesDefinition.Context getContext() {
    return context;
  }

}
