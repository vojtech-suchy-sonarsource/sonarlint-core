/*
ACR-6a089314758643238154fbb92fd4850b
ACR-fd2e436de88f4e66bd77203a3b941b88
ACR-5baf0eb55ee943438c07b581f26e313f
ACR-3261ba76005d4935bb4ad0374601e84b
ACR-abf58c7e5b2f45aab854f0d993ef4b86
ACR-b90f3096a4ba4e848daf0d01f6e54893
ACR-66e713f4e74646afad04e08b54e26ede
ACR-f9684119cf334fc09da60361ad72f50d
ACR-a31fb5a8bd79494f9e0262fc713419e3
ACR-09cb1debcf27475d9923aea7a9371713
ACR-9ed9ed51eaf640ed866f7c2801ba0bc2
ACR-6e51922f7a244859bd6b6c64f847dd2a
ACR-2b3cc6a694a94eeea4ef2d816d4ca0f0
ACR-391bd7231bf849ed9dc85d860f3d9520
ACR-86fd3390a1764858b9f899b6c9799e29
ACR-67aedcbc3484440b8de7d685b53c9188
ACR-0eb8a387796743e8a7ee1aa5f992293b
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;

public class Sloop {
  private final SonarLintRpcServer rpcServer;
  private final Process process;

  public Sloop(SonarLintRpcServer rpcServer, Process process) {
    this.rpcServer = rpcServer;
    this.process = process;
  }

  public CompletableFuture<Void> shutdown() {
    return rpcServer.shutdown();
  }

  public SonarLintRpcServer getRpcServer() {
    return rpcServer;
  }

  /*ACR-29a87499ba2847adaa2241b6bb2e7c70
ACR-e2232ca3c3e2483d92a9cf66bb830cc4
   */
  public CompletableFuture<Integer> onExit() {
    return process.onExit().thenApply(Process::exitValue);
  }

  public boolean isAlive() {
    return process.isAlive();
  }

  public long getPid() {
    return process.pid();
  }
}
