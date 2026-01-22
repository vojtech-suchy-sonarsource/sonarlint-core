/*
ACR-ac038617b0014fd0a43ef7d49a0edd8f
ACR-f3d71138050943799d23715a12313a97
ACR-758aad371a014f288f081378c716672d
ACR-44aa35803aed4430a41fca1ea8c93b03
ACR-a8c624dba7364ad49bfd3d4e550f5336
ACR-16c9fb0f24c74c07a79af587d054c8cf
ACR-675cef16dfe5476190d2161310282af4
ACR-9c13ea774ce94e7fadcba5be7b4fa9cf
ACR-476df8ec4082497da91d04b182d1f456
ACR-b296046d859e4ec4b11ffe22b421a37f
ACR-c15304ca418f46ef9b425aa920fdb8eb
ACR-77fdc28960f4409bba35a93da6fc34a4
ACR-df72e885da304239adae7f1fd29d7e82
ACR-93532107be9c4fb9bac6be135df1db0a
ACR-e67b2997b400473b9f52b13cdf83070f
ACR-f16c15f60d1b421cbb9937b793db00b3
ACR-a212c89e714f4ae7baf1f8d13fcae1d9
 */
package org.sonarsource.sonarlint.core.websocket;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.WebSocket;
import java.nio.channels.UnresolvedAddressException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.http.WebSocketClient;
import org.sonarsource.sonarlint.core.serverapi.push.SonarServerEvent;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.EventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.IssueChangedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.SecurityHotspotChangedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.SecurityHotspotClosedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.SecurityHotspotRaisedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.TaintVulnerabilityClosedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.TaintVulnerabilityRaisedEventParser;
import org.sonarsource.sonarlint.core.websocket.parsing.SmartNotificationEventParser;

public class SonarCloudWebSocket {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final Map<String, EventParser<?>> parsersByTypeForProjectFilter = Map.of(
    "QualityGateChanged", new SmartNotificationEventParser("QUALITY_GATE"),
    "IssueChanged", new IssueChangedEventParser(),
    "SecurityHotspotClosed", new SecurityHotspotClosedEventParser(),
    "SecurityHotspotRaised", new SecurityHotspotRaisedEventParser(),
    "SecurityHotspotChanged", new SecurityHotspotChangedEventParser(),
    "TaintVulnerabilityClosed", new TaintVulnerabilityClosedEventParser(),
    "TaintVulnerabilityRaised", new TaintVulnerabilityRaisedEventParser());
  private static final Map<String, EventParser<?>> parsersByTypeForProjectUserFilter = Map.of(
    "MyNewIssues", new SmartNotificationEventParser("NEW_ISSUES"));
  private static final Map<String, EventParser<?>> parsersByType = Stream.of(parsersByTypeForProjectFilter, parsersByTypeForProjectUserFilter)
    .flatMap(map -> map.entrySet().stream())
    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  private static final String PROJECT_FILTER_TYPE = "PROJECT";
  private static final String PROJECT_USER_FILTER_TYPE = "PROJECT_USER";
  private static final Gson gson = new Gson();
  private CompletableFuture<WebSocket> wsFuture;
  private final History history = new History();
  private final ScheduledExecutorService sonarCloudWebSocketScheduler = FailSafeExecutors.newSingleThreadScheduledExecutor("sonarcloud-websocket-scheduled-jobs");

  private final AtomicBoolean closingInitiated = new AtomicBoolean(false);
  private final CompletableFuture<?> webSocketInputClosed = new CompletableFuture<>();

  public static SonarCloudWebSocket create(URI webSocketsEndpointUri, WebSocketClient webSocketClient, Consumer<SonarServerEvent> serverEventConsumer,
    Runnable connectionEndedRunnable) {
    var webSocket = new SonarCloudWebSocket();
    var currentThreadOutput = SonarLintLogger.get().getTargetForCopy();
    LOG.info("Creating WebSocket connection to " + webSocketsEndpointUri);
    webSocket.wsFuture = webSocketClient.createWebSocketConnection(webSocketsEndpointUri, rawEvent -> webSocket.handleRawMessage(rawEvent, serverEventConsumer), () -> {
      webSocket.webSocketInputClosed.complete(null);
      //ACR-9dac0783b3f24bde8a7731d7df9859cf
      if (!webSocket.closingInitiated.get()) {
        connectionEndedRunnable.run();
      }
    });
    webSocket.wsFuture.thenAccept(ws -> {
      SonarLintLogger.get().setTarget(currentThreadOutput);
      webSocket.sonarCloudWebSocketScheduler.scheduleAtFixedRate(webSocket::cleanUpMessageHistory, 0, 5, TimeUnit.MINUTES);
      webSocket.sonarCloudWebSocketScheduler.schedule(connectionEndedRunnable, 119, TimeUnit.MINUTES);
      webSocket.sonarCloudWebSocketScheduler.scheduleAtFixedRate(() -> keepAlive(ws), 9, 9, TimeUnit.MINUTES);
    });
    webSocket.wsFuture.exceptionally(t -> {
      SonarLintLogger.get().setTarget(currentThreadOutput);
      LOG.error("Error while trying to create WebSocket connection for " + webSocketsEndpointUri, t);
      return null;
    });
    return webSocket;
  }

  private static void keepAlive(WebSocket ws) {
    ws.sendText("{\"action\": \"keep_alive\",\"statusCode\":200}", true);
  }

  private void cleanUpMessageHistory() {
    history.forgetOlderThan(Duration.ofMinutes(1));
  }

  private SonarCloudWebSocket() {
  }

