/*
ACR-226f43f0ee5e45beb88dff8f5bfdfd1c
ACR-392730c9a4624f50a8d95906a41023d0
ACR-e9d183c8132541ddb379612e5b4e9d75
ACR-6a0da9f9c1284bf7a7675670f94244a0
ACR-589fe5a353e142ffad95788b7a6ab033
ACR-d7916d3ed77d44888d5e157e75585cd0
ACR-97f9fb03064e45c59fa1e3b14eb0a50c
ACR-44d58b510425440895a9c266f3f10ebd
ACR-00e26789cd6848769a775b291bc35ddd
ACR-2f705ce93a7841379d80c10797eb06b5
ACR-f5295e83edc54ccebaa7b29d3b841e2f
ACR-9d5c15e22ae7404fb240e6fb603b0d99
ACR-819927c308794960bbef165c68e6639f
ACR-40316ff9e3c64b29a1ba09c9bf611903
ACR-cc2e5908ec384410a2e419d0858049b2
ACR-83ee9be763eb44a997a017c7228fc164
ACR-926e0ce6f4734d7ca8f24410ceb3431e
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

    //ACR-68e71bc1a61d40079f52bc655d4f18dd
    inputFileCache.doAdd(new TestInputFileBuilder("src/readme.txt").setBaseDir(basedir).build());

    //ACR-28372a1ec89b4c558f3996a883607253
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

    //ACR-9dfc596c7647488ea9115b84eb05124f
    assertThat(fs.inputFile(fs.predicates().hasLanguage("java"))).isNotNull();
  }

  @Test
  void unsupported_resolve_path() {
    assertThrows(UnsupportedOperationException.class, () -> fs.resolvePath("foo"));
  }

}
