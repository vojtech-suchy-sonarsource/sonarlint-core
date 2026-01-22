/*
ACR-00aed105fa574433bb3957c4ab5458c6
ACR-c23e486b7bd846f1849d4a87fa3b26ae
ACR-fc9179266c9d49138ad5137d53f1ae84
ACR-0459b1fea019470f95df205e8c849f93
ACR-5d68559015b34a629ece1018b4124af7
ACR-576eb0ab28a145b99e1df8f494959f28
ACR-228cde8d7ae747e1a5ac9da667fb3ba4
ACR-d6d75f8cbd384951a31acd971c8fbf50
ACR-8805c81351b54b818326a6f45f0206e9
ACR-454534f7219a4ea29ca35dc9957e6943
ACR-5769d3b98f634a5bba5cbd3a09acd1ea
ACR-aa39dfe40a2b4d28b666e1f7f008bfff
ACR-9f165cd5bfe24bc39361b7c496ca2cd7
ACR-3ebf17feb69448c688a67a242075cdca
ACR-a73118acd4d8474282a70bf0a69ce343
ACR-f0a7e837db684f18a2652bfa5e4733fb
ACR-a3e3ccda1c614f748f39e034d8e65d80
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
    //ACR-7ae386a83d834e12aee37e636f88e6f3
    removeThread(afterThreadSet, "threadJobProcessorPoolSpawner");
    //ACR-e6cc4063bd8640388f75f18c21bb0567
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
