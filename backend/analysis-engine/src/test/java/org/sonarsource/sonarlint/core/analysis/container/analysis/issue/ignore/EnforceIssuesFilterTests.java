/*
ACR-c0379d16c64b42c49ae27a8d0bbf3158
ACR-e4cfbe368ed84e0490291c7ad69f2276
ACR-102770d0057a4891b3f7c8256ecaabfa
ACR-32c6e542d20c4e7e8be92df72a9e1660
ACR-0ed381169a70483491334745f9caa5ec
ACR-8a8dd2a98ac34ac9a37c70e9e4162a1f
ACR-50b620367051490fabe1e3ff45e2c11a
ACR-6d9c9ab159534d0d80ad1e540e596e2c
ACR-327e172fefa144b48150423d3ad94404
ACR-5b8dc20957b04623843cacfcf00e77e7
ACR-fb382a53d6884397901d464b3540a092
ACR-28c836841fb245b9a74fc0db5550e422
ACR-0bf77918a6124eb28b48ffdb05d47181
ACR-e43212262d4b48b99d12f91deb653f8c
ACR-ede10bf14f8b45a7a3919314e33d2e9a
ACR-7ca5d6a945c646e08cf3bd74c1fd7ee5
ACR-f0e6fdc2976d4223968bf2a0e243944e
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
