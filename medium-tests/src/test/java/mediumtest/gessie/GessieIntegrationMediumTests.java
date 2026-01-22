/*
ACR-67b826ea4824460f8168a5d4eb8214c2
ACR-2d5987ec6c5a406aaf60f660979a921f
ACR-93e2b5011dfb4a458c6b35392354b587
ACR-bda1b355d107483abc9ce7c8bb7ce4a8
ACR-44910b5023a74185a269dfaaea04fa2a
ACR-a6996f111bc24e97a1bf7cf26eebb1d2
ACR-f6e5f8163ec1485faca684a695a8f539
ACR-39e4007a373a4a1696b82fc566c03dc4
ACR-3883b3bc7da241bfae328ecbc6ada0ca
ACR-fbfeb6983139436c840be9c3ffb911df
ACR-85d2706595dc4f7aa212a92898c81d60
ACR-8ebf5ede61f54e55a93b07d8678b9950
ACR-42bef1dff6a24b6d98e3d3626cc5dc34
ACR-71b5449b5a804088af21872f0aa4d1f4
ACR-49c333cfcd5e4609b61a61b2bd3950aa
ACR-fce5b1e693e54fd7a16d16b003810b71
ACR-71edb6badc6a4f6b873f5ca60ca9b7f7
 */
package mediumtest.gessie;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.telemetry.gessie.GessieSpringConfig;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;

@ExtendWith(SystemStubsExtension.class)
class GessieIntegrationMediumTests {

  private static final String IDE_ENDPOINT = "/ide";
  private static final String FAILED_ONCE = "Failed once";

  @RegisterExtension
  static WireMockExtension gessieEndpointMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @BeforeAll
  static void mockGessieEndpoint() {
    gessieEndpointMock.stubFor(post("/ide").willReturn(aResponse().withStatus(202)));
  }

  @BeforeEach
  void setUp() {
    System.setProperty("sonarlint.http.retry.interval.seconds", "0");
    System.setProperty(GessieSpringConfig.PROPERTY_GESSIE_API_KEY, "value");
  }

  @AfterEach
  void tearDown() {
    System.clearProperty("sonarlint.http.retry.interval.seconds");
    System.clearProperty(GessieSpringConfig.PROPERTY_GESSIE_API_KEY);
  }

  @SonarLintTest
  void it_should_send_startup_event(SonarLintTestHarness harness) throws URISyntaxException, IOException {
    harness.newBackend()
      .withGessieTelemetryEnabled(gessieEndpointMock.baseUrl())
      .start();

    var fileContent = getTestJson("GessieRequest");
    await().untilAsserted(() -> gessieEndpointMock.verify(postRequestedFor(urlEqualTo(IDE_ENDPOINT))
      .withHeader("x-api-key", new EqualToPattern("value"))
      .withRequestBody(equalToJson(fileContent))));
  }

  @SonarLintTest
  void it_should_not_send_anything_if_gessie_telemetry_is_disabled(SonarLintTestHarness harness) {
    harness.newBackend()
      .start();

    await().untilAsserted(() -> gessieEndpointMock.verify(0, anyRequestedFor(urlEqualTo(IDE_ENDPOINT))));
  }

  @SonarLintTest
  void it_should_retry_503_error(SonarLintTestHarness harness) throws URISyntaxException, IOException {
    gessieEndpointMock.stubFor(post("/ide")
      .inScenario("Retry")
      .whenScenarioStateIs(Scenario.STARTED)
      .willSetStateTo(FAILED_ONCE)
      .willReturn(aResponse().withStatus(503)));
    gessieEndpointMock.stubFor(post("/ide")
      .inScenario("Retry")
      .whenScenarioStateIs(FAILED_ONCE)
      .willReturn(aResponse().withStatus(202)));

    harness.newBackend()
      .withGessieTelemetryEnabled(gessieEndpointMock.baseUrl())
      .start();

    var fileContent = getTestJson("GessieRequest");
    await().atMost(15, TimeUnit.SECONDS)
      .untilAsserted(() -> gessieEndpointMock.verify(2, postRequestedFor(urlEqualTo(IDE_ENDPOINT))
      .withHeader("x-api-key", new EqualToPattern("value"))
      .withRequestBody(equalToJson(fileContent))));
  }

  @SonarLintTest
  void it_should_retry_503_error_only_twice(SonarLintTestHarness harness) throws URISyntaxException, IOException {
    gessieEndpointMock.stubFor(post("/ide")
      .willReturn(aResponse().withStatus(503)));

    harness.newBackend()
      .withGessieTelemetryEnabled(gessieEndpointMock.baseUrl())
      .start();

    var fileContent = getTestJson("GessieRequest");
    //ACR-c3573df8f9f9487fab8b7adeca4d1972
    await().timeout(5, TimeUnit.SECONDS)
      .pollDelay(2, TimeUnit.SECONDS)
      .untilAsserted(() -> gessieEndpointMock.verify(3, postRequestedFor(urlEqualTo(IDE_ENDPOINT))
      .withHeader("x-api-key", new EqualToPattern("value"))
      .withRequestBody(equalToJson(fileContent))));
  }

  private String getTestJson(String fileName) throws URISyntaxException, IOException {
    var resource = Objects.requireNonNull(getClass().getResource("/response/gessie/GessieIntegrationMediumTests/" + fileName + ".json"))
      .toURI();
    return Files.readString(Path.of(resource));
  }

}
