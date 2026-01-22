/*
ACR-d3a9dc22b62f40b1a51c0d7823e18838
ACR-3a4f5d63a03c4a3cb90bd5cab44c8f2d
ACR-440f554d4bf94a51a6f3252597cf14f5
ACR-e429bf2928ed4eda93b9d1a1096a2bc8
ACR-839f129a49e74df8bd2466a6b0a4efa0
ACR-03e4dccbcda94a3aa03a3f0f6bb3efd5
ACR-ef2b43a13df144ee899bf5a20ee4c600
ACR-10249d516d1148f28adb1aa7549938e0
ACR-61dee2db2a2d4a849f3ef1f932c85177
ACR-d21eaae8332846ecb4180a01d250c1ff
ACR-26e5493b66a74da49ee042a451e020c0
ACR-db4e4e39cbfb4777ac211fa07d45b255
ACR-eea89c252c1c4af38c705cd0989e4902
ACR-b71ecb7e4ebf4271a5c67166945dcd49
ACR-e2284cc448d340c9b49d759301a246d9
ACR-32081769ccb94489bd7b2d217188d10c
ACR-969ce57192134da49a4ff5f3a901c5a4
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
