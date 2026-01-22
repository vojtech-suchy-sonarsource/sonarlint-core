/*
ACR-e051e7b80d6c4271bd730c624abcbe2d
ACR-1294f22e61064286be79ef8cd252114a
ACR-fd026375359047f6a7e4463fee9e7a33
ACR-17a6396ccafe45018af4474b316d6d8a
ACR-35546e3792424328aca73943db9a705f
ACR-f1f1d9c13ab34470a04be62f39405fa1
ACR-6a0af0396e564952bb4e3bef528dbaf6
ACR-39b8c075081542e2b9df849b8bc5817a
ACR-f3a42f0e353c4ef99407d67356da1167
ACR-7a49716d3b1947a1b4c4197bea7e3de1
ACR-220cfe5892b94a92b055753f96c0b36c
ACR-d0d60bc5fb7849909349cce02a76ff80
ACR-0e6714ec67104ec4b92b3b7182e72583
ACR-491e0f8a2a7b4607b3b652372f1193c5
ACR-06b183732f2c41d68012b22887ff01a2
ACR-dd484bab24a84904a45539c9ffa65446
ACR-04ef1437684d488189209699eb289a1b
 */
package org.sonarsource.sonarlint.core.http;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebSocketClientTest {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static ExecutorService executor;

  @BeforeAll
  static void setUp() {
    executor = Executors.newSingleThreadExecutor();
  }

  @AfterAll
  static void tearDown() {
    if (executor != null) {
      executor.shutdown();
      try {
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
          executor.shutdownNow();
        }
      } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
  }

  @Test
  void should_validate_null_uri() {
    var client = new WebSocketClient("test-agent", "token", executor);
    
    var future = client.createWebSocketConnection(null, message -> {}, () -> {});
    
    assertThat(future).isCompletedExceptionally();
    assertThatThrownBy(future::get)
      .hasCauseInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("WebSocket URI must use 'ws' or 'wss' scheme");
  }

  @Test
  void should_validate_invalid_scheme() {
    var client = new WebSocketClient("test-agent", "token", executor);
    
    var future = client.createWebSocketConnection(URI.create("http://example.com"), message -> {}, () -> {});
    
    assertThat(future).isCompletedExceptionally();
    assertThatThrownBy(future::get)
      .hasCauseInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("WebSocket URI must use 'ws' or 'wss' scheme");
  }

  @Test
  void should_accept_valid_ws_uri() {
    var client = new WebSocketClient("test-agent", "token", executor);
    
    var future = client.createWebSocketConnection(URI.create("ws://example.com"), message -> {}, () -> {});

    assertThat(future).isNotCompletedExceptionally();
  }

  @Test
  void should_accept_valid_wss_uri() {
    var client = new WebSocketClient("test-agent", "token", executor);
    
    var future = client.createWebSocketConnection(URI.create("wss://example.com"), message -> {}, () -> {});

    assertThat(future).isNotCompletedExceptionally();
  }

}
