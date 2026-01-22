/*
ACR-647b39f938a24624802cb71b0c9c4f00
ACR-e7bba204bd224a00b5d9cfefeae61ff6
ACR-1b81076d01fc4f089528d54d1ec1ad46
ACR-75f1da26067b4649a598429686c309eb
ACR-da08bb582ee743788703f9a960436d4a
ACR-3470f47101834b0ea962dd63e55c1455
ACR-c88d6302a93143039a5bcda5979bce34
ACR-636e525406ba41a19289f469920da9b1
ACR-e7f90bf9dbbf429e9710967e1979a748
ACR-9da08f3bb24f44349700f1208e5f458e
ACR-c38ed8205eba4a0b9c3c81c5879c847f
ACR-24fea5b7f1de4285bc32d49f621f0803
ACR-5edbb6e5030d4e3aa04db11308b92b29
ACR-93b7f511b6114ef5b661c04255c88cb2
ACR-398f82603687445bb7bcb06033fb54e5
ACR-179e432a234740e985061c641a9fef83
ACR-4638e4bde11847adb82c59b40867946f
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.WildcardPattern;
import org.sonarsource.sonarlint.core.analysis.container.analysis.SonarLintPathPattern;

public class IssuePattern {

  private final SonarLintPathPattern pathPattern;
  private final WildcardPattern rulePattern;

  public IssuePattern(String pathPattern, String rulePattern) {
    this.pathPattern = new SonarLintPathPattern(pathPattern);
    this.rulePattern = WildcardPattern.create(rulePattern);
  }

  public WildcardPattern getRulePattern() {
    return rulePattern;
  }

  public boolean matchRule(RuleKey rule) {
    return rulePattern.match(rule.toString());
  }

  public boolean matchFile(@Nullable String filePath) {
    return filePath != null && pathPattern.match(filePath);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
