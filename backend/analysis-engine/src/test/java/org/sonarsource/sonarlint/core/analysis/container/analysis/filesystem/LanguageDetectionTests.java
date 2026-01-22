/*
ACR-4c2d3aa3bf734001b8c6b0e1cca57971
ACR-6df4cc969bf04300aa156ee570365881
ACR-233184f2cda3442f82fb61e52ba54f2b
ACR-38efa001fa55447abd5c4b3a20ac3c50
ACR-bea43f5ccb504dd29e3033e7637126ad
ACR-ad289bf9d699405998efeef75fa59511
ACR-5d78bd3bcfd3412cb28771d378a2af53
ACR-4d36aac51d634d218e0b2bdfedff0642
ACR-cddc1390fbf7429f96f2296d4e71d7b3
ACR-208c06393eda48889e734759413c26d0
ACR-d6e8e1a989164458a3e54267756b6087
ACR-3d8605944a2d4feb99e62d62a2e83996
ACR-6d8d4653945047a8875f9f5b1d7448c2
ACR-d5b74adb350c43d9b100d408300893c2
ACR-96b472a7c446431c96990cd0ec74168b
ACR-9028c470d8e84c4ab8852c426f31b858
ACR-848c3e171bca4066871df1a5a2793fc0
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
