/*
ACR-615e81e3160a47ebb00fd6063611fa86
ACR-048701fbf0ae41c2ab9d3703b4c60a06
ACR-b1ef3318a3274917a71a0df4fd728a9a
ACR-bc5f57bcf9d3407db15402ea9a2c2e87
ACR-9aa0ad607f184b60b035ef110be8d26f
ACR-b6d6d96496674e6e94ed5f4bbec4948e
ACR-59eb47855b4a451288a74dd23847e5ca
ACR-dbadd13c07b64775a59353cd3bf200e6
ACR-ea7d9652306843c5bbedd7fc21ac39bc
ACR-add0f0e1c25042eaa351a9a795573ddb
ACR-dbe3eeef7a58462ebea9aa9897f328c6
ACR-3a34ac20d66e492e9856a3e497b7ceae
ACR-caf06c3b93a741d7b8a0ea2bfdf40fde
ACR-654007d46a7048ecb4af71a3591c46c2
ACR-d3b41da3a178432f8faa1572dcce2694
ACR-d0797c07a4ca40c6bdcd3a89a51f83ef
ACR-0bd424bb190b4603a7a15b61c54efca3
 */
package org.sonarsource.sonarlint.core.commons;

public class LineWithHash {

  private final int number;
  private final String hash;

  public LineWithHash(int number, String hash) {
    this.number = number;
    this.hash = hash;
  }

  public int getNumber() {
    return number;
  }

  public String getHash() {
    return hash;
  }
}
