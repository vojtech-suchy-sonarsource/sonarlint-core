/*
ACR-f3b8cc1d72d849cf8a162b68fb5ada09
ACR-fb0d423ea001494b8cb099c416465c0b
ACR-e342e75f2db945fe838454f9ca074f1c
ACR-87a7e4013e9c49749bfe638bd834c86a
ACR-728a567226624c769e653d5e9876c376
ACR-c7247bb5de8f44f2a9a4e2ac2fdd60ae
ACR-0f38c4118cb641fdbc3446b70eeaf5c4
ACR-493e8b0cc9374989a61f140e0494010c
ACR-2e410bba60714ceb8aaecd2dfa779dc8
ACR-4b7de856a4704355bcb343371fbf56a6
ACR-939caa80815a45caa0ca65fef975d565
ACR-acb1ed53069f40f7a1a423f109ada55e
ACR-9b9969f27eca49d781b505032884c14a
ACR-dca6a1437cb940c5b45bdc72546645d2
ACR-813f0b024ae9483f8cf048a647448a4c
ACR-167918c11a1b40838d449b8aed67f989
ACR-83e4b48b1f1d49d9b1a6ad56a0820519
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
    //ACR-3bcce5c7e21f47d79275b6d3b3ab00cd
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
