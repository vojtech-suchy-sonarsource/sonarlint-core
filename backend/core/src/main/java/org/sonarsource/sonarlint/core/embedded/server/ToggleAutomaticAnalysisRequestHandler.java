/*
ACR-ceb1fe926ebf4e668a989dbd9e987710
ACR-be3b54ac5f0a4d88b1e44bde0fcc40e8
ACR-cd2e060ff3ce48f8a1adee9a74826058
ACR-7d7ea63d17c440f590cb00efc79367fe
ACR-d8889b097faf491dafe84857fbfd7c26
ACR-4959a39477764260a590f44288f5c31e
ACR-1434fc1568914bf7bc736b463fb0621e
ACR-08cd8a6973b4434ea62e69ff265e1c9c
ACR-5a322f5ad1894b8eb04e9637280d6b67
ACR-e26dd6812e1e4afcbe2abd76531b6b53
ACR-c213cc1b39684fa19a5fb5129d6fef12
ACR-07181cb03f8248198552881c34b049ad
ACR-d1f60f813b5c4ac5a65bb8828e738981
ACR-5b1d7132efe04932b243082dbcfffe0b
ACR-51114050840b4d08bd11a4eb44d198a1
ACR-91d0a759373d47a9b51401e843231709
ACR-65236416ba5044f0ba02e91e690c7611
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
