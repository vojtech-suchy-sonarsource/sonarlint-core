/*
ACR-837478c987de4a6bbcc7f166c971bf56
ACR-b3e0cb147b454a91a78949ecef1058be
ACR-ff190eaad1ab4f1a99ec597e1f41a7f8
ACR-9706193d58914d24b9b27ddcd19299a8
ACR-d37bfab639d74c17b8dcd62f6c720133
ACR-8feec723b77249159953d2c64549c287
ACR-9b3e25670833433e85d46a5ba1c4e971
ACR-4d308b580d214adfb5f0619336d30885
ACR-7657d89d3da14328925bd06d1756260f
ACR-767196c64b954bd08a50cfc2bf8a2369
ACR-fe6d5d7e146e4cf1b6c73d0f1694a01a
ACR-b74a3f739abe477e9191dc01ce6960a6
ACR-f80666591e544319a3707270bdafdd40
ACR-4eb2be9ea57a40ee99946922bcf5ef4c
ACR-a0c3b97c8e54455bb5ea8f6aacf84da6
ACR-7fece94dee574b7d949625b009c52ce3
ACR-2ee6545a633d45bc9fd650b9e08102c5
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
    //ACR-ecdc6faf18f140ff91d74958850a4f93
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
