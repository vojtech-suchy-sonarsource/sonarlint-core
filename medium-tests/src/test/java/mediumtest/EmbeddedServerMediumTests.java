/*
ACR-3c154e07d7054de1b43e7e23d7cfcd96
ACR-35ed8faea5d7462a9010a415ec2a9782
ACR-218dd7a7a9ed4a659368524d2a7fba87
ACR-8458872bf8314ffe919e5c7a1e2553b4
ACR-9864ef9c03ca49269fea19d49650f27d
ACR-281cd733929b46a6b7e89dc1c505a6f4
ACR-14e8eddb4d814120b35f46e2864e0af8
ACR-02b80ee52d014e5d9bcd7bfa119fa46c
ACR-ae00db4795e040389e16d138da650fbc
ACR-a9c775173cfd406f88c2af010a228144
ACR-1a5cc989d21c4a4c869539a799ba49b5
ACR-13544ddf3c6047c2997e4f8d414c5e69
ACR-e909c12aea9b4f5982b5390144f7828a
ACR-5c5a8a227ffd4aab89dad7c8a824c77e
ACR-439110abde1446879b52e36780d8aaf5
ACR-6ad4d14017e04472babac840e51591de
ACR-1804e132386f4f22bc13c7f6781979aa
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
