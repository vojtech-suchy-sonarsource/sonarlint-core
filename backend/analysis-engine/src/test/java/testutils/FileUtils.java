/*
ACR-2ac083b552484745af205d2adbe124b2
ACR-659bc68b05bb4fb5b614b2cc580254fb
ACR-d02d6adf2741401f8892042973b3f762
ACR-0200fea4f335406890fe06e42bdd8158
ACR-e5ca4f2c4ba94325910bee2fbf9ab250
ACR-d3f438013ce743258c4d4f66ea97314f
ACR-946baa2b600e4b5c8a6da4c00f5ab51b
ACR-db08530e38be4b4c9d3511623be9c636
ACR-02d713682b21484a9393630ac23c2189
ACR-89beea7013114e89b9ecc9b901428137
ACR-3aa43afc0d3244f0bc22452abe4a8f0f
ACR-5bccd5104d9640e4916e5a7d4ecd8828
ACR-ebaff083c4104e83871a4e6a0bde2023
ACR-34a77898f95f4a0d820950dccc45c12a
ACR-ca8bf7e832944efd94846ca2012d2fcb
ACR-01cc76a8107b497ba29293241c6e7d4a
ACR-d1bfdf177ed34dff933ffc082407bdd2
 */
package testutils;

import java.io.File;
import java.util.regex.Pattern;

public class FileUtils {

  private static final String PATH_SEPARATOR_PATTERN = Pattern.quote(File.separator);

  /*ACR-68415181ac6b40b199b504bc992f9d78
ACR-60a2a746d0d64c36a6ff29926d42783e
ACR-ed62626bbcdb4e878ecb4c3b045d7569
ACR-3b05872b10974dba8ee090885740f778
ACR-b24a584b8c43473292c98c47014445ad
   */
  public static String toSonarQubePath(String path) {
    if (File.separatorChar != '/') {
      return path.replaceAll(PATH_SEPARATOR_PATTERN, "/");
    }
    return path;
  }

}
