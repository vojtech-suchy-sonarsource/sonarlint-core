/*
ACR-86edbf7563a5426bbe36b2d24f1b3d30
ACR-6d822880620e462f9382f7790e0bd728
ACR-431da6e8dc794007a11e1d9911910a01
ACR-5346568f52b1469988ab36e2b4155480
ACR-51d01bd218474702909d4ad4655e308e
ACR-60ff9f95739f450888c20190571650cb
ACR-833716920dc84b5ea15a500cd48379b3
ACR-fd675dc15ee94b05aabf55b914422ced
ACR-74d3a01e407a4189b01a8fea44cdd43b
ACR-86e58dd4682a4aaf8857a38fdef3d516
ACR-318e116d53834327b171d68be79dd701
ACR-74050f38f0484a239dc675a7c944c567
ACR-f1bc60b504614b9e87f599f779663cd1
ACR-fbb37b386de94e2cae730ed13c84c61f
ACR-a03e57b81d3d4f6399a00184d744fcf3
ACR-f61eb6119bcb46efa87dc0eceab16099
ACR-d124f63a14184b03b3149d5024eae482
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
