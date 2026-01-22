/*
ACR-50426a23cee04d649ef03725a51457dc
ACR-060e3b2ebc34404eb591ed480b11b710
ACR-84f0180fd3d74ef9a2f6c1759646b339
ACR-9dffef884bc146a5b9524951bd782df2
ACR-f895711d1d2e4c7284540318deb73e6f
ACR-38ef3d76efc942d19e61d54d6a4558a7
ACR-cd7d1f7ad204407d9c3b5f7dd17ce760
ACR-7122d4f969c3462b97a8722b9e11e5b7
ACR-50e56ed5821a438a968e9ed44d72fe84
ACR-70d7abd6c4da4275ad8c57ba566cf2f2
ACR-9069a60e39bd4a768d0332ce89a3a97c
ACR-85330b1885b54640bc1e0690cbe86654
ACR-7fee3583e1664993ab8ab499ac6b12b8
ACR-1c292eedbe5e46faba59227cfa7c0748
ACR-f19c5fc66e59426eb04db18ac7b852e3
ACR-cd8d392de22948f5bb3a97c7baf780c7
ACR-c6ba1aaa1c734633a28251df0359b6d2
 */
package org.sonarsource.sonarlint.core.serverapi.settings;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class SettingsApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  @Test
  void test_fetch_project_settings() {
    var valuesBuilder = Settings.FieldValues.Value.newBuilder();
    valuesBuilder.putValue("filepattern", "**/*.xml");
    valuesBuilder.putValue("rulepattern", "*:S12345");
    var value1 = valuesBuilder.build();
    valuesBuilder.clear();
    valuesBuilder.putValue("filepattern", "**/*.java");
    valuesBuilder.putValue("rulepattern", "*:S456");
    var value2 = valuesBuilder.build();

    var response = Settings.ValuesWsResponse.newBuilder()
      .addSettings(Settings.Setting.newBuilder()
        .setKey("sonar.inclusions")
        .setValues(Settings.Values.newBuilder().addValues("**/*.java")))
      .addSettings(Settings.Setting.newBuilder()
        .setKey("sonar.java.fileSuffixes")
        .setValue("*.java"))
      .addSettings(Settings.Setting.newBuilder()
        .setKey("sonar.issue.exclusions.multicriteria")
        .setFieldValues(Settings.FieldValues.newBuilder().addFieldValues(value1).addFieldValues(value2)).build())
      .build();
    mockServer.addProtobufResponse("/api/settings/values.protobuf?component=foo", response);

    var projectSettings = new SettingsApi(mockServer.serverApiHelper()).getProjectSettings("foo", new SonarLintCancelMonitor());

    assertThat(projectSettings).containsOnly(
      entry("sonar.inclusions", "**/*.java"),
      entry("sonar.java.fileSuffixes", "*.java"),
      entry("sonar.issue.exclusions.multicriteria", "1,2"),
      entry("sonar.issue.exclusions.multicriteria.1.filepattern", "**/*.xml"),
      entry("sonar.issue.exclusions.multicriteria.1.rulepattern", "*:S12345"),
      entry("sonar.issue.exclusions.multicriteria.2.filepattern", "**/*.java"),
      entry("sonar.issue.exclusions.multicriteria.2.rulepattern", "*:S456"));
  }

  @Test
  void test_fetch_global_setting() {
    var response = Settings.ValuesWsResponse.newBuilder()
      .addSettings(Settings.Setting.newBuilder()
        .setKey("sonar.multi-quality-mode.enabled")
        .setValue("true"))
      .addSettings(Settings.Setting.newBuilder()
        .setKey("fake.property")
        .setValue("false"))
      .build();
    mockServer.addProtobufResponse("/api/settings/values.protobuf", response);

    var globalSettings = new SettingsApi(mockServer.serverApiHelper()).getGlobalSettings(new SonarLintCancelMonitor());

    assertThat(globalSettings).isEqualTo(Map.of("sonar.multi-quality-mode.enabled", "true", "fake.property", "false"));
  }
}
