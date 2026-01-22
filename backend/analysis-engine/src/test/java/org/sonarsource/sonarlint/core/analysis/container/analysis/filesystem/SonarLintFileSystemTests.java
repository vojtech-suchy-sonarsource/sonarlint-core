/*
ACR-3b204b76f86d40b199d3516b747ba32f
ACR-391d976a07f2476194576f76ae89c924
ACR-c5fec0c410ce4bc0b1c901bfc164b151
ACR-746f21bf4fe6467c8d6e599fbc66b079
ACR-d5f34621215f4d0d9f21c70accfb8d38
ACR-8e0a02721b2a48e6b8a4c78f8e51579c
ACR-fb888e1a99fe4c67885cec6a8a2ad013
ACR-d8f8f1fe2c514ac6b0f4fd3c4af99801
ACR-2b0d98f33ab24a3a8fe0cae5eba65295
ACR-ab6eec72b4994d4abff6c2cfb7cce2d0
ACR-1e91970947394183940394c8cf813bd8
ACR-1b572d6ebfa64016bcb8ed57f623c26b
ACR-35dcdc33fc0448cebe5eac41b7077f26
ACR-bcd5215263414c16bb3c831c125bac94
ACR-fe711b3cf2584d6d8639b088f18b5b67
ACR-e0dca557541d4f43ac47fdc8b9d898a2
ACR-52a873542f68407e8ef4b75aad23d8e0
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import testutils.TestInputFileBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SonarLintFileSystemTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private SonarLintFileSystem fs;
  @TempDir
  Path basedir;
  private final InputFileIndex inputFileCache = new InputFileIndex();

  @BeforeEach
  void prepare() throws Exception {
    fs = new SonarLintFileSystem(AnalysisConfiguration.builder().setBaseDir(basedir).build(), inputFileCache);
  }

  @Test
  void return_fake_workdir() throws IOException {
    assertThat(fs.workDir()).isEqualTo(basedir.toFile());
  }

  @Test
  void add_languages() {
    assertThat(fs.languages()).isEmpty();

    inputFileCache.doAdd(new TestInputFileBuilder("src/Foo.php").setLanguage(SonarLanguage.PHP).build());
    inputFileCache.doAdd(new TestInputFileBuilder("src/Bar.java").setLanguage(SonarLanguage.JAVA).build());

    assertThat(fs.languages()).containsOnly("java", "php");
  }

  @Test
  void files() {
    assertThat(fs.inputFiles(fs.predicates().all())).isEmpty();

    var inputFile = new TestInputFileBuilder("src/Foo.php").setBaseDir(basedir).setLanguage(SonarLanguage.PHP).build();
    inputFileCache.doAdd(inputFile);
    inputFileCache.doAdd(new TestInputFileBuilder("src/Bar.java").setBaseDir(basedir).setLanguage(SonarLanguage.JAVA).build());
    inputFileCache.doAdd(new TestInputFileBuilder("src/Baz.java").setBaseDir(basedir).setLanguage(SonarLanguage.JAVA).build());

    //ACR-48a9aef459374d67bc3a05ac0d09898d
    inputFileCache.doAdd(new TestInputFileBuilder("src/readme.txt").setBaseDir(basedir).build());

    //ACR-5683dca4784f4e9a9351915126592cd3
    assertThat(fs.inputFile(fs.predicates().is(inputFile.file()))).isNotNull();

    assertThat(fs.inputFile(fs.predicates().hasURI(new File(basedir.toFile(), "src/Bar.java").toURI()))).isNotNull();
    assertThat(fs.inputFile(fs.predicates().hasURI(new File(basedir.toFile(), "does/not/exist").toURI()))).isNull();
    assertThat(fs.inputFile(fs.predicates().hasURI(new File(basedir.toFile(), "../src/Bar.java").toURI()))).isNull();

    assertThat(fs.files(fs.predicates().all())).hasSize(4);
    assertThat(fs.files(fs.predicates().hasLanguage("java"))).hasSize(2);
    assertThat(fs.files(fs.predicates().hasLanguage("cobol"))).isEmpty();

    assertThat(fs.hasFiles(fs.predicates().all())).isTrue();
    assertThat(fs.hasFiles(fs.predicates().hasLanguage("java"))).isTrue();
    assertThat(fs.hasFiles(fs.predicates().hasLanguage("cobol"))).isFalse();

    assertThat(fs.inputFiles(fs.predicates().all())).hasSize(4);
    assertThat(fs.inputFiles(fs.predicates().hasLanguage("php"))).hasSize(1);
    assertThat(fs.inputFiles(fs.predicates().hasLanguage("java"))).hasSize(2);
    assertThat(fs.inputFiles(fs.predicates().hasLanguage("cobol"))).isEmpty();

    assertThat(fs.languages()).containsOnly("java", "php");
  }

  @Test
  void input_file_returns_null_if_file_not_found() {
    assertThat(fs.inputFile(fs.predicates().hasLanguage("cobol"))).isNull();
  }

  @Test
  void input_file_fails_if_too_many_results() {
    inputFileCache.doAdd(new TestInputFileBuilder("src/Bar.java").setLanguage(SonarLanguage.JAVA).build());
    inputFileCache.doAdd(new TestInputFileBuilder("src/Baz.java").setLanguage(SonarLanguage.JAVA).build());

    var thrown = assertThrows(IllegalArgumentException.class, () -> fs.inputFile(fs.predicates().all()));
    assertThat(thrown).hasMessageStartingWith("expected one element");
  }

  @Test
  void input_file_supports_non_indexed_predicates() {
    inputFileCache.doAdd(new TestInputFileBuilder("src/Bar.java").setLanguage(SonarLanguage.JAVA).build());

    //ACR-740e5ff795f7435692a96b60ccf38669
    assertThat(fs.inputFile(fs.predicates().hasLanguage("java"))).isNotNull();
  }

  @Test
  void unsupported_resolve_path() {
    assertThrows(UnsupportedOperationException.class, () -> fs.resolvePath("foo"));
  }

}
