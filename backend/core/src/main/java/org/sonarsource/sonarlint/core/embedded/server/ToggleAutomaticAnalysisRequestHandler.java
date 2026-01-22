/*
ACR-b3d84f9b8e01484bbc14b76bafa5d88c
ACR-338fc3212dc649fe95d71e73431150ff
ACR-5e5f2db5c4034506a5d7aacbb2270477
ACR-53092014878949698a329f9bc6dbfeca
ACR-59f5c568d2d3406f95d7a216e5a76c65
ACR-b6d7434ecfa545fe8c5700c345896418
ACR-650f25b2e6644f188d13e556ebeadc7b
ACR-49497439da4a4297ab428c0972adca22
ACR-cff362bb826b4f34a6b0e3dfdfca5864
ACR-65f17519664c4ee88d260ec6d13fa485
ACR-49438fa98a1a4fa0a3b0dfbd2c0d4823
ACR-1446087717a64948836ff2c972aba23a
ACR-b5542d56ac5b405daacfd381b8f78497
ACR-bd49c37f7659423ca95978c6abb391a1
ACR-88b39c677dbf4aefbe2700a82d595bcd
ACR-00e2236438754ed183519dc0770ded3b
ACR-94e48c425f584cef93b5e50ad64264dc
 */
package org.sonarsource.sonarlint.core.embedded.server;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.URIBuilder;
import org.sonarsource.sonarlint.core.analysis.AnalysisService;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class ToggleAutomaticAnalysisRequestHandler implements HttpRequestHandler {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final AnalysisService analysisService;
  private final Gson gson = new Gson();

  public ToggleAutomaticAnalysisRequestHandler(AnalysisService analysisService) {
    this.analysisService = analysisService;
  }

  @Override
  public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext context) throws HttpException, IOException {
    LOG.debug("Received request for toggling automatic analysis");

    if (!Method.POST.isSame(request.getMethod())) {
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      return;
    }

    var params = new HashMap<String, String>();
    try {
      new URIBuilder(request.getUri(), StandardCharsets.UTF_8)
        .getQueryParams()
        .forEach(p -> params.put(p.getName(), p.getValue()));
    } catch (URISyntaxException e) {
      handleError(response,  "Invalid URI");
      return;
    }

    var enabledParam = params.get("enabled");
    if (enabledParam == null) {
      handleError(response, "Missing 'enabled' query parameter");
      return;
    }

    boolean enabled;
    try {
      enabled = Boolean.parseBoolean(enabledParam);
    } catch (Exception e) {
      handleError(response, "Invalid 'enabled' parameter value");
      return;
    }

    try {
      analysisService.didChangeAutomaticAnalysisSetting(enabled);
      response.setCode(HttpStatus.SC_OK);
    } catch (Exception e) {
      LOG.error("Failed to toggle automatic analysis", e);
      response.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      var errorResponse = new ErrorMessage("Failed to toggle automatic analysis: " + e.getMessage());
      response.setEntity(new StringEntity(gson.toJson(errorResponse), ContentType.APPLICATION_JSON));
    }
  }

  private void handleError(ClassicHttpResponse response, String clientMessage) {
    response.setCode(HttpStatus.SC_BAD_REQUEST);
    var errorResponse = new ErrorMessage(clientMessage);
    response.setEntity(new StringEntity(gson.toJson(errorResponse), ContentType.APPLICATION_JSON));
  }

  public record ErrorMessage(String message) {
  }

}
