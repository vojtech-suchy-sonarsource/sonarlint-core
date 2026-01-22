/*
ACR-cae3b92d8d3f48d78d0aff5f8602fbd6
ACR-e1bc1bb55a9d4b17bd5d16ede308d38e
ACR-d1d0f662a06d44e0810aedb7ad3e6726
ACR-01615d33159c40bba93886fab792808d
ACR-91d32ea011734076a43779c69b77759e
ACR-c6732f6ac8f94aedb2e0114b74b1e699
ACR-3daa5acea9fc48caa58132f731391204
ACR-1cea44db68d84b3fb13ee8d1d5feab26
ACR-5033d958c325419b8709493684beab7a
ACR-789d2cf3cc0f4d7cb54566ddf0c68091
ACR-7ffb68c3f40447eeaca5d114e54113e3
ACR-77d0bcf1f13f433798b43fd70173cbd8
ACR-f7919659edd84bcebbbb40d869797d45
ACR-c5676ec7982f4e959898de566692694d
ACR-8c8a097fea6148868b4fcf67cedcd2a2
ACR-c1d348396eb74b70bd64b49312aa6393
ACR-2fa1c736395b4fef8b5be49c42699632
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
