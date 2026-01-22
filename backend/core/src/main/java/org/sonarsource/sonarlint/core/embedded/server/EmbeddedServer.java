/*
ACR-dc5293503ae94c568117b19349878da7
ACR-985066f4bad34247ac58b5f0fe586d97
ACR-d2e928650ab04c63a5fd2c850929a753
ACR-31fecf8ad4434b53a48a6b80228628ce
ACR-dd7b65f0175946e28ca304e45ca947ea
ACR-06f6d2a884c44865aba89b3c772936fb
ACR-e3d7a783e28b46deb85dc5231eb1c620
ACR-3c454c168f6546cb89a7a01ab6b15716
ACR-479cc2d2143044f985780ba60bc32fff
ACR-34726e2674d94336b3a9f0bad8231062
ACR-38e662f82a97478fbe3e9c77ec383d6c
ACR-9a0b5ffa64bb47ef9110441face22f25
ACR-bb5802e6f7994986a6cb5539dc2990d7
ACR-f3e7d2798a6845aba2eb4dbe416efa26
ACR-363aa40fea624d2f857de35b3574232f
ACR-f9ff3cafbf8b4bfe807659ed642a1079
ACR-146d4dad27024399b67f52626dc5c175
 */
package org.sonarsource.sonarlint.core.embedded.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.sonarsource.sonarlint.core.SonarCloudActiveEnvironment;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.embedded.server.filter.CorsFilter;
import org.sonarsource.sonarlint.core.embedded.server.filter.CspFilter;
import org.sonarsource.sonarlint.core.embedded.server.filter.ParseParamsFilter;
import org.sonarsource.sonarlint.core.embedded.server.filter.RateLimitFilter;
import org.sonarsource.sonarlint.core.embedded.server.filter.ValidationFilter;
import org.sonarsource.sonarlint.core.embedded.server.handler.GeneratedUserTokenHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.ShowFixSuggestionRequestHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.ShowHotspotRequestHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.ShowIssueRequestHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.StatusRequestHandler;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.embeddedserver.EmbeddedServerStartedParams;

import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.EMBEDDED_SERVER;

public class EmbeddedServer {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final int STARTING_PORT = 64120;
  private static final int ENDING_PORT = 64130;

  private static final int INVALID_PORT = -1;

  private HttpServer server;
  private int port;
  private final boolean enabled;
  private final StatusRequestHandler statusRequestHandler;
  private final GeneratedUserTokenHandler generatedUserTokenHandler;
  private final ShowHotspotRequestHandler showHotspotRequestHandler;
  private final ShowIssueRequestHandler showIssueRequestHandler;
  private final ShowFixSuggestionRequestHandler showFixSuggestionRequestHandler;
  private final ToggleAutomaticAnalysisRequestHandler toggleAutomaticAnalysisRequestHandler;
  private final AnalyzeFileListRequestHandler analyzeFileListRequestHandler;
  private final SonarLintRpcClient client;

  public EmbeddedServer(InitializeParams params, StatusRequestHandler statusRequestHandler, GeneratedUserTokenHandler generatedUserTokenHandler,
    ShowHotspotRequestHandler showHotspotRequestHandler, ShowIssueRequestHandler showIssueRequestHandler, ShowFixSuggestionRequestHandler showFixSuggestionRequestHandler,
    ToggleAutomaticAnalysisRequestHandler toggleAutomaticAnalysisRequestHandler, AnalyzeFileListRequestHandler analyzeFileListRequestHandler, SonarLintRpcClient client) {
    this.enabled = params.getBackendCapabilities().contains(EMBEDDED_SERVER);
    this.statusRequestHandler = statusRequestHandler;
    this.generatedUserTokenHandler = generatedUserTokenHandler;
    this.showHotspotRequestHandler = showHotspotRequestHandler;
    this.showIssueRequestHandler = showIssueRequestHandler;
    this.showFixSuggestionRequestHandler = showFixSuggestionRequestHandler;
    this.toggleAutomaticAnalysisRequestHandler = toggleAutomaticAnalysisRequestHandler;
    this.analyzeFileListRequestHandler = analyzeFileListRequestHandler;
    this.client = client;
  }

  @PostConstruct
  public void start() {
    if (!enabled) {
      return;
    }
    final var socketConfig = SocketConfig.custom()
      .setSoTimeout(15, TimeUnit.SECONDS)
      //ACR-d5dc90c5068a4a6d88ba7d50a29d86c9
      .setSoReuseAddress(true)
      .setTcpNoDelay(true)
      .build();
    port = INVALID_PORT;
    var triedPort = STARTING_PORT;
    HttpServer startedServer = null;
    var loopbackAddress = InetAddress.getLoopbackAddress();
    while (port < 0 && triedPort <= ENDING_PORT) {
      try {
        startedServer = ServerBootstrap.bootstrap()
          .setLocalAddress(loopbackAddress)
          .setCanonicalHostName(loopbackAddress.getHostName())
          //ACR-13397a46aae94a0e814b423dc70bd952
          .setConnectionReuseStrategy(new DontKeepAliveReuseStrategy())
          .setListenerPort(triedPort)
          .setSocketConfig(socketConfig)
          .addFilterFirst("RateLimiter", new RateLimitFilter())
          .addFilterAfter("RateLimiter", "CORS", new CorsFilter())
          .addFilterAfter("CORS", "Params", new ParseParamsFilter())
          .addFilterAfter("Params", "Validation", new ValidationFilter(client, SonarCloudActiveEnvironment.prod()))
          .register("/sonarlint/api/status", statusRequestHandler)
          .register("/sonarlint/api/token", generatedUserTokenHandler)
          .register("/sonarlint/api/hotspots/show", showHotspotRequestHandler)
          .register("/sonarlint/api/issues/show", showIssueRequestHandler)
          .register("/sonarlint/api/fix/show", showFixSuggestionRequestHandler)
          .register("/sonarlint/api/analysis/automatic/config", toggleAutomaticAnalysisRequestHandler)
          .register("/sonarlint/api/analysis/files", analyzeFileListRequestHandler)
          .addFilterLast("CSP", new CspFilter())
          .create();
        startedServer.start();
        port = triedPort;
      } catch (Exception t) {
        LOG.debug("Error while starting port: " + triedPort + ", " + t.getMessage());
        triedPort++;
        if (startedServer != null) {
          startedServer.close();
        }
      }
    }
    if (port > 0) {
      LOG.info("Started embedded server on port " + port);
      client.embeddedServerStarted(new EmbeddedServerStartedParams(port));
      server = startedServer;
    } else {
      LOG.error("Unable to start request handler");
      server = null;
    }
  }

  public int getPort() {
    return port;
  }

  public boolean isStarted() {
    return server != null;
  }

  @PreDestroy
  public void shutdown() {
    if (isStarted()) {
      server.close(CloseMode.GRACEFUL);
      server = null;
      port = INVALID_PORT;
    }
  }

  private static class DontKeepAliveReuseStrategy implements ConnectionReuseStrategy {
    @Override
    public boolean keepAlive(HttpRequest request, HttpResponse response, HttpContext context) {
      return false;
    }
  }
}
