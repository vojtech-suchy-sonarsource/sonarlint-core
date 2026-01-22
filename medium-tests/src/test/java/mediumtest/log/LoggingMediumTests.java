/*
ACR-07fc6f0a46ab4797855d362c998e525d
ACR-79a95ffd2f794dc99c1801a7f256f05f
ACR-4cf9a3e8c0314ad5af03b7ca75a10c69
ACR-39a0b48377e44827bb7828f333613c8c
ACR-4ff2b129bd0f40a6b2a7b19afbc1779a
ACR-d3bc6ecf01534fb49f4bbf37713d61a7
ACR-82ae849776694602812cfd2e38352cf7
ACR-ddc8dfca25fa4c9da740525c4e1d5180
ACR-7206d8807a9e4370a93470bd1bb29dee
ACR-adbc8d9409564c88a7b8835dd54b283f
ACR-539abd4f7b9144b79cefa3b8d861d5e2
ACR-297a1acee528494299ed883cfc6ffd3c
ACR-308e0cebacd7479b9f887345ba1bc5d1
ACR-4a8f71c8cabd4cfd87a796c432088b9d
ACR-1905ba2c74b24c6a928c09eb155f1cf0
ACR-637626cda8254063a911ccccc608117b
ACR-e637acef12b349bf804bdb4ad8f24e83
 */
package mediumtest.log;

import java.time.Duration;
import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.SetLogLevelParams;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class LoggingMediumTests {

  @SonarLintTest
  void it_should_print_a_debug_log_when_level_allows(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withLogLevel(LogLevel.TRACE)
      .start(fakeClient);

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(new ConfigurationScopeDto("id", null, true, "name", null))));

    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("Added configuration scope 'id'"));
  }

  @SonarLintTest
  void it_should_not_print_a_log_when_level_does_not_allow(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withLogLevel(LogLevel.OFF)
      .start(fakeClient);

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(new ConfigurationScopeDto("id", null, true, "name", null))));

    await().during(Duration.ofSeconds(1)).untilAsserted(() -> assertThat(fakeClient.getLogMessages()).isEmpty());
  }

  @SonarLintTest
  void it_should_adjust_the_logging_after_initialization(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withLogLevel(LogLevel.DEBUG)
      .start(fakeClient);
    backend.getLogService().setLogLevel(new SetLogLevelParams(LogLevel.OFF));
    fakeClient.clearLogs();

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(new ConfigurationScopeDto("id", null, true, "name", null))));

    await().during(Duration.ofSeconds(1)).untilAsserted(() -> assertThat(fakeClient.getLogMessages()).isEmpty());
  }

}
