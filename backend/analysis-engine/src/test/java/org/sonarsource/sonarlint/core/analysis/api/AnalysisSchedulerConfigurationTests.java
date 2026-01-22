/*
ACR-f4780c87977a4fa6bbe911d89180fba3
ACR-4c3fc4c531cd4108b25420d0a4cb80a6
ACR-d6a0a2a99411470097709c634ab792db
ACR-cd906f5a1fe94f36b6dfcf525397006a
ACR-55b58c895b6546a28ba9562ea1ccabbc
ACR-89346604f1164e5abcd368c3286df067
ACR-4667677827ff4df2854440907254a093
ACR-1ccdb6f7f5de4d98b2768c3a0a9f35cb
ACR-23f4212ba2554119957ffff49d42d863
ACR-76c9037f8f514281a369fbf9dc1248b8
ACR-a62aae36185d4ef092bf32cee073b5be
ACR-9726881a758f4f0aa346a835eedd5445
ACR-0752db7f4612499583e60780df785ea5
ACR-acf9d9561d6c4d6bb73b36b2c3264b7b
ACR-fab03620d5f64e099242b34b8a04bf6a
ACR-9c2187a5f9814eb0a8bd313121fab1c1
ACR-0ee20d1515b4498ba20e4b7c3fc23682
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
