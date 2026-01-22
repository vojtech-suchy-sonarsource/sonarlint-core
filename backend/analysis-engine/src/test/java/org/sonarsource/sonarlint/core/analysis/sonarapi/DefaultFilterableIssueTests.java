/*
ACR-f914a992b52e4062ad169a7a934143c6
ACR-ac3567b433a448e698e08bc512f74303
ACR-ad530467a21d4033a53fb6dba53e10f0
ACR-ff80c61e67014b8c97ed2e91ad7b241c
ACR-a56cefe9b19b45dea8c3c7a9051e09be
ACR-9bd82884a27247f9950e38c93ec53866
ACR-88706a71324b4fc3ba6c17e96979122f
ACR-7b088955261949e3a0edc4e17e0711ef
ACR-255ee7899358450cb86939f4f3780449
ACR-a146741bc3de4f93925914537abd6711
ACR-7c68e4d2c2064bbc8c8099d40d15fc1b
ACR-860e8f1b8833400293adf692b77108cd
ACR-7c62e0d0125b49eb8fae6b8bc54aa6b9
ACR-c28e626a735e4d3ab338f1614bb40da1
ACR-55339965fd174512b5f5861cedc50582
ACR-5c19e034148c4ff5a83210b096f73547
ACR-f642f1de5c484ec78e9af41ac3249ce0
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.sonarlint.core.analysis.api.Issue;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextPointer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.DefaultTextRange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultFilterableIssueTests {

  @Test
  void delegate_textRange_to_rawIssue() {
    TextRange textRange = new DefaultTextRange(new DefaultTextPointer(0, 1), new DefaultTextPointer(2, 3));
    var activeRule = mock(ActiveRule.class);
    when(activeRule.ruleKey()).thenReturn(RuleKey.of("foo", "S123"));
    var rawIssue = new Issue(activeRule, null, Map.of(), textRange, null, null, null, Optional.empty());
    var underTest = new DefaultFilterableIssue(rawIssue, mock(InputComponent.class));
    assertThat(underTest.textRange()).usingRecursiveComparison().isEqualTo(textRange);
  }
}
