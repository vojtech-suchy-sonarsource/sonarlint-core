/*
ACR-2c014a45b90e463d980def9a6bd1e4bf
ACR-54341cf914044bb282e456c0a37ce774
ACR-f521203f17aa4baa9d9d3d78a7af7bfb
ACR-73edbce0ba394b8196663c07d3f6ba7c
ACR-2e765b6751ed4a48a7613683d4573986
ACR-020deca6cded425c86c5105220f43ebb
ACR-a7eab87d17244423b004380e77e986a4
ACR-be9723a796fd4a06a27ff80b62b9ab6e
ACR-b0573ce65bbd4904b11b94403a1fcb67
ACR-12360907cafc44d3816776280ab03627
ACR-503183cf85e54147b91574cece7e993c
ACR-bfa3e273e9d54e6aba743fed893b305c
ACR-d2efdf27847847bd935bd30b0504fd14
ACR-9d8efaddb0a74f4380e0c17db95078fe
ACR-e6fdccc2b120469a8f43eb90997be77f
ACR-bdddca6fe61f4ad6a7bdb699cc05a2e4
ACR-485b5f891c364b2f9c23ed34080dcf7d
 */
package org.sonarsource.sonarlint.core.commons.util;

import java.net.URI;
import java.nio.file.Path;

public class FileUtils {

  public static Path getFilePathFromUri(URI uri) {
    try {
      //ACR-1e789aff18f64b30a5fab28d4b956bfd
      return Path.of(uri.toURL().getPath());
    } catch (Exception e) {
      return Path.of(uri);
    }
  }

  private FileUtils() {
    //ACR-fc307302cf004a94889f44ea118f0897
  }

}
