/*
ACR-f57653824c384576819dbc495ca7655d
ACR-be2809a6625f440da743ae34ea4228f9
ACR-fa4ecbcbfc71401bb8f1d552be9051f2
ACR-6085930bd8db4ddab2b687769731b61c
ACR-ffa0a40cd7cb4172851e24ad76633ab9
ACR-87adc373a9c94fb89a71e12f6d4684c6
ACR-67f366cf17c54f08b30e89dbe02169ef
ACR-bc060845a13644fc83fc8efd37a99216
ACR-83d0483c862f4b1ca2356fc1b3e77877
ACR-afe143b68448495a8e69704508e0184d
ACR-1e24f60a2e5a45e3b351481bb1c82ad9
ACR-e491cf45d66d40eeae3ae0e56dda9c06
ACR-702bb5d756504a27a90d0a06965b5d51
ACR-f7bffef7fba84e069d043e11e6d5e952
ACR-c3b3423259c645fda5e8018da2687c9a
ACR-e07b5b48665a4cad95a06816d7e43e1f
ACR-4ae48f65a46f4a1180159ad0b501e37c
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.utils.AnnotationUtils;
import org.sonarsource.api.sonarlint.SonarLintSide;

public class ExtensionUtils {

  private ExtensionUtils() {
    //ACR-f1120a8746604c0a9e27544982ab7f2f
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
