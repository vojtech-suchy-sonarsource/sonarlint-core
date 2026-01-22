/*
ACR-f720a62759174d6ca7c107160d5a94ea
ACR-727dec3e9f764468b130c2b69cced411
ACR-55e80420076c4bf4a9282988b7e624f9
ACR-bb4941b3a58c4d26a43172471f9dfec1
ACR-07e0e685c60441438649ff756f7b289d
ACR-619d0842e55b490c8679a3659b138410
ACR-1397a15499eb4cb5aca72d672c6a58d5
ACR-0f2460c5d63647c9b33a5b313df23f97
ACR-4165e5873ec94ea59eb817cb7e5040cb
ACR-037104f694884b4e85795562901c0e1f
ACR-98b51cfc2af64889920b3ac0ae4d311f
ACR-33c7aa6ab83e4989b587ad31391cf1eb
ACR-e38302d5c2644087a53ee77a381daad5
ACR-c8d54420e14d485b8b32f2462a0b2b36
ACR-4890c87ce52f48fc942568d88768aebf
ACR-4ed8a14d434b4774b5f4e0d1c1e50c96
ACR-5e9bada3dc2f42e282a80e735cfaa444
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.utils.AnnotationUtils;
import org.sonarsource.api.sonarlint.SonarLintSide;

public class ExtensionUtils {

  private ExtensionUtils() {
    //ACR-1aac0408271a4a0c9dd23d6d44572bf5
  }

  public static boolean isInstantiationStrategy(Object extension, String strategy) {
    var annotation = AnnotationUtils.getAnnotation(extension, InstantiationStrategy.class);
    if (annotation != null) {
      return strategy.equals(annotation.value());
    }
    return InstantiationStrategy.PER_PROJECT.equals(strategy);
  }

  public static boolean isSonarLintSide(Object extension) {
    return AnnotationUtils.getAnnotation(extension, SonarLintSide.class) != null;
  }

  public static boolean isScannerSide(Object extension) {
    return AnnotationUtils.getAnnotation(extension, ScannerSide.class) != null ||
      AnnotationUtils.getAnnotation(extension, SonarLintSide.class) != null;
  }

  public static boolean isType(Object extension, Class<?> extensionClass) {
    var clazz = extension instanceof Class ? (Class<?>) extension : extension.getClass();
    return extensionClass.isAssignableFrom(clazz);
  }
}
