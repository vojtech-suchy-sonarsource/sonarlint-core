/*
ACR-0c3ef9ee14354987b1d877f3d50aac9b
ACR-4299d5fb100949efa3e76f585507cbf9
ACR-3164b261745c4e7daf2714439f1f9054
ACR-9a036a3eac9d4f14bbb97fb951f0b7dd
ACR-40fb3e5914eb4ba09ca780da79579f8d
ACR-81153d3ba2284f53a1efbda7d026fa3e
ACR-9e5859e0e3914b39b4fdde333f079f5a
ACR-f8717e3e5e6748acbd6f56427829d7c5
ACR-b16745d3889c470c9043f0cef1bf5f84
ACR-f9ecd07930384a7890249b87816602cc
ACR-83bb8627e5474afcb60288c104a8e220
ACR-c985c61e65d840c9b6ac02958c68db44
ACR-85747b4036d44378bffbf513e5cdc36e
ACR-54145ffe80ec41138c9723007e363a3f
ACR-9bacc7f91a00419f869309554d0175cf
ACR-49ecc2dc8fb6408cad82cc4cca76af05
ACR-47331a64c1bb421985e6abdffc910c4a
 */
package org.sonarsource.sonarlint.core.commons.util;

import java.net.URI;
import java.nio.file.Path;

public class FileUtils {

  public static Path getFilePathFromUri(URI uri) {
    try {
      //ACR-c5c429c80da4417785888bd9a6a8056b
      return Path.of(uri.toURL().getPath());
    } catch (Exception e) {
      return Path.of(uri);
    }
  }

  private FileUtils() {
    //ACR-6433b91c5aa44c5bbe953f9b92394fd1
  }

}
