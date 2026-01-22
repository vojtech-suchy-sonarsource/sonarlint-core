/*
ACR-1794fbc14ad04f6b9c57e801b664f2b3
ACR-0c678d66fe4e4f79a6e890cea944f356
ACR-b97e4e81ba54456b8a998eb499adad8e
ACR-02fd81893f8b490598298269f02a8992
ACR-87f6c3e20b0d4aa684e2577f7455b5cf
ACR-8b2172a803804e7cbccec9bf245855c3
ACR-d84b8733ee6e4234aaff6ad10cbb4bc5
ACR-90bed9445d4744d796ce4953cb818af1
ACR-2fd3a312a16141c0939ce2f6127e38fe
ACR-f9af4fdbb7fc4ab4bf68d570d713fd59
ACR-ad337b9fd63f4b519007d5fd135f91c9
ACR-c0b6cfb130334307ac31c72654a30f5f
ACR-e2ecad6667984dc1952d487f6055d2c0
ACR-4c24266d36c645da964d61677eb8fe18
ACR-f6cad154903d467480727ba79c78d2fa
ACR-b9dacd2e64814aa290f8c0f20af063fb
ACR-59b21afe9c8b4735bf9b0b95d497a73b
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
