/*
ACR-620f094ad1e24e4c9c18eefa7a5ad0c1
ACR-460e27bff16b4583b6aa95b6b186e264
ACR-3574ddfd892a42bd830191bef6a8b22f
ACR-9f9c5903dcd94254b87517a8502a025d
ACR-373f944589154ab3ab0345978450ff3b
ACR-eab6438fdc2a46519f7a87c42a4d684f
ACR-eafa03e8572c475bbe33c1a22096f7a1
ACR-49fd47de66da4b14b9ec6c8c5940c91c
ACR-c619d362168e4c5a9a4d9352013afe13
ACR-cf111c65cc61422c8cdb20e465fb38d7
ACR-7bc058bf17484d56b0614680c845e1b0
ACR-2b59919e78b541cca6566940e14e1f46
ACR-3f8c89996dd34680a86005385bd01552
ACR-e4cb72678021474cbb2c1ed88e651586
ACR-ceb4f78871954523bcd00ebed3d91dfc
ACR-adc5568484e74e5db3914963470dc3a2
ACR-b40b62cb33ed4c7b8f15865f510dd0f7
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.issue.filter.IssueFilterChain;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata.Metadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputProject;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssueInclusionPatternInitializer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssuePattern;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultFilterableIssue;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import testutils.OnDiskTestClientInputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EnforceIssuesFilterTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private IssueInclusionPatternInitializer exclusionPatternInitializer;
  private EnforceIssuesFilter ignoreFilter;
  private DefaultFilterableIssue issue;
  private IssueFilterChain chain;

  @BeforeEach
  void init() {
    exclusionPatternInitializer = mock(IssueInclusionPatternInitializer.class);
    issue = mock(DefaultFilterableIssue.class);
    chain = mock(IssueFilterChain.class);
    when(chain.accept(issue)).thenReturn(true);
  }

  @Test
  void shouldPassToChainIfNoConfiguredPatterns() {
    ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer);
    assertThat(ignoreFilter.accept(issue, chain)).isTrue();
    verify(chain).accept(issue);
  }

  @Test
  void shouldPassToChainIfRuleDoesNotMatch() {
    var rule = "rule";
    var ruleKey = mock(RuleKey.class);
    when(ruleKey.toString()).thenReturn(rule);
    when(issue.ruleKey()).thenReturn(ruleKey);

    var matching = mock(IssuePattern.class);
    when(matching.matchRule(ruleKey)).thenReturn(false);
    when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(List.of(matching));

    ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer);
    assertThat(ignoreFilter.accept(issue, chain)).isTrue();
    verify(chain).accept(issue);
  }

  @Test
  void shouldAcceptIssueIfFullyMatched() {
    var rule = "rule";
    var path = "org/sonar/api/Issue.java";
    var ruleKey = mock(RuleKey.class);
    when(ruleKey.toString()).thenReturn(rule);
    when(issue.ruleKey()).thenReturn(ruleKey);

    var matching = mock(IssuePattern.class);
    when(matching.matchRule(ruleKey)).thenReturn(true);
    when(matching.matchFile(path)).thenReturn(true);
    when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(List.of(matching));
    when(issue.getComponent()).thenReturn(createComponentWithPath(path));

    ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer);
    assertThat(ignoreFilter.accept(issue, chain)).isTrue();
    verifyNoInteractions(chain);
  }

  private InputComponent createComponentWithPath(String path) {
    return new SonarLintInputFile(new OnDiskTestClientInputFile(Paths.get(path), path, false, StandardCharsets.UTF_8),
      f -> mock(Metadata.class));
  }

  @Test
  void shouldRefuseIssueIfRuleMatchesButNotPath() {
    var rule = "rule";
    var path = "org/sonar/api/Issue.java";
    var componentKey = "org.sonar.api.Issue";
    var ruleKey = mock(RuleKey.class);
    when(ruleKey.toString()).thenReturn(rule);
    when(issue.ruleKey()).thenReturn(ruleKey);
    when(issue.componentKey()).thenReturn(componentKey);

    var matching = mock(IssuePattern.class);
    when(matching.matchRule(ruleKey)).thenReturn(true);
    when(matching.matchFile(path)).thenReturn(false);
    when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(List.of(matching));
    when(issue.getComponent()).thenReturn(createComponentWithPath(path));

    ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer);
    assertThat(ignoreFilter.accept(issue, chain)).isFalse();
    verifyNoInteractions(chain);
  }

  @Test
  void shouldRefuseIssueIfRuleMatchesAndNotFile() {
    var rule = "rule";
    var path = "org/sonar/api/Issue.java";
    var ruleKey = mock(RuleKey.class);
    when(ruleKey.toString()).thenReturn(rule);
    when(issue.ruleKey()).thenReturn(ruleKey);

    var matching = mock(IssuePattern.class);
    when(matching.matchRule(ruleKey)).thenReturn(true);
    when(matching.matchFile(path)).thenReturn(true);
    when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(List.of(matching));
    when(issue.getComponent()).thenReturn(new SonarLintInputProject());

    ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer);
    assertThat(ignoreFilter.accept(issue, chain)).isFalse();
    verifyNoInteractions(chain);
  }
}
