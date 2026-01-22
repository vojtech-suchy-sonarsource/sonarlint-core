/*
ACR-6a29a56ffb254995a07287ff1696841c
ACR-623977b61b3d46ae8bed545575bed300
ACR-938b0062a4d34296baf49c65c53f196c
ACR-7bddda280d354ad799a6d1dfd812f9f8
ACR-1967103af85d42b5964648e0ccb400f6
ACR-245096bb74e74505a0991e8d3de4fa0d
ACR-c6dea0fde2ea43a7836384c87600cc4e
ACR-cfcf8203aae147928b7a7a8602efa80e
ACR-046fad0029b049fb9ce2573fee92b2f0
ACR-9c3b1e2121294125abb94d867fc0b76b
ACR-2f3721f3841d49f4872e26800e1c09ba
ACR-a5eb4ed0382d4e478d7fdbb445a55c8a
ACR-7a3d457c5f3243ca9d2f9487ce2bba40
ACR-406f9fad3d3d42eabae88b408eb52bc7
ACR-34e5b1f90f6c41c59565d994a3c7bb46
ACR-96caf678aaf54d37a2611d3992bd4b34
ACR-5df6c1fc87a54bd898506462a21f234f
 */
package org.sonarsource.sonarlint.core.analysis.command;

import java.util.concurrent.atomic.AtomicLong;
import org.sonarsource.sonarlint.core.analysis.container.global.ModuleRegistry;

public abstract class Command {
  private static final AtomicLong analysisGlobalNumber = new AtomicLong();
  private final long sequenceNumber = analysisGlobalNumber.incrementAndGet();

  public abstract void execute(ModuleRegistry moduleRegistry);

  public final long getSequenceNumber() {
    return sequenceNumber;
  }

  public boolean isReady() {
    return true;
  }

  public void cancel() {
    //ACR-b04663a1b8ec420e8cbd3661e785631a
  }

  public boolean shouldCancelPost(Command executingCommand) {
    return false;
  }

  public boolean shouldCancelQueue() {
    return false;
  }
}
