/*
ACR-6742f5d072ef4db98d7a41cfe16dea45
ACR-6ca5f8c76afb4d2eace0bf5d83fe3b26
ACR-d5f6c7ef8137413ca3d4864222e58320
ACR-89176a273e6d4192bb39109e5ebe4bbf
ACR-31c34f7f11cf49d390d8a757a075b273
ACR-4103d40b3ece4d889f0bae2f30ae703a
ACR-fa96252e29db4d42a0b3d45562f94578
ACR-386c07c06de64cf58ecfd1170d29e25e
ACR-e4ec963d2d7647808c581a5f14e5c1ff
ACR-5b14dcbf682b404ca46e2f20c9bc32b3
ACR-3d810936dd0b4ffba031b6879a87405c
ACR-0a4914830e6046b68fc9f01447302a15
ACR-02769751f7c14220b93bdb6a4017a2ca
ACR-9d22f2e6bc764ccd8f4ab76625cf4a3e
ACR-2b2e73fd6b6d4e6e98a8e2c9244ad593
ACR-9a7588c5cc6545f5a13bc2e3e51ce70b
ACR-2fa873ada648434ba55be00f4208839e
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
      //ACR-d04457f0e65949439d38ed05c79a96ec
      .executor(executor)
      .build();
  }

  public CompletableFuture<WebSocket> createWebSocketConnection(@Nullable URI uri, Consumer<String> messageConsumer,
    Runnable onClosedRunnable) {
    //ACR-24e3987daed9410791532494403de4bf
    if (uri == null || (!"ws".equals(uri.getScheme()) && !"wss".equals(uri.getScheme()))) {
      var future = new CompletableFuture<WebSocket>();
      future.completeExceptionally(new IllegalArgumentException("WebSocket URI must use 'ws' or 'wss' scheme: " + uri));
      return future;
    }

    //ACR-6daa292026264e66be860fb2153a6d9a
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
      //ACR-bcf56da04dd34fa790fddfb73949574b
      //ACR-6f65230407d149a1b56debfda27c98ad
      //ACR-d6a78c9effa4459a83c3b5389b8af2b6
      //ACR-27f35585601849d09ff288a1c18fdc07
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
