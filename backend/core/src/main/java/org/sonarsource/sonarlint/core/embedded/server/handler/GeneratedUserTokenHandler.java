/*
ACR-740a308bec1542e981f643270d216c6e
ACR-6b74f2332d184bd49be8768aabb1384c
ACR-ed30bffd614a464ea9498dc09fd501d7
ACR-c263954efd894d3dac4cf7d118465fd3
ACR-e151958fc4a449d3853ce94549163645
ACR-06f068830ecc432980ad2ce833294081
ACR-cbd30a1bac9440f4998eed0381b42c66
ACR-97755307e5064b2f8b4e46cd154af2a3
ACR-ad1f083b2c364343b04e22ab6245515b
ACR-614751c434db42179e4e2f1925ba05ba
ACR-6e982e9e92054fc4a09ea09b6d2e2146
ACR-55e28fc6a5fc46829315e00fca9e00cb
ACR-1ab12a0991934d749bfd280f52dc7014
ACR-6f6952c1ea254eecb0788ade127cb760
ACR-851fd38afa634079ba52a7327f4cad5c
ACR-55ef9329a1754c489efbcb1c973becb6
ACR-5d7a7fdae55c4fb8b11a36e8f1281f2e
 */
package org.sonarsource.sonarlint.core.embedded.server.handler;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.embedded.server.AwaitingUserTokenFutureRepository;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenResponse;

import static java.util.function.Predicate.not;

public class GeneratedUserTokenHandler implements HttpRequestHandler {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final AwaitingUserTokenFutureRepository awaitingUserTokenFutureRepository;

  public GeneratedUserTokenHandler(AwaitingUserTokenFutureRepository awaitingUserTokenFutureRepository) {
    this.awaitingUserTokenFutureRepository = awaitingUserTokenFutureRepository;
  }

  @Override
  public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext context) throws HttpException, IOException {
    if (!Method.POST.isSame(request.getMethod())) {
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      return;
    }

    String token = extractAndValidateToken(request);
    if (token == null) {
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      return;
    }

    var origin = AttributeUtils.getOrigin(context);

    awaitingUserTokenFutureRepository.consumeFutureResponse(origin)
      .filter(not(CompletableFuture::isCancelled))
      .ifPresentOrElse(pendingFuture -> {
        pendingFuture.complete(new HelpGenerateUserTokenResponse(token));
        response.setCode(HttpStatus.SC_OK);
        response.setEntity(new StringEntity("OK"));
      }, () -> response.setCode(HttpStatus.SC_FORBIDDEN));
  }

  private static String extractAndValidateToken(ClassicHttpRequest request) throws IOException, ParseException {
    var requestEntityString = EntityUtils.toString(request.getEntity(), "UTF-8");
    String token = null;
    try {
      token = new Gson().fromJson(requestEntityString, TokenPayload.class).token;
    } catch (Exception e) {
      //ACR-d2f83360e4664b8c9640c5245e3a3eed
      LOG.error("Could not deserialize user token", e);
    }
    return token;
  }

  private static class TokenPayload {
    private String token;
  }
}
