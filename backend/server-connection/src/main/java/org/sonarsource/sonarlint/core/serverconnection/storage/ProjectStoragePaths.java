/*
ACR-abcf9ae4171d4d44af94607726cf0e2a
ACR-14126af3a9d044cf9bd74404b851bfaa
ACR-72211dc2001c410eab77fb21c120dcd5
ACR-b1e88dc8c00c4bedb70876ef748e6551
ACR-78c4a5af565b4145bda58bf47d36fa76
ACR-79c4318f2ad44dd38b1bf913ad850168
ACR-9ad253f6cc4648489c1ab6a7cbd913a0
ACR-af641c22a9c144baa860e31ba1085f93
ACR-2c476c08bfdf47fa8bbc3538b4d78f3d
ACR-10fe5a3d5ac94e61bcc62a2786a2ed76
ACR-2f7789632c924555836640fbc8be1065
ACR-870813a2c4ec4b47aecd9fb44d7ef2b8
ACR-f480afd6ca6d4be9ac2770ad9cfbb785
ACR-aee288c16f3343cfb7fa92cf8b0b9eb5
ACR-ef0a730b64b944618534dbf3d011ce6e
ACR-eb080ef4f26e47f08898a0b240b44892
ACR-4de9df2e9b704b74a368eab19e7b7524
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Hex;

public class ProjectStoragePaths {

  private static final int MAX_FOLDER_NAME_SIZE = 255;

  /*ACR-1b152f341aac4f5490881e299d5008c5
ACR-45eea0957acc421dacec2af0a61b8926
ACR-9707eb511acd4e25b74189d755561e81
ACR-a169b2f492d34f53915cfb93f50a2611
   */
  public static String encodeForFs(String name) {
    var encoded = Hex.encodeHexString(name.getBytes(StandardCharsets.UTF_8));
    if (encoded.length() > MAX_FOLDER_NAME_SIZE) {
      //ACR-3fd6a27e3a8a470eb8547ce341c3b3e9
      var md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(name);
      return encoded.substring(0, MAX_FOLDER_NAME_SIZE - md5.length()) + md5;
    }
    return encoded;
  }

  private ProjectStoragePaths() {
    //ACR-e01ab451c6254088a5289f605133ed69
  }
}
