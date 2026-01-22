/*
ACR-cc85630a5fab4c9084779a17479c606a
ACR-ce686e54f1df469284959f97828e4069
ACR-7fa9f3fc46624e97a8c3a09cbe750454
ACR-377e9edac5c343a38cb45a01848830e1
ACR-6d0a98178d664a99ae3b0e5a1ddca7b9
ACR-8be8d0e3610d4294b0d49dc7933f8044
ACR-1dde57e4c2124c8db1801f019f7d5b78
ACR-e2f7b37e384249a89f7f3681ec39bfce
ACR-c90bb44b8e934df480a40f4a6ac82425
ACR-eda72e7f94b34161be5de7d064af8325
ACR-fe831af506da4cf6aa200ceaa9cd0b09
ACR-f295d4c5c72b4a6b9cd5b11a44f855a1
ACR-86f4b69a074344f49f3e4376867ed391
ACR-6ba8955000384ae09935e1a7c45344e9
ACR-2c271727bf7c4568b59df4c72b3011d0
ACR-92df535f276f4a2d9033b5c3dcc64c7a
ACR-91f3e48f943a4d08bb8931eaa9bc4bef
 */
package testutils;

import java.io.File;
import java.util.regex.Pattern;

public class FileUtils {

  private static final String PATH_SEPARATOR_PATTERN = Pattern.quote(File.separator);

  /*ACR-7c41cf5082144ce89d751b445e4917dd
ACR-c6fa56ded27b4933b15978cb3de9fc4e
ACR-327f01645a9246a38233bbb307c16140
ACR-eccf30135d754831b6a48f6dd4069773
ACR-88bf250e2df547caaded8e288581c67a
   */
  public static String toSonarQubePath(String path) {
    if (File.separatorChar != '/') {
      return path.replaceAll(PATH_SEPARATOR_PATTERN, "/");
    }
    return path;
  }

}
