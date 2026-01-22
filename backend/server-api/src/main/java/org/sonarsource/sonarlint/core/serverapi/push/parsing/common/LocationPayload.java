/*
ACR-0eec298fcf574db99e871b46740e5b20
ACR-4ddecc608fb14554a399db43c6cbe73c
ACR-62f2e59e06074035a1675e21bfdb267b
ACR-4720d67f9863490b8c88351b4914578b
ACR-d39c0aae91224919ab82c414c365ca9a
ACR-366b6ecca3c24f95894e240f2cd162e8
ACR-f6676eeee458431d9f12dd373755c946
ACR-24fbb90b2cdf42d383d5779d3933946e
ACR-34a7b2d8a8d34286a2321fe3aacfacc6
ACR-449acfc49ea6433cbd7f06541ceaccea
ACR-c5233dea11464824be048290a9c1023e
ACR-94048f126a4843d59170c5af7f4b8e86
ACR-e3805d148dbb422bbc7fe58a683811be
ACR-96481b446a7542bda9df842907a63e24
ACR-edf1135e990344729c0ebb41e800215c
ACR-31931e916caf4728a8f4d56056a65510
ACR-c26ee347f4e14c5cbcc87a3e8551bc5a
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
