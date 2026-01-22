/*
ACR-8f0d392454e9468c899164b3f8e96bfc
ACR-1f242f4ceb2f42be877246a145f904e9
ACR-24b8a7f4db7d41a79ba70a42646def33
ACR-e0bc6275f32c4b64b8d3f22468f7e0c2
ACR-070641ddfd5b46abacfc3123d9739a61
ACR-ba536e2ee37742e59aba725aa977d752
ACR-4490701f8beb4040861fce617a9d721b
ACR-1681611a34784b309bb83e08da976c68
ACR-94e715c158c54b61969154a2cab7663a
ACR-9143e7c0d5194b51a9cf85731af154e0
ACR-0bd637f4bfe24ce081b58831ad1d1e8f
ACR-6eb3c1a8b1da4566943502431a3e3a1c
ACR-bfd5bbd17176445d9421a11f541a842a
ACR-74af287dddbf4f329f38f0ed707931ff
ACR-8b75734277fa4b7a9a9a021e37e80d54
ACR-6d29c9801e43437f86f6838b5907c2ad
ACR-3ea0811829e9469a9ae82bc5d6bc8f50
 */
package mediumtest;

import com.google.gson.JsonArray;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.validate.ValidateConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import org.sonarsource.sonarlint.core.test.utils.server.ServerFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.EMBEDDED_SERVER;

class ConnectionSetupMediumTests {

  public static final String EXPECTED_MESSAGE = "UTM parameters should match regular expression: [a-z0-9\\-]+";

  @SonarLintTest
  void it_should_open_the_sonarlint_auth_url_for_sonarcloud(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    ServerFixture.Server scServer = harness.newFakeSonarCloudServer()
      .start();

    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarCloudConnection("connectionId").start(fakeClient);

    var futureResponse = backend.getConnectionService().helpGenerateUserToken(new HelpGenerateUserTokenParams(scServer.baseUrl()));

    verify(fakeClient, timeout(3000)).openUrlInBrowser(new URL(scServer.url("/sonarlint/auth?ideName=ClientName&port=" + backend.getEmbeddedServerPort())));

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/token"))
      .header("Content-Type", "application/json; charset=utf-8")
      .header("Origin", scServer.baseUrl())
      .POST(HttpRequest.BodyPublishers.ofString("{\"token\": \"value\"}")).build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(200);

    assertThat(futureResponse)
      .succeedsWithin(Duration.ofSeconds(3))
      .extracting(HelpGenerateUserTokenResponse::getToken)
      .isEqualTo("value");
  }

  @SonarLintTest
  void it_should_open_token_generation_url_for_sonarcloud_with_tracking(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    ServerFixture.Server scServer = harness.newFakeSonarCloudServer()
      .start();

    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarCloudConnection("connectionId").start(fakeClient);

    var futureResponse = backend.getConnectionService().helpGenerateUserToken(
      new HelpGenerateUserTokenParams(scServer.baseUrl(),
        new HelpGenerateUserTokenParams.Utm("referral", "sq-ide-product-name", "create-new-sqc-connection", "generate-token-2")));

    verify(fakeClient, timeout(3000)).openUrlInBrowser(
      new URL(scServer.url("/sonarlint/auth?ideName=ClientName&port=" + backend.getEmbeddedServerPort() +
        "&utm_medium=referral&utm_source=sq-ide-product-name&utm_content=create-new-sqc-connection&utm_term=generate-token-2")));

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/token"))
      .header("Content-Type", "application/json; charset=utf-8")
      .header("Origin", scServer.baseUrl())
      .POST(HttpRequest.BodyPublishers.ofString("{\"token\": \"value\"}")).build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(200);

    assertThat(futureResponse)
      .succeedsWithin(Duration.ofSeconds(3))
      .extracting(HelpGenerateUserTokenResponse::getToken)
      .isEqualTo("value");
  }

  @SonarLintTest
  void it_should_throw_invalid_parameters_for_invalid_utm_params(SonarLintTestHarness harness) {
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarCloudConnection("connectionId").start();

    var futureResponse = backend.getConnectionService().helpGenerateUserToken(
      new HelpGenerateUserTokenParams("irrelevant",
        new HelpGenerateUserTokenParams.Utm("referral", "sq-ide-product-name", "create-new-sqc-connection", "INVALID")));

    assertThat(futureResponse)
      .failsWithin(Duration.ofSeconds(3))
      .withThrowableOfType(ExecutionException.class)
      .withCauseInstanceOf(ResponseErrorException.class)
      .havingCause()
      .withMessage(EXPECTED_MESSAGE)
      .extracting("responseError.message", "responseError.code", "responseError.data")
      .containsOnly(EXPECTED_MESSAGE, ResponseErrorCode.InvalidParams.getValue(), utmArray());
  }

