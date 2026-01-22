/*
ACR-cf5f1ee635eb48119dabb0d6abda665d
ACR-aada638969c34b19a9c9eace0dcf83a4
ACR-609e38089acd4d38bb00146ab78adc5c
ACR-996f363c037748a4a11d6240e2db00f1
ACR-881d0c4673e04b82865eea5cb615c409
ACR-b393cfbf5d31468cb21cf35b74fe6121
ACR-d9d542830f6b42e1a2608d065a8765e7
ACR-f12e6aa3ca6f4a6ba46677691ecffdf9
ACR-e89609fca9d246c58ea9f226988128d1
ACR-c9784658ef8644059fd722263188e1ef
ACR-9b0614dfd89b4da59bff0b55d6e32f4c
ACR-077411b5bc914a019e6aa8a2527e620f
ACR-8d13835964d34c1884a71fd85f529511
ACR-320820791fef41aeaf2972048523562e
ACR-f2bd7a75e6634701b6d7c818b72c0439
ACR-cc2957da7b0148d39d812539d911c89b
ACR-93ca5bf42fdd43128c172cd65e7cfeac
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
      //ACR-b1a887ad3647462598151550382f7d41
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
      //ACR-c794a5b39b954980ac673279870cce94
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
      //ACR-cc4582f6230446fa9bc96bfbe28aa548
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
      //ACR-45398b00bd0147928f5fe8915a2edb30
      try {
        //ACR-d5d89f30688e474ab6b6132126bc24d5
        if (this.wsFuture.isCompletedExceptionally()) {
          LOG.debug("WebSocket connection was already closed, skipping close operation");
        } else if (this.wsFuture.isDone()) {
          this.wsFuture.thenAccept(ws -> close(ws, this.webSocketInputClosed)).get();
        } else {
          //ACR-8f208f234e2c48d78637c45524bbfb99
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
        //ACR-783aa5a6144b40acb9d81b812194a0c8
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
    //ACR-5a757856afb347d8974454280f173642
    //ACR-ab51532bebc948c4b5f5c822ec70ba3c
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
      //ACR-cdd97c391cff4446b776a2619f947216
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
