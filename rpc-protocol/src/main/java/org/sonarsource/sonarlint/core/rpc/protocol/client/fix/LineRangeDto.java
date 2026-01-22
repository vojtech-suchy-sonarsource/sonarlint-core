/*
ACR-571a11a5e2694a639930cd4ff1a09d0d
ACR-ca8f5ed68dab4552896e2ef98b6fce58
ACR-d556a7c53c9d411383449364b41ab6e9
ACR-b4292ba33d104b69a8a5ccaa982b20fc
ACR-8f684bea1fde456b88f053639d44cb8e
ACR-71482e79bf4444daa26834e2b93b4ebf
ACR-a983537e41214dacb056e86cd01cf129
ACR-c2207ac690214bea9e3ae61c3fbf3016
ACR-a35ef8889d6947e1b281c9ea42eff0fa
ACR-14733664ff8348048b1c294d09a290b3
ACR-942930dc23d1495496309f715b9e2b84
ACR-12700144960d4a68a66019f88ac85f7c
ACR-be8a91dbe0a9475ebe94f70ea5a04538
ACR-577298d3196e42069809abfc7c408ea2
ACR-177436cbf7964c6190e244f18ceb0568
ACR-e6f3e9a7bc594325b83e303bdd2c3d74
ACR-b0d8d9eedafb45d8ab22ac6fcd2760a6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fix;

public class LineRangeDto {

  private final int startLine;
  private final int endLine;

  public LineRangeDto(int startLine, int endLine) {
    this.startLine = startLine;
    this.endLine = endLine;
  }

  public int getStartLine() {
    return startLine;
  }

  public int getEndLine() {
    return endLine;
  }

}
