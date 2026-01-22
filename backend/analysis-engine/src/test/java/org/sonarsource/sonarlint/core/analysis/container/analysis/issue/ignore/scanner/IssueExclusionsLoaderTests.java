/*
ACR-633a19275d4145d49166f6eb80eec1d5
ACR-05b8b774131b43f3bc6657c4d6c620c0
ACR-e4a4309d5599421c998749478ce37788
ACR-a1363850b15c4a4c9f3bc3fa651ab975
ACR-e71e5b4cdab540c0944547e0c5629ba8
ACR-b4e407012f524a4ebbabaddaec657304
ACR-8ac606c4e1d24e8496b1f5ee5cb1b9db
ACR-56fedcd7def443ec8fa77ad23eb759be
ACR-99886b7803154d978121389bff922d5a
ACR-79a1c85d1f6449db9c1dcc7891952d06
ACR-57e2c027123a48d9b6f9df3f63e01b9a
ACR-181594c489ad4d4b85887d7b76d9c74f
ACR-71319ce9cc5547c0b5f01336b04a8cd7
ACR-b0651ca52e2f47a5b6a036ebd2808c67
ACR-ec18ec0163ab4d46b0abda26a892ff77
ACR-73c331084cf0431ea5bc232c3c8fe92b
ACR-15017fca1ce54308a6f7459ec516b035
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata.Metadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.IgnoreIssuesFilter;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssueExclusionPatternInitializer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssuePattern;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import testutils.OnDiskTestClientInputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class IssueExclusionsLoaderTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private IssueExclusionPatternInitializer exclusionPatternInitializer;
  private IgnoreIssuesFilter ignoreIssuesFilter;

  private IssueExclusionsLoader scanner;

  @BeforeEach
  void before() throws Exception {
    exclusionPatternInitializer = mock(IssueExclusionPatternInitializer.class);
    ignoreIssuesFilter = mock(IgnoreIssuesFilter.class);
    scanner = new IssueExclusionsLoader(exclusionPatternInitializer, ignoreIssuesFilter);
  }

  private SonarLintInputFile createFile(String path) {
    return new SonarLintInputFile(new OnDiskTestClientInputFile(Paths.get(path), path, false, StandardCharsets.UTF_8), f -> mock(Metadata.class));
  }

  @Test
  void testToString() {
    assertThat(scanner).hasToString("Issues Exclusions - Source Scanner");
  }

  @Test
  void createComputer() {

    assertThat(scanner.createCharHandlerFor(createFile("src/main/java/Foo.java"))).isNull();

    when(exclusionPatternInitializer.getAllFilePatterns()).thenReturn(Collections.singletonList("pattern"));
    scanner = new IssueExclusionsLoader(exclusionPatternInitializer, ignoreIssuesFilter);
    assertThat(scanner.createCharHandlerFor(createFile("src/main/java/Foo.java"))).isNotNull();

  }

  @Test
  void populateRuleExclusionPatterns() {
    var pattern1 = new IssuePattern("org/foo/Bar*.java", "*");
    var pattern2 = new IssuePattern("org/foo/Hell?.java", "checkstyle:MagicNumber");
    when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(Arrays.asList(pattern1, pattern2));

    var loader = new IssueExclusionsLoader(exclusionPatternInitializer, ignoreIssuesFilter);
    var file1 = createFile("org/foo/Bar.java");
    loader.addMulticriteriaPatterns(file1);
    var file2 = createFile("org/foo/Baz.java");
    loader.addMulticriteriaPatterns(file2);
    var file3 = createFile("org/foo/Hello.java");
    loader.addMulticriteriaPatterns(file3);

    verify(ignoreIssuesFilter).addRuleExclusionPatternForComponent(file1, pattern1.getRulePattern());
    verify(ignoreIssuesFilter).addRuleExclusionPatternForComponent(file3, pattern2.getRulePattern());
    verifyNoMoreInteractions(ignoreIssuesFilter);
  }

}
