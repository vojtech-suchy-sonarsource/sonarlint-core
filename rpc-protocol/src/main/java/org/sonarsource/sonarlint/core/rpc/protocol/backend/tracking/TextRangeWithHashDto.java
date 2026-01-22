/*
ACR-71cd5be96f2d4cdfb4da4fa618ef435e
ACR-686b383d76eb4ca4a624dad410ec491e
ACR-95274378e0824adbbef8f4c18007da1d
ACR-e6c71940c89d4d6aa6ac4785bfe7642b
ACR-aafb291cb48b4e569e68f99b8b5b2e6f
ACR-e8673e86ba67456e9fe2a933b4ea1ba2
ACR-bac5c775e99c4068b71b6c75feae0031
ACR-192da89aafbc40c19ce5d836f3fb4f17
ACR-828df96f088a46718153034953a8dde5
ACR-90b8453ec72c4f058b30db361763d89d
ACR-04b73e1e910f4edfa6aa246875d4cb0d
ACR-d37f98f8fa6945019500e9070761b8da
ACR-b1ac75bdb208492e98cd465c89bfaabb
ACR-083adfd3d1a7477eb533fdceaafbbbf0
ACR-0df161881c8c40399a00b3041d8a71c0
ACR-c12181be9b3848d68a457270b3f7deeb
ACR-dc0e1ebd48cb40a8ac5b17719f1506a9
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
