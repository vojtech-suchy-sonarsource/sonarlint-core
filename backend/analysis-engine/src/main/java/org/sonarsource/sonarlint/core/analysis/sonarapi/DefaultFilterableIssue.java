/*
ACR-77f1161bb39c4a6898854daae028064e
ACR-265efad12355447095556270442efc2d
ACR-35f689a8dde54132bb966737336c9364
ACR-1887f2b14f17420899c914ad5dff769c
ACR-b797d14b9b364b42a611a6f7efcc2f40
ACR-74bcc23cbbf04914b62259c115a75696
ACR-ad7c0310b18045789f840d6027f4a1da
ACR-70182ced9ec74393b0495e164427cc77
ACR-bdc24a8736f143e284fb21945dae5f69
ACR-86add72ac2544c79bc15874bd5701fb0
ACR-58c428145daf406796387fe96371b0c5
ACR-09b01b6732874681b43d9c3d597eccd5
ACR-924b557ece164050aa6f5014622011d7
ACR-d15f3c98dc4f45549da5ab213d332b14
ACR-100386992def402ea1138cdb883d47a2
ACR-019cfa3160804469879530fd1ef25e8f
ACR-278a243ae4044f418269815e05a676c1
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
