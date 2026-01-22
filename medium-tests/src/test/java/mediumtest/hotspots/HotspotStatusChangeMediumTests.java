/*
ACR-1a0ec6fa8324487d8df00bb6ab95d473
ACR-c4d552bd627a4c07a9051a429daad134
ACR-29067d615e954569adb6c11650133e8c
ACR-899ebf88ceb44b41af632613a0ef4856
ACR-6a023aed0b0b472a863a0bdfd91abc57
ACR-2f11025a34674ffe863160edad4211b3
ACR-2a4e6cfe935247d7bded4ada01dbe91b
ACR-d482ae37074746aeac3a844afbb29d36
ACR-d64945e0ca4647ef8e5cbda494e226b0
ACR-507b91e6d0424335992c7fbfafdd0c3b
ACR-19514aa7a22d45fda839b23bc1b772b4
ACR-e2d911b891df4f09bc4032329969c1b2
ACR-11544c1dbdbe4d4d8569acbc5f399e89
ACR-f071fd70ae94440593a6c4c2ac51218c
ACR-4d4bcae1d9694d659597fc11c443952c
ACR-12e87aafb3c741bbb3d9abce3fea2d57
ACR-0d86482f69b34b729b0f318cfcee93f4
 */
package mediumtest.hotspots;

import com.github.tomakehurst.wiremock.client.WireMock;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.junit.jupiter.api.Disabled;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.ChangeHotspotStatusParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;
import static org.sonarsource.sonarlint.core.test.utils.server.ServerFixture.ServerStatus.DOWN;

class HotspotStatusChangeMediumTests {

  @SonarLintTest
  void it_should_fail_the_future_when_the_server_returns_an_error(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().withStatus(DOWN).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    var response = setStatusToSafe(backend, "configScopeId", "hotspotKey");

    assertThat(response)
      .failsWithin(Duration.ofSeconds(2))
      .withThrowableOfType(ExecutionException.class)
      .havingCause()
      .isInstanceOf(ResponseErrorException.class);
  }

  @SonarLintTest
  void it_should_do_nothing_when_the_configuration_scope_is_unknown(SonarLintTestHarness harness) {
    var backend = harness.newBackend().start();

    var response = setStatusToSafe(backend, "configScopeId", "hotspotKey");

    assertThat(response).succeedsWithin(Duration.ofSeconds(2));
  }

  @SonarLintTest
  void it_should_do_nothing_when_the_configuration_scope_bound_connection_is_unknown(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    var response = setStatusToSafe(backend, "configScopeId", "hotspotKey");

    assertThat(response).succeedsWithin(Duration.ofSeconds(2));
  }

  @SonarLintTest
  void it_should_update_the_status_on_sonarcloud_through_the_web_api(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarCloudServer().start();

    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .withSonarCloudConnection("connectionId", "orgKey")
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    var response = setStatusToSafe(backend, "configScopeId", "hotspotKey");

    assertThat(response).succeedsWithin(Duration.ofSeconds(2));
    server.getMockServer()
      .verify(WireMock.postRequestedFor(urlEqualTo("/api/hotspots/change_status"))
        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
        .withRequestBody(equalTo("hotspot=hotspotKey&status=REVIEWED&resolution=SAFE")));
  }

  @SonarLintTest
  void it_should_update_the_status_on_sonarqube_through_the_web_api(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    var response = setStatusToSafe(backend, "configScopeId", "hotspotKey");

    assertThat(response).succeedsWithin(Duration.ofSeconds(2));
    waitAtMost(2, SECONDS).untilAsserted(() -> server.getMockServer()
      .verify(WireMock.postRequestedFor(urlEqualTo("/api/hotspots/change_status"))
        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
        .withRequestBody(equalTo("hotspot=hotspotKey&status=REVIEWED&resolution=SAFE"))));
  }

  @SonarLintTest
  @Disabled("TODO")
  void it_should_update_the_hotspot_status_in_the_storage(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    var response = setStatusToSafe(backend, "configScopeId", "hotspotKey");

    assertThat(response).succeedsWithin(Duration.ofSeconds(2));
    server.getMockServer()
      .verify(WireMock.postRequestedFor(urlEqualTo("/api/hotspots/change_status"))
        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
        .withRequestBody(equalTo("hotspot=hotspotKey&status=REVIEWED&resolution=SAFE")));
  }

  @SonarLintTest
  void it_should_count_status_change_in_telemetry(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withTelemetryEnabled()
      .start();

    var response = setStatusToSafe(backend, "configScopeId", "hotspotKey");

    assertThat(response).succeedsWithin(Duration.ofSeconds(2));
    assertThat(backend.telemetryFileContent().hotspotStatusChangedCount()).isEqualTo(1);
  }

  private CompletableFuture<Void> setStatusToSafe(SonarLintTestRpcServer backend, String configScopeId, String hotspotKey) {
    return backend.getHotspotService().changeStatus(new ChangeHotspotStatusParams(configScopeId, hotspotKey, HotspotStatus.SAFE));
  }
}
