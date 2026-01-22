/*
ACR-f037d25c11b3481da48f487cf73fbd2b
ACR-989beecde53c4dc48c9ad0bd208cba17
ACR-e8d3cbcc7c344f1b8520e7ced4bae10a
ACR-387359c5e012421bb91c6436e7e318cd
ACR-4ebad82190a64a219c1de87081dc1f78
ACR-59806ec68c0b4c20a9ac6c8924e701cd
ACR-cda70436a51045449051a888ece3a6ec
ACR-2853d39b84fd4868b5658eeb3f48cff7
ACR-5630af62b991411f9be264b4b09701d3
ACR-c4591c9757e54bb796c65710ab1b6a36
ACR-6a289d01bbff4dca9a79fb08395d4fc8
ACR-7a2888a0fed5460cb595182f5bf000e0
ACR-4fab18e575b146e3a9c8221c78e0769a
ACR-adf85e5cbca94952b0924cb11f44d780
ACR-888e74ea976c4d4393d084384bda3333
ACR-cdd34ad555424827a92a562dc7ccb0d8
ACR-617421c29bce46dc977f908d351611b8
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
