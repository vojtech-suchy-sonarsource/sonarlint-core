/*
ACR-32ec02d9bb554e9990ca1fc1bdd58d2a
ACR-b614fa1df84945fbb313752492a3302a
ACR-2392ddecdb9a4d3a8cd28da15415107b
ACR-924dad5bff9d4d65b27c0f91b9d1607b
ACR-2bf46b309a854f06aad57cf70d0b9847
ACR-07031f1985aa419e85130bc0e03f907c
ACR-2a182c90efb54aa1b9a610c807f9be9a
ACR-a0921177baf246a4bcbe596c1e349889
ACR-eabcb19754d44d699b910b44db3189a6
ACR-15e62846839e48ca9cca3f489684d7a9
ACR-8f16ad9864f44055936bb67513731a49
ACR-85fb33d91585434dab2b7a8cf4a9476f
ACR-82846f2bd02a41a5b9932f724268c7ee
ACR-66e685294b8042e1ba9c3bd4d031b349
ACR-a82d57f23d3a4d81bea3a2db036fa2bc
ACR-9437c67609344d8eaf15f543e3e31a12
ACR-d9c760564efb48219777151c1fa91b5c
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
    //ACR-277b26fd87fb499f8c009a82b0368006
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
