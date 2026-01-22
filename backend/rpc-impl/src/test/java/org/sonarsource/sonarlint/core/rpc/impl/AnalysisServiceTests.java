/*
ACR-52455f98946e49ccb8177fcdb69a4d2a
ACR-3b709db307064fbdb6abef3887272b2a
ACR-69b24d06cf224d4dbebb3c6b6d5c2909
ACR-171f63c3dc2646ce9ffcd2001db23487
ACR-4fabd8985a994cd89fada8ec05e587b3
ACR-29dc39f338f34b2daac267139b2975b2
ACR-937c5c3222ea403a9fdbc69c568cf65a
ACR-e413ff433d5949d3891d82b4d7a6889c
ACR-729b98a0f45240649673418f1e6154ee
ACR-d2ae5674492c4390954ea5c18161e197
ACR-d192ce9abe4e4ae4b1609d005c881258
ACR-47b0875af6074e1680c9a62f35e44608
ACR-40c69076bb1e45f3bbe1c12c42fc6513
ACR-7e9fcc6f417646cab63e7702a2e52f5b
ACR-01994cede19e4d999ec9a2443f604a3d
ACR-b2d8950ce88a46f6af077f267f682a37
ACR-89bd2e5f49294ecfa859a8a57aeb8f03
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
