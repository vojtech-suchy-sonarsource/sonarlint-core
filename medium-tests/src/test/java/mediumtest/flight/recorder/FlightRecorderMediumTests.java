/*
ACR-7ed1ec1394004ae5b90b34ea04e5a7be
ACR-b978d8af31e74e948a48b4341ba51305
ACR-ea31f1dd57754ff7b9d8f7e046af5dd7
ACR-ed3dde2395c94c4d93650159bcb3fe98
ACR-2b811d42629947728b0985db2408a11c
ACR-8940c20d54e540e292287c34f6a9acae
ACR-4858cd810b57476b946a1ec74708ee6f
ACR-b0a4feb0284448cea9879304b36b03b7
ACR-85fd566bff524840b326d2effb3a4344
ACR-063e6cbe7b8440e4aeaa279acfae7cf1
ACR-4777360a6d264184b0ae064752a4e897
ACR-697b4513a0cf489988aa0e63ab64cec8
ACR-7025bcb28b90484ea1dd4c9acc2b903d
ACR-59040c858ed34c57b482db3fc1cc2a8f
ACR-4bf8234b645945d8a056ebddf2a8c9ae
ACR-68945e7ea7b44d4fbe5e0860e3737718
ACR-972dcdcbe7e24f84a9374b8c709c6a88
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
    //ACR-953241da81e6424eb7d1158ecc880468
    sentryServer.stubFor(post(urlPathMatching("/api/\\d+/store/"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{\"id\": \"event-id-12345\"}")));

    //ACR-7f05dc6a7bba4e5ea89a0bd37e296ba3
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

    //ACR-5a8c0f1a16d2413186eff4867c9fedcb
    //ACR-1fa0cbdec2dd49c3a12468deda01636a
    //ACR-a582100e8fe44dbdb1f4ef6038baad3f
    //ACR-ef2c3ddd73234df7b9fe62b4d5bd86a6
    await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->  assertThat(sentryServer.getAllServeEvents()).hasSize(3));
  }
}
