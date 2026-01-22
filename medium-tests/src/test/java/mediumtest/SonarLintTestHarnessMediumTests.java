/*
ACR-9124e5bb3ab4438f932d256aa2fe4450
ACR-d62e1dec84ef48278655bc53b7325d65
ACR-d0d34246482e43b3a0060bac18c779b4
ACR-79f68dfec1b148589bc81feaeb00ccd1
ACR-71b2491f5052420092199a15a07b3e00
ACR-214f52f4a3a3481784e6413be5ca8f5d
ACR-fd4461fcfb6d4e2cb5b652c49dcdde35
ACR-3a540782bbb54914a990b0a12cd20d08
ACR-8cc2a62314d9456fafd0ceb91848f077
ACR-926a7198c297422d808cd1182d9eec51
ACR-827a8e45fdb34b5597051b6bb488df4d
ACR-4af9766b7daf4148a3618c63962163d8
ACR-40257eac6e7e44f79630f56fc57dddb4
ACR-ced6e7e3f4bb4e958d7b9a97b36e4ad2
ACR-a20c8fb7c90b4414863f8fa40a1ae509
ACR-8d87046e9abc46a28d50ec445531c500
ACR-0aca736538574997a9f48eb35e4b456b
 */
package mediumtest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import org.sonarsource.sonarlint.core.test.utils.server.ServerFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SonarLintTestHarnessTest {

  private SonarLintTestHarness harness;
  private TestLogHandler logHandler;

  @BeforeEach
  void setUp() {
    harness = new SonarLintTestHarness();

    var testLogger = Logger.getLogger(SonarLintTestHarness.class.getName());
    logHandler = new TestLogHandler();
    testLogger.addHandler(logHandler);
    testLogger.setLevel(Level.ALL);
  }

  @Test
  void should_shutdown_normally() throws IOException {
    SonarLintTestRpcServer backend = new TestBackend(mock(SonarLintRpcClientDelegate.class), CompletableFuture.completedFuture(null));
    harness.addBackend(backend);
    TestServer server = new TestServer();
    harness.addServer(server);

    harness.afterEach(emptyContext());

    assertThat(harness.getBackends()).isEmpty();
    assertThat(harness.getServers()).isEmpty();
    assertThat(server.isShutdownCalled()).isTrue();
  }

  @Test
  void should_handle_exceptionally_callback() throws IOException {
    CompletableFuture<Void> failingFuture = new CompletableFuture<>();
    failingFuture.completeExceptionally(new RuntimeException("Simulated exception"));
    SonarLintTestRpcServer backend = new TestBackend(mock(SonarLintRpcClientDelegate.class), failingFuture);
    harness.addBackend(backend);
    TestServer server = new TestServer();
    harness.addServer(server);

    harness.afterEach(emptyContext());

    assertThat(harness.getBackends()).isEmpty();
    assertThat(harness.getServers()).isEmpty();
    assertThat(server.isShutdownCalled()).isTrue();
    assertThat(logHandler.getRecords()).anySatisfy(logRecord -> {
      assertThat(logRecord.getLevel()).isEqualTo(Level.WARNING);
      assertThat(logRecord.getMessage()).contains("Error shutting down backend");
      assertThat(logRecord.getThrown()).isNotNull();
    });
  }

  @Test
  void should_handle_catch_block_exceptions() throws IOException {
    SonarLintTestRpcServer backend1 = new ThrowingBackend(mock(SonarLintRpcClientDelegate.class),
      new CompletionException("Simulated completion exception", new RuntimeException()));
    SonarLintTestRpcServer backend2 = new ThrowingBackend(mock(SonarLintRpcClientDelegate.class), new IllegalStateException("Simulated illegal state exception"));
    harness.addBackend(backend1);
    harness.addBackend(backend2);
    TestServer server = new TestServer();
    harness.addServer(server);

    harness.afterEach(emptyContext());

    assertThat(harness.getBackends()).isEmpty();
    assertThat(harness.getServers()).isEmpty();
    assertThat(server.isShutdownCalled()).isTrue();
    assertThat(logHandler.getRecords()).anySatisfy(logRecord -> {
      assertThat(logRecord.getLevel()).isEqualTo(Level.WARNING);
      assertThat(logRecord.getMessage()).contains("Failed to shutdown backend");
      assertThat(logRecord.getThrown()).isInstanceOf(CompletionException.class);
    });
    assertThat(logHandler.getRecords()).anySatisfy(logRecord -> {
      assertThat(logRecord.getLevel()).isEqualTo(Level.WARNING);
      assertThat(logRecord.getMessage()).contains("Failed to shutdown backend");
      assertThat(logRecord.getThrown()).isInstanceOf(IllegalStateException.class);
    });
  }

  @Test
  void should_handle_server_exceptions() throws IOException {
    SonarLintTestRpcServer testBackend = new TestBackend(mock(SonarLintRpcClientDelegate.class), CompletableFuture.completedFuture(null));
    harness.addBackend(testBackend);
    ServerFixture.Server throwingServer1 = new ThrowingTestServer(new RuntimeException("Server 1 shutdown error"));
    ServerFixture.Server throwingServer2 = new ThrowingTestServer(new RuntimeException("Server 2 shutdown error"));
    harness.addServer(throwingServer1);
    harness.addServer(throwingServer2);

    harness.afterEach(emptyContext());

    assertThat(harness.getBackends()).isEmpty();
    assertThat(harness.getServers()).isEmpty();
    assertThat(logHandler.getRecords()).anySatisfy(logRecord -> {
      assertThat(logRecord.getLevel()).isEqualTo(Level.WARNING);
      assertThat(logRecord.getMessage()).contains("Failed to shutdown server");
      assertThat(logRecord.getThrown()).isInstanceOf(RuntimeException.class);
      assertThat(logRecord.getThrown().getMessage()).contains("Server 1 shutdown error");
    });
    assertThat(logHandler.getRecords()).anySatisfy(logRecord -> {
      assertThat(logRecord.getLevel()).isEqualTo(Level.WARNING);
      assertThat(logRecord.getMessage()).contains("Failed to shutdown server");
      assertThat(logRecord.getThrown()).isInstanceOf(RuntimeException.class);
      assertThat(logRecord.getThrown().getMessage()).contains("Server 2 shutdown error");
    });
  }

  @Test
  void should_handle_multiple_backends_and_servers() throws IOException {
    SonarLintTestRpcServer backend1 = new TestBackend(mock(SonarLintRpcClientDelegate.class), CompletableFuture.completedFuture(null));
    CompletableFuture<Void> failingFuture = new CompletableFuture<>();
    failingFuture.completeExceptionally(new RuntimeException("Backend 2 error"));
    SonarLintTestRpcServer backend2 = new TestBackend(mock(SonarLintRpcClientDelegate.class), failingFuture);
    SonarLintTestRpcServer backend3 = new ThrowingBackend(mock(SonarLintRpcClientDelegate.class), new IllegalStateException("Backend 3 error"));
    harness.addBackend(backend1);
    harness.addBackend(backend2);
    harness.addBackend(backend3);
    TestServer server1 = new TestServer();
    ServerFixture.Server server2 = new ThrowingTestServer(new RuntimeException("Server 2 error"));
    harness.addServer(server1);
    harness.addServer(server2);

    harness.afterEach(emptyContext());

    assertThat(harness.getBackends()).isEmpty();
    assertThat(harness.getServers()).isEmpty();
    assertThat(server1.isShutdownCalled()).isTrue();
    assertThat(logHandler.getRecords()).anySatisfy(logRecord -> assertThat(logRecord.getMessage()).contains("Error shutting down backend"));
    assertThat(logHandler.getRecords()).anySatisfy(logRecord -> assertThat(logRecord.getMessage()).contains("Failed to shutdown backend"));
    assertThat(logHandler.getRecords()).anySatisfy(logRecord -> assertThat(logRecord.getMessage()).contains("Failed to shutdown server"));
  }

  private static ExtensionContext emptyContext() {
    var context = mock(ExtensionContext.class);
    when(context.getTestMethod()).thenReturn(Optional.empty());
    return context;
  }

  static class TestLogHandler extends Handler {
    private final List<LogRecord> logRecords = new java.util.ArrayList<>();

    @Override
    public void publish(LogRecord logRecord) {
      logRecords.add(logRecord);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    public List<LogRecord> getRecords() {
      return logRecords;
    }
  }

  private static class TestBackend extends SonarLintTestRpcServer {
    private final CompletableFuture<Void> shutdownFuture;

    TestBackend(SonarLintRpcClientDelegate client, CompletableFuture<Void> shutdownFuture) throws IOException {
      super(client);
      this.shutdownFuture = shutdownFuture;
    }

    @Override
    public CompletableFuture<Void> shutdown() {
      return shutdownFuture;
    }
  }

  private static class ThrowingBackend extends SonarLintTestRpcServer {
    private final RuntimeException exceptionToThrow;

    ThrowingBackend(SonarLintRpcClientDelegate client, RuntimeException exceptionToThrow) throws IOException {
      super(client);
      this.exceptionToThrow = exceptionToThrow;
    }

    @Override
    public CompletableFuture<Void> shutdown() {
      throw exceptionToThrow;
    }
  }

  static class TestServer extends ServerFixture.Server {
    private boolean shutdownCalled = false;

    public TestServer() {
      super(null, null, null, null, null, null, null, null, null, false, null, null, null, null);
    }

    @Override
    public void shutdown() {
      shutdownCalled = true;
    }

    public boolean isShutdownCalled() {
      return shutdownCalled;
    }
  }

  static class ThrowingTestServer extends ServerFixture.Server {
    private final RuntimeException exceptionToThrow;

    ThrowingTestServer(RuntimeException exceptionToThrow) {
      super(null, null, null, null, null, null, null, null, null, false, null, null, null, null);
      this.exceptionToThrow = exceptionToThrow;
    }

    @Override
    public void shutdown() {
      throw exceptionToThrow;
    }
  }

}
