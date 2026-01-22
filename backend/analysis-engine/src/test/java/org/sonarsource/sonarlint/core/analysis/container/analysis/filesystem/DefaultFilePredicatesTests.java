/*
ACR-3947d5ea52a340f988438d1d3e0ac918
ACR-5ca80b6c02ca4a6d8ecdd2873795b6f2
ACR-0fa4add3018b41c183db3d0da9fe9490
ACR-76b533adde70465b931691ceabc75b4d
ACR-44f490a688e046f7b6d2f7415f9117ab
ACR-300508ddb0cf445fbba72d0f5e03e730
ACR-9ba19c12587f4289928f52a118775f37
ACR-c345e861566e400bac7327d832b5f5be
ACR-9d5c1e3357ef41518dcfb874a645ee1f
ACR-db49e173685a4d7ba5768fee06768c32
ACR-6d7be520596944799c1c27f4fbca67df
ACR-7897bd119ccb49d792b6851d6444edfd
ACR-21e22a37b7c74b3e957efc34c160341f
ACR-11be20aa467643e8b698bad95ba7c488
ACR-c746cbd2443641b3a53a11545af812c1
ACR-aba8e476ece545b78d3b349e107dc8a1
ACR-17cc0df9286c4233a2ac9fd65a1c7604
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import testutils.OnDiskTestClientInputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class DefaultFilePredicatesTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private InputFile javaFile;
  private FilePredicates predicates;

  @TempDir
  Path baseDir;

  @BeforeEach
  void before() throws IOException {
    predicates = new DefaultFilePredicates();
    var filePath = baseDir.resolve("src/main/java/struts/Action.java");
    Files.createDirectories(filePath.getParent());
    Files.write(filePath, "foo".getBytes(StandardCharsets.UTF_8));
    var clientInputFile = new OnDiskTestClientInputFile(filePath, "src/main/java/struts/Action.java", false, StandardCharsets.UTF_8, SonarLanguage.JAVA);
    InputStream fileInputStream = Files.newInputStream(filePath);
    javaFile = new SonarLintInputFile(clientInputFile, f -> new FileMetadata().readMetadata(fileInputStream, StandardCharsets.UTF_8, filePath.toUri(), null))
      .setType(Type.MAIN)
      .setLanguage(SonarLanguage.JAVA);
  }

  @Test
  void all() {
    assertThat(predicates.all().apply(javaFile)).isTrue();
  }

  @Test
  void none() {
    assertThat(predicates.none().apply(javaFile)).isFalse();
  }

  @Test
  void matches_inclusion_pattern() {
    assertThat(predicates.matchesPathPattern("file:**/src/main/**/Action.java").apply(javaFile)).isTrue();
    assertThat(predicates.matchesPathPattern("**/src/main/**/Action.java").apply(javaFile)).isTrue();
    assertThat(predicates.matchesPathPattern("src/main/**/Action.java").apply(javaFile)).isTrue();
    assertThat(predicates.matchesPathPattern("src/**/*.php").apply(javaFile)).isFalse();
  }

  @Test
  void matches_inclusion_patterns() {
    assertThat(predicates.matchesPathPatterns(new String[] {"src/other/**.java", "src/main/**/Action.java"}).apply(javaFile)).isTrue();
    assertThat(predicates.matchesPathPatterns(new String[] {}).apply(javaFile)).isTrue();
    assertThat(predicates.matchesPathPatterns(new String[] {"src/other/**.java", "src/**/*.php"}).apply(javaFile)).isFalse();
  }

  @Test
  void does_not_match_exclusion_pattern() {
    assertThat(predicates.doesNotMatchPathPattern("src/main/**/Action.java").apply(javaFile)).isFalse();
    assertThat(predicates.doesNotMatchPathPattern("src/**/*.php").apply(javaFile)).isTrue();
  }

  @Test
  void does_not_match_exclusion_patterns() {
    assertThat(predicates.doesNotMatchPathPatterns(new String[] {}).apply(javaFile)).isTrue();
    assertThat(predicates.doesNotMatchPathPatterns(new String[] {"src/other/**.java", "src/**/*.php"}).apply(javaFile)).isTrue();
    assertThat(predicates.doesNotMatchPathPatterns(new String[] {"src/other/**.java", "src/main/**/Action.java"}).apply(javaFile)).isFalse();
  }

  @Test
  void has_relative_path_unsupported() {
    assertThrows(UnsupportedOperationException.class, () -> predicates.hasRelativePath("src/main/java/struts/Action.java").apply(javaFile));
  }

  @Test
  void has_uri() {
    var uri = javaFile.uri();
    assertThat(predicates.hasURI(uri).apply(javaFile)).isTrue();

    assertThat(predicates.hasURI(baseDir.resolve("another.php").toUri()).apply(javaFile)).isFalse();
  }

  @Test
  void has_name() {
    var fileName = javaFile.filename();
    assertThat(predicates.hasFilename(fileName).apply(javaFile)).isTrue();

    assertThat(predicates.hasFilename("another.php").apply(javaFile)).isFalse();
    assertThat(predicates.hasFilename("Action.php").apply(javaFile)).isFalse();
  }

  @Test
  void has_extension() {
    var extension = "java";
    assertThat(predicates.hasExtension(extension).apply(javaFile)).isTrue();

    assertThat(predicates.hasExtension("php").apply(javaFile)).isFalse();
    assertThat(predicates.hasExtension("").apply(javaFile)).isFalse();

  }

  @Test
  void has_path() {
    assertThrows(UnsupportedOperationException.class, () -> predicates.hasPath("src/main/java/struts/Action.java").apply(javaFile));
  }

  @Test
  void is_file() {
    assertThat(predicates.is(javaFile.file()).apply(javaFile)).isTrue();
    assertThat(predicates.is(new File("foo.php")).apply(javaFile)).isFalse();
  }

  @Test
  void has_language() {
    assertThat(predicates.hasLanguage("java").apply(javaFile)).isTrue();
    assertThat(predicates.hasLanguage("php").apply(javaFile)).isFalse();
  }

  @Test
  void has_languages() {
    assertThat(predicates.hasLanguages(Arrays.asList("java", "php")).apply(javaFile)).isTrue();
    assertThat(predicates.hasLanguages("java", "php").apply(javaFile)).isTrue();
    assertThat(predicates.hasLanguages(Arrays.asList("cobol", "php")).apply(javaFile)).isFalse();
    assertThat(predicates.hasLanguages("cobol", "php").apply(javaFile)).isFalse();
    assertThat(predicates.hasLanguages(Collections.emptyList()).apply(javaFile)).isTrue();
  }

  @Test
  void has_type() {
    assertThat(predicates.hasType(InputFile.Type.MAIN).apply(javaFile)).isTrue();
    assertThat(predicates.hasType(InputFile.Type.TEST).apply(javaFile)).isFalse();
  }

  @Test
  void has_status() {
    assertThat(predicates.hasAnyStatus().apply(javaFile)).isTrue();
    try {
      predicates.hasStatus(InputFile.Status.SAME).apply(javaFile);
      fail("Expected exception");
    } catch (Exception e) {
      assertThat(e).isInstanceOf(UnsupportedOperationException.class);
    }
  }

  @Test
  void not() {
    assertThat(predicates.not(predicates.hasType(InputFile.Type.MAIN)).apply(javaFile)).isFalse();
    assertThat(predicates.not(predicates.hasType(InputFile.Type.TEST)).apply(javaFile)).isTrue();
  }

  @Test
  void and() {
    //ACR-ecac30bd850540b6bba0ecaca60a188f
    assertThat(predicates.and().apply(javaFile)).isTrue();
    assertThat(predicates.and().apply(javaFile)).isTrue();
    assertThat(predicates.and(Collections.emptyList()).apply(javaFile)).isTrue();

    //ACR-30c6edaabf1042399c6b1cfe61a94122
    assertThat(predicates.and(predicates.all(), predicates.all()).apply(javaFile)).isTrue();
    assertThat(predicates.and(predicates.all(), predicates.none()).apply(javaFile)).isFalse();
    assertThat(predicates.and(predicates.none(), predicates.all()).apply(javaFile)).isFalse();

    //ACR-171481f5329b4e76b9be7db28b015881
    assertThat(predicates.and(Arrays.asList(predicates.all(), predicates.all())).apply(javaFile)).isTrue();
    assertThat(predicates.and(Arrays.asList(predicates.all(), predicates.none())).apply(javaFile)).isFalse();

    //ACR-80befa4ecce94c9bafce768ce70b88b2
    assertThat(predicates.and(new FilePredicate[] {predicates.all(), predicates.all()}).apply(javaFile)).isTrue();
    assertThat(predicates.and(new FilePredicate[] {predicates.all(), predicates.none()}).apply(javaFile)).isFalse();
  }

  @Test
  void or() {
    //ACR-32f41d00748f47e6a6a203bcc2af3347
    assertThat(predicates.or().apply(javaFile)).isTrue();
    assertThat(predicates.or().apply(javaFile)).isTrue();
    assertThat(predicates.or(Collections.emptyList()).apply(javaFile)).isTrue();

    //ACR-9955f44bfdcb4f809576a4690d6e7694
    assertThat(predicates.or(predicates.all(), predicates.all()).apply(javaFile)).isTrue();
    assertThat(predicates.or(predicates.all(), predicates.none()).apply(javaFile)).isTrue();
    assertThat(predicates.or(predicates.none(), predicates.all()).apply(javaFile)).isTrue();
    assertThat(predicates.or(predicates.none(), predicates.none()).apply(javaFile)).isFalse();

    //ACR-d80612c15052413b8e7e954d74763daf
    assertThat(predicates.or(Arrays.asList(predicates.all(), predicates.all())).apply(javaFile)).isTrue();
    assertThat(predicates.or(Arrays.asList(predicates.all(), predicates.none())).apply(javaFile)).isTrue();
    assertThat(predicates.or(Arrays.asList(predicates.none(), predicates.none())).apply(javaFile)).isFalse();

    //ACR-2c8895d4700a40d78879c27144688212
    assertThat(predicates.or(new FilePredicate[] {predicates.all(), predicates.all()}).apply(javaFile)).isTrue();
    assertThat(predicates.or(new FilePredicate[] {predicates.all(), predicates.none()}).apply(javaFile)).isTrue();
    assertThat(predicates.or(new FilePredicate[] {predicates.none(), predicates.none()}).apply(javaFile)).isFalse();
  }
}
