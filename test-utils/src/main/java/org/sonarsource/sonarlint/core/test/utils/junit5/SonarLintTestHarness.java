/*
ACR-de23dbbef2374f6f8ce35f13859eb51d
ACR-0f1b681489ab4874a57072244d4059bb
ACR-6b4553c1770d4df288262b0ed27ad764
ACR-6c26b5995d4942e28bc9c08216dce58e
ACR-8022e6788f024bf0aca44309f2e9967c
ACR-e5e5b6ea41764da2a1e73b0c4e3b425a
ACR-0f15692132f94d149451ff370c1c7c85
ACR-cf699a1608e1472ea9b7c20511c456f9
ACR-ac00b4d750ef422dbd1214c1538955b3
ACR-bef71afdea754474bcc9f6b14eb805ed
ACR-93394d650c95465b8df298401a21360e
ACR-8bdaefd9f10d4dd48aee6c505f5a5921
ACR-fa4c531dd7774d3eada739df1a5f8151
ACR-07c6506a4708455ea318ae8567208904
ACR-b9342c9406714bb3972b330a4944d088
ACR-238594d7f96e463795a775859979f471
ACR-67f40367824b48fa9b34f0ec9b688ff6
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
    //ACR-fea920f007d44473b22edf0753cb0abe
    for (SonarLintTestRpcServer backend : backends) {
      doShutdown(backend);
    }
    backends.clear();
    //ACR-ba1f1deb6670434f95431bceb50f267f
    for (ServerFixture.Server server : servers) {
      try {
        server.shutdown();
      } catch (Exception e) {
        //ACR-a8e552b980be409ebd0674e8e7f208cd
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
      //ACR-96cc3e1431af4e4180aecc965ebb7635
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
