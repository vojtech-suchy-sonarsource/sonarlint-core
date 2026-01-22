/*
ACR-930f84f1e91b4c09b28985702b83c7bb
ACR-6721f245853c49c8b0364968e2dde21f
ACR-28dc060c7d3e46d795a4580ce599001b
ACR-8ab2c7e8e03b473686dc895975bcc506
ACR-ce30937a21c04f34bf770cc9c8488115
ACR-14e5f014310d4f19b1e0f72e64860e1e
ACR-94f89db1ff5b433aa7e42daec8ac5c06
ACR-bf20c0c07cd944acaf5d5f634ee7c1d2
ACR-02b3223efb854c369576117d0f9d24f6
ACR-568f2ef657ee48c9b33d2dd583099f9a
ACR-a2cdbf1ebea44bf887a670cccf7f4080
ACR-26f0a3627b344b4f967939dde38d99fe
ACR-7b306476327f476eb6e1ada719249b75
ACR-7f4efbd8c2284068983bdad2546b7c32
ACR-872f5a47cbb949eca12859b910cb4c34
ACR-01480c773e9344989783a91122298b3a
ACR-3fd14ec1b4984d51814c845bd5b5e61d
 */
package org.sonarsource.sonarlint.core.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class WebSocketClient {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
  private static final String USER_AGENT_HEADER_NAME = "User-Agent";

  private final String userAgent;
  @Nullable
  private final String token;
  private final HttpClient httpClient;

  WebSocketClient(String userAgent, @Nullable String token, ExecutorService executor) {
    this.userAgent = userAgent;
    this.token = token;
    this.httpClient = HttpClient
      .newBuilder()
      //ACR-86a3c1ad81bf4947953e67835984c402
      .executor(executor)
      .build();
  }

  public CompletableFuture<WebSocket> createWebSocketConnection(@Nullable URI uri, Consumer<String> messageConsumer,
    Runnable onClosedRunnable) {
    //ACR-dc026595857949ac907505311708e86d
    if (uri == null || (!"ws".equals(uri.getScheme()) && !"wss".equals(uri.getScheme()))) {
      var future = new CompletableFuture<WebSocket>();
      future.completeExceptionally(new IllegalArgumentException("WebSocket URI must use 'ws' or 'wss' scheme: " + uri));
      return future;
    }

    //ACR-d4a235b3f61f443692748716eafe250d
    var currentThreadOutput = SonarLintLogger.get().getTargetForCopy();
    return httpClient
      .newWebSocketBuilder()
      .header(AUTHORIZATION_HEADER_NAME, "Bearer " + token)
      .header(USER_AGENT_HEADER_NAME, userAgent)
      .buildAsync(uri, new MessageConsumerWrapper(messageConsumer, onClosedRunnable, currentThreadOutput));
  }

  private record MessageConsumerWrapper(Consumer<String> messageConsumer, Runnable onWebSocketInputClosedRunnable,
                                        @Nullable LogOutput currentThreadOutput) implements WebSocket.Listener {

    @Override
    public void onOpen(WebSocket webSocket) {
      //ACR-ff406d01699e47d79c63bd20ffe6a59d
      //ACR-93cc1fd4771d470d92dc306784b14e54
      //ACR-653d187a2db94b2496bcd9c8aa39dc5a
      //ACR-c9caf602fafc4ddd9a8760866ad66084
      SonarLintLogger.get().setTarget(currentThreadOutput);
      LOG.debug("WebSocket opened");
      WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
      SonarLintLogger.get().setTarget(currentThreadOutput);
      messageConsumer.accept(data.toString());
      return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
      SonarLintLogger.get().setTarget(currentThreadOutput);
      LOG.error("Error occurred on the WebSocket", error);
      onWebSocketInputClosedRunnable.run();
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
      SonarLintLogger.get().setTarget(currentThreadOutput);
      LOG.debug("WebSocket closed, status=" + statusCode + ", reason=" + reason);
      onWebSocketInputClosedRunnable.run();
      return null;
    }
  }

}
