/*
ACR-30328a5d307c46ebba23a4709200b717
ACR-6238edad3c1f42eb9286b1b012ae3b69
ACR-a023533c25ec47c28750ba591b1d7866
ACR-bfc3a7491ac645118aa073c2e0104246
ACR-c3980bfc0362432ba4a6001c737e64f0
ACR-e62f08e4a60e4584ac4e474fc5d037f1
ACR-b796350a1d7940fea7bc207fde1239dc
ACR-a73f93f23db4457ea01d7335d1e65a9c
ACR-bbf22e4fb4ec4e409029049532a9d305
ACR-8475d734864442a686ebad76be08370a
ACR-74343c8e8e7d4dc9a775a7accbc8bd2f
ACR-f68a678ac2c8467da2635cfebed54d0f
ACR-ada20b7cae7b40ccad6fcfd0030d8c0c
ACR-f871da8f466f4bc1ba0e8e7b04e97fb0
ACR-c50a3f31773c47c1a4e4ee3c5cd29477
ACR-18157293caf0446aace1d9587bdc2f84
ACR-2df46e50af1a46e280ce22cd0feb00ce
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.scan.issue.filter.FilterableIssue;
import org.sonar.api.scan.issue.filter.IssueFilter;
import org.sonar.api.scan.issue.filter.IssueFilterChain;
import org.sonar.api.utils.WildcardPattern;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultFilterableIssue;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class IgnoreIssuesFilter implements IssueFilter {

  private final Map<InputComponent, List<WildcardPattern>> rulePatternByComponent = new HashMap<>();

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  @Override
  public boolean accept(FilterableIssue issue, IssueFilterChain chain) {
    var component = ((DefaultFilterableIssue) issue).getComponent();
    if ((component.isFile() && ((SonarLintInputFile) component).isIgnoreAllIssues())
      || (component.isFile() && ((SonarLintInputFile) component).isIgnoreAllIssuesOnLine(issue.line()))) {
      return false;
    }
    if (hasRuleMatchFor(component, issue)) {
      return false;
    }
    return chain.accept(issue);
  }

  public void addRuleExclusionPatternForComponent(SonarLintInputFile inputFile, WildcardPattern rulePattern) {
    if ("*".equals(rulePattern.toString())) {
      inputFile.setIgnoreAllIssues(true);
    } else {
      rulePatternByComponent.computeIfAbsent(inputFile, x -> new LinkedList<>()).add(rulePattern);
    }
  }

  private boolean hasRuleMatchFor(InputComponent component, FilterableIssue issue) {
    for (WildcardPattern pattern : rulePatternByComponent.getOrDefault(component, Collections.emptyList())) {
      if (pattern.match(issue.ruleKey().toString())) {
        LOG.debug("Issue {} ignored by exclusion pattern {}", issue, pattern);
        return true;
      }
    }
    return false;

  }
}
