/*
ACR-0ed3aed9cfa54507bfdfbafb8d5b4510
ACR-75ff580597474f6b9d36f7e517c1d152
ACR-af92c458de2a4ecaa33b822986449045
ACR-a8763734e49040c3b16b62f9652b5c1e
ACR-20ee60be2223439aa98265c97ca477bb
ACR-4c88f276c9aa46aa8711ab281e3722d2
ACR-8c7f4828910c48adae12df0d1153a4ea
ACR-27662dcabf8f466ca37de62b57762634
ACR-0c70cf2c542c42b7ba54431323c08f5c
ACR-f7f80ab927a24f9780ab77dd89bbff79
ACR-cc7404cb24054c17939108fdc32374fa
ACR-137ea62bfec04b268fa827f431ea0367
ACR-e7994644db4f4728a22f361e6c3ba2c6
ACR-4fb11852fcce4d9bb4ad10ba98086be5
ACR-a7cb7927e73d4bd1b36265daa259bf2c
ACR-77e75c38a0274e9b9f2147989e4cf8f9
ACR-d4b1adc60ecb4d21a367606edc6b97b9
 */
package org.sonarsource.sonarlint.core.embedded.server;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.analysis.AnalysisService;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class ToggleAutomaticAnalysisRequestHandlerTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester(true);

  private final Gson gson = new Gson();
  private AnalysisService analysisService;
  private ToggleAutomaticAnalysisRequestHandler toggleAutomaticAnalysisRequestHandler;
  private HttpContext context;

  @BeforeEach
  void setup() {
    analysisService = mock(AnalysisService.class);
    context = mock(HttpContext.class);
    toggleAutomaticAnalysisRequestHandler = new ToggleAutomaticAnalysisRequestHandler(analysisService);
  }

  @Test
  void should_reject_non_post_requests() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest(Method.GET, "/analysis/automatic/config");
    var response = new BasicClassicHttpResponse(200);

    toggleAutomaticAnalysisRequestHandler.handle(request, response, context);

    assertThat(response.getCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    verifyNoInteractions(analysisService);
  }

  @Test
  void should_reject_invalid_enabled_parameter() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest(Method.POST, "/analysis/automatic/config?invalid=param");
    var response = new BasicClassicHttpResponse(200);

    toggleAutomaticAnalysisRequestHandler.handle(request, response, context);

    assertThat(response.getCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    verifyNoInteractions(analysisService);
  }

  @Test
  void should_handle_analysis_service_exception() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest(Method.POST, "/analysis/automatic/config?enabled=true");
    var response = new BasicClassicHttpResponse(200);
    var exception = new RuntimeException("Analysis service failed");
    doThrow(exception).when(analysisService).didChangeAutomaticAnalysisSetting(anyBoolean());

    toggleAutomaticAnalysisRequestHandler.handle(request, response, context);

    assertThat(response.getCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    assertThat(response.getEntity()).isNotNull();
    var responseContent = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
    var errorMessage = gson.fromJson(responseContent, ToggleAutomaticAnalysisRequestHandler.ErrorMessage.class);
    assertThat(errorMessage.message()).contains("Failed to toggle automatic analysis");
  }

}
