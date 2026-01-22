/*
ACR-0bc344c29a314f7781b82060aa62364b
ACR-7359a8f46f0a4ff78a2f48fd88607a87
ACR-0eba7fe25d754047a71f20ecd714225f
ACR-c0b43bc6ccdd475ab9d66e2515d33cc7
ACR-e2d6d40bf66d4648a781469f55a1fc82
ACR-0df8708dfaad405f8cb5ee36a6d7a3c7
ACR-8b9424dc4439474897e5d906c5fc7199
ACR-9d00745b04e64009be0564e6ed1491c6
ACR-a41dccdcb0df483287613168d2497d92
ACR-acd3fa5c452e4d12a0be3aed1e9e194d
ACR-4bcf099706b745dd8701112808403a67
ACR-bb0d356bc94e4238bb4c27068c4466aa
ACR-e88a833f9d00445a9e1f8c252129d554
ACR-011d9a1297b84be18bc4c57d884a1f80
ACR-c49345c7732d41aea2df5e7ba15b4c8b
ACR-eff60515895146d7a519a473c84e2185
ACR-14ace177fe0c4b1fb61f5b7d47e9abef
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
