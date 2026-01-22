/*
ACR-0bfe0bee536a41ac829521a9abcbe24b
ACR-6529044f7280433baac52a8c1b69993e
ACR-119edd89d75e474dbe31932504b557e0
ACR-bdac59e7d8cb4525a500d1f0552c6119
ACR-09200c778b41488cb72b9d536e21bd6a
ACR-ae46a789b2f24b08abcbb59c5f4b5c93
ACR-5135d7bc96f04622972baea12ac67c11
ACR-7bae54e46de54b6098129ede13d0ef63
ACR-5a830a4cc8164eae9e77ea38b1d2d113
ACR-693667ac11344e418f68b0703a226056
ACR-a33196c115004178bafdda30b5853258
ACR-64168564f7614dbe9cfb8bc8be0ca5d5
ACR-84bad8afad9943cfa37e13694b9d5784
ACR-8dc54f2c9a5d4a6a9cfcc4ba21d9ab42
ACR-5c1d5bd0d63448a88229c183a9271213
ACR-b631dfdc1198431eafbb0259affe647f
ACR-884b3dd6f6ca49d58d2a5e5bd12ffd42
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

public class TextRangeWithHashDto {
  private final int startLine;
  private final int startLineOffset;
  private final int endLine;
  private final int endLineOffset;

  private final String hash;

  public TextRangeWithHashDto(int startLine, int startLineOffset, int endLine, int endLineOffset, String hash) {
    this.startLine = startLine;
    this.startLineOffset = startLineOffset;
    this.endLine = endLine;
    this.endLineOffset = endLineOffset;
    this.hash = hash;
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

  public String getHash() {
    return hash;
  }
}
