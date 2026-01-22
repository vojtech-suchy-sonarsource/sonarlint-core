/*
ACR-af9a953378434e04b1cd28a28e0fee76
ACR-d25c89e23d5b430b9cfd0b63a321709f
ACR-4af21e7f26c84edd9eab38b9c5e97e04
ACR-1dc516db8d1d404ba5fbb0b344097562
ACR-0250fa5885b84fba82c8b162517bd7e0
ACR-d9bf53f383e243f59787cdc7e172a2d5
ACR-109ea05211324e14976a8bea1d8cb5b5
ACR-8785d15d7946423997fe1e1655fa1fc3
ACR-73bc5128d48e4f93b52c43be18725bc5
ACR-5584663cc77440bfac7aeac561a7843d
ACR-f648144725254936a663aac2b8040af0
ACR-f5b4f0e11ccd4240948d2a625aaf8644
ACR-a554cdd371b342b0a3e6884779807e76
ACR-f3b894b2b05840d68b0cd2ae6e74dabb
ACR-7e92bd2516234d9690471a6546e16385
ACR-101ccbf0aafb498a893188a0f99c6218
ACR-b3e1ff85b5434a7fa059853b50dd1c84
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
