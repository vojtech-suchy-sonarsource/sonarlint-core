/*
ACR-5ba5f2769ff04ecba0d1e07596bf5d51
ACR-faa9e70536bb44ee98d6c4db5b3523b8
ACR-7750a68b7b2142809e81efe68bdd8c7f
ACR-a1f5b22cbe754672960b47f97a2407f9
ACR-128d0ea81f0642aaa816167c461b155b
ACR-9c6828e3f47a40bc962716f514b4100f
ACR-3c6e41ea0b8c46869176997798db3b6f
ACR-18c1f73684bf42d2a410e8d33aa6f22f
ACR-3d9c1ad08c28447c9f276adff8374b72
ACR-efb3ccc5a04949e78abc7f1ed5af9b64
ACR-1067fcb21a4e4825913e299c53de0f80
ACR-0739a228a34a4fa7bc85f97c0508dcc8
ACR-bd1a736c18d64282b2260705d4e0c81a
ACR-f19fd5576a1748db954090803f7ab215
ACR-a7afbeb078d94655b64f9f4bb392bd8e
ACR-e2fc957482c647ba9a9458f926f7965e
ACR-5c98266a69194cabbf7f7d9e1d57ce77
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
      //ACR-ca44e8739961409eb624f22fef46db71
      LOG.error("Could not deserialize user token", e);
    }
    return token;
  }

  private static class TokenPayload {
    private String token;
  }
}
