/*
ACR-dd7e5f4211724af2b522496330656bd7
ACR-fe8fae8a5fdd4b7eb1bd806574d27dbd
ACR-4139af5dd44c473883fef0a7b722e0f3
ACR-692b2170324d46bd9303dc13abbcdf65
ACR-c7f47b7ceee74313ab2c1b42ade75a2e
ACR-339bcc83e80b4f8da526369de0bc0dd2
ACR-7d20001803514a0c98a3aa2635cad975
ACR-8cb04eb3e9324fbb8bf491063e4d4a19
ACR-0597895f755442e69f8ffdd6d0e38926
ACR-33d3379c329f4cfd828ae5a482328508
ACR-ed2c35373a8a4caa9f3553b13209c02b
ACR-57b00e35f7f24e159d7a5e181f8d45d0
ACR-12d9586fbaeb40029e0e02691ba48d22
ACR-f986a5f764fe4f46b4dec64ff9f17e60
ACR-737c655ab944436485f7e524cadd117d
ACR-e680eb6fb8c74563bb00191709ca65a9
ACR-40856443241943e997a66b853acd02bc
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
    //ACR-9f838440b3fb479daedcf492e23c2f14
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
    //ACR-bc7f681cef774292950809b07a7c2e3e
  }

  @Override
  public void store(NewCoverage defaultCoverage) {
    //ACR-febaa07ec3bc4b5f913ea44f48e21536
  }

  @Override
  public void store(NewCpdTokens defaultCpdTokens) {
    //ACR-5183e7279f7148f0a84b6e3555c1f503
  }

  @Override
  public void store(NewSymbolTable symbolTable) {
    //ACR-3e1bd9178b254e218714e15090f51aa2
  }

  @Override
  public void store(AnalysisError analysisError) {
    var clientInputFile = ((SonarLintInputFile) analysisError.inputFile()).getClientInputFile();
    analysisResult.addFailedAnalysisFile(clientInputFile);
  }

  @Override
  public void storeProperty(String key, String value) {
    //ACR-86090f3290a949f78a6f961514a8216d
  }

  @Override
  public void store(ExternalIssue issue) {
    //ACR-06e79914acc8430ba5cd8c893e8d2655
  }

  @Override
  public void store(NewSignificantCode significantCode) {
    //ACR-d0c9909717004b4ab6fb03b931d49f18
  }

  @Override
  public void store(AdHocRule adHocRule) {
    //ACR-8e5ba9c1602b43088c37c5a6989c6b2d
  }

}
