/*
ACR-6dc618b65a524e26a771ae913f653e31
ACR-581190d3bd6347f4bd9cc46cd63dc168
ACR-4517507189a049499c06e5a5e1d24ccc
ACR-f51253c3b5d44226aba78b7a84f86f15
ACR-8aa273f42b3242278d4066077c750f52
ACR-625990651d764d5886d8c143e6108cff
ACR-8f4ab8670ac3464c9687a3ec720b03f5
ACR-dfe2efab78454fc296a6886165eb4035
ACR-1e4f5a3d479746548fd00a658d1a2788
ACR-8f6bfebd05734d4b9ecc10beb4a1e1d0
ACR-19c1f3f9ff2443b4b529ce5a16f33d1c
ACR-6b07f249873b472b887c6b1805c7edf1
ACR-834e0065896a4691826497ad9c54af25
ACR-10ec1aab8e4d4544b73d109a84f8766d
ACR-385eba0ae1524c5eb4e9e707888bb2a9
ACR-91ee53c6a7204f2e8f127eab2e9f0c1e
ACR-d778f9be5e0d47faad5b477ac450a53f
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
