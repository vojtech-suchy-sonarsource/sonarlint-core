/*
ACR-30f430c17ceb404798689b7d8862cf85
ACR-e87efe9817904d489fe0d522dca99f6e
ACR-6954c4414a7846ba8f0b3ab6a30b9dd4
ACR-041432946b29491ca214e791193446b6
ACR-98e3afff6efe46c0b9d65d763079f626
ACR-00734e31ce314a3e89cc07fd7ea360cc
ACR-771dccddf28241a090b5249b5d862e9f
ACR-71f3226b8cfa48c686d8fe4ff87661c7
ACR-83b0e205c1c2405db8796797bcc31121
ACR-e18b79b3e79145b6ab569836cfe585c1
ACR-4d1e6c332fab49b4bb77e272d295bd3d
ACR-b8bc35ab78c043c19eeddf729856b0cc
ACR-263ea789c296403b83383e86215fa29c
ACR-4b59b4cd7f394110a20be0a55703ec38
ACR-1916bbbb146146bfaecb741f60dfe098
ACR-415bb1c14133412091372d0d97042af9
ACR-8738deeab54a4f66a1deec359ef803db
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
