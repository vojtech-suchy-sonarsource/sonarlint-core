/*
ACR-55e3a82c713d438686ea955db70d71c9
ACR-52020bbc26e0406ba9e2017279bc0f57
ACR-6632337ad9fc451da424aa2cc588ac1b
ACR-c21cd78182aa4b9aa916d19c02e6c745
ACR-8741d58a482b44159374ff2f1889bcf8
ACR-9699014e97ff46ed9435cae96f27713f
ACR-bc847cf6586d4495bf5f5263c5cf6ea3
ACR-5dd6e05ee7aa4d6695b56b8ebf5d473c
ACR-a19aa1f7a1734fa9a5b130c6235b6716
ACR-bcb192e14a864e398ae946bd5794e366
ACR-6f93b772ac214c65b19b53db61aab7f3
ACR-fb34f11fa6234486badc7afbe4b67db1
ACR-f57eb141fd69434189d5b89752e8c9c1
ACR-ede8c9a57cd342d8aac5bda83ed5fdbb
ACR-3d259784c7c546d49096f15b64c995c5
ACR-7d65ea407e084e56987fc124a842aa4b
ACR-7b44582828aa464fbe83374fedc63710
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.utils.System2;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalSettingsTests {

  @RegisterExtension
  SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void emptyNodePathPropertyForSonarJS() {
    var underTest = new GlobalSettings(AnalysisSchedulerConfiguration.builder().build(), new PropertyDefinitions(System2.INSTANCE));

    var nodeJsExecutableValue = underTest.getString("sonar.nodejs.executable");

    assertThat(nodeJsExecutableValue).isNull();
  }

  @Test
  void customNodePathPropertyForSonarJS() {
    var providedNodePath = Paths.get("foo/bar/node");
    var underTest = new GlobalSettings(AnalysisSchedulerConfiguration.builder().setNodeJs(providedNodePath).build(),
      new PropertyDefinitions(System2.INSTANCE));

    var nodeJsExecutableValue = underTest.getString("sonar.nodejs.executable");

    assertThat(nodeJsExecutableValue).isEqualTo(providedNodePath.toString());
  }

}
