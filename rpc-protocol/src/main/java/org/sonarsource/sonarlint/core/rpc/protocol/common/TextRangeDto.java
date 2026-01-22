/*
ACR-3d51dc94404d4c43af7d81014f12442d
ACR-b8c7b484ea584e069aaea49c1fe6375c
ACR-fda0ef876d9a444abe19ceabb39ed04f
ACR-7d3be4e9259f4251b1ba136d8932c30e
ACR-a8c6aa18d72841e294223ee17a512434
ACR-e75772775f4a4168bc9765801569b38e
ACR-3fcd7eb6e0504efeb9e16ef85edaa1f6
ACR-3d57d97100684ff8881cf7aebce58e30
ACR-4110ca9049bd4380b3de2242b4e00a2e
ACR-a93da9dcf24d461599a816524a5dd008
ACR-ca3ba7c2c0e143d0a14e0fdf45daffbc
ACR-d8e7ff17fc7a4dd58c9a2da71d07aec9
ACR-46614d6c54754d6db0b2c187cfb779af
ACR-67d3c7f3c8784585abf597fff069050b
ACR-21e81194804d41c7ac4ef8ae7f74d144
ACR-0ffe65f3a879409c9600d830a8b96986
ACR-99453cc0de844ff4b33083283e1ba067
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
