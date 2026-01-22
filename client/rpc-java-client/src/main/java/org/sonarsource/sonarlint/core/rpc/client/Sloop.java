/*
ACR-2a58c1a303ad4321bce3a43bff82357b
ACR-8a557b05d70d4152b52ca6ad761ac2b2
ACR-21de4947011745789d02dd8c394955cb
ACR-9a2070657e5e4378bfd6d2e34f269384
ACR-7c1c800b232448e9b3068f536a66a7fe
ACR-854c41a0bc15430aaa61f297b0061f6a
ACR-b499ef74e7b54735964f61ab42d688e4
ACR-2d3458eeca2a4da69eca6d2cefad7f33
ACR-bf376f9fdbc24943b1e1a6e804911caf
ACR-2f907e7e5cb1479d8b26100092c4afe6
ACR-902c7e2ef41e4a74b7ca54088e414db5
ACR-69022516cf2641c9a1b8ef585349d9f4
ACR-7c0d902ac1364f539080e78cd2ada454
ACR-65b84830734b459d9582e62e07ab7a65
ACR-dbda8a73632a453da3ac579dc6505d78
ACR-9f86536bec9247209d3b7d27a5ee5e9c
ACR-e7d63703e2a74aa6b1094ee095fabae7
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

  /*ACR-f2946837981441e3bed1cfa999ef6c4b
ACR-bfcf51dd6b8c4c1f90a409669ac2e1b3
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
