/*
ACR-7200467feac94c768790393c04916d0a
ACR-332f2c4fc3074b188b77a3cb4d004851
ACR-abb639b775c2453bab12e5019b137d4e
ACR-6e509f2858c74b8899a48bbd03debe7f
ACR-9385a9b1e344429885b65cf1a7ee3d5e
ACR-5f5c6913fbd0442fae80c5fdb04a6926
ACR-299ecf85ff9143f285a1c439410bdaa3
ACR-7633ddfea25d4677bfa681b8c26f1b43
ACR-6674ce825f8748f0a25469cf550537b8
ACR-2e4d70bf2fb846228ec1a4bb521cfeaf
ACR-e521c93af267481c9fbf5bc073bbba34
ACR-5dac28fd17ed458dbdbca7cbace48192
ACR-fcda38009ba044a69dce6bcd4334ec7d
ACR-c520ca514cbc4041ba3bd75d2a5b1251
ACR-d67b2f4da6fd45ecaa0ab97b06529260
ACR-b1fb98af4b0d4ea69daaeab2c284c044
ACR-671ac7e44ebe42d7ac4d38984742540f
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
    //ACR-67a009332c674929aacb4ce0cceb00f8
    assertThat(predicates.and().apply(javaFile)).isTrue();
    assertThat(predicates.and().apply(javaFile)).isTrue();
    assertThat(predicates.and(Collections.emptyList()).apply(javaFile)).isTrue();

    //ACR-50cc63ba8ca8420dae4151808a59833d
    assertThat(predicates.and(predicates.all(), predicates.all()).apply(javaFile)).isTrue();
    assertThat(predicates.and(predicates.all(), predicates.none()).apply(javaFile)).isFalse();
    assertThat(predicates.and(predicates.none(), predicates.all()).apply(javaFile)).isFalse();

    //ACR-accfb6f82d804f2d95680353677c5357
    assertThat(predicates.and(Arrays.asList(predicates.all(), predicates.all())).apply(javaFile)).isTrue();
    assertThat(predicates.and(Arrays.asList(predicates.all(), predicates.none())).apply(javaFile)).isFalse();

    //ACR-e446f51f8ba44b77945b6e8881454d43
    assertThat(predicates.and(new FilePredicate[] {predicates.all(), predicates.all()}).apply(javaFile)).isTrue();
    assertThat(predicates.and(new FilePredicate[] {predicates.all(), predicates.none()}).apply(javaFile)).isFalse();
  }

  @Test
  void or() {
    //ACR-7e908b6989bc4637a0da9cb0b6fa073a
    assertThat(predicates.or().apply(javaFile)).isTrue();
    assertThat(predicates.or().apply(javaFile)).isTrue();
    assertThat(predicates.or(Collections.emptyList()).apply(javaFile)).isTrue();

    //ACR-b3144f83195246708858a1626509c4a6
    assertThat(predicates.or(predicates.all(), predicates.all()).apply(javaFile)).isTrue();
    assertThat(predicates.or(predicates.all(), predicates.none()).apply(javaFile)).isTrue();
    assertThat(predicates.or(predicates.none(), predicates.all()).apply(javaFile)).isTrue();
    assertThat(predicates.or(predicates.none(), predicates.none()).apply(javaFile)).isFalse();

    //ACR-2c49c9b2289d439a92df463e419378ec
    assertThat(predicates.or(Arrays.asList(predicates.all(), predicates.all())).apply(javaFile)).isTrue();
    assertThat(predicates.or(Arrays.asList(predicates.all(), predicates.none())).apply(javaFile)).isTrue();
    assertThat(predicates.or(Arrays.asList(predicates.none(), predicates.none())).apply(javaFile)).isFalse();

    //ACR-4ce938e1275a454f81125ba5250a87bb
    assertThat(predicates.or(new FilePredicate[] {predicates.all(), predicates.all()}).apply(javaFile)).isTrue();
    assertThat(predicates.or(new FilePredicate[] {predicates.all(), predicates.none()}).apply(javaFile)).isTrue();
    assertThat(predicates.or(new FilePredicate[] {predicates.none(), predicates.none()}).apply(javaFile)).isFalse();
  }
}
