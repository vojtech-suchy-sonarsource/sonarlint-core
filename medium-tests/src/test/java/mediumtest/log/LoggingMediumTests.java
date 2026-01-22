/*
ACR-786222b95b7f4df9af86b970e002b4c3
ACR-7613e74ea0924c7aa802d286955e3c91
ACR-4481360aac37429e8b3488f5374e6c48
ACR-16b33d0a83fb4756b8b5b24b20312cee
ACR-ec316de5b7244f048d4fd109402ecabd
ACR-0ad1b89d06a546da9e67e71dec4b1981
ACR-f48b81ae64f44a9fab5041579a16d0c3
ACR-688dcd46ebe44f4ea8805f7a53e41cc9
ACR-fc46ef95037b4370b92d5f845c9b60bb
ACR-08da9f367c1b46d398279baee387b480
ACR-162910eef12f45f7bdc27fbcf7b49b90
ACR-095095f93d9a42b69e84d8e2af62f536
ACR-8b491ac0d2344e8a9a223a2f0710df8d
ACR-5dc8aa7cfd0a468581e6ba2c89c92065
ACR-45e8b76195c14e2a91b98b88b7c04cff
ACR-e97b6071d6f744f086cc06c6d85af990
ACR-d7f8d92a0c9241288f633e2965b40517
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
