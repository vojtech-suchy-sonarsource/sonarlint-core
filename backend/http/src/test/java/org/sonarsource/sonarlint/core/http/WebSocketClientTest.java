/*
ACR-bdb5a859dff74a4496e5122b35344741
ACR-5e52381d8d1a472495bb316ee4d00ca8
ACR-ee795c8299c04f2e857ce1490fc0686f
ACR-beb846b3abf04ae3a2861598c3a92309
ACR-e23ab68c3acd48c4a2f051079a6cfbf8
ACR-3e27ca7e534b4d6f8a37dc005c082b14
ACR-a188348f64d54b248236d3aa7e1630ab
ACR-0cc99e0df0f045399632db79a1de75f8
ACR-a30837c35f4843a0972bd8b35bef4d59
ACR-4d3f8c9fc95847daa87bcc7a983f9037
ACR-04f1057f6de84df5ba298cf999fd316e
ACR-98ab1acc8a134566af4c69bcbd140411
ACR-52141f4b5a324c4ab34ff360b6bbe98a
ACR-3321ea7579a147a39cab80aee83f9fa1
ACR-48528b8b93ed4385ae91be5fbdd75e81
ACR-918680e548c04dceaa8b588d632f62a4
ACR-eafd5a5a482e489187a3058befa4105c
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
