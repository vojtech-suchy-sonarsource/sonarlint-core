/*
ACR-a28ff232140f4fa7b4166e2b5def2d83
ACR-1eb6fa723323467c8e8650a1002d0cf7
ACR-3baac73e122a4a45ab2f2632568a33ec
ACR-66f6a52cb9274949a9937c5d3a040562
ACR-cae292301a5f433b982bf97036bc7c7b
ACR-2a7fb795d9e64f8097633f9d4851f1a9
ACR-f83091c7be934c40836de96a87e312f6
ACR-e16cefab52604d81a6e41903b9841760
ACR-959b4573fad74d7eb46e44997c671a79
ACR-a240d737b5ec41f280bb16c33656a76c
ACR-9cacc7145af0484ea5aa195663ed0509
ACR-2c013c7a2362412e8c63def11c5bf65b
ACR-397f3df4e5964344a832f4c293b8795a
ACR-b8ca4bc199524864a1343f4a622a27d9
ACR-cb21aab888eb47b59910f333de3f1775
ACR-a781000d761243d7ac3c7589dda04142
ACR-252066c045ab402daba9ff66b63d6c6b
 */
package org.sonarsource.sonarlint.core.commons.api;

import java.util.Objects;

public class TextRange {

  private final int startLine;
  private final int startLineOffset;
  private final int endLine;
  private final int endLineOffset;

  public TextRange(int startLine, int startLineOffset, int endLine, int endLineOffset) {
    this.startLine = startLine;
    this.startLineOffset = startLineOffset;
    this.endLine = endLine;
    this.endLineOffset = endLineOffset;
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(endLine, endLineOffset, startLine, startLineOffset);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TextRange other)) {
      return false;
    }
    return endLine == other.endLine && endLineOffset == other.endLineOffset && startLine == other.startLine && startLineOffset == other.startLineOffset;
  }
}
