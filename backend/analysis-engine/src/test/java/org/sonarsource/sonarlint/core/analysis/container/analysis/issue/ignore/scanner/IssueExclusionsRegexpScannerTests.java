/*
ACR-60b11681007348b2b564c9dfbafb80de
ACR-56c5ad57888c4cf8a2f650692d55ccd8
ACR-820fd451cf0f4be4addacc527d331ddc
ACR-d6d5da9114914d34b656f31510309c04
ACR-527f458d3907414ebc2ddf1eddb39d80
ACR-bcad6a118cb849e3a2f9372281c34aff
ACR-a5ed1dccb2474bef945670d558ce37b0
ACR-e45a151997594efeb73108b3253d2e9a
ACR-cb67f7bc223e4aef8790e5bf95b314d8
ACR-023c91fc8d334c83b8612eda3b62dd91
ACR-1c880e0715f5433687625c2c3220f9ab
ACR-4520454127cf4807b256a413ffe2f192
ACR-b9f5e950e4fa41e4a0fd2aa694075b48
ACR-8c8780c546b1486495a2da3a07dd705b
ACR-02309caef4f84d0f8971397bc3c6937f
ACR-ba7caca280e54dfcbf314d4e140719ab
ACR-8e795c7370364377a02a8c2390927c27
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
