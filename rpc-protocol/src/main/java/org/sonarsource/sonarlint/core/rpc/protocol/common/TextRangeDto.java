/*
ACR-ab5c4593a09346708e74e917950e2f75
ACR-97b723ae673b41cb8f4d8cb9530dfcf2
ACR-8d06b978143e4d48a8418c0a12089c47
ACR-c9093e6633254030a459d0d197167bdc
ACR-02e025ad2c1f4276ba0d9f45f2f3f4d3
ACR-0b9ab503aa67419289378a37449092d2
ACR-3450e6aca0f946b3a884c4af2aed305d
ACR-a67c79a9811248ef89e56e220e22901c
ACR-dc62b73c86d247168ef09da8185ae918
ACR-f68a1c2bbb9845fdb7145d796c223fc1
ACR-420ddef34ff948aa834adb171ecb66c7
ACR-c7814c031e314e4584f12993f6dedc2d
ACR-06a26cbd17294a15885918efb689069f
ACR-5e4d57d9794d455586449734c76d87b2
ACR-abe1c265132a476e8a9d9e28f0e75355
ACR-e586754a894748c6bfc68d66bac73f63
ACR-769b0225453f42c28ba4f8104ba4fa14
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

public class TextRangeDto {
  private final int startLine;
  private final int startLineOffset;
  private final int endLine;
  private final int endLineOffset;

  public TextRangeDto(int startLine, int startLineOffset, int endLine, int endLineOffset) {
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
}
