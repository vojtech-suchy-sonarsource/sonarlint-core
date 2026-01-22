/*
ACR-c1dbc35b3964456182b0051b6cd390d4
ACR-01547903ad2c424c9b04ebf5ec2c51c5
ACR-aa99a75675a445c194f9390d6812b390
ACR-13c570f74f274c98bf64805858a96846
ACR-9b3e530505e146f98798d76afa940d1b
ACR-95257e0d523048f4800324df18fad590
ACR-8c9b64eb023142cda6acad2a5dca8dbf
ACR-7250c344c6794f97bc5494a872937baf
ACR-0baa67c5ba3b428681d19e5fd0f506a9
ACR-ed1ee71e2eef4a01b862a77df48f5302
ACR-225b9c158ad64d1ea905f2cf7833e681
ACR-7a4917277a3c4120bfccdcae7d68fba3
ACR-190435bc696f4909814c0196ebd4b33c
ACR-bb2566c655d44491962794ea27e68bc9
ACR-fc027a10cef14ecd82ce89e70e7f84da
ACR-4bc7ecde4f474910bfda299bed009d66
ACR-7e798099400c492388cb89de487ccaec
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
