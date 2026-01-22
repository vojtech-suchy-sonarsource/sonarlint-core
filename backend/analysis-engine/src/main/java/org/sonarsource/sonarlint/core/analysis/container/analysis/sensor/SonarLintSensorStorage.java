/*
ACR-e6beb0cc2683490b9fcdd1f0d0372b04
ACR-b657baba41a04da4a9ef25e7836f6d8c
ACR-4cd09402818240009f44e54db9e4f5d2
ACR-0dadb1a578d94d31860e5088eb960708
ACR-f4f454726dc04acf9cb9622caa4bdb42
ACR-60202f5ad2144b9d928431c2c94b412c
ACR-73c8e53d029d4fd08f97e658120bfd3d
ACR-693eff43fd01401d8d8c41375b46b884
ACR-4167769b293642269538291b4f19117c
ACR-072821e70a10451bac1d2e7f43389100
ACR-b84528c7076e4b84939c630950d91304
ACR-0939b293ac7b47d08dbe82fb0e5c5661
ACR-b9e88721fee5435d9a4b8f6c213cb6e3
ACR-8bbb8be3137d4210bd53fb0cb7acf43e
ACR-4ee9cca2a2ca4980ab423df75b57c622
ACR-3615214154e3473b8c2b98ef1b45fd12
ACR-b98b77dc7cf44cb48a6057fe3ad60f59
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Strings;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.code.NewSignificantCode;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.issue.ExternalIssue;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.issue.Issue.Flow;
import org.sonar.api.batch.sensor.issue.fix.QuickFix;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.batch.sensor.rule.AdHocRule;
import org.sonar.api.batch.sensor.symbol.NewSymbolTable;
import org.sonar.api.issue.impact.Severity;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisResults;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFileEdit;
import org.sonarsource.sonarlint.core.analysis.api.TextEdit;
import org.sonarsource.sonarlint.core.analysis.container.analysis.IssueListenerHolder;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.IssueFilters;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.TextRangeUtils;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultSonarLintIssue;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;

public class SonarLintSensorStorage implements SensorStorage {

  private final ActiveRules activeRules;
  private final IssueFilters filters;
  private final IssueListenerHolder issueListener;
  private final AnalysisResults analysisResult;

  public SonarLintSensorStorage(ActiveRules activeRules, IssueFilters filters, IssueListenerHolder issueListener, AnalysisResults analysisResult) {
    this.activeRules = activeRules;
    this.filters = filters;
    this.issueListener = issueListener;
    this.analysisResult = analysisResult;
  }

  @Override
  public void store(Measure newMeasure) {
    //ACR-ee9b55218cce48bfbeadc654633dbc2e
  }

  @Override
  public void store(Issue issue) {
    if (!(issue instanceof DefaultSonarLintIssue sonarLintIssue)) {
      throw new IllegalArgumentException("Trying to store a non-SonarLint issue?");
    }
    var inputComponent = sonarLintIssue.primaryLocation().inputComponent();

    var activeRule = activeRules.find(sonarLintIssue.ruleKey());
    if ((activeRule == null) || noSonar(inputComponent, sonarLintIssue)) {
      return;
    }

    var primaryMessage = sonarLintIssue.primaryLocation().message();
    var flows = mapFlows(sonarLintIssue.flows());
    var quickFixes = transform(sonarLintIssue.quickFixes());
    var overriddenImpacts = transform(sonarLintIssue.overridenImpacts());

    var newIssue = new org.sonarsource.sonarlint.core.analysis.api.Issue(activeRule, primaryMessage, overriddenImpacts, issue.primaryLocation().textRange(),
      inputComponent.isFile() ? ((SonarLintInputFile) inputComponent).getClientInputFile() : null, flows, quickFixes, sonarLintIssue.ruleDescriptionContextKey());
    if (filters.accept(inputComponent, newIssue)) {
      issueListener.handle(newIssue);
    }
  }

  private static List<org.sonarsource.sonarlint.core.analysis.api.QuickFix> transform(List<QuickFix> quickFixes) {
    return quickFixes.stream().map(SonarLintSensorStorage::transform).toList();
  }

  private static Map<SoftwareQuality, ImpactSeverity> transform(Map<org.sonar.api.issue.impact.SoftwareQuality, Severity> overriddenImpacts) {
    return overriddenImpacts.entrySet().stream()
      .map(e -> Map.entry(SoftwareQuality.valueOf(e.getKey().name()), ImpactSeverity.valueOf(e.getValue().name())))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private static org.sonarsource.sonarlint.core.analysis.api.QuickFix transform(QuickFix qf) {
    return new org.sonarsource.sonarlint.core.analysis.api.QuickFix(
      qf.inputFileEdits().stream().map(edit -> new ClientInputFileEdit(
        ((SonarLintInputFile) edit.target()).getClientInputFile(),
        edit.textEdits().stream().map(textEdit -> new TextEdit(TextRangeUtils.convert(textEdit.range()), textEdit.newText())).toList())).toList(),
      qf.message());
  }

  private static boolean noSonar(InputComponent inputComponent, Issue issue) {
    var textRange = issue.primaryLocation().textRange();
    return inputComponent.isFile()
      && textRange != null
      && ((SonarLintInputFile) inputComponent).hasNoSonarAt(textRange.start().line())
      && !Strings.CI.contains(issue.ruleKey().rule(), "nosonar");
  }

  private static List<org.sonarsource.sonarlint.core.analysis.api.Flow> mapFlows(List<Flow> flows) {
    return flows.stream()
      .map(f -> new org.sonarsource.sonarlint.core.analysis.api.Flow(new ArrayList<>(f.locations())))
      .filter(f -> !f.locations().isEmpty())
      .toList();
  }

  @Override
  public void store(NewHighlighting highlighting) {
    //ACR-b4a5ad34d9564afe83780f5b4ca5a621
  }

  @Override
  public void store(NewCoverage defaultCoverage) {
    //ACR-93fb86c05f804de098ced7a89d1c7117
  }

  @Override
  public void store(NewCpdTokens defaultCpdTokens) {
    //ACR-29474ed7c507409e837219ea1c869897
  }

  @Override
  public void store(NewSymbolTable symbolTable) {
    //ACR-1fda9f2ec3364d20bb6ae21045c5847d
  }

  @Override
  public void store(AnalysisError analysisError) {
    var clientInputFile = ((SonarLintInputFile) analysisError.inputFile()).getClientInputFile();
    analysisResult.addFailedAnalysisFile(clientInputFile);
  }

  @Override
  public void storeProperty(String key, String value) {
    //ACR-3b668608f44d44568490a8b0f9dd2e93
  }

  @Override
  public void store(ExternalIssue issue) {
    //ACR-fabd85463c344e6a852ba8c7472a7359
  }

  @Override
  public void store(NewSignificantCode significantCode) {
    //ACR-c11890c8c7d5453c90a4bb86535aa553
  }

  @Override
  public void store(AdHocRule adHocRule) {
    //ACR-6b90cd80fa0946ea887839fc7f2ec28e
  }

}
