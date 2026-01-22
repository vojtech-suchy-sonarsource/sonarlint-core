/*
ACR-a699a261cea7411da0e04fd03869ae20
ACR-f2602e148fda42c0b01f58b3f4a0baca
ACR-c7ed87f889404de3ad95587485b55265
ACR-c0a20f3b021347218852987ea3069a91
ACR-b78623a17a7b424490ade4e411e6f695
ACR-cd16810722a4464090f3f66acc6e68c1
ACR-cc68fd7778cb412f9073b7d95c421255
ACR-7ba6a2effb5a4b958fd67dfa418b06f3
ACR-cdb004a506ab4b10904da28376d28988
ACR-0430e34650574718af969d1484d42dc2
ACR-3dd7800011414124b81aee39134f79b3
ACR-5f69156096c2442aabeb0bed3e96f2f2
ACR-27a5b04756354ea4b35b97a1f9a41a20
ACR-90ee862ebe9a4f92ad9c0d4baea9e31a
ACR-14e2509091f2418f93602567d62810be
ACR-39cd634bba0942ad84de19158bfea8e0
ACR-80037ae71e16417785269fa7da2a466d
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
