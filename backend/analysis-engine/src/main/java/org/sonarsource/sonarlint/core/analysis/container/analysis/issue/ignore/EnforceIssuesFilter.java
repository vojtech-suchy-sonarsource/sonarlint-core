/*
ACR-d2a9a887303c4e34b4d80e4830f48187
ACR-20a4bfe77cae4939a1a942abeb6a11c2
ACR-0e01b846b03a406988e50b63ecc7d588
ACR-1e656974cec64d2ca0686e04098e2440
ACR-cac033ed11124e9dbd2a457c06ab548d
ACR-5781924db02445538c47a80f2f1ab859
ACR-ab856d7fa0744d84a997b4793c7f791c
ACR-1d56fc2b32cf4ed489069c2a155cce70
ACR-e6f1eeff1324412dbadaececeff806e2
ACR-2c532734863e4a709dd583ba5aed37a7
ACR-798573a2156d42c3aad29dd47b4660e2
ACR-d87ed317e5bc4fce99af6c1a5dfd5a58
ACR-1d33b28a74e3483f91124f66ff607feb
ACR-eda52d22756949e2a4a4977869d4e3ae
ACR-8cd0cbb6289849138958ab277b253e2a
ACR-d5eeafbd6f0b43bbbe0dd3138876cc5e
ACR-e923306eab4745d9919d8f7ebfd4afad
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sonar.api.scan.issue.filter.FilterableIssue;
import org.sonar.api.scan.issue.filter.IssueFilter;
import org.sonar.api.scan.issue.filter.IssueFilterChain;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssueInclusionPatternInitializer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssuePattern;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultFilterableIssue;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class EnforceIssuesFilter implements IssueFilter {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final List<IssuePattern> multicriteriaPatterns;

  public EnforceIssuesFilter(IssueInclusionPatternInitializer patternInitializer) {
    this.multicriteriaPatterns = Collections.unmodifiableList(new ArrayList<>(patternInitializer.getMulticriteriaPatterns()));
  }

  @Override
  public boolean accept(FilterableIssue issue, IssueFilterChain chain) {
    var atLeastOneRuleMatched = false;
    var atLeastOnePatternFullyMatched = false;
    IssuePattern matchingPattern = null;

    for (IssuePattern pattern : multicriteriaPatterns) {
      if (pattern.matchRule(issue.ruleKey())) {
        atLeastOneRuleMatched = true;
        var component = ((DefaultFilterableIssue) issue).getComponent();
        if (component.isFile()) {
          var file = (SonarLintInputFile) component;
          if (pattern.matchFile(file.relativePath())) {
            atLeastOnePatternFullyMatched = true;
            matchingPattern = pattern;
          }
        }
      }
    }

    if (atLeastOneRuleMatched) {
      if (atLeastOnePatternFullyMatched) {
        LOG.debug("Issue {} enforced by pattern {}", issue, matchingPattern);
      }
      return atLeastOnePatternFullyMatched;
    } else {
      return chain.accept(issue);
    }
  }
}
