/*
ACR-218c9a23178c45fbb63a9c64fdbc8763
ACR-afbf6b3bb64349d58f9ec6d48799e5c1
ACR-c3f1c05778204384bab9f97a66ceb736
ACR-56fd5c9f8b654e84adcee66a34074e7d
ACR-5430b0cf50894caf90c8f4c9d62ad49c
ACR-57aebd02bb6143de957bcbabf1543fc9
ACR-089cf58fc45149448b42e741cc0ac79e
ACR-7d85ddbe96214798923c67d79bd1273e
ACR-c971ff6e09b34f3fa97678c0d38e1da2
ACR-1af25a3e1994464d9d6990de5a79506f
ACR-f2b61d9b5d614b909d6abc4df3f38a19
ACR-c2b19cd29bb04fbfa1bf3acad2fbd03f
ACR-544f266878d94f16a68b59631bee048f
ACR-3193bab3fab14a4aac18106909a1b20e
ACR-b4e96072e8e146ef96840c9403877418
ACR-f87c6905b35a4174b51c15894500c73e
ACR-4b6292536f074ed5ab5b78db9f155f11
 */
package mediumtest.sloop;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.client.Sloop;
import org.sonarsource.sonarlint.core.rpc.client.SloopLauncher;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.ClientConstantInfoDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import utils.PluginLocator;

import static mediumtest.sloop.UnArchiveUtils.unarchiveDistribution;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.PHP;

class SloopLauncherWithJreTests {
  @TempDir
  private static Path sonarUserHome;

  @TempDir
  private static Path unarchiveTmpDir;

  private static Sloop sloop;
  private static SonarLintRpcServer server;

  private static SloopLauncherTests.DummySonarLintRpcClient client;

  @BeforeAll
  static void setup() {
    var sloopDistPath = SystemUtils.IS_OS_WINDOWS ? SloopDistLocator.getWindowsDistPath() : SloopDistLocator.getLinux64DistPath();
    var jrePath = SystemUtils.IS_OS_WINDOWS ? JreLocator.getWindowsJrePath() : JreLocator.getLinuxJrePath();
    var sloopOutDirPath = unarchiveSloop(sloopDistPath);
    client = new SloopLauncherTests.DummySonarLintRpcClient();
    var sloopLauncher = new SloopLauncher(client);
    sloop = sloopLauncher.start(sloopOutDirPath.toAbsolutePath(), jrePath.toAbsolutePath());
    server = sloop.getRpcServer();
  }

  @AfterAll
  static void tearDown() throws ExecutionException, InterruptedException {
    sloop.shutdown().get();
    var exitCode = sloop.onExit().join();
    assertThat(exitCode).isZero();
  }

  @Test
  void test_all_rules_returns() {
    var telemetryInitDto = new TelemetryClientConstantAttributesDto("SonarLint ITs", "SonarLint ITs",
      "1.2.3", "4.5.6", Collections.emptyMap());
    var clientInfo = new ClientConstantInfoDto("clientName", "integrationTests");

    server.initialize(new InitializeParams(clientInfo, telemetryInitDto, HttpConfigurationDto.defaultConfig(), null, Set.of(), sonarUserHome.resolve("storage"), sonarUserHome.resolve("workDir"),
    Set.of(PluginLocator.getPhpPluginPath().toAbsolutePath()), Collections.emptyMap(), Set.of(PHP), Collections.emptySet(), Collections.emptySet(), Collections.emptyList(),
      Collections.emptyList(), sonarUserHome.toString(), Map.of(), false, null, false, null)).join();

    var result = server.getRulesService().listAllStandaloneRulesDefinitions().join();
    assertThat(result.getRulesByKey()).hasSize(222);
    var expectedJreLog = "Using JRE from " + (SystemUtils.IS_OS_WINDOWS ? JreLocator.getWindowsJrePath() : JreLocator.getLinuxJrePath());
    assertThat(client.getLogs()).extracting(LogParams::getMessage).contains(expectedJreLog);
  }

  @NotNull
  private static Path unarchiveSloop(Path sloopDistPath) {
    var sloopOutDirPath = unarchiveTmpDir.resolve("sloopDistOut");
    unarchiveDistribution(sloopDistPath.toString(), sloopOutDirPath);
    return sloopOutDirPath;
  }
}
