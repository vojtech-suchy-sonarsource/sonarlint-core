/*
ACR-d7ee45564ebf4cae88359cd9717ffde4
ACR-af9024719b804f78924fcb54454b31d6
ACR-8c36aeac7b8d493880ddc8aa8b85c4cb
ACR-438e929c0f984584841606e876c2e544
ACR-11d0e406dfa94e0f932021af445b9c82
ACR-bad52e504d35406ab31ca7b6e708c4cc
ACR-84f0da308d6d40458ea02e7bf3d82e12
ACR-7f50ef1dc0264430a1fbf7c0033160fc
ACR-ae61b88bd7f34e55a3b92a004c85eb39
ACR-259962647fcd42a6b7ad32edd82dbefb
ACR-2f6e147b9c83402d94c52230b4c82ed9
ACR-e0fe3966280b4d0196599ab4d2f57868
ACR-3c075d55f5544825861f27eae18e5aaa
ACR-68ea738982884122ad7b3b102bf70069
ACR-394c1492f20c42a59aade4440b4bdb57
ACR-8466ad996025497cad813117c7028165
ACR-cabf374906e64d608bce5cf52c0b29c5
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
