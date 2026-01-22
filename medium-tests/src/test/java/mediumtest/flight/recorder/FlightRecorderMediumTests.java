/*
ACR-c562be5a7ff04e43891489f803aca163
ACR-fc8105ac46774868bb39f323751c1ffe
ACR-92cc08a921ff44649447c88b75dbd356
ACR-6d6038ae58ac44d9a17cfb41dcddd2f6
ACR-72ba81593e59436dbe1e25a3724cf3e6
ACR-de614e8d1e494865842d52ee213e0080
ACR-7f4c571b0dd34888b9c86a318297e7fd
ACR-57fc0b3437e141c99af0e9ce05cf9607
ACR-755794ca956f4c61be4b6f38decbd6b3
ACR-f550b7577bba45c081be60bc0365a64a
ACR-4f7b755993ed4916ad7096b2f83ea1a9
ACR-7a0207ac01d541d088fdd5198e4c0f8c
ACR-5a6eadae463c427aabf52ea86293ed92
ACR-c6dc3eae4bd747a39f90799598288e44
ACR-0ab567d050114364b98ef3c76870f9bb
ACR-6daa5d0bcb0546529c812b27b80329dd
ACR-5cc49b8bdaa74456ab9d6688547d15fd
 */
package mediumtest.flight.recorder;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.monitoring.MonitoringService;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import utils.TestPlugin;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FLIGHT_RECORDER;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.MONITORING;
import static utils.AnalysisUtils.analyzeFileAndGetIssues;
import static utils.AnalysisUtils.createFile;

@ExtendWith(SystemStubsExtension.class)
class FlightRecorderMediumTests {

  private static final String CONFIGURATION_SCOPE_ID = "configScopeId";
  private WireMockServer sentryServer;

  @BeforeEach
  void setup() {
    sentryServer = new WireMockServer(wireMockConfig().dynamicPort());
    sentryServer.start();
    System.setProperty(MonitoringService.DSN_PROPERTY, createValidSentryDsn(sentryServer));
    System.setProperty("java.net.useSystemProxies", "false");
    setupSentryStubs();
  }

  @AfterEach
  void tearDown() {
    sentryServer.stop();
  }

  private String createValidSentryDsn(WireMockServer server) {
    return "http://fake-public-key@localhost:" + server.port() + "/12345";
  }

  private void setupSentryStubs() {
    //ACR-3bcddc3dc9354bd6a570f77fca3bd5c4
    sentryServer.stubFor(post(urlPathMatching("/api/\\d+/store/"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{\"id\": \"event-id-12345\"}")));

    //ACR-4a06605154fe414b90767115ca5d29a7
    sentryServer.stubFor(post(urlPathMatching("/api/\\d+/envelope/"))
      .willReturn(aResponse()
        .withStatus(200)));
  }

  @SonarLintTest
  void simplePhpWithFlightRecorder(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "foo.php", """
      <?php
      function writeMsg($fname) {
          $i = 0; // NOSONAR
          echo "Hello world!";
      }
      ?>
      """);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .withBackendCapability(MONITORING)
      .withBackendCapability(FLIGHT_RECORDER)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine()).contains(tuple("php:S1172", 2));

    backend.getFlightRecordingService().captureThreadDump();

    //ACR-6f7817491ac84348a94366a7cabe18d8
    //ACR-3b50d4e7d3ab4c21a24a0018ad36640c
    //ACR-77efda5be8364229b152a5295aface09
    //ACR-eab019e1a69540258e1ebe54a6016537
    await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->  assertThat(sentryServer.getAllServeEvents()).hasSize(3));
  }
}
