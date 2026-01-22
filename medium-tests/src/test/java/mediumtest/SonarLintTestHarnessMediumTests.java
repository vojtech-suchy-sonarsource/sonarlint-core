/*
ACR-223b8212d5714b3b955527eff4fe70a3
ACR-0d18329fe6cd4107881184e81ae2cb83
ACR-50463eac736143b8a3c61e3a40ae8628
ACR-6c3ed48bac9b47abb27c6b4a770f925a
ACR-28c73dfe8a1c49839598442780628e38
ACR-70e9618ec87a4603b3fd2776f654b308
ACR-cd8125f827f643aeb113be076ce5e351
ACR-a9a5cbbe72de43d9be021186a8fd04a8
ACR-af64b8098f27459287a64e9f4ac7cfba
ACR-f8f3600c775a4fd39070b9994fa91a54
ACR-cd9e3c03fd7947f098b7c97a3bbf4ce3
ACR-d5087b9cc65d4e0cae374a1c2c59c4fd
ACR-62e36ef465b04892b86d43f6201f4e1d
ACR-989f84a84ffd40b5b81f972ecadf479e
ACR-a8942de0bba043748bde4fd964278b4e
ACR-5890b0a1ff7b47b083fd185ca08c131b
ACR-d4ffe6ab3dfb46548680aa6a42d67d64
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
