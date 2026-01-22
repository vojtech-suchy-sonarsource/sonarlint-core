/*
ACR-d6db5395be2d43bebb6c290da35a0fde
ACR-33ebb4514dec4355922651e80e0d7177
ACR-e52e48341f534c919ff6f1b5d9acf20e
ACR-204e57aa2ee24b739c2f29594cec4b66
ACR-fc88646d26a542f49017d5088fcea8b6
ACR-ad5134ed22cf418dbb9f383e88545191
ACR-714cfec8e8d84eacbf900d19c61a6c17
ACR-4ce2e7311a334f4ab0e166511b51fcd9
ACR-d4a1a1a1cdeb4f139b13b1a799c79e1b
ACR-4000cfd7f0514a50af873aa42e1679c2
ACR-0a67386b729d447aa2734df7d8149e09
ACR-02b49530a56a469abd5039489f4429d9
ACR-e5547d50b1a94f44939dde2d1b4b4cba
ACR-4bf144e64f144d7f82f0838810ce6157
ACR-d02854fbbf394d3281e5c098c5f2fef5
ACR-21b903ee9fb547c29e11a6123447cda3
ACR-fd338dc8ddcf43968145f15e41ed1e91
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata.Metadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner.IssueExclusionsLoader.DoubleRegexpMatcher;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import testutils.OnDiskTestClientInputFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class IssueExclusionsRegexpScannerTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private SonarLintInputFile javaFile;

  private List<Pattern> allFilePatterns;
  private List<DoubleRegexpMatcher> blockPatterns;
  private IssueExclusionsRegexpScanner regexpScanner;
  private final FileMetadata fileMetadata = new FileMetadata();

  @BeforeEach
  void init() {
    blockPatterns = Arrays.asList(new DoubleRegexpMatcher(Pattern.compile("// SONAR-OFF"), Pattern.compile("// SONAR-ON")),
      new DoubleRegexpMatcher(Pattern.compile("// FOO-OFF"), Pattern.compile("// FOO-ON")));
    allFilePatterns = Collections.singletonList(Pattern.compile("@SONAR-IGNORE-ALL"));

    javaFile = new SonarLintInputFile(new OnDiskTestClientInputFile(Paths.get("src/Foo.java"), "src/Foo.java", false, StandardCharsets.UTF_8), f -> mock(Metadata.class));
    regexpScanner = new IssueExclusionsRegexpScanner(javaFile, allFilePatterns, blockPatterns);
  }

  @Test
  void shouldDetectPatternLastLine() throws URISyntaxException, IOException {
    var filePath = getResource("file-with-single-regexp-last-line.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(javaFile.isIgnoreAllIssues()).isTrue();
  }

  @Test
  void shouldDoNothing() throws Exception {
    var filePath = getResource("file-with-no-regexp.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(javaFile.isIgnoreAllIssues()).isFalse();
  }

  @Test
  void shouldExcludeAllIssues() throws Exception {
    var filePath = getResource("file-with-single-regexp.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(javaFile.isIgnoreAllIssues()).isTrue();
  }

  @Test
  void shouldExcludeAllIssuesEvenIfAlsoDoubleRegexps() throws Exception {
    var filePath = getResource("file-with-single-regexp-and-double-regexp.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(javaFile.isIgnoreAllIssues()).isTrue();
  }

  @Test
  void shouldExcludeLines() throws Exception {
    var filePath = getResource("file-with-double-regexp.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(javaFile.isIgnoreAllIssues()).isFalse();
    assertThat(IntStream.rangeClosed(1, 20).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(21, 25).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(26, 34).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
  }

  @Test
  void shouldAddPatternToExcludeLinesTillTheEnd() throws Exception {
    var filePath = getResource("file-with-double-regexp-unfinished.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(javaFile.isIgnoreAllIssues()).isFalse();
    assertThat(IntStream.rangeClosed(1, 20).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(21, 34).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
  }

  @Test
  void shouldAddPatternToExcludeSeveralLineRanges() throws Exception {
    var filePath = getResource("file-with-double-regexp-twice.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(javaFile.isIgnoreAllIssues()).isFalse();
    assertThat(IntStream.rangeClosed(1, 20).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(21, 25).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(26, 28).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(29, 33).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
  }

  @Test
  void shouldAddPatternToExcludeLinesWithWrongOrder() throws Exception {
    var filePath = getResource("file-with-double-regexp-wrong-order.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(IntStream.rangeClosed(1, 24).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(25, 35).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
  }

  @Test
  void shouldAddPatternToExcludeLinesWithMess() throws Exception {
    var filePath = getResource("file-with-double-regexp-mess.txt");
    fileMetadata.readMetadata(Files.newInputStream(filePath), UTF_8, filePath.toUri(), regexpScanner);

    assertThat(IntStream.rangeClosed(1, 20).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(21, 29).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    assertThat(IntStream.rangeClosed(30, 37).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
  }

  private Path getResource(String fileName) throws URISyntaxException {
    return Paths.get(this.getClass().getResource("/IssueExclusionsRegexpScannerTests/" + fileName).toURI());
  }

}
