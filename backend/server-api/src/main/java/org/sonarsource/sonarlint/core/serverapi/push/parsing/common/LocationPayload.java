/*
ACR-35ec4ef6e25e43cdba01cb1ce142477f
ACR-06139a7248b644bdaa24b38ae8ddb084
ACR-b326e710671245ca927ad5a239d38a6d
ACR-b171936b2c01480ba44e06f258b6fdc3
ACR-feb71d382a8440f895f980d59eb99404
ACR-07401305222140b49d2c4a3108d9e735
ACR-1307a75cb3404b928e649d5a9c744974
ACR-109bebda732144e9b8ecb18ad069f084
ACR-18467463ebc04364b006682524ad6595
ACR-90a64444abb842f8b2ec597249709080
ACR-c3f91c3db09a427ebb9b5e2d4a0b20b4
ACR-adae75cf01394ef2b90ccaeda34c5f37
ACR-cb0bc8ff9f1c49cc923290f94bc04518
ACR-b32e17a01afc46ccb9af7177dcd62775
ACR-120ad0b35a304816ac9524561b6402ea
ACR-3b299dd845bc4022975baefd5fcea5ac
ACR-b688f59e16d848d0af423973e616f249
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing.common;

import static java.util.Objects.isNull;
import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.isBlank;

public class LocationPayload {
  private String filePath;
  private String message;
  private TextRangePayload textRange;

  public String getFilePath() {
    return filePath;
  }

  public String getMessage() {
    return message;
  }

  public TextRangePayload getTextRange() {
    return textRange;
  }

  public boolean isInvalid() {
    return isBlank(filePath) || isBlank(message) || isNull(textRange) || textRange.isInvalid();
  }

  public static class TextRangePayload {
    private int startLine;
    private int startLineOffset;
    private int endLine;
    private int endLineOffset;
    private String hash;

    public int getStartLine() {
      return startLine;
    }

    public int getStartLineOffset() {
      return startLineOffset;
    }

    public int getEndLine() {
      return endLine;
    }

    public int getEndLineOffset() {
      return endLineOffset;
    }

    public String getHash() {
      return hash;
    }

    public boolean isInvalid() {
      return isBlank(hash);
    }
  }
}
