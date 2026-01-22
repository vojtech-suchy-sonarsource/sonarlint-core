/*
ACR-cf059c7f72e347958542e46364507194
ACR-a2fa2bdc3116467084fd053ce581079e
ACR-7ee49f09971545fe96b406e63c040ba4
ACR-720f691627664b0896d1d77a72cd57d3
ACR-045e846191a644cc9324ce53ef0b2daa
ACR-a6a6fe0f4b5e428498ae25bcfbe5072b
ACR-322afbb488d749749535184980024f2c
ACR-eb9f951045144d9b868e14c9433cef04
ACR-112904bda7354a5787088e2418f17604
ACR-94ef213ac5724a22b72fa084cc4eae10
ACR-ea93c4a4849b4113a1bd05e64e32d96d
ACR-329a9f6f326641008b298cb667c86e47
ACR-9f63c29c958942948fba5a678a265f17
ACR-7754f09062364417b1259838b6094d0c
ACR-aab73c9eaf83482b9d391b2bc3a6ea21
ACR-08543db55e724fdda53c17dacf318d03
ACR-9f344129bc4948a6906f154aefd30b60
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;

/*ACR-576e7a8c5ac7429cbcb6a9552371523d
ACR-8a436f8535db4e5480815d5e86bd34ec
ACR-becb84b71b964231abeaf555824c8914
 */
@Deprecated(since = "10.2")
public class QuickFixDto {

  private final List<FileEditDto> inputFileEdits;
  private final String message;

  public QuickFixDto(List<FileEditDto> inputFileEdits, String message) {
    this.inputFileEdits = inputFileEdits;
    this.message = message;
  }

  public List<FileEditDto> fileEdits() {
    return inputFileEdits;
  }

  public String message() {
    return message;
  }
}
