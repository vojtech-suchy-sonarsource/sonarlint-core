/*
ACR-cf4a8a43a38846edb3a731e392796996
ACR-90ed04686b484e7d8849e01fe7aa9c5e
ACR-fa5fae577d684d1ea9278c24f31e432c
ACR-0bdbd4a5e44c4dca835d371d214bb5f8
ACR-fffde38d47b14ad9b719c588e9f596ff
ACR-14cd21b9ff7e4d4aa7d7426895f33fdf
ACR-f5fbfbc9fd8a4f0c96a9db7598705f78
ACR-dcf3406667f6489588e464abc4b5937e
ACR-1ccea457c2e744d786ab17cff7f31870
ACR-6ec9478aa4d548a7b72ba635dd190301
ACR-1ff09fc1188c4c1fa59ef596a19ad6fd
ACR-d90eb4bcb6b542cf94a5f4dfcb8cbf5f
ACR-e7e5f3ab12c04e5e816ba678c81344bf
ACR-53eb45b91d8f42bd8a213c1f3097b848
ACR-59ddcf1752004235b85b4a0082cbd6c1
ACR-ba95ceb1eb944b488c81eabfb6703376
ACR-c981416414c54ff78d166315bddfda00
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix;

public class SuggestFixChangeDto {
  private final int startLine;
  private final int endLine;
  private final String newCode;

  public SuggestFixChangeDto(int startLine, int endLine, String newCode) {
    this.startLine = startLine;
    this.endLine = endLine;
    this.newCode = newCode;
  }

  public int getStartLine() {
    return startLine;
  }

  public int getEndLine() {
    return endLine;
  }

  public String getNewCode() {
    return newCode;
  }
}
