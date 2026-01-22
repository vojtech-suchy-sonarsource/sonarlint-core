/*
ACR-79fbf74cd8204f699ccfa122e3a84794
ACR-8069933b501642faac6b343318f2acef
ACR-bd308b36b8a14362bc4e0fe369dadea1
ACR-e699c2cd457740e494bfbda5229a7b34
ACR-d3049e7558a644a995cd1d33899ed3cb
ACR-ff6244d2cc7f4831b0405c02616512f4
ACR-21fd8a8082a44b9a99d52fb516a98487
ACR-3efaec346661424caf63516ad13b244d
ACR-00efb40823b7414c8b6bdd2fa33a306f
ACR-9005fa39c7554b8190d8d84dc3ebc0e7
ACR-1197301c43a14202aab3a749511b02fc
ACR-78ffb6ecf2b240edbe3b9dcc804a3f77
ACR-4a097b620b0d4a5f8dd85f4d788b8009
ACR-580fcb3517d7400790b30c4cc7c4ee03
ACR-3fd11214c445419c8c88b7db97fd99e5
ACR-9b6766ba45304227b17f902b53aeb5b5
ACR-cb94b9a806a244f1bcd37dc794319c07
 */
package mediumtest.http;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.GetOrganizationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.test.utils.ProtobufUtils.protobufBody;

class TimeoutMediumTests {

  @RegisterExtension
  static WireMockExtension sonarcloudMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @AfterEach
  void tearDown() {
    System.clearProperty("sonarlint.http.responseTimeout");
  }

  @SonarLintTest
  void it_should_timeout_on_long_response(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .build();
    var backend = harness.newBackend()
      .withHttpResponseTimeout(Duration.ofSeconds(1))
      .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
      .start(fakeClient);
    sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?organizations=myOrg&ps=500&p=1")
      .willReturn(aResponse().withStatus(200)
        .withFixedDelay(2000)
        .withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder()
          .addOrganizations(Organizations.Organization.newBuilder()
            .setKey("myCustom")
            .setName("orgName")
            .setDescription("orgDesc")
            .build())
          .build()))));

    var future = backend.getConnectionService().getOrganization(new GetOrganizationParams(Either.forLeft(new TokenDto("token")), "myOrg", SonarCloudRegion.EU));

    assertThat(future)
      .failsWithin(3, TimeUnit.SECONDS)
      .withThrowableOfType(ExecutionException.class)
      .withCauseExactlyInstanceOf(ResponseErrorException.class);
  }

}
