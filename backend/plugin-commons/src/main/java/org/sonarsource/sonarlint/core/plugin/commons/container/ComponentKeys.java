/*
ACR-550f2ad921a24dff963f8cf9fe68093e
ACR-0d0623266b2b4441b673db15ed0b0baf
ACR-93ef4bac75414bcea3f8f40267c1b840
ACR-48ff3b295eb74c5da87191a7c35dd00a
ACR-b984aa98951a4a9ab5fc623ac42c9318
ACR-87a8ada4076d42ffbc06b31e4ccfb4ef
ACR-6b4babac95e845d1aa1d945b4a977ac7
ACR-9a128a36d0094bb8bc8f9ad44b8e663c
ACR-bd4bb08b90804fc197d24577873380dd
ACR-cd9504d7fb7b493d9656fe8c491aa65c
ACR-0a640d665ba349e080a5f3951f7c3f8d
ACR-142289e12b8f4fe0904f4c1342990f8a
ACR-0945efdf8ae84a69a5bcb40f2772424e
ACR-3387ba2bc73246759e06dacaa1c11adf
ACR-937028cd73074d0298405f60cc2617c0
ACR-0febb2e351ac42b3b4f411bf0d7c028d
ACR-486922c4a1e34f358cfc7c3ceb8dbe74
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
