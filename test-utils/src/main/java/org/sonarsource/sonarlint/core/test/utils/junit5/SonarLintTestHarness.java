/*
ACR-2867c2b872d6459f896c7d223d529438
ACR-bc00bfd6d91849a9a321d62c192581d5
ACR-2aca87255abd48c89aad085f4864fed4
ACR-e8e2ef8991784c8da00d79545eb1ec9d
ACR-0ad2467db0a642c1b2029347441f0192
ACR-eefc725e299046c3b6ce53e1ac5d9ca8
ACR-cc8bf31ed9b640e5b7d345815624be12
ACR-e57817d14a6c4b95bd47c18d2dd0421f
ACR-a4b75d1503f44e24a3b83101dab7be93
ACR-80d14ebbf56c4b1fa4b078a40a654541
ACR-718c9fca63404e1f816e3c02c9f1dbf2
ACR-87a3e44724be40808c2acc85727e3732
ACR-4bd57f16f571412abdea7f9eb25db6f4
ACR-c37bb706192f43eb9eb0ae0524c993ae
ACR-56ad3622f82d40fd9e65168340d1a9fd
ACR-4a1b6f171bc9492ea7c86eb3fb74d139
ACR-363e6d73bb43483dadc973dfc2cab342
 */
package org.sonarsource.sonarlint.core.test.utils.junit5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.server.ServerFixture;

public class SonarLintTestHarness extends TypeBasedParameterResolver<SonarLintTestHarness> implements BeforeAllCallback, AfterEachCallback, AfterAllCallback {
  private static final Logger LOG = Logger.getLogger(SonarLintTestHarness.class.getName());
  private static final long SHUTDOWN_TIMEOUT_SECONDS = 10;

  private final List<SonarLintTestRpcServer> backends = new ArrayList<>();
  private final List<ServerFixture.Server> servers = new ArrayList<>();
  private boolean isStatic;

  @Override
  public SonarLintTestHarness resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    return this;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    isStatic = true;
  }

  @Override
  public void afterAll(ExtensionContext context) {
    if (isStatic) {
      shutdownAll();
    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    if (!isStatic) {
      shutdownAll();
    }
  }

  private void shutdownAll() {
    //ACR-c4363ce097ee44aba7ace1f5ba2858e1
    for (SonarLintTestRpcServer backend : backends) {
      doShutdown(backend);
    }
    backends.clear();
    //ACR-27a397384e3d4c1297b92f79acd41f58
    for (ServerFixture.Server server : servers) {
      try {
        server.shutdown();
      } catch (Exception e) {
        //ACR-badd7162077b42db8f17fe5dfac2089d
        LOG.log(Level.WARNING, "Failed to shutdown server", e);
      }
    }
    servers.clear();
  }

  private static void doShutdown(SonarLintTestRpcServer backend) {
    try {
      CompletableFuture<Void> future = backend.shutdown();
      future.orTimeout(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .exceptionally(ex -> {
          LOG.log(Level.WARNING, "Error shutting down backend", ex);
          return null;
        })
        .join();
    } catch (CompletionException | IllegalStateException e) {
      //ACR-f424198f6eb14bd8a76d7e91a16499b9
      LOG.log(Level.WARNING, "Failed to shutdown backend", e);
    }
  }

  public SonarLintBackendFixture.SonarLintBackendBuilder newBackend() {
    return SonarLintBackendFixture.newBackend(backends::add);
  }

  public SonarLintBackendFixture.SonarLintClientBuilder newFakeClient() {
    return SonarLintBackendFixture.newFakeClient();
  }

  public ServerFixture.SonarQubeServerBuilder newFakeSonarQubeServer() {
    return ServerFixture.newSonarQubeServer(servers::add);
  }

  public ServerFixture.SonarQubeServerBuilder newFakeSonarQubeServer(String version) {
    return ServerFixture.newSonarQubeServer(servers::add, version);
  }

  public ServerFixture.SonarQubeCloudBuilder newFakeSonarCloudServer() {
    return ServerFixture.newSonarCloudServer(servers::add);
  }

  public void addBackend(SonarLintTestRpcServer backend) {
    backends.add(backend);
  }

  public void addServer(ServerFixture.Server server) {
    servers.add(server);
  }

  public List<SonarLintTestRpcServer> getBackends() {
    return backends;
  }

  public List<ServerFixture.Server> getServers() {
    return servers;
  }

  public void shutdown(SonarLintTestRpcServer backend) {
    if (backends.remove(backend)) {
      doShutdown(backend);
    }
  }
}
