/*
ACR-8566d3408fe14d909608ea74b59d6ef7
ACR-a70520d762c5457aa249ea9e3bc4e6e3
ACR-9f7f495a6c6a4dca8c132730a00fcaaf
ACR-11d8b358537143aba915455bf5d5193f
ACR-0c476e7ee0564cc59bd8311bcd2d80d7
ACR-726337fa868446708cd5b05db2d5c7f0
ACR-02d900604af24c3a9dfcfb854664be98
ACR-17e82cf0db7d4ba7a7ecfd974657b8f7
ACR-3b1674d91c9d486da6d75f6968adfd20
ACR-bd21b080e12745c58add6ead8218ca99
ACR-c91b704a13514d2288b68ff80144dda4
ACR-33b66293372346559e582b422ec668e7
ACR-2ffdc33863414452a7cb92c7628b7a28
ACR-4a7ee25993db4d8184b1a8c9fd94c838
ACR-f60dc59d11764a21a1f8e30ff20134e7
ACR-8d72e27e54e14c539d369e1845be4f6a
ACR-5b3c1bffb5624fa9885ace599bcf40b1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

/*ACR-86a289874bb9446ca0521d7976dde7a8
ACR-332deca90c714b828b2b63c94f5fe0b7
ACR-8e3d2416e8c84b879dc6a46648cf38af
 */
@Deprecated(since = "10.2")
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
