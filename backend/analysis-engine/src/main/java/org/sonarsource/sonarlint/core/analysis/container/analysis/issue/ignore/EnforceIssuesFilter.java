/*
ACR-86ab1267738a4eb8ae7b5f3f3b90cb81
ACR-921205bfc219449d9e13018842bb80ed
ACR-671dd2ed4edc404597eb2c0470785378
ACR-c58f156980e04adb93654d12f11bed72
ACR-7e7880e5ac4a40d189dd842829d397c5
ACR-fab6cd6af48e4fcfa5d52076b0447eab
ACR-31697dbb46cb40cc8388b95c62cda0c0
ACR-7ecc4de1c2c14e728c0c1dcaabfc2f62
ACR-b603f9ea2ea0469b9932d26af72fc4e6
ACR-2d7c43fb2135480c8a4dbfd72778f8c6
ACR-4e2ca86838ea43d1bf5568fadc8cc7cd
ACR-584ac6e0f3914694bd777368a3acf4ab
ACR-c05100e6cfc34c9680a55138c3718572
ACR-f7457f62c72041969c5513dce50dba1c
ACR-da4166d5125a416894d41c622228b72e
ACR-0b884dac277744d5b987fde617f10a4d
ACR-774bc51661f2491ab3ed60d7956248f3
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
