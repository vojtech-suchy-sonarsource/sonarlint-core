/*
ACR-622d4e56a4c54a58ae3a8fab0204e7f0
ACR-f8770ffe9df44f178aefe66737ebeb25
ACR-8e405ab78cf945f19e85b453a8ec2819
ACR-de22a31062854f159e11b664af5d268e
ACR-27cf6d1901844d6eabe78f7912c31502
ACR-715ef54d146b4dc9aabdeb1777097a12
ACR-c5e12f1cf2be4699afd81c82708242c6
ACR-dc62b31557654617854fbb67aa11aeb9
ACR-2f474860b5ad43a58dac916cefc350fd
ACR-189f76f0299c4254ab65b6934e94915b
ACR-b696d0530164447cbe30bd4b4c9e0a32
ACR-0d5f51e2fb0c4976a1e0637b0d1f886c
ACR-62691b887d6046d8b0de062da0eb1198
ACR-76e021910ea346f690c38ec697819bce
ACR-41b3f6493d374eb99721c8e7c47301fb
ACR-378e166cafde4c46b1b712adcdb3331d
ACR-ea4164596d1048559f336c6d344af854
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.issue.filter.IssueFilterChain;
import org.sonar.api.utils.WildcardPattern;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultFilterableIssue;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

 class IgnoreIssuesFilterTests {
   @RegisterExtension
   private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private final DefaultFilterableIssue issue = mock(DefaultFilterableIssue.class);
  private final IssueFilterChain chain = mock(IssueFilterChain.class);
  private final IgnoreIssuesFilter underTest = new IgnoreIssuesFilter();
  private SonarLintInputFile component;
  private final RuleKey ruleKey = RuleKey.of("foo", "bar");

  @BeforeEach
  void prepare() {
    component = mock(SonarLintInputFile.class);
    when(issue.getComponent()).thenReturn(component);
    when(issue.ruleKey()).thenReturn(ruleKey);
  }

  @Test
  void shouldPassToChainIfMatcherHasNoPatternForIssue() {
    when(chain.accept(issue)).thenReturn(true);
    assertThat(underTest.accept(issue, chain)).isTrue();
    verify(chain).accept(any());
  }

  @Test
  void shouldRejectIfRulePatternMatches() {
    var pattern = mock(WildcardPattern.class);
    when(pattern.match(ruleKey.toString())).thenReturn(true);
    underTest.addRuleExclusionPatternForComponent(component, pattern);

    assertThat(underTest.accept(issue, chain)).isFalse();
  }

  @Test
  void shouldAcceptIfRulePatternDoesNotMatch() {
    var pattern = mock(WildcardPattern.class);
    when(pattern.match(ruleKey.toString())).thenReturn(false);
    underTest.addRuleExclusionPatternForComponent(component, pattern);

    assertThat(underTest.accept(issue, chain)).isFalse();
  }
}
