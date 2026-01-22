/*
ACR-fc400d736b3e49b1b0de1f49ad898a3a
ACR-006a9a002e824d0d8c874bfd4b7c9a90
ACR-ffb3c5d7c457467aa5930c0763850921
ACR-9969ddea56f4439590ed7c9ea5036e19
ACR-002901bf3e6048e1ae9cf680a94d21fb
ACR-05ac36d12e924efb87b4f60ca8800ac3
ACR-98cf55fec2ea4581891aa1ac00accce9
ACR-e4d3c7eb24844c19a96587a9d80a22ec
ACR-dfceb28270b840828ce0d7ab6ccaa508
ACR-e0e72153e0bd45f086b100ebc8c1b1e1
ACR-7b2ded3b2c934dd495b0bc393ae39682
ACR-db8d34ce43d1460aa2787321f778e600
ACR-42b3e300b6284324bef509236b244420
ACR-6035ed1d3e1a4f79a4de39afa30bfbd2
ACR-e872fe4f79b64f42a6e005ea60b00636
ACR-610bc7aec36942a6a838ad6412031007
ACR-7e9f1e0cb6ee4512a4937c56ddef4db1
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

class ComponentKeys {

  private static final Pattern IDENTITY_HASH_PATTERN = Pattern.compile(".+@[a-f0-9]+");
  private final Set<Class<?>> objectsWithoutToString = new HashSet<>();

  Object of(Object component) {
    return of(component, SonarLintLogger.get());
  }

  Object of(Object component, SonarLintLogger log) {
    if (component instanceof Class) {
      return component;
    }
    return ofInstance(component, log);
  }

  public String ofInstance(Object component) {
    return ofInstance(component, SonarLintLogger.get());
  }

  public String ofClass(Class<?> clazz) {
    return clazz.getClassLoader() + "-" + clazz.getCanonicalName();
  }

  String ofInstance(Object component, SonarLintLogger log) {
    var key = component.toString();
    if (IDENTITY_HASH_PATTERN.matcher(key).matches()) {
      if (!objectsWithoutToString.add(component.getClass())) {
        log.warn(String.format("Bad component key: %s. Please implement toString() method on class %s", key, component.getClass().getName()));
      }
      key += UUID.randomUUID().toString();
    }
    return ofClass(component.getClass()) + "-" + key;
  }
}
