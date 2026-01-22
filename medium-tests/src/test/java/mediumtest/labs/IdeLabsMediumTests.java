/*
ACR-08a0515437f741929b3c07b4eca8dbe2
ACR-d16170f80eb74a3a8ab3cd78401d6178
ACR-d0ad4f07e5fc4dc892463aaa4bc7fc34
ACR-b8c1bd9429c241a98312e370e54c5a5a
ACR-6be2866e25dc4e96b398dbc500c9b94c
ACR-7de9a046b13a4d5baedab26f85d26ed3
ACR-a655c57ec7de46be9d8e89753c03d164
ACR-6d079cf5d16b4accb4b5941c19e05c7c
ACR-1bfbd5a22e0a481aa82dbb6f1af963fb
ACR-5b5a9279dd4c4fa591bcaf4978b5c242
ACR-6d04044facb440a49f871feddf5ff351
ACR-7a84c9cb9b2e4412bcecdff9059d1c7e
ACR-b7c92ba291db4861a60f0289bf030f10
ACR-cdc48768c4384409b5120c4f99bea2a0
ACR-335e8c1937594763a982c4dbec490d1c
ACR-5b567939b04f4c3fa3c0889b8879177c
ACR-038e6ea7bc9548238a34c253e92ad110
 */
package mediumtest.labs;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.labs.JoinIdeLabsProgramParams;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.sonarsource.sonarlint.core.labs.IdeLabsSpringConfig.PROPERTY_IDE_LABS_SUBSCRIPTION_URL;

public class IdeLabsMediumTests {
  @RegisterExtension
  static WireMockExtension marketingCloudMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @AfterAll
  static void tearDown() {
    System.clearProperty(PROPERTY_IDE_LABS_SUBSCRIPTION_URL);
  }

  @SonarLintTest
  void it_should_join_labs_successfully(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withTelemetryEnabled()
      .withIdeLabsSubscriptionUrl(marketingCloudMock.baseUrl())
      .start();
    marketingCloudMock.stubFor(post("/")
      .willReturn(okJson("{ \"valid_email\": true }")));
    var sampleEmail = "example@example.com";
    var ideName = "VSCode";


    var response = backend.getIdeLabsService().joinIdeLabsProgram(new JoinIdeLabsProgramParams(sampleEmail, ideName));

    assertThat(response).succeedsWithin(2, TimeUnit.SECONDS);
    assertMarketingCloudEndpointCalled(sampleEmail, ideName);
    assertThat(response.join().isSuccess()).isTrue();
  }

  @SonarLintTest
  void it_should_fail_to_join_labs_with_invalid_email(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withIdeLabsSubscriptionUrl(marketingCloudMock.baseUrl())
      .start();
    marketingCloudMock.stubFor(post("/")
      .willReturn(okJson("{ \"valid_email\": false }")));
    var sampleEmail = "invalid-email";
    var ideName = "VSCode";

    var response = backend.getIdeLabsService().joinIdeLabsProgram(new JoinIdeLabsProgramParams(sampleEmail, ideName));

    assertThat(response).succeedsWithin(2, TimeUnit.SECONDS);
    assertMarketingCloudEndpointCalled(sampleEmail, ideName);
    assertThat(response.join().isSuccess()).isFalse();
    assertThat(response.join().getMessage()).contains("The provided email address is not valid. Please enter a valid email address.");
  }

  @SonarLintTest
  void it_should_handle_server_error_when_joining_labs(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withIdeLabsSubscriptionUrl(marketingCloudMock.baseUrl())
      .start();
    marketingCloudMock.stubFor(post("/")
      .willReturn(aResponse().withStatus(500)));
    var sampleEmail = "example@example.com";
    var ideName = "VSCode";

    var response = backend.getIdeLabsService().joinIdeLabsProgram(new JoinIdeLabsProgramParams(sampleEmail, ideName));

    assertThat(response).succeedsWithin(2, TimeUnit.SECONDS);
    assertMarketingCloudEndpointCalled(sampleEmail, ideName);
    assertThat(response.join().isSuccess()).isFalse();
    assertThat(response.join().getMessage()).contains("An unexpected error occurred. Server responded with status code: 500");
  }

  void assertMarketingCloudEndpointCalled(String email, String source) {
    var expectedRequestBody = String.format("""
      {
        "email": "%s",
        "source": "%s"
      }
      """, email, source);

    marketingCloudMock.verify(postRequestedFor(urlEqualTo("/"))
      .withHeader("Content-Type", equalTo("application/json"))
      .withRequestBody(equalToJson(expectedRequestBody)));
  }
}
