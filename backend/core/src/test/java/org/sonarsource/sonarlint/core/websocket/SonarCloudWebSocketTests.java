/*
ACR-13fda7c86e5e4e4fb1ec0515f8555686
ACR-128765c7ae03488da12d3989cff2fda7
ACR-a43340cc3b70479d874c2e8802472e36
ACR-79ebe7d8ed7d4893ab63be204ce1076e
ACR-a68f17a15ed4446890afcfc234df23e0
ACR-5b56ef819d774db789505661b57983ee
ACR-948870b9dd3a4d77a2958da2d24a33d7
ACR-6918ae449fa6485189ba0443ff413dfa
ACR-ba12c853ae3545b2bcac72abf4baccde
ACR-43a2e7befe87405585f11360ca6479ae
ACR-6cb82123299d4977bf051b389f6b31d4
ACR-bbe6e96693b14a3e84a01ee868334c8a
ACR-cb893c1191784ff8a3177773a636cea0
ACR-d1ecedbe2e094cbfa2902d8caf8c79c3
ACR-eb1241d8d2284c8086528039c89a7261
ACR-ae351a0bc96d414eb4671c18e28cd0e1
ACR-88eb607807104900a100f79fd1e1b722
 */
package org.sonarsource.sonarlint.core.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.http.WebSocket;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.http.WebSocketClient;
import org.sonarsource.sonarlint.core.serverapi.push.SonarServerEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SonarCloudWebSocketTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private WebSocketClient webSocketClient;
  private WebSocket mockWebSocket;
  private CompletableFuture<WebSocket> wsFuture;
  private Consumer<SonarServerEvent> serverEventConsumer;
  private Runnable connectionEndedRunnable;
  private SonarCloudWebSocket sonarCloudWebSocket;
  private URI testUri;

  @BeforeEach
  void setUp() {
    webSocketClient = mock(WebSocketClient.class);
    mockWebSocket = mock(WebSocket.class);
    wsFuture = new CompletableFuture<>();
    serverEventConsumer = mock(Consumer.class);
    connectionEndedRunnable = mock(Runnable.class);
    testUri = URI.create("wss://test.example.com/websocket");
  }

  @Test
  void should_create_websocket_connection_successfully() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    assertThat(sonarCloudWebSocket).isNotNull();
    verify(webSocketClient).createWebSocketConnection(eq(testUri), any(Consumer.class), any(Runnable.class));
    assertThat(logTester.logs()).anyMatch(log -> log.contains("Creating WebSocket connection to " + testUri));
  }

  @Test
  void should_handle_connection_failure_with_generic_exception() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.completeExceptionally(new RuntimeException("Generic error"));

    assertThat(sonarCloudWebSocket).isNotNull();
    assertThat(logTester.logs(LogOutput.Level.ERROR)).anyMatch(log -> log.contains("Error while trying to create WebSocket connection for " + testUri));
  }

  @Test
  void should_close_websocket_connection_with_proper_completion() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.completedFuture(null));
    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());

    //ACR-014f2bff987e430b8c1da5c67d47d2cd
    onClosedCaptor.getValue().run();
    //ACR-4850283d4e7745299e3ea8661a1ccb91
    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs()).anyMatch(log -> log.contains("Closing SonarCloud WebSocket connection, reason=Test reason"));
    assertThat(logTester.logs()).anyMatch(log -> log.contains("Waiting for SonarCloud WebSocket input to be closed..."));
    assertThat(logTester.logs()).anyMatch(log -> log.contains("SonarCloud WebSocket closed"));
  }

  @Test
  void should_handle_close_execution_exception() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Close failed")));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.ERROR)).anyMatch(log -> log.contains("Cannot close the WebSocket output"));
  }

  @Test
  void should_handle_unresolved_address_exception_during_close() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new UnresolvedAddressException()));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-c01d3701f8ae4edd96552cdca89b6329
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());
    onClosedCaptor.getValue().run();

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.DEBUG)).anyMatch(log -> log.contains("WebSocket could not be closed gracefully"));
  }

  @Test
  void should_handle_ioexception_with_output_closed_message() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new IOException("Output closed")));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-eaa5aed1abd9490dbb2e069f1d5d6e09
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());
    onClosedCaptor.getValue().run();

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.DEBUG)).anyMatch(log -> log.contains("WebSocket could not be closed gracefully"));
  }

  @Test
  void should_handle_ioexception_with_closed_output_message() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new IOException("closed output")));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-1fe00f46cf9145bc9909bf42f0f029e1
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());
    onClosedCaptor.getValue().run();

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.DEBUG)).anyMatch(log -> log.contains("WebSocket could not be closed gracefully"));
  }

  @Test
  void should_handle_ioexception_with_different_message() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new IOException("Connection reset")));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-e5a82776f5ae4d0db611a1761ee3a7e1
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());
    onClosedCaptor.getValue().run();

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.ERROR)).anyMatch(log -> log.contains("Cannot close the WebSocket output"));
  }

  @Test
  void should_handle_already_closed_websocket() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(true);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket, never()).sendClose(anyInt(), anyString());
  }

  @Test
  void should_handle_failed_websocket_future() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.completeExceptionally(new RuntimeException("Connection failed"));

    sonarCloudWebSocket.close("Test reason");

    assertThat(logTester.logs()).anyMatch(log -> log.contains("WebSocket connection was already closed, skipping close operation"));
  }

  @Test
  void should_handle_pending_websocket_future() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class)))
      .thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);

    sonarCloudWebSocket.close("Test reason");

    assertThat(logTester.logs()).anyMatch(log -> log.contains("WebSocket connection was still pending, cancelled"));
  }

  @Test
  void should_check_if_websocket_is_open() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isInputClosed()).thenReturn(false);
    when(mockWebSocket.isOutputClosed()).thenReturn(false);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    assertThat(sonarCloudWebSocket.isOpen()).isTrue();
  }

  @Test
  void should_return_false_when_websocket_is_closed() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isInputClosed()).thenReturn(true);
    when(mockWebSocket.isOutputClosed()).thenReturn(false);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    assertThat(sonarCloudWebSocket.isOpen()).isFalse();
  }

  @Test
  void should_return_false_when_websocket_future_is_not_done() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);

    assertThat(sonarCloudWebSocket.isOpen()).isFalse();
  }

  @Test
  void should_return_false_when_websocket_future_failed() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.completeExceptionally(new RuntimeException("Connection failed"));

    assertThat(sonarCloudWebSocket.isOpen()).isFalse();
  }

  @Test
  void should_return_false_when_websocket_future_is_cancelled() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.cancel(true);

    assertThat(sonarCloudWebSocket.isOpen()).isFalse();
  }

  @Test
  void should_handle_connection_ended_callback() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());

    //ACR-a7b538f7abb0415cb6404d3c0bc732ba
    onClosedCaptor.getValue().run();

    verify(connectionEndedRunnable).run();
  }

  @Test
  void should_not_call_connection_ended_callback_when_closing_initiated() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-746c55342d7c449491b13cbfaef1f6b5
    sonarCloudWebSocket.close("Test reason");

    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());

    //ACR-b3fd782308e340709f4910d654fad8c6
    onClosedCaptor.getValue().run();

    //ACR-f10b3036a2fb4cbbb44b31e6054d6347
    verify(connectionEndedRunnable, never()).run();
  }

} 
