/*
ACR-772f7eaa86834144ac91e075f152d899
ACR-c27dacf5559f4815972a712189604280
ACR-da1eec7e16fb440f915f6416f6f05fd0
ACR-d71864af4cce45ddb9bdc6cb85b6b396
ACR-2f339c8dea5a4b9baa73cc5249317a56
ACR-b6ea9e36f39943be9b0ccedd960b8fae
ACR-5865eb68daae4a71a967de7d18848f2d
ACR-5143d93c91dd4c0ab908ab7a44efe6ee
ACR-bbc9d140d0ce4baea83becba1e3681e3
ACR-17bfbc2a180444b4a9865d3a68100502
ACR-b6cf6d9b81d449a2960ada744c814d20
ACR-2e3a31dde5604e38b05b06e8c1fc2098
ACR-2ea3f277be3349619a0855454e1cf8d8
ACR-2d9e1f6c75c347048fe535b6ece5d504
ACR-96cc95021a994cdc89f52f8011ab25f6
ACR-6b927e642230487691f765a5ac74470b
ACR-26055583374f488ca4e997e5e61c2b5e
 */
package org.sonarsource.sonarlint.core.commons;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SonarLintCoreVersion {

  private SonarLintCoreVersion() {
  }

  public static String get() {
    String version;
    var packageInfo = SonarLintCoreVersion.class.getPackage();
    if (packageInfo != null && packageInfo.getImplementationVersion() != null) {
      version = packageInfo.getImplementationVersion();
    } else {
      version = getLibraryVersion();
    }
    return version;
  }

  public static String getLibraryVersion() {
    var version = "unknown";
    var resource = SonarLintCoreVersion.class.getResourceAsStream("/sl_core_version.txt");
    if (resource != null) {
      try {
        version = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        return version;
      }
    }

    return version;
  }

}
