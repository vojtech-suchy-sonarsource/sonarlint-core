/*
ACR-e66beedba3794c629dae08f622399a4f
ACR-e38c9bead58f4f949e5b4125d71cdece
ACR-818a5e8819b44ca7b7006246f71be761
ACR-9198516f8c4a410ab9eb2485449fde42
ACR-2bd6a22ffed448ae904044d4edf2c44f
ACR-e04ca75ae4614acebcbb9276fe0cd4a4
ACR-9a50186a673c4b1d939af2f98c475175
ACR-eac9800c78a14728af7494fb63d0d7fa
ACR-7639f8ebc14a4ad89e09bda39224de24
ACR-38d485bb7a2447c58aa3624e60832d6c
ACR-0e8f137ad65e4237b4439a4d2eec86cb
ACR-6fda876b1e7343c780d10d6d91e30c9d
ACR-2132f940468e406799b3d50fe25abcf6
ACR-c7fd3bd8f826430db92685ab0505c64b
ACR-dde73ccbb05a4fef9e59b46bbeef6a8d
ACR-f87df42fa62f40739a2d6e2767331802
ACR-443a3e22344547119050054c066aa57b
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
      //ACR-e2197c408eed4776b28b159abd840fde
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
          //ACR-407c475c61f94a44b669ddcf964d7cfb
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
