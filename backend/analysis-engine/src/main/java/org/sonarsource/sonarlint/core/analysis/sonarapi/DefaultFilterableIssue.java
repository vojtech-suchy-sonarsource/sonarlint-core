/*
ACR-225ca3cc4ea64f94a84563df22607463
ACR-8433e8df819f4f54b3eef47f59c8a003
ACR-d04bbd6227b941748460d1e770f55997
ACR-892435fb91ab4df6b45087083ec26f93
ACR-11d59b207af2496d9fd6d7058edd6200
ACR-7f5bf8364f054ffd835d0207191f23b2
ACR-a24ee9bf549d4e068d12ff664bbaea0d
ACR-e117645bbc0f441189bb078f8a6e9c7f
ACR-21d6dd637c4042fe9a45c0ee2a75f1eb
ACR-1e04556ede2145cc91264b4823d39a47
ACR-9b50291d291347c180f30bffe2f3c4a9
ACR-22213a07f1cd446381b0e05018f0b310
ACR-eb72306216fe46c1a1f3d3d3fdc454d1
ACR-e183f1b0cf22416599092b5f5f65db3e
ACR-302d37839b2b4a439e003bb758ebd4eb
ACR-68e56418132c41abb08cc0bdec438f3c
ACR-10f73e3dba3c42e691dda252c2a03efc
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.issue.filter.FilterableIssue;
import org.sonarsource.sonarlint.core.analysis.api.Issue;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextPointer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextRange;

public class DefaultFilterableIssue implements FilterableIssue {
  private final Issue rawIssue;
  private final InputComponent component;

  public DefaultFilterableIssue(Issue rawIssue, InputComponent component) {
    this.rawIssue = rawIssue;
    this.component = component;
  }

  @Override
  public String componentKey() {
    return component.key();
  }

  @Override
  public RuleKey ruleKey() {
    return rawIssue.getRuleKey();
  }

  @Override
  public String severity() {
    throw unsupported();
  }

  @Override
  public String message() {
    throw unsupported();
  }

  @Override
  public Integer line() {
    return rawIssue.getStartLine();
  }

  @Override
  public String projectKey() {
    throw unsupported();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  private static UnsupportedOperationException unsupported() {
    return new UnsupportedOperationException("Not available for issues filters");
  }

  @Override
  public Double gap() {
    throw unsupported();
  }

  public InputComponent getComponent() {
    return component;
  }

  @Override
  public TextRange textRange() {
    var textRange = rawIssue.getTextRange();
    if (textRange == null) {
      return null;
    }
    return new DefaultTextRange(new DefaultTextPointer(textRange.getStartLine(), textRange.getStartLineOffset()),
      new DefaultTextPointer(textRange.getEndLine(), textRange.getEndLineOffset()));
  }

}
