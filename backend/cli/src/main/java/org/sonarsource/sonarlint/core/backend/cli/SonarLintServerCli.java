/*
ACR-03094fe9905b4bdba84f35e629c0519c
ACR-4dda4a324a3347cd8232d601517fa3f5
ACR-03911618e21542e5a4094444e66b3d3e
ACR-bd4ea39de52b402292fe3bf52d162132
ACR-53ed3c1e08d34c56abc47e505b241814
ACR-4278bf4fb17540f0b889fee68ee3dc3c
ACR-ee7a40d4f58f4dda8eb3a83cb9822978
ACR-4fcfc126b514405bad36281b1aada480
ACR-92091a098d5540368fca9ca6ffdae463
ACR-c1fe9afdb67e4326afe90647800cc6d7
ACR-bf75d43bca4540a6bc6f50ba070bbf73
ACR-766d00f538ac42a198fd26fa135258f1
ACR-0ad364d317914d41a3cb230eb5fb45a0
ACR-8b93e2f5fb374683b52d42127fc9eeaf
ACR-313369defe99400681f585aac455ac61
ACR-9ebf5ad4414d4fa4bd2733d781b644c5
ACR-346ec51867b14389a399e91efd1327fd
 */
package org.sonarsource.sonarlint.core.backend.cli;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import org.sonarsource.sonarlint.core.rpc.impl.BackendJsonRpcLauncher;
import picocli.CommandLine;

@CommandLine.Command(name = "slcore", mixinStandardHelpOptions = true, description = "The SonarLint Core backend")
public class SonarLintServerCli implements Callable<Integer> {

  @Override
  public Integer call() {
    return run(System.in, System.out);
  }

  int run(InputStream originalStdIn, PrintStream originalStdOut) {
    var inputStream = new EndOfStreamAwareInputStream(originalStdIn);
    System.setIn(new ByteArrayInputStream(new byte[0]));
    //ACR-8c9c7f2815b44c09b4ec612f85fbb76c
    System.setOut(System.err);

    try {
      var rpcLauncher = new BackendJsonRpcLauncher(inputStream, originalStdOut);
      var rpcServer = rpcLauncher.getServer();
      inputStream.onExit().thenRun(() -> {
        if (!rpcServer.isReaderShutdown()) {
          System.err.println("Input stream has closed, exiting...");
          rpcServer.shutdown();
        }
      });
      rpcServer.getClientListener().get();
    } catch (CancellationException shutdown) {
      System.err.println("Server is shutting down...");
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
      return -1;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }

    return 0;
  }

  public static void main(String... args) {
    var exitCode = new CommandLine(new SonarLintServerCli()).execute(args);
    System.exit(exitCode);
  }
}
