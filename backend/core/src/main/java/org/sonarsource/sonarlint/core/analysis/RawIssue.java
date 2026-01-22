/*
ACR-68ff275d1fe3485394b4b4d20901eb77
ACR-6dbf47c25c7d4e18b104fe702c98109f
ACR-c3a0b651eb1b460c8397271b8a2cbd88
ACR-8a5a4bf84c9845a08901373e8134c2fd
ACR-3c22cdb29d3044b7bc39e340011b5f1d
ACR-cef3d9c44ac54231a7ddd5368bb8bb7a
ACR-52336c6a28ff4c7a995c9833841f5b32
ACR-7bd126ccdf674fc8bc06209f224b3e13
ACR-608664dc4c0c494dbd7410d8bb6d5cdd
ACR-ace932dce49d47febf4ad1c90de8f20e
ACR-3362a576505c40ba9ebf7c27a0fc3991
ACR-730d4d5eefb24eee8629f5d8fbec5503
ACR-309b1007ee0d45e9ab01950796278846
ACR-c7b67bc4ea144924a03d32b3a14e621b
ACR-e2911a4d1a2546e6ae9b96d338e11af1
ACR-059b7628cf624973bc222d003207e096
ACR-f3e53355397d4b6d9f98abe2cdad18df
 */
package org.sonarsource.sonarlint.core.analysis;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.active.rules.ActiveRuleDetails;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.api.Flow;
import org.sonarsource.sonarlint.core.analysis.api.Issue;
import org.sonarsource.sonarlint.core.analysis.api.QuickFix;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.tracking.TextRangeUtils;

public class RawIssue {

  private final Issue issue;
  private final ActiveRuleDetails activeRule;
  private final Map<SoftwareQuality, ImpactSeverity> impacts = new EnumMap<>(SoftwareQuality.class);
  @Nullable
  private final String textRangeHash;
  @Nullable
  private final String lineHash;

  public RawIssue(Issue issue) {
    this.issue = issue;
    this.activeRule = (ActiveRuleDetails) issue.getActiveRule();
    this.impacts.putAll(activeRule.impacts());
    this.impacts.putAll(issue.getOverriddenImpacts());
    var textRangeWithHash = TextRangeUtils.getTextRangeWithHash(getTextRange(), getClientInputFile());
    this.textRangeHash = textRangeWithHash == null ? null : textRangeWithHash.getHash();
    var lineWithHash = TextRangeUtils.getLineWithHash(issue.getTextRange(), getClientInputFile());
    this.lineHash = lineWithHash == null ? null : lineWithHash.getHash();
  }

  public IssueSeverity getSeverity() {
    return activeRule.issueSeverity();
  }

  public RuleType getRuleType() {
    return activeRule.type();
  }

  public boolean isSecurityHotspot() {
    return getRuleType() == RuleType.SECURITY_HOTSPOT;
  }

  public CleanCodeAttribute getCleanCodeAttribute() {
    return activeRule.cleanCodeAttribute();
  }

  public Map<SoftwareQuality, ImpactSeverity> getImpacts() {
    return impacts;
  }

  public String getRuleKey() {
    return activeRule.ruleKeyString();
  }

  public String getMessage() {
    return issue.getMessage();
  }

  public List<Flow> getFlows() {
    return issue.flows();
  }

  public List<QuickFix> getQuickFixes() {
    return issue.quickFixes();
  }

  @CheckForNull
  public TextRange getTextRange() {
    return issue.getTextRange();
  }

  @CheckForNull
  public Path getIdeRelativePath() {
    var inputFile = issue.getInputFile();
    return inputFile != null ? Path.of(inputFile.relativePath()) : null;
  }

  @CheckForNull
  public URI getFileUri() {
    var inputFile = issue.getInputFile();
    return inputFile != null ? inputFile.uri() : null;
  }

  public boolean isInFile() {
    return issue.getInputFile() != null;
  }

  @CheckForNull
  public ClientInputFile getClientInputFile() {
    return issue.getInputFile();
  }

  @CheckForNull
  public VulnerabilityProbability getVulnerabilityProbability() {
    return activeRule.vulnerabilityProbability();
  }

  @CheckForNull
  public String getRuleDescriptionContextKey() {
    return issue.getRuleDescriptionContextKey().orElse(null);
  }

  public Collection<Integer> getLineNumbers() {
    Set<Integer> lineNumbers = new HashSet<>();
    Optional.ofNullable(getTextRange())
      .map(textRange -> IntStream.rangeClosed(textRange.getStartLine(), textRange.getEndLine()))
      .ifPresent(intStream -> intStream.forEach(lineNumbers::add));

    getFlows()
      .forEach(flow -> flow.locations().stream()
        .filter(issueLocation -> Objects.nonNull(issueLocation.getStartLine()))
        .filter(issueLocation -> Objects.nonNull(issueLocation.getEndLine()))
        .map(issueLocation -> IntStream.rangeClosed(issueLocation.getStartLine(), issueLocation.getEndLine()))
        .forEach(intStream -> intStream.forEach(lineNumbers::add)));

    return lineNumbers;
  }

  public Optional<Integer> getLine() {
    return Optional.ofNullable(issue.getStartLine());
  }

  public Optional<String> getTextRangeHash() {
    return Optional.ofNullable(textRangeHash);
  }

  public Optional<String> getLineHash() {
    return Optional.ofNullable(lineHash);
  }
}
