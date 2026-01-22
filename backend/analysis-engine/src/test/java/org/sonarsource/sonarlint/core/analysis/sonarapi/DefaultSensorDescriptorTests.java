/*
ACR-8065505a8de9446e92d19a302fb5c6bb
ACR-387752e633df4f4c9012a8a2d5c9b56c
ACR-422a6aff1b9e4c148191d8b1922326e1
ACR-64a8c9507814438daf2e241ef2ba6280
ACR-f4cc54556e2e421c9769428595da5e62
ACR-8001907aa75a4c79ad8d24c5fa587939
ACR-bf0f61503f9d43508bf182aab06a16a6
ACR-5df23e2b6d854cbc919c75dddb9f904a
ACR-c5a497cec8d9408dba5019edfed40c8d
ACR-9e1e3022620f4d76b1588de60b58cdf6
ACR-bc934f99153e4629acd45f46af398692
ACR-dee035dbe8464b6dbb73361081ea8c9d
ACR-9ea7aff5018b462f9dca3ae43fc5d030
ACR-74847ed778f34d1bbb1c9bcad34890fa
ACR-e4f119ff709340d1a74bd7c9f5e02085
ACR-b132b294fb7b4eb6b662e559d338f45f
ACR-1fce80bc7b1f449eacbee3fe4a107398
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
