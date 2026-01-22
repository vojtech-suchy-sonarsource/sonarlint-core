/*
ACR-f992f416963b415683c2aa3601d5c9b1
ACR-77f9b2de96484b52a4ab81a4b317f0b5
ACR-9f8c0965224a43b49b7758f03e692a0e
ACR-155f4396c9044c3f8ab93620a05dd98c
ACR-5432be826eff4ad0b6a8936724120e28
ACR-13b63e35a67e45099f4bee0a69cf92ba
ACR-9e8b3611b9bf488e9f510cae839e54d0
ACR-498e82c0be514e5f9b8dc6e0729256f0
ACR-eb27861c012e441ab44c8e40c228c0e4
ACR-22c5b3eae2fb49ddbee3a483dab0f18c
ACR-5c5ff4e33301402d98d1ca1d6ff5dd54
ACR-1a3e1f8dc6a14c41a8202352da859ae1
ACR-08ab390f000d455998d5ec46979e1dc6
ACR-a0da896006114e71a2fff39312a6183c
ACR-a1790e5c136d445fab7e4130c347ade4
ACR-a2e6b409d43a458eade1ea3d165a1e02
ACR-344b0ba656584b5f87ae931a7e1b29e8
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
