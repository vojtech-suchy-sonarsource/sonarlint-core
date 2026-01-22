/*
ACR-f0318c0c8f814f07ba4a6215c2e22d8b
ACR-b19c6623f4ba416fad89fbd28f8b565c
ACR-8dd67c9d75c442199b4eb9b723075b88
ACR-367a5039abf04c97b8d57ecc31ded16e
ACR-04958278c6f9497d99baebcd9e738951
ACR-a15408491237491babaf28fc427a0a36
ACR-b252fda579f64ebfb8dc9543aedb4c2b
ACR-e2d2243e3934492d9d2ebb4dcf68c7d7
ACR-bba011c40ec04395be92561414bee9bd
ACR-0dd2fbc78b0e408bbddc7a1bdc6cbad2
ACR-5042414027d149c2b391661780763ceb
ACR-89620de18eef43deafca3915c7a897f8
ACR-af1c0c467ac64bcd8da9b5ebf9db4bd2
ACR-c8c3932d1294400f974b92f02693d273
ACR-539c14075d5b450fb04cc80e95d5e637
ACR-661086c976c44fc0a6a3917012745eb4
ACR-a983c436163842698ac5d75f52a226a6
 */
package mediumtest.connection;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.validate.ValidateConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.test.utils.ProtobufUtils.protobufBody;

class ConnectionValidatorMediumTests {

  public static final Either<TokenDto, UsernamePasswordDto> A_TOKEN = Either.forLeft(new TokenDto("aToken"));

  @RegisterExtension
  static WireMockExtension serverMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @SonarLintTest
  void test_connection_without_credentials_fail(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();

    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarQubeConnectionDto(serverMock.baseUrl(), Either.forLeft(new TokenDto(null))))).join();

    assertThat(response.isSuccess()).isFalse();
  }

  @SonarLintTest
  void test_connection_ok(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();
    serverMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withBody("{\"id\": \"20160308094653\",\"version\": \"9.9\",\"status\": \"UP\"}")));
    serverMock.stubFor(get("/api/authentication/validate?format=json")
      .willReturn(aResponse().withBody("{\"valid\": true}")));

    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarQubeConnectionDto(serverMock.baseUrl(), A_TOKEN))).join();

    assertThat(response.isSuccess()).isTrue();
  }

  @SonarLintTest
  void test_connection_organization_not_found(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();
    serverMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withBody("{\"id\": \"20160308094653\",\"version\": \"9.9\",\"status\": \"UP\"}")));
    serverMock.stubFor(get("/api/authentication/validate?format=json")
      .willReturn(aResponse().withBody("{\"valid\": true}")));
    serverMock.stubFor(get("/api/organizations/search.protobuf?organizations=myOrg&ps=500&p=1")
      .willReturn(aResponse().withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder().build()))));

    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarCloudConnectionDto("myOrg", A_TOKEN, SonarCloudRegion.EU))).join();

    assertThat(response.isSuccess()).isFalse();
    assertThat(response.getMessage()).isEqualTo("No organizations found for key: myOrg");
  }

  @SonarLintTest
  void test_connection_ok_with_org(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();
    serverMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withBody("{\"id\": \"20160308094653\",\"version\": \"9.9\",\"status\": \"UP\"}")));
    serverMock.stubFor(get("/api/authentication/validate?format=json")
      .willReturn(aResponse().withBody("{\"valid\": true}")));
    serverMock.stubFor(get("/api/organizations/search.protobuf?organizations=myOrg&ps=500&p=1")
      .willReturn(aResponse().withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder()
        .addOrganizations(Organizations.Organization.newBuilder()
          .setKey("myOrg")
          .setName("My Org")
          .build())
        .build()))));
    serverMock.stubFor(get("/api/organizations/search.protobuf?organizations=myOrg&ps=500&p=2")
      .willReturn(aResponse().withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder().build()))));

    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarCloudConnectionDto("myOrg", A_TOKEN, SonarCloudRegion.EU))).join();

    assertThat(response.isSuccess()).isTrue();
  }

  @SonarLintTest
  void test_connection_ok_without_org(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();
    serverMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withBody("{\"id\": \"20160308094653\",\"version\": \"9.9\",\"status\": \"UP\"}")));
    serverMock.stubFor(get("/api/authentication/validate?format=json")
      .willReturn(aResponse().withBody("{\"valid\": true}")));
    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarCloudConnectionDto(null, A_TOKEN, SonarCloudRegion.EU))).join();

    assertThat(response.isSuccess()).isTrue();
  }

  @SonarLintTest
  void test_unsupported_server(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();
    serverMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withBody("{\"id\": \"20160308094653\",\"version\": \"6.7\",\"status\": \"UP\"}")));

    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarQubeConnectionDto(serverMock.baseUrl(), A_TOKEN))).join();

    assertThat(response.isSuccess()).isFalse();
    assertThat(response.getMessage()).isEqualTo("Your SonarQube Server instance has version 6.7. Version should be greater or equal to 9.9");
  }

  @SonarLintTest
  void test_client_error(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();
    serverMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withStatus(400)));

    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarQubeConnectionDto(serverMock.baseUrl(),
      Either.forRight(new UsernamePasswordDto("foo", "bar"))))).join();

    assertThat(response.isSuccess()).isFalse();
    assertThat(response.getMessage()).isEqualTo("Error 400 on " + serverMock.baseUrl() + "/api/system/status");
  }

  @SonarLintTest
  void test_response_error(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();
    serverMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withBody("{\"id\": }")));

    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarQubeConnectionDto(serverMock.baseUrl(),
      Either.forRight(new UsernamePasswordDto("foo", "bar"))))).join();

    assertThat(response.isSuccess()).isFalse();
    assertThat(response.getMessage()).isEqualTo("Unable to parse server infos from: {\"id\": }");
  }

  @SonarLintTest
  void should_catch_connection_error_to_server(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(serverMock.baseUrl())
      .start();

    var response = backend.getConnectionService().validateConnection(new ValidateConnectionParams(new TransientSonarQubeConnectionDto("https://foo.bar:1234",
      Either.forLeft(new TokenDto("token"))))).join();

    assertThat(response.isSuccess()).isFalse();
    assertThat(response.getMessage()).startsWith("Request failed");
  }

}
