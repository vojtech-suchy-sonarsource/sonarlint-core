/*
ACR-404b43f92ccb48cf83601dab70569b05
ACR-11f2966f95aa495bb8b6ccf3f90bda08
ACR-01571ffcac7848489557e5689c5af16d
ACR-c600615b65604230a822086186660274
ACR-16a2a49a99f54fd8933773963b6f9c6e
ACR-1b5442f97aed4e7a83580fe766037870
ACR-57801a378adc40a9aa8f4e51ba2038bd
ACR-26bcb45434c841839a8fcdb8a80ef7eb
ACR-cdce82f2215d4ba9957fc280d106c537
ACR-d2f98f48d6dc4428a57dc6d6aa7f4a90
ACR-3e69bdb1de6244a8a2f19c54609ad8c3
ACR-384931c142af4267a72da1d04ef0c919
ACR-d7659156c73748ab981248335b559430
ACR-faa60c13072e4100a6ec849b187f15b0
ACR-e00687f31e3d47fb9f0518e4e39cae40
ACR-9acd8a14e7e4417f82ed4c7dcf192712
ACR-d8cb8f5f47874da785df68150d6af8c5
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.analysis.AnalysisScheduler;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.plugin.commons.PluginsLoader;

import static java.nio.file.Files.createDirectory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AnalysisSchedulerConfigurationTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void testDefaults() {
    var config = AnalysisSchedulerConfiguration.builder()
      .build();
    assertThat(config.getWorkDir()).isNull();
    assertThat(config.getEffectiveSettings()).isEmpty();
    assertThat(config.getClientPid()).isZero();
  }

  @Test
  void extraProps() {
    Map<String, String> extraProperties = new HashMap<>();
    extraProperties.put("foo", "bar");
    var config = AnalysisSchedulerConfiguration.builder()
      .setExtraProperties(extraProperties)
      .build();
    assertThat(config.getEffectiveSettings()).containsOnly(entry("foo", "bar"));
  }

  @Test
  void effectiveConfig_should_add_nodejs() {
    Map<String, String> extraProperties = new HashMap<>();
    extraProperties.put("foo", "bar");
    var config = AnalysisSchedulerConfiguration.builder()
      .setExtraProperties(extraProperties)
      .setNodeJs(Paths.get("nodejsPath"))
      .build();
    assertThat(config.getEffectiveSettings()).containsOnly(entry("foo", "bar"), entry("sonar.nodejs.executable", "nodejsPath"));
  }

  @Test
  void overrideDirs(@TempDir Path temp) throws Exception {
    var work = createDirectory(temp.resolve("work"));
    var config = AnalysisSchedulerConfiguration.builder()
      .setWorkDir(work)
      .build();
    assertThat(config.getWorkDir()).isEqualTo(work);
  }

  @Test
  void providePid() {
    var config = AnalysisSchedulerConfiguration.builder().setClientPid(123).build();
    assertThat(config.getClientPid()).isEqualTo(123);
  }

  @Test
  void should_not_fail_if_module_supplier_is_not_provided(@TempDir Path workDir) {
    assertDoesNotThrow(() -> {
      var analysisGlobalConfig = AnalysisSchedulerConfiguration.builder().setClientPid(1234L).setWorkDir(workDir).build();
      var result = new PluginsLoader().load(new PluginsLoader.Configuration(Set.of(), Set.of(), false, Optional.empty()), Set.of());
      new AnalysisScheduler(analysisGlobalConfig, result.getLoadedPlugins(), logTester.getLogOutput());
    });
  }
}
