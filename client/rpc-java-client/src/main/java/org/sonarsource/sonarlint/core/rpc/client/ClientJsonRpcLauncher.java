/*
ACR-e992a93a6caf4f9d8d765bd9da36b3d4
ACR-d24c8dffe8ae4622b68682a960c41cdb
ACR-4911b806a0714cdb8c8f7e90825a73a7
ACR-56412b361e20493eb6bdc3de1b3182ef
ACR-d0ec344880a744bf87950e3f84b6a473
ACR-4b664b450730486bbf35ba97fbbfbe28
ACR-badfa4fc5ed44957a638d1a422fa8474
ACR-7b36f78036ec4eff9b648f17f1edb8ba
ACR-ae211e2503164aab8a1996a7c225a3e6
ACR-b506eccd250f40df80e65474a0623782
ACR-61819712dd8b46b7a716235e43f3100c
ACR-92e9a7e415ab450886a8e034455b6809
ACR-db938c5c94644e299a8122f475402e7b
ACR-76042561c41045fc964a55242175565f
ACR-94a5cd78ccd8485ebd1a2e4dd79aea5e
ACR-36cd5423405743c3a77b0aa1f521ab63
ACR-a2e38ab1edd340f4bf354a9848e3c0c4
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.rpc.protocol.RpcErrorHandler;
import org.sonarsource.sonarlint.core.rpc.protocol.SingleThreadedMessageConsumer;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintLauncherBuilder;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;

public class ClientJsonRpcLauncher implements Closeable {
  private final SonarLintRpcServer serverProxy;
  private final Future<Void> future;
  private final ExecutorService messageReaderExecutor;
  private final ExecutorService messageWriterExecutor;
  private final ExecutorService requestAndNotificationsSequentialExecutor;
  private final ExecutorService requestsExecutor;

  public ClientJsonRpcLauncher(InputStream in, OutputStream out, SonarLintRpcClientDelegate clientDelegate) {
    messageReaderExecutor = Executors.newCachedThreadPool(r -> {
      var t = new Thread(r);
      t.setName("Client message reader");
      return t;
    });
    messageWriterExecutor = Executors.newCachedThreadPool(r -> {
      var t = new Thread(r);
      t.setName("Client message writer");
      return t;
    });
    this.requestAndNotificationsSequentialExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "SonarLint Client RPC sequential executor"));
    this.requestsExecutor = Executors.newCachedThreadPool(r -> new Thread(r, "SonarLint Client RPC request executor"));
    var client = new SonarLintRpcClientImpl(clientDelegate, requestsExecutor, requestAndNotificationsSequentialExecutor);
    var clientLauncher = new SonarLintLauncherBuilder<SonarLintRpcServer>()
      .setLocalService(client)
      .setRemoteInterface(SonarLintRpcServer.class)
      .setInput(in)
      .setOutput(out)
      .setExecutorService(messageReaderExecutor)
      .wrapMessages(m -> new SingleThreadedMessageConsumer(m, messageWriterExecutor,
        ex -> client.logClientSideError("Error consuming RPC message", ex)))
      .traceMessages(getMessageTracer())
      .setExceptionHandler(RpcErrorHandler::handleError)
      .create();

    this.serverProxy = clientLauncher.getRemoteProxy();
    this.future = clientLauncher.startListening();
  }

  private static PrintWriter getMessageTracer() {
    if ("true".equals(System.getProperty("sonarlint.debug.rpc"))) {
      try {
        return new PrintWriter(Paths.get(System.getProperty("user.home")).resolve(".sonarlint").resolve("rpc_client_session.log").toFile(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        System.err.println("Cannot write rpc debug logs file");
        e.printStackTrace();
      }
    }
    return null;
  }

  public SonarLintRpcServer getServerProxy() {
    return serverProxy;
  }

  @Override
  public void close() {
    requestsExecutor.shutdown();
    requestAndNotificationsSequentialExecutor.shutdown();
    //ACR-96db75ad3b744b1bafc360d0d06b14f8
    future.cancel(true);
    messageReaderExecutor.shutdownNow();
    messageWriterExecutor.shutdownNow();
    try {
      if (!messageReaderExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
        throw new IllegalStateException("Unable to terminate the client message reader thread in a timely manner");
      }
      if (!messageWriterExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
        throw new IllegalStateException("Unable to terminate the client message writer thread in a timely manner");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted!", e);
    }
  }
}
