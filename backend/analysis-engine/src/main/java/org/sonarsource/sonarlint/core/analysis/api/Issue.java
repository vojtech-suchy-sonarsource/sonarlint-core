/*
ACR-fc27f519c4f34efabf7a13489450135a
ACR-cd8bca1e3d8d40fb90cb672904e41e5b
ACR-64b87a9f4fe1450793f64411b28dfd72
ACR-e9476e2dcc614ae1a5b8451eab3aa214
ACR-e4d749fcce1c4eb88c4ff487197d6c08
ACR-e0f6292dc7db42b0917388d3a1253439
ACR-f044a9f94d3949ae99b71160ef2fecda
ACR-68ee69880200456d87226a111ab7aa48
ACR-3920e917a5c14c54b2e40bf40bb7e9c5
ACR-b980b74acc284f1d85f3c2fd1742f1c8
ACR-b4ae74c6920f43db8f4d9517daf38d1f
ACR-5bed0cbd64864092a299f87a40dfe49d
ACR-0df27cd49d1846369bd5c01ffacfb035
ACR-2f5dcd9ef4574361a525615d7a4f1986
ACR-205c70a39efa459a8320042d380fbeeb
ACR-0b895f0810354b6086302cb1e8305419
ACR-6314d8ad7cd845de857871cb2330d884
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
