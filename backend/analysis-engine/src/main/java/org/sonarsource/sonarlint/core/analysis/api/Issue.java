/*
ACR-4c17afc5f55847f5b064731ebbe7da4e
ACR-9552989e161c487b8dec4a16f874ab62
ACR-dbb5f315d2244123ae6562ddf46d2c34
ACR-aa57a60a4dfa49769d7cb0e9fe259cc7
ACR-551710c294f249efa8091c33d5757cb6
ACR-30a41b5caaba45d28d4b630fad749e51
ACR-9c8771b10857408893def4c89dddd7b3
ACR-611c4025856a43dcaa6b9e3b602ca795
ACR-5b24066f683e4f9a92d29b1f77e66736
ACR-ae80e71681e24ddb855cd8ca477107e3
ACR-d392d0740dca488a8053524ef35b2249
ACR-b2b4ac0960dc4377853c360c0f6525fb
ACR-ff969f2a7b4740189f5552939e89f909
ACR-b5618967a64a4a94aaa126d5cc0ea092
ACR-17cdf109126c4030a7fdc58a5b04c357
ACR-056b2716fc8b4b91858cf5966019d5df
ACR-6ac6ba3703b64b04a5586eeeae9d2404
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

public class Issue implements IssueLocation {
  private final String primaryMessage;
  private final ClientInputFile clientInputFile;
  private final List<Flow> flows;
  private final List<QuickFix> quickFixes;
  private final Optional<String> ruleDescriptionContextKey;
  private final TextRange textRange;
  private final ActiveRule activeRule;
  private final Map<SoftwareQuality, ImpactSeverity> overriddenImpacts;

  public Issue(ActiveRule activeRule, @Nullable String primaryMessage, Map<SoftwareQuality, ImpactSeverity> overriddenImpacts, @Nullable org.sonar.api.batch.fs.TextRange textRange,
    @Nullable ClientInputFile clientInputFile, List<Flow> flows, List<QuickFix> quickFixes, Optional<String> ruleDescriptionContextKey) {
    this.activeRule = activeRule;
    this.overriddenImpacts = overriddenImpacts;
    this.textRange = Optional.ofNullable(textRange).map(WithTextRange::convert).orElse(null);
    this.primaryMessage = primaryMessage;
    this.clientInputFile = clientInputFile;
    this.flows = flows;
    this.quickFixes = quickFixes;
    this.ruleDescriptionContextKey = ruleDescriptionContextKey;
  }

  public ActiveRule getActiveRule() {
    return activeRule;
  }

  public RuleKey getRuleKey() {
    return activeRule.ruleKey();
  }

  @Override
  public String getMessage() {
    return primaryMessage;
  }

  @Override
  @CheckForNull
  public ClientInputFile getInputFile() {
    return clientInputFile;
  }

  public List<Flow> flows() {
    return flows;
  }

  public List<QuickFix> quickFixes() {
    return quickFixes;
  }

  @Override
  @CheckForNull
  public TextRange getTextRange() {
    return textRange;
  }

  public Map<SoftwareQuality, ImpactSeverity> getOverriddenImpacts() {
    return overriddenImpacts;
  }

  public Optional<String> getRuleDescriptionContextKey() {
    return ruleDescriptionContextKey;
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append("[");
    sb.append("rule=").append(getRuleKey());
    if (textRange != null) {
      var startLine = textRange.getStartLine();
      sb.append(", line=").append(startLine);
    }
    if (clientInputFile != null) {
      sb.append(", file=").append(clientInputFile.uri());
    }
    sb.append("]");
    return sb.toString();
  }
}