  @NotNull
  private static JsonArray utmArray() {
    JsonArray arrayOfInvalidParameters = new JsonArray();
    arrayOfInvalidParameters.add("utm_term");
    return arrayOfInvalidParameters;
  }

  @SonarLintTest
  void it_should_open_the_sonarlint_auth_url_for_sonarqube_9_7_plus(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var server = harness.newFakeSonarQubeServer("9.9").start();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarQubeConnection("connectionId", server).start(fakeClient);

    var futureResponse = backend.getConnectionService().helpGenerateUserToken(new HelpGenerateUserTokenParams(server.baseUrl()));

    verify(fakeClient, timeout(3000)).openUrlInBrowser(new URL(server.url("/sonarlint/auth?ideName=ClientName&port=" + backend.getEmbeddedServerPort())));

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/token"))
      .header("Content-Type", "application/json; charset=utf-8")
      .header("Origin", server.baseUrl())
      .POST(HttpRequest.BodyPublishers.ofString("{\"token\": \"value\"}")).build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(200);

    assertThat(futureResponse)
      .succeedsWithin(Duration.ofSeconds(3))
      .extracting(HelpGenerateUserTokenResponse::getToken)
      .isEqualTo("value");
  }

  @SonarLintTest
  void it_should_reject_tokens_from_missing_origin(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var server = harness.newFakeSonarQubeServer("9.9").start();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarQubeConnection("connectionId", server).start(fakeClient);

    backend.getConnectionService().helpGenerateUserToken(new HelpGenerateUserTokenParams(server.baseUrl()));

    verify(fakeClient, timeout(3000)).openUrlInBrowser(new URL(server.url("/sonarlint/auth?ideName=ClientName&port=" + backend.getEmbeddedServerPort())));

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/token"))
      .header("Content-Type", "application/json; charset=utf-8")
      .POST(HttpRequest.BodyPublishers.ofString("{\"token\": \"value\"}")).build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(400);
  }

  @SonarLintTest
  void it_should_reject_tokens_from_unexpected_origin(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var server = harness.newFakeSonarQubeServer("9.9").start();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).withClientName("ClientName").withSonarQubeConnection("connectionId", server).start(fakeClient);

    backend.getConnectionService().helpGenerateUserToken(new HelpGenerateUserTokenParams(server.baseUrl()));

    verify(fakeClient, timeout(3000)).openUrlInBrowser(new URL(server.url("/sonarlint/auth?ideName=ClientName&port=" + backend.getEmbeddedServerPort())));

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/token"))
      .header("Content-Type", "application/json; charset=utf-8")
      .header("Origin", "https://unexpected.sonar")
      .POST(HttpRequest.BodyPublishers.ofString("{\"token\": \"value\"}")).build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(403);
  }

  @SonarLintTest
  void it_should_open_the_sonarlint_auth_url_without_port_for_sonarqube_9_7_plus_when_server_is_not_started(SonarLintTestHarness harness) throws MalformedURLException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().withClientName("ClientName").start(fakeClient);
    var server = harness.newFakeSonarQubeServer("9.9").start();

    var futureResponse = backend.getConnectionService().helpGenerateUserToken(new HelpGenerateUserTokenParams(server.baseUrl()));

    assertThat(futureResponse)
      .succeedsWithin(Duration.ofSeconds(3))
      .extracting(HelpGenerateUserTokenResponse::getToken)
      .isNull();
    verify(fakeClient, timeout(3000)).openUrlInBrowser(new URL(server.url("/sonarlint/auth?ideName=ClientName")));
  }

  @SonarLintTest
  void it_should_reject_incoming_user_token_with_wrong_http_method(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).start(fakeClient);

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/token"))
      .GET().build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(400);
  }

  @SonarLintTest
  void it_should_reject_incoming_user_token_with_wrong_body(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().withBackendCapability(EMBEDDED_SERVER).start(fakeClient);

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/token"))
      .header("Content-Type", "application/json; charset=utf-8")
      .POST(HttpRequest.BodyPublishers.ofString("{\"token\":")).build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(400);
  }

  @SonarLintTest
  void it_should_fail_to_validate_connection_if_host_not_found(SonarLintTestHarness harness) throws InterruptedException, ExecutionException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend().start(fakeClient);

    var connectionResponse = backend.getConnectionService()
      .validateConnection(new ValidateConnectionParams(new TransientSonarQubeConnectionDto("http://notexists", Either.forRight(new UsernamePasswordDto("foo", "bar"))))).get();

    assertThat(connectionResponse.isSuccess()).isFalse();
    assertThat(connectionResponse.getMessage()).contains("Request failed");
  }
}
