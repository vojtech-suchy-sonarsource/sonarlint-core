/*
ACR-173401e430ba42b4a3755b7c054b8258
ACR-3c2876de582c4a6db6369768d55594cf
ACR-c62faf0c4152492499abf013f50dfa04
ACR-c60f605068df4b009dd2f69d40454aed
ACR-4e5c7f86573746799443a54ff1905f4e
ACR-f90d3cd31ad84d0783bde9a1c07ff113
ACR-db6cbacb5c53455bb5be4a894e68da48
ACR-5969b9493edb48b5adee216a948f39c6
ACR-ee902670080943afacdec8775a3a68d6
ACR-b6bcbcc21f8a440e9122127a5a95a15b
ACR-3695e445a6ab41a582dae3024a1afdf9
ACR-992d501211c942ab8846548cf95726b7
ACR-0e2478857f3a4235af5b0e1ff06a9077
ACR-b9bf1bcf021747c8a4b8c7d8759ebce8
ACR-d10700078b114d0c9c986e51b53c36ec
ACR-19e40ac510df49c2b6236594f5af3d88
ACR-688427d681be4db59c7496752370d546
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultSensorDescriptorTests {

  @Test
  void describe() {
    var descriptor = new DefaultSensorDescriptor();
    descriptor
      .name("Foo")
      .onlyOnLanguage("java")
      .onlyOnFileType(InputFile.Type.MAIN)
      .onlyWhenConfiguration(c -> c.hasKey("sonar.foo.reportPath") && c.hasKey("sonar.foo.reportPath2"))
      .createIssuesForRuleRepository("squid-java");

    assertThat(descriptor.name()).isEqualTo("Foo");
    assertThat(descriptor.languages()).containsOnly("java");
    assertThat(descriptor.type()).isEqualTo(InputFile.Type.MAIN);
    var settings = new MapSettings(Map.of("sonar.foo.reportPath", "foo"));
    assertThat(descriptor.configurationPredicate().test(settings.asConfig())).isFalse();
    settings = new MapSettings(Map.of("sonar.foo.reportPath", "foo", "sonar.foo.reportPath2", "foo"));
    assertThat(descriptor.configurationPredicate().test(settings.asConfig())).isTrue();
    assertThat(descriptor.ruleRepositories()).containsOnly("squid-java");
  }

}