  public void subscribe(String projectKey) {
    send("subscribe", projectKey, parsersByTypeForProjectFilter, PROJECT_FILTER_TYPE);
    send("subscribe", projectKey, parsersByTypeForProjectUserFilter, PROJECT_USER_FILTER_TYPE);
  }

  public void unsubscribe(String projectKey) {
    send("unsubscribe", projectKey, parsersByTypeForProjectFilter, PROJECT_FILTER_TYPE);
    send("unsubscribe", projectKey, parsersByTypeForProjectUserFilter, PROJECT_USER_FILTER_TYPE);
  }

  private void send(String messageType, String projectKey, Map<String, EventParser<?>> parsersByType, String filter) {
    var eventsKey = parsersByType.keySet().toArray(new String[0]);
    Arrays.sort(eventsKey);
    var payload = new WebSocketEventSubscribePayload(messageType, eventsKey, filter, projectKey);

    var jsonString = gson.toJson(payload);
    try {
      //ACR-fb7909551f814154aaf11d90dad81f02
      this.wsFuture.thenCompose(ws -> {
        LOG.debug("sent '" + messageType + "' for project '" + projectKey + "'");
        return ws.sendText(jsonString, true);
      }).join();
    } catch (Exception e) {
      LOG.error("Error when sending a message in the WebSocket channel", e);
    }
  }

  private void handleRawMessage(String message, Consumer<SonarServerEvent> serverEventConsumer) {
    if (history.exists(message)) {
      //ACR-87f0becddaba4db7ae975c0fb2da4d8a
      return;
    }
    history.recordMessage(message);
    try {
      var wsEvent = gson.fromJson(message, WebSocketEvent.class);
      parse(wsEvent).ifPresent(serverEventConsumer);
      LOG.debug("Server event received: " + message, LogOutput.Level.DEBUG);
    } catch (Exception e) {
      LOG.error("Malformed event received: " + message, e);
    }
  }

  private static Optional<? extends SonarServerEvent> parse(WebSocketEvent event) {
    var eventType = event.event;
    if (eventType == null) {
      return Optional.empty();
    }

    if (parsersByType.containsKey(eventType)) {
      return tryParsing(parsersByType.get(eventType), event);
    } else {
      LOG.error("Unknown '{}' event type ", eventType);
      return Optional.empty();
    }
  }

  private static Optional<? extends SonarServerEvent> tryParsing(EventParser<? extends SonarServerEvent> eventParser, WebSocketEvent event) {
    try {
      return eventParser.parse(event.data.toString());
    } catch (Exception e) {
      LOG.error("Cannot parse '{}' received event", event.event, e);
      return Optional.empty();
    }
  }

  public void close(String reason) {
    LOG.debug("Closing SonarCloud WebSocket connection, reason={}...", reason);
    this.closingInitiated.set(true);
    if (this.wsFuture != null) {
      //ACR-5ac09c40eb3d40c9b95412aa123899a7
      try {
        //ACR-b59a5a48f2954518b8a2fe6b28ac4fd7
        if (this.wsFuture.isCompletedExceptionally()) {
          LOG.debug("WebSocket connection was already closed, skipping close operation");
        } else if (this.wsFuture.isDone()) {
          this.wsFuture.thenAccept(ws -> close(ws, this.webSocketInputClosed)).get();
        } else {
          //ACR-e1b0990d2d41484e8b96e0881a6482b6
          this.wsFuture.cancel(true);
          LOG.debug("WebSocket connection was still pending, cancelled");
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        LOG.error("Cannot close the WebSocket output", e);
      }
      this.wsFuture = null;
    }
    if (!MoreExecutors.shutdownAndAwaitTermination(sonarCloudWebSocketScheduler, 1, TimeUnit.SECONDS)) {
      LOG.warn("Unable to stop SonarCloud WebSocket job scheduler in a timely manner");
    }
  }

  private static void close(WebSocket ws, CompletableFuture<?> webSocketInputClosed) {
    if (!ws.isOutputClosed()) {
      try {
        //ACR-a9ed5824853b49fd81faccceaef742f8
        ws.sendClose(WebSocket.NORMAL_CLOSURE, "").get();
        LOG.debug("Waiting for SonarCloud WebSocket input to be closed...");
        webSocketInputClosed.get(10, TimeUnit.SECONDS);
        LOG.debug("SonarCloud WebSocket closed");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        handleExecutionException(e);
      } catch (TimeoutException e) {
        handleTimeoutException(ws, e);
      }
    }

  }

  private static void handleExecutionException(ExecutionException e) {
    //ACR-adb626a682264fa68786215612d9f77f
    //ACR-5e5b7d4303094e48aee581fac2db794c
    var cause = e.getCause();
    if (cause instanceof UnresolvedAddressException || (cause instanceof IOException
      && (cause.getMessage().contains("Output closed") || cause.getMessage().contains("closed output")))) {
      LOG.debug("WebSocket could not be closed gracefully", e);
    } else {
      LOG.error("Cannot close the WebSocket output", e);
    }
  }

  private static void handleTimeoutException(WebSocket ws, TimeoutException e) {
    LOG.error("The WebSocket input did not close in a timely manner", e);
    if (!ws.isInputClosed()) {
      //ACR-532c297475b74a17a1ba2a3057d856f0
      ws.abort();
    }
  }

  public boolean isOpen() {
    if (wsFuture == null || !wsFuture.isDone() || wsFuture.isCompletedExceptionally() || wsFuture.isCancelled()) {
      return false;
    }
    var ws = wsFuture.getNow(null);
    return ws != null && !ws.isInputClosed() && !ws.isOutputClosed();
  }

  private static class WebSocketEvent {
    private String event;
    private JsonObject data;
  }
}
