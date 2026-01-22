/*
ACR-fbba707a31114c8a9d43c2060293285e
ACR-3c3e156a27ac4b619e19e9d85e77b5ec
ACR-4a44e87d5dc64506acd446c81e9645ab
ACR-e63cb4e18a4b4519b89bdd15de54aaa9
ACR-d828f2d10b6347ca958d90524352173d
ACR-15e768f990cd4550a5301f6318aa86f3
ACR-e9af1b05f0fc486caf5634e403df4120
ACR-98740cb207a34c3e9551d9a0edb980d5
ACR-1eb022a21a994d7c8e7dcf98ce5540cd
ACR-7077e40f55af44669ca2f74e6b37ff1b
ACR-fb52eb9bbea043d6bb7331969b37d63c
ACR-5679ee8e0fba4ef2a7663ce6077a1e34
ACR-15991961dc7d4df3a90d3be6ed1cda54
ACR-5882e5ddc96d4995a810a68bb4ec43d8
ACR-79e4f949f7a94e71a7f733e0099b2253
ACR-9ea598e45e10485d843e7b3139e59f75
ACR-7b7f3b5ae88442ada5aa2cbda6013d7c
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
    //ACR-d4e88c196b14437985b0b4b8f49a90da
  }

  public boolean shouldCancelPost(Command executingCommand) {
    return false;
  }

  public boolean shouldCancelQueue() {
    return false;
  }
}
