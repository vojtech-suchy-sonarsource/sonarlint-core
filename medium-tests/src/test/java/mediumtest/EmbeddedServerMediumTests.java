/*
ACR-0295b93891b64394a51c2a44b84f9b05
ACR-989e688913cf46e59948562371fe1a03
ACR-ec36dd5b51fa4293b910395864e071b6
ACR-cc3db1edd4864678b2685fd09ccf5d54
ACR-a8c59ec093f94afd8f04f4ff441b65fe
ACR-a0da7402a26746ee87b4f084281722fc
ACR-be52c5b3b8514ac6b423d741e9e80b2e
ACR-7d49a73fdaaa42478d68a8800b76d19a
ACR-e2eee95e48de42239074b117918e80c0
ACR-5c9f06c84c154000a5c4c747f48851e3
ACR-60509ab3787b405e99370d4adaeaa119
ACR-e2e3d5614966474baf27afac911b1f63
ACR-456f4adf8b7b4e1786c08ef7385511f9
ACR-c8b0ee6329694a378120f4b48541770e
ACR-8a17240fe2804696852e2d658ae8f32d
ACR-bbffd014947c4b75bab98bc6fe825c7a
ACR-878c28f521b743deb1cdc4142248de74
 */
package mediumtest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import org.eclipse.jetty.http.HttpStatus;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.common.Strings.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.EMBEDDED_SERVER;

class EmbeddedServerMediumTests {

  @SonarLintTest
  void it_should_return_the_ide_name_and_empty_description_if_the_origin_is_not_trusted(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").start(fakeClient);

    var embeddedServerPort = backend.getEmbeddedServerPort();
    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + embeddedServerPort + "/sonarlint/api/status"))
      .header("Origin", "https://untrusted")
      .GET().build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response)
      .extracting(HttpResponse::statusCode, HttpResponse::body)
      .containsExactly(HttpStatus.OK_200, "{\"ideName\":\"ClientName\",\"description\":\"\",\"needsToken\":true,\"capabilities\":{\"canOpenFixSuggestion\":true}}");
    assertCspResponseHeader(response, embeddedServerPort);
  }

  @SonarLintTest
  void it_should_not_trust_origin_having_known_connection_prefix(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getClientLiveDescription()).thenReturn("WorkspaceTitle");

    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarQubeConnection("connectionId", "https://sonar.my").start(fakeClient);

    var embeddedServerPort = backend.getEmbeddedServerPort();
    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + embeddedServerPort + "/sonarlint/api/status"))
      .header("Origin", "https://sonar")
      .GET().build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response)
      .extracting(HttpResponse::statusCode, HttpResponse::body)
      .containsExactly(HttpStatus.OK_200, "{\"ideName\":\"ClientName\",\"description\":\"\",\"needsToken\":true,\"capabilities\":{\"canOpenFixSuggestion\":true}}");
    assertCspResponseHeader(response, embeddedServerPort);
  }

  @SonarLintTest
  void it_should_return_the_ide_name_and_full_description_if_the_origin_is_trusted(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getClientLiveDescription()).thenReturn("WorkspaceTitle");

    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarQubeConnection("connectionId", "https://sonar.my").start(fakeClient);

    var embeddedServerPort = backend.getEmbeddedServerPort();
    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + embeddedServerPort + "/sonarlint/api/status"))
      .header("Origin", "https://sonar.my")
      .GET().build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response)
      .extracting(HttpResponse::statusCode, HttpResponse::body)
      .containsExactly(HttpStatus.OK_200, "{\"ideName\":\"ClientName\",\"description\":\"WorkspaceTitle\",\"needsToken\":false,\"capabilities\":{\"canOpenFixSuggestion\":true}}");
    assertCspResponseHeader(response, embeddedServerPort);
  }

  private void assertCspResponseHeader(HttpResponse<String> response, int embeddedServerPort) {
    assertThat(response.headers().map().get("Content-Security-Policy-Report-Only"))
      .contains("connect-src 'self' http://localhost:" + embeddedServerPort + ";");
  }

  @SonarLintTest
  void it_should_set_preflight_response_accordingly_when_receiving_preflight_request(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getClientLiveDescription()).thenReturn("WorkspaceTitle");

    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarQubeConnection("connectionId", "http://sonar.my").start(fakeClient);

    var request = HttpRequest.newBuilder()
      .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
      .uri(URI.create("http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/token"))
      .header("Origin", "http://sonar.my")
      .build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.headers().map())
      .extracting("access-control-allow-methods", "access-control-allow-origin", "access-control-allow-private-network")
      .containsExactly(List.of("GET, POST, OPTIONS"), List.of("http://sonar.my"), List.of("true"));
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
    assertThat(response.headers().map()).doesNotContainKey("Content-Security-Policy-Report-Only");
  }

  @SonarLintTest
  void it_should_receive_bad_request_response_if_not_right_method(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getClientLiveDescription()).thenReturn("WorkspaceTitle");

    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarQubeConnection("connectionId", "https://sonar.my").start(fakeClient);

    var embeddedServerPort = backend.getEmbeddedServerPort();
    var requestToken = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + embeddedServerPort + "/sonarlint/api/token"))
      .header("Origin", "https://sonar.my")
      .GET().build();
    var requestStatus = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + embeddedServerPort + "/sonarlint/api/status"))
      .header("Origin", "https://sonar.my")
      .DELETE().build();
    var responseToken = java.net.http.HttpClient.newHttpClient().send(requestToken, HttpResponse.BodyHandlers.ofString());
    var responseStatus = java.net.http.HttpClient.newHttpClient().send(requestStatus, HttpResponse.BodyHandlers.ofString());

    assertThat(responseToken.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST_400);
    assertThat(responseStatus.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST_400);
    assertThat(responseToken.headers().map()).doesNotContainKey("Content-Security-Policy-Report-Only");
    assertThat(responseStatus.headers().map()).doesNotContainKey("Content-Security-Policy-Report-Only");
  }

  @SonarLintTest
  void it_should_rate_limit_origin_if_too_many_requests(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").start(fakeClient);

    var embeddedServerPort = backend.getEmbeddedServerPort();
    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + embeddedServerPort + "/sonarlint/api/status"))
      .header("Origin", randomAlphabetic(10))
      .GET().build();
    for (int i = 0; i < 15; i++) {
      java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response)
      .extracting(HttpResponse::statusCode, HttpResponse::body)
      .containsExactly(HttpStatus.TOO_MANY_REQUESTS_429, "");
  }

  @SonarLintTest
  void it_should_not_allow_request_if_origin_is_missing(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").start(fakeClient);

    var embeddedServerPort = backend.getEmbeddedServerPort();
    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + embeddedServerPort + "/sonarlint/api/status"))
      .GET().build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response)
      .extracting(HttpResponse::statusCode, HttpResponse::body)
      .containsExactly(HttpStatus.BAD_REQUEST_400, "");
  }

  @SonarLintTest
  void it_should_not_rate_limit_over_time(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").start(fakeClient);

    var embeddedServerPort = backend.getEmbeddedServerPort();
    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + embeddedServerPort + "/sonarlint/api/status"))
      .header("Origin", randomAlphabetic(10))
      .GET().build();
    for (int i = 0; i < 15; i++) {
      java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
    await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
      var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
    });
  }

  @SonarLintTest
  void it_should_notify_client_when_started(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").start(fakeClient);

    await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
      var startedPort = fakeClient.getEmbeddedServerPort();
      assertThat(startedPort).isGreaterThan(0);
      assertThat(startedPort).isEqualTo(backend.getEmbeddedServerPort());
    });
  }

}
