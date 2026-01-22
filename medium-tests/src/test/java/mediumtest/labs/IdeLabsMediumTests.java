/*
ACR-c8784b378278403fad4d4732f6f68846
ACR-2071432bfaf74c9ebffd90e7d553a6e2
ACR-3edcff5675b142ca90fd23e7e9e69ec8
ACR-aa3da3b175b8401987ea9dc81db3b565
ACR-4895e1b5cc8843b49243c08637c03c14
ACR-637f7f9cc6444218a2060c17b62dab77
ACR-5edc1bec52b643888721c8d0c6f09475
ACR-63881cb22aaf4184be092da1345f8ee2
ACR-9039d954465c48afbce60c5361aa4368
ACR-6d28c2f823b04d97a803d712b9e4c087
ACR-5a592a6f6ed54c4ea170534b9a148ec5
ACR-f9d868490bd24487b7710ffe8b26b608
ACR-93eb2652cd544e38835813f330c2e81b
ACR-be408a1d069d4fc2b4f7597dec0fe6a0
ACR-c09612cc65fb43b6b2a584f2afe58775
ACR-7f1845c25cc340d7a26982841af7d187
ACR-4e3569adc6c3451f933cf84f1963e8d1
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
