/*
ACR-6c755797b1db4a5a9205e5269cc14428
ACR-fe1446cade034879a7eb0923c66f115e
ACR-53a8dde9a50745c08917e9ed4b000e08
ACR-4cbecde71481493fbc58d4541913b96c
ACR-57c7e7fda2594ea59e5bac05b5815a62
ACR-4cf520b2ce884c93b5511cbb30e72567
ACR-ce388b65ba7e40ccbb6b7aee786b4b1a
ACR-ea58d2f0ea17467ca6ff81955cee415b
ACR-4a8fe94ae0dd446d9f8b3e5a3c6d2696
ACR-32b559bb753d445b8cc9a4fe90a86aa0
ACR-30110051473f40ebb2f2e9f3d8ab2013
ACR-c87b672b2e0644b69a00b2802ff5ffc0
ACR-dc7dee4508a84a348ec05403f76ab74f
ACR-3791b228eab74c46824e47b468d14b3d
ACR-b39e816d8c984fa3bd0ba26bdc354d2f
ACR-71b38184cc78491f96061ceecbdc67d2
ACR-9af724899ba3487bbee16d21cdba20c3
 */
package mediumtest.analysis;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.embedded.server.ToggleAutomaticAnalysisRequestHandler;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.EMBEDDED_SERVER;

class ToggleAutomaticAnalysisMediumTests {

  @RegisterExtension
  SonarLintLogTester logTester = new SonarLintLogTester(true);

  private final Gson gson = new Gson();

  @SonarLintTest
  void it_should_enable_automatic_analysis(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start();

    var response = executePostAutomaticAnalysisEnablementRequest(backend, "enabled=true");
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(response.statusCode()).isEqualTo(200));
  }

  @SonarLintTest
  void it_should_disable_automatic_analysis(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start();

    var response = executePostAutomaticAnalysisEnablementRequest(backend, "enabled=false");
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(response.statusCode()).isEqualTo(200));
  }

  @SonarLintTest
  void it_should_return_bad_request_for_missing_parameter(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start();

    var response = executePostAutomaticAnalysisEnablementRequest(backend, "invalid=param");
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(response.statusCode()).isEqualTo(400));
    var errorMessage = gson.fromJson(response.body(), ToggleAutomaticAnalysisRequestHandler.ErrorMessage.class);
    assertThat(errorMessage.message()).isEqualTo("Missing 'enabled' query parameter");
  }

  @SonarLintTest
  void it_should_return_bad_request_for_get_request(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start();

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> 
      assertThat(backend.getEmbeddedServerPort()).isGreaterThan(0));

    var response = executeGetAutomaticAnalysisEnablementRequest(backend);
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(response.statusCode()).isEqualTo(400));
  }

  private HttpResponse<String> executeGetAutomaticAnalysisEnablementRequest(SonarLintTestRpcServer backend) throws IOException, InterruptedException {
    return HttpClient.newHttpClient().send(
      executeToggleAutomaticAnalysisRequest(backend, null).GET().build(),
      HttpResponse.BodyHandlers.ofString()
    );
  }

  private HttpResponse<String> executePostAutomaticAnalysisEnablementRequest(SonarLintTestRpcServer backend, String queryParams) throws IOException, InterruptedException {
    return HttpClient.newHttpClient().send(
      executeToggleAutomaticAnalysisRequest(backend, queryParams).POST(HttpRequest.BodyPublishers.noBody()).build(),
      HttpResponse.BodyHandlers.ofString()
    );
  }

  private HttpRequest.Builder executeToggleAutomaticAnalysisRequest(SonarLintTestRpcServer backend, String queryParams) {
    var uri = "http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/analysis/automatic/config" + (queryParams != null && !queryParams.isEmpty() ? ("?" + queryParams) : "");
    return HttpRequest.newBuilder()
      .uri(URI.create(uri))
      .header("Origin", "http://localhost");
  }

}
