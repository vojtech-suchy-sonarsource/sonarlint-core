/*
ACR-5a2382c9580543c9b668ae98153bada9
ACR-8001fe7a5f2e468abadb322965af98bc
ACR-c02ca49471b440228f0828384b11a02f
ACR-f1de7f43f65a493eb60d012d6b7067ad
ACR-902aefdae8a947b38dc6701dae28c49d
ACR-d1db975ffe8d4828a9d16dda54834519
ACR-9a58aa7177234dacb5a0bc46c19236dd
ACR-7a4b6ab0f63f40809c1596af38607b52
ACR-99225818a6984e8babaf9bb0d4614720
ACR-dc8e9d84731d4b9984a0b66dbb48ca1e
ACR-c8249f0267ed4f18b97a983e7087051f
ACR-60c0665c0550493db2c8426273272ebe
ACR-72184e69ab6a4b51acce2a4e4e5bcf59
ACR-5b6d25f6a9264d45b2aadac10b6cbd7b
ACR-3c55637c5e1f4099a1a86da900cb4a65
ACR-1f65c85aae614b079aef2f9dc5acfd61
ACR-5cfaf5b3eea942448f71b83fa16661fd
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.resources.Language;
import org.sonar.api.utils.MessageException;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import testutils.TestInputFileBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LanguageDetectionTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @TempDir
  private Path basedir;

  @Test
  void test_sanitizeExtension() {
    assertThat(LanguageDetection.sanitizeExtension(".cbl")).isEqualTo("cbl");
    assertThat(LanguageDetection.sanitizeExtension(".CBL")).isEqualTo("cbl");
    assertThat(LanguageDetection.sanitizeExtension("CBL")).isEqualTo("cbl");
    assertThat(LanguageDetection.sanitizeExtension("cbl")).isEqualTo("cbl");
  }

  @Test
  void search_by_file_extension() {
    var detection = new LanguageDetection(new MapSettings(Map.of()).asConfig());

    assertThat(detection.language(newInputFile("Foo.java"))).isEqualTo(SonarLanguage.JAVA);
    assertThat(detection.language(newInputFile("src/Foo.java"))).isEqualTo(SonarLanguage.JAVA);
    assertThat(detection.language(newInputFile("Foo.JAVA"))).isEqualTo(SonarLanguage.JAVA);
    assertThat(detection.language(newInputFile("Foo.jav"))).isEqualTo(SonarLanguage.JAVA);
    assertThat(detection.language(newInputFile("Foo.Jav"))).isEqualTo(SonarLanguage.JAVA);

    assertThat(detection.language(newInputFile("abc.abap"))).isEqualTo(SonarLanguage.ABAP);
    assertThat(detection.language(newInputFile("abc.ABAP"))).isEqualTo(SonarLanguage.ABAP);

    assertThat(detection.language(newInputFile("abc.truc"))).isNull();
    assertThat(detection.language(newInputFile("abap"))).isNull();
  }

  @Test
  void recognise_yaml_files() {
    var detection = new LanguageDetection(new MapSettings(Map.of()).asConfig());

    assertThat(detection.language(newInputFile("lambda.yaml"))).isEqualTo(SonarLanguage.YAML);
    assertThat(detection.language(newInputFile("lambda.yml"))).isEqualTo(SonarLanguage.YAML);
    assertThat(detection.language(newInputFile("config/lambda.yml"))).isEqualTo(SonarLanguage.YAML);
    assertThat(detection.language(newInputFile("config/lambda.YAML"))).isEqualTo(SonarLanguage.YAML);

    assertThat(detection.language(newInputFile("wrong.ylm"))).isNull();
    assertThat(detection.language(newInputFile("config.js"))).isNotEqualTo(SonarLanguage.YAML);
  }

  @Test
  void recognise_kts_files() {
    var detection = new LanguageDetection(new MapSettings(Map.of()).asConfig());

    assertThat(detection.language(newInputFile("settings.kts"))).isEqualTo(SonarLanguage.KOTLIN);

    assertThat(detection.language(newInputFile("settings.kms"))).isNull();
    assertThat(detection.language(newInputFile("settings.js"))).isNotEqualTo(SonarLanguage.KOTLIN);
  }

  @Test
  void recognise_css_files() {
    var detection = new LanguageDetection(new MapSettings(Map.of()).asConfig());

    assertThat(detection.language(newInputFile("style.css"))).isEqualTo(SonarLanguage.CSS);
    assertThat(detection.language(newInputFile("style.less"))).isEqualTo(SonarLanguage.CSS);
    assertThat(detection.language(newInputFile("style.scss"))).isEqualTo(SonarLanguage.CSS);

    assertThat(detection.language(newInputFile("style.stylus"))).isNull();
  }

  @Test
  void recognise_go_file() {
    var detection = new LanguageDetection(new MapSettings(Map.of()).asConfig());

    assertThat(detection.language(newInputFile("myFile.go"))).isEqualTo(SonarLanguage.GO);
    assertThat(detection.language(newInputFile("folder/myFile.go"))).isEqualTo(SonarLanguage.GO);

    assertThat(detection.language(newInputFile("style.nogo"))).isNull();
  }

  @Test
  void recognise_terraform_file() {
    var detection = new LanguageDetection(new MapSettings(Map.of()).asConfig());

    assertThat(detection.language(newInputFile("myFile.tf"))).isEqualTo(SonarLanguage.TERRAFORM);
    assertThat(detection.language(newInputFile("folder/myFile.tf"))).isEqualTo(SonarLanguage.TERRAFORM);

    assertThat(detection.language(newInputFile("style.notf"))).isNull();
  }

  @Test
  void should_not_fail_if_no_language() {
    var detection = new LanguageDetection(new MapSettings(Map.of()).asConfig());
    assertThat(detection.language(newInputFile("Foo.blabla"))).isNull();
  }

  @Test
  void fail_if_conflicting_language_suffix() {
    var settings = new MapSettings(Map.of(SonarLanguage.XML.getFileSuffixesPropKey(), "xhtml",
      SonarLanguage.HTML.getFileSuffixesPropKey(), "xhtml"));
    var detection = new LanguageDetection(settings.asConfig());
    var inputFile = newInputFile("abc.xhtml");
    var e = assertThrows(MessageException.class, () -> detection.language(inputFile));
    assertThat(e.getMessage())
      .contains("Language of file \"file://")
      .contains("abc.xhtml\" can not be decided as the file extension matches both ")
      .contains("HTML: xhtml")
      .contains("XML: xhtml");
  }

  private InputFile newInputFile(String path) {
    return new TestInputFileBuilder(path).setBaseDir(basedir).build();
  }

  static class MockLanguage implements Language {
    private final String key;
    private final String[] extensions;

    MockLanguage(String key, String... extensions) {
      this.key = key;
      this.extensions = extensions;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public String getName() {
      return key;
    }

    @Override
    public String[] getFileSuffixes() {
      return extensions;
    }
  }
}
