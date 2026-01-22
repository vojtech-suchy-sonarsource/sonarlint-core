/*
ACR-d59baa05ea0c422bb58c385bfc657b11
ACR-28567128e4ef493eb3320d7bd36aa67f
ACR-1f64ddf70fed4af9824a5d917f4b6479
ACR-9add943927594163a10a7e4537c3a3e3
ACR-8a346b86248c4bfdb0efb1ce45899e80
ACR-16824908d38b4a0299b2dcee276904e8
ACR-c347e667e4154197a32fece5a4e00300
ACR-18c58ea4fdf44b2b83c176ba778bfe25
ACR-35fa06036cfd49a393916b4fe7e5d013
ACR-6b1e46548e0a4ee7b18c232ce5d0feab
ACR-a5c1de193cdd4482a824f0b1535b57b7
ACR-c4c8f37707754ba8bea02209a823b55e
ACR-76737e30d5d544ecb1952ea3d60e0c4c
ACR-ac4b3d1d5ca44207b4127255b943dd72
ACR-b4198bb1a4484c8fbadc4da1d0927b79
ACR-36d7a24bc87f40e380d1eaefa89daafc
ACR-67b00b7082864d3a9bfba0e9fc109457
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.nio.file.Path;

public class LocationDto {
  private final TextRangeDto textRange;
  private final String message;
  private final Path ideFilePath;
  private final String codeSnippet;

  public LocationDto(TextRangeDto textRange, String message, Path ideFilePath, String codeSnippet) {
    this.textRange = textRange;
    this.message = message;
    this.ideFilePath = ideFilePath;
    this.codeSnippet = codeSnippet;
  }

  public TextRangeDto getTextRange() {
    return textRange;
  }

  public String getMessage() {
    return message;
  }

  public Path getIdeFilePath() {
    return ideFilePath;
  }

  public String getCodeSnippet() {
    return codeSnippet;
  }
}
