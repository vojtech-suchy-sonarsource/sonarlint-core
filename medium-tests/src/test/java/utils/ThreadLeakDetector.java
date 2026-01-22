/*
ACR-81681619d81d4d64a82c477615a6ceea
ACR-a98360dcc43b4795b1cc85ac01f6c1c5
ACR-6a6d9711b4b245e2846ad74fc6515114
ACR-08bc1ec6aca34336b4330ada9d5eba66
ACR-ad94922046e64d8aa03ff5936c0616b6
ACR-ba649af8eed84e79ab78c79ba8aad8d9
ACR-87a20bf52c2d4efbb2953db21753514c
ACR-e99489c02dd840a2b5cef016e99e9e88
ACR-6c1516b5c90a4ae3ba46b9001e5ae31d
ACR-3ccfb5fbbed94f939a5c42b2e81fdd67
ACR-a04b42e86c174e99a67af6ce20f15126
ACR-09c16445e8854c5da5e9dc4d5d1beccd
ACR-e36543f2f31b4ef29970b78f9fd004c3
ACR-40b630cac34f485693668ccb0e827afe
ACR-12e73194792846bdba970488ae2b630b
ACR-0aa81e1f838246d6a822a1a13a0892e2
ACR-ca02aa82804840c8990d436f04b126ca
 */
package utils;

import java.util.Set;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreadLeakDetector implements BeforeAllCallback, AfterAllCallback {
  private Set<Thread> beforeThreadSet;

  @Override
  public void beforeAll(ExtensionContext context) {
    this.beforeThreadSet = Thread.getAllStackTraces().keySet();
  }

  @Override
  public void afterAll(ExtensionContext context) {
    var afterThreadSet = Thread.getAllStackTraces().keySet();
    afterThreadSet.removeAll(beforeThreadSet);
    //ACR-92f12972d7a74f2eb55bb95f598991ee
    removeThread(afterThreadSet, "threadJobProcessorPoolSpawner");
    //ACR-21a1f8b1ee074dae8356f3ca81552702
    removeThread(afterThreadSet, "Attach Listener");
    assertThat(afterThreadSet).isEmpty();
  }

  private static void removeThread(Set<Thread> afterThreadSet, String name) {
    var xodusThreadJobProcessorPoolSpawner = afterThreadSet.stream()
      .filter(thread -> thread.getName().contains(name))
      .findFirst();
    xodusThreadJobProcessorPoolSpawner.ifPresent(afterThreadSet::remove);
  }
}
