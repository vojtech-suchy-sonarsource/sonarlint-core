/*
ACR-07b44bc1b6e34aa4a4f54b6bbbc35562
ACR-bf11be520da84039848451a8fb23d558
ACR-a4cc1dc5b36345839dd8423010933f9f
ACR-889976b52a624dc9b7631f718848a3ec
ACR-0719238bfdd44558bb4256947ae1c81f
ACR-cf8fe1bd3805407f93e50ea9366e4e90
ACR-079d0f44a6c14015b8a33ce4a53386d4
ACR-fb5cbec03b5c49c2ad6c4fed1189f932
ACR-8a7d165453ad49e08bbb6ba25529f169
ACR-f7b4d80637e747a589615214fed43eaf
ACR-42756b60edc3478e87300f405c5c2f97
ACR-8e2cb7d3f3f84391ba42b1616b2c6add
ACR-5e0434c679284750a1dd03827bcc6bf5
ACR-678b935697b040ea9b786adace184e44
ACR-75e6c1f93ab346fab615e608976f467c
ACR-b512b8ee42144b38af1d3b9fa0e7c637
ACR-49e8c420dfbb4a31930fd3e232046762
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.sensor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.code.NewSignificantCode;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.issue.ExternalIssue;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.batch.sensor.rule.AdHocRule;
import org.sonar.api.batch.sensor.symbol.NewSymbolTable;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisResults;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.IssueListenerHolder;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.IssueFilters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SonarLintSensorStorageTests {

  @Mock
  private ActiveRules activeRules;
  @Mock
  private IssueFilters filters;
  @Mock
  private IssueListenerHolder issueListener;
  @Mock
  private AnalysisResults analysisResult;
  @Mock
  private SonarLintInputFile inputFile;
  @Mock
  private ClientInputFile clientInputFile;

  private SonarLintSensorStorage underTest;

  @BeforeEach
  void setUp() {
    underTest = new SonarLintSensorStorage(activeRules, filters, issueListener, analysisResult);
  }

  @Test
  void store_Measure_doesnt_interact_with_its_param() {
    var measure = mock(Measure.class);
    underTest.store(measure);
    verifyNoInteractions(measure);
  }

  @Test
  void store_ExternalIssue_doesnt_interact_with_its_param() {
    var externalIssue = mock(ExternalIssue.class);
    underTest.store(externalIssue);
    verifyNoInteractions(externalIssue);
  }

  @Test
  void store_DefaultSignificantCode_doesnt_interact_with_its_param() {
    var significantCode = mock(NewSignificantCode.class);
    underTest.store(significantCode);
    verifyNoInteractions(significantCode);
  }

  @Test
  void store_DefaultHighlighting_doesnt_interact_with_its_param() {
    var highlighting = mock(NewHighlighting.class);
    underTest.store(highlighting);
    verifyNoInteractions(highlighting);
  }

  @Test
  void store_DefaultCoverage_doesnt_interact_with_its_param() {
    var coverage = mock(NewCoverage.class);
    underTest.store(coverage);
    verifyNoInteractions(coverage);
  }

  @Test
  void store_DefaultCpdTokens_doesnt_interact_with_its_param() {
    var cpdTokens = mock(NewCpdTokens.class);
    underTest.store(cpdTokens);
    verifyNoInteractions(cpdTokens);
  }

  @Test
  void store_DefaultSymbolTable_doesnt_interact_with_its_param() {
    var symbolTable = mock(NewSymbolTable.class);
    underTest.store(symbolTable);
    verifyNoInteractions(symbolTable);
  }

  @Test
  void store_AdHocRule_doesnt_interact_with_its_param() {
    var adHocRule = mock(AdHocRule.class);
    underTest.store(adHocRule);
    verifyNoInteractions(adHocRule);
  }

  @Test
  void store_should_throw_exception_for_non_sonarlint_issue() {
    var issue = mock(Issue.class);
    
    assertThatThrownBy(() -> underTest.store(issue))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Trying to store a non-SonarLint issue?");
  }

  @Test
  void store_AnalysisError_should_add_failed_analysis_file() {
    var analysisError = mock(AnalysisError.class);
    when(analysisError.inputFile()).thenReturn(inputFile);
    when(inputFile.getClientInputFile()).thenReturn(clientInputFile);
    
    underTest.store(analysisError);
    
    verify(analysisResult).addFailedAnalysisFile(clientInputFile);
  }

}
