/*
ACR-72d5788341c246d4b5420a7e63cdb99e
ACR-79ec4aad3a344f679d63354afad4beb8
ACR-25be764019ce4c959a1f8c5fbe925672
ACR-d4d9c260bc5648b8bf5d5a8fd0846785
ACR-fab7f38944764e5191a74ec836eae217
ACR-dc55c290d5044972850653caef498cd5
ACR-a980bb99b5db429fa96a5bea416a380e
ACR-af8a1a0f93db4295a3ad4459ddd41159
ACR-a386454f62cc49128a5a6090c195675c
ACR-f5cce3852cb94bb9b835726b56304fd1
ACR-9eaac80ad46643c78e9d390dea23cd24
ACR-6e32ff6e954c4b089014b5dc5e245f53
ACR-92a33fe7d06d4552b12776b41634958d
ACR-bdd9db4f624b44e69d74f3c6ed8b4e73
ACR-018adb4532524355bde7c9da2d22b31e
ACR-1bfc54041cfe464eb494e30921f5c99a
ACR-fd2156b4473c496baeea91582426fe51
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
