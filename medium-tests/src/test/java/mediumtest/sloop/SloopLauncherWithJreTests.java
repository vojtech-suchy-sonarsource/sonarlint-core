/*
ACR-b524eeda7f9d440580bc14914471f501
ACR-9541df2c3b2c48b3a6d64a22326f244d
ACR-34300b88a94440aeb8d02934be011c0b
ACR-91e741acda4b43829098bea2e83ebdcd
ACR-9b266eecfa294dfbbe6ae0a5b4f541f5
ACR-4095fddd2a94451e9677fd0c9d86ebc5
ACR-9f920389beba454783974790ac0a6d63
ACR-1b50d61399e1455290ab4d9cdd781b91
ACR-12b5cd8f086149e5a167b870295af617
ACR-1634871e821e414ab78cb80f38fda7d0
ACR-ba3dd331cbf44977891b306ac44f28f5
ACR-8ab46ba8caa548ca861107e43fd9e4af
ACR-59f0f31290264def95a47c3d3db3eb6d
ACR-2d296029e69e4d10ba420e967a4d9d50
ACR-1683cd763d8d4d63b5591c022c7cdceb
ACR-2b074e1437954cb7863108c721c8691e
ACR-8e196bb86697417abae81e8763fc8ad2
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
