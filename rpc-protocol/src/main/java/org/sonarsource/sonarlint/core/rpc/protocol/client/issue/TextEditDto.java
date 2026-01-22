/*
ACR-8fe611c817624409a1cd8aedd90efc3d
ACR-fba638aebbef4888929515f2596089cc
ACR-eb82ff10d48242938d3ad5adb9303990
ACR-db3b86a7ae8d4a0bb4cce4740fd8b484
ACR-5ce203d34bf344bea81d4e9cee312056
ACR-f2998f5bd25a45bba4915c3fd7ad8bd6
ACR-7813215c84294e03b7c6461151039bbd
ACR-5a1d4fd769634f5ca2e0fcb1aac3c774
ACR-1968c33c23a6432482dd63ea27ea2cca
ACR-b0366bbceee949029c1dca21a8aa8a12
ACR-ccd16d031bbe497fb96a199a10513e55
ACR-7a5dad1ee04e4d09a02598f6a5a033e3
ACR-ac7f9e994ccf477b8facf99a7411c08e
ACR-d28bd231696e4f1fb20a91d4e09bef73
ACR-bf18ebe797824a93bbddcd4f9f4c65da
ACR-4b6daacf28f3449e83b87fe7a11c8fe4
ACR-839aeda4375542ef967daa29d4c5fd87
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

public class TextEditDto {
  private final TextRangeDto range;
  private final String newText;

  public TextEditDto(TextRangeDto range, String newText) {
    this.range = range;
    this.newText = newText;
  }

  public TextRangeDto range() {
    return range;
  }

  public String newText() {
    return newText;
  }

}
