/*
ACR-ab3419484c9d4db4b949e7804b79f2f6
ACR-cca70d61c2f84831a24e9c112d53e7e1
ACR-2f6ea27bb9394c3db3734e0efcea56d8
ACR-781866137edb4287aa51dd1cdc3eca80
ACR-b21a707cde424e5db9410111e5278b72
ACR-bb4abf0ca49d4494a039d7d0d074c794
ACR-efbe06c1add14534bba349cb526dba01
ACR-7d6274a60b8c46c1ac4829afe30e7243
ACR-45170dfebd674193b58054e014318937
ACR-2787e1f2f2d940ef8295c4c4e724a738
ACR-50d247f67ae04953835cc9f374382e4f
ACR-1352ad9be6d24d91b14b1d161c575656
ACR-8367655f0b7d473fa7577092904657e8
ACR-d198e7284be74ec495eec2a814505ec5
ACR-546594d8d10548de9aa7387c1fcae9a9
ACR-e4e05473769f4647a7c0bf07b9bdcb1f
ACR-102f0b32636e419d98114924945bb140
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
