/*
ACR-c3d04b6efed744508f96f8255948fb34
ACR-2c6ea59774f4416db7674acd80a696cc
ACR-d726553eeb0e4d129b99b623e7783ac6
ACR-babe42d34fac4fe5b6ad54e4a2553bb1
ACR-2c1d275c16464c449a1f8e8fdb96fd9f
ACR-acd732601f7947b996c11dc4d1ed5f5f
ACR-0d8be331e8784cb5afe75c5798c81089
ACR-709c9ab1ab6448639141957cc2a81720
ACR-36abb9d7746c467eb2e2940e8814d1c3
ACR-7523275424c7461fbddd802d200b3c08
ACR-e56bb59c88dd4c1fa3d5315961199195
ACR-bd58d0916afd4a93b93cd53aef212a6d
ACR-ba68008156d141e695f05cd364de6ae6
ACR-d8d9c317077d4ac6a1cfd49c593ce43a
ACR-ee8025c39bc44e1b8dfbb7f1751816ff
ACR-c7e617ae359640648d46f996aeccf426
ACR-c005cdf90b294932920c5553792f1273
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
