/*
ACR-420d88a25aa44a39bdac0c5e25bef8c9
ACR-225adedf165840399bbf01efffabac91
ACR-b9552d2a8bab4046b8e6eb8a39190dac
ACR-e00334748fb242dc86004be79b3d74e6
ACR-353ed88e677d4db481326e7104849fbe
ACR-f8d0a3bbd1024095b60e080725eb25cc
ACR-52167d195c0943299175851e3a7b2695
ACR-2be30f4ff94141a8b01c7e761c256561
ACR-56f5fec292dd4f91a14ab67399c1a207
ACR-c6d18e8a95044bba93c03f9fab2587cc
ACR-755fb2d2365f400e8b2c354b7b6093df
ACR-bbbdc19911864b8dac9c20944687e937
ACR-76e39df423b94d49a63c6ba710d40759
ACR-4b428f0b5903462aacc1607357f98916
ACR-babcf9409d794e0a8a8bb3075999b931
ACR-bf0db7583dc7411cb167ceda3120e9d9
ACR-0d45e05046444642960dc2a87cef014b
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.sensor.issue.IssueLocation;
import org.sonarsource.sonarlint.core.active.rules.ActiveRuleDetails;
import org.sonarsource.sonarlint.core.analysis.RawIssue;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFileEdit;
import org.sonarsource.sonarlint.core.analysis.api.Flow;
import org.sonarsource.sonarlint.core.analysis.api.Issue;
import org.sonarsource.sonarlint.core.analysis.api.QuickFix;
import org.sonarsource.sonarlint.core.analysis.api.TextEdit;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextPointer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextRange;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalysisServiceTests {

  @Test
  void it_should_convert_issue_flaws_and_quick_fixes_to_raw_issue_dto() throws IOException {
    var issueLocation = mock(IssueLocation.class);
    var inputComponent = mock(SonarLintInputFile.class);
    when(inputComponent.isFile()).thenReturn(true);
    when(inputComponent.key()).thenReturn("inputComponentKey");
    when(issueLocation.message()).thenReturn("issue location message");
    when(issueLocation.textRange()).thenReturn(new DefaultTextRange(new DefaultTextPointer(1, 2),
      new DefaultTextPointer(3, 4)));
    when(issueLocation.inputComponent()).thenReturn(inputComponent);
    var clientInputFile = mock(ClientInputFile.class);
    when(clientInputFile.contents()).thenReturn("content");
    var issue = new Issue(new ActiveRuleDetails("repo:ruleKey", "languageKey", null, null, org.sonarsource.sonarlint.core.commons.IssueSeverity.BLOCKER,
      org.sonarsource.sonarlint.core.commons.RuleType.BUG, org.sonarsource.sonarlint.core.commons.CleanCodeAttribute.CLEAR,
      Map.of(), org.sonarsource.sonarlint.core.commons.VulnerabilityProbability.HIGH),
      "primary message", Map.of(),
      new DefaultTextRange(new DefaultTextPointer(1, 1), new DefaultTextPointer(1, 1)),
      clientInputFile, List.of(new Flow(List.of(issueLocation))), List.of(new QuickFix(List.of(
        new ClientInputFileEdit(clientInputFile, List.of(new TextEdit(
          new TextRange(5, 6, 7, 8), "Quick fix text")))),
        "Quick fix message")),
      Optional.of(""));

    var rawIssueDto = AnalysisRpcServiceDelegate.toDto(new RawIssue(issue));

    assertThat(rawIssueDto.getRuleKey()).isEqualTo("repo:ruleKey");
    var rawIssueLocationDto = rawIssueDto.getFlows().get(0).getLocations().get(0);
    assertThat(rawIssueLocationDto.getMessage()).isEqualTo("issue location message");
    var issueLocationTextRange = rawIssueLocationDto.getTextRange();
    assertThat(issueLocationTextRange).isNotNull();
    assertThat(issueLocationTextRange.getStartLine()).isEqualTo(1);
    assertThat(issueLocationTextRange.getStartLineOffset()).isEqualTo(2);
    assertThat(issueLocationTextRange.getEndLine()).isEqualTo(3);
    assertThat(issueLocationTextRange.getEndLineOffset()).isEqualTo(4);
    var quickFix = rawIssueDto.getQuickFixes().get(0);
    assertThat(quickFix).isNotNull();
    var fileEdit = quickFix.fileEdits().get(0);
    assertThat(fileEdit).isNotNull();
    var textEdit = fileEdit.textEdits().get(0);
    assertThat(textEdit).isNotNull();
    var textRange = textEdit.range();
    assertThat(textRange).isNotNull();
    assertThat(textRange.getStartLine()).isEqualTo(5);
    assertThat(textRange.getStartLineOffset()).isEqualTo(6);
    assertThat(textRange.getEndLine()).isEqualTo(7);
    assertThat(textRange.getEndLineOffset()).isEqualTo(8);
  }

}
