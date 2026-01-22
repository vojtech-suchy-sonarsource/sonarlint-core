/*
ACR-e7e404cc7bfc46dfa24832de759a659c
ACR-6588696f5522410383d008ddf0cefff0
ACR-db6e0303471745d69e7245d68e6dc1c0
ACR-82389cc142fe406eb605f1bc4da56472
ACR-8d9bd86e93ed4004a4facfe2b14e5367
ACR-9d353ab37cb74d21b8ac3aee83ba5052
ACR-e77c7778ed9e49c4acbba17a47ad27de
ACR-4502826ac4c54a5ea4b739548c850b4c
ACR-4476308b473e4f08a84ef7d3cfc11e42
ACR-16f626459804451480fb38cfadc99bdf
ACR-5a3a6e4e03c74a9da5eef351c61b9ca0
ACR-f19441afdef34450a3f702c825da38ec
ACR-9e451ff34e1d4d50b759f411957fbf44
ACR-a206a4f80b0f41c88760a15d23bac023
ACR-28c15c654cb545a1aae4f419cbc55789
ACR-fc9cac836f934700a54d25ff76407eaf
ACR-b04b8866ba9f47d89bc3525fae2f4fe0
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Hex;

public class ProjectStoragePaths {

  private static final int MAX_FOLDER_NAME_SIZE = 255;

  /*ACR-762816a65af643aca69ff7dddd088027
ACR-79efac888cec4f9ca6aecf52c6c5d1e9
ACR-6954e0e4b44d4143a8067813dd539a95
ACR-88e39e17d23a447e8997a8a0e355b839
   */
  public static String encodeForFs(String name) {
    var encoded = Hex.encodeHexString(name.getBytes(StandardCharsets.UTF_8));
    if (encoded.length() > MAX_FOLDER_NAME_SIZE) {
      //ACR-fcdb0b503cb847dc810a39a15a78f8d1
      var md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(name);
      return encoded.substring(0, MAX_FOLDER_NAME_SIZE - md5.length()) + md5;
    }
    return encoded;
  }

  private ProjectStoragePaths() {
    //ACR-7584818c5b7541188ea27c98458d0c6c
  }
}
