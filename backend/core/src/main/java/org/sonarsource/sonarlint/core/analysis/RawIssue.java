/*
ACR-b017ee41565c4e9a91d9205b332b69d2
ACR-28bb266ab5d94b8582eb9bcca798395c
ACR-cb76046ca5fa4cf0a87cbaaf95f8e4ff
ACR-9c66e889256f43a7924c794059a7c06c
ACR-ae05d522c56d444c8009440d079bb904
ACR-6f5fde6d15f549178f59fe06a4969a83
ACR-ca41eaad52fd40afb419204f5737925d
ACR-ade47a99ec9641559b0e6046fdb72db9
ACR-3507cb6980aa4da8bcfe1bd21cf688d3
ACR-e3553633add74650ba5fdcf373f660f0
ACR-daaa08d530d04b55b38130a3d5d55980
ACR-1fc1081372814c32bc1f4d241e6b46f2
ACR-c7aeb08f4a0a4b7f8654b11532f70070
ACR-92ebb1ac72914d489aabe333dc66ec2e
ACR-993e630673644a46be3c4929f8dddd83
ACR-07a0608e1f034ee6b7aad7ceb1d0f69c
ACR-5aad3c9113f042b480c5263a4756646c
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
