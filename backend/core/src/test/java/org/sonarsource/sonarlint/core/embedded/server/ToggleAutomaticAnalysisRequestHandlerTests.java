/*
ACR-3926b3af168e481a89f947ab14685b5c
ACR-6a42b08c6302445f8552aa307d45c051
ACR-e0bf97a58c794804ab665b5f2e7e07fd
ACR-785f115c66ae4ab7a14634ce7972fece
ACR-771d68d4d1e3486bad0a9723f90847f6
ACR-6894b77396c54a3b8325305d7c3ba17b
ACR-0b4820cdeda54e3aa6f5102aa3266dea
ACR-7c22432e94974cdca970e258bdeaef01
ACR-e8dd180d3f4e45708ade31a1bc37bbfd
ACR-1a5fabd3a0f44abd8c95a0ae7c7e759a
ACR-567721977856475b830a4a5d951d39f2
ACR-878c0c523496491f9d8487838c01333a
ACR-1ffb03e363dc4f0e912fad2b79bfc01f
ACR-434f032b38054341962143f81dd3de7b
ACR-3eb62819883249ff8a74be6ce158aa61
ACR-7f4e169cda4a495ca6a9a28db60829ee
ACR-8b9e03fcd1a942daa02747c33bbb855c
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
