/*
ACR-814032f1eae24eb9abad9c797a345bce
ACR-61deb91d94334d7580000520dbcf99e3
ACR-b13e07248c204090bf5999c4aa755c57
ACR-01378f871742458c9f2aa3d78c002fd3
ACR-b8398295c3ba418883e90dd16d603335
ACR-4f22c62bed044caf90f5e5efaed4288d
ACR-2213e3f946084ce89fdee6f271d9a6a1
ACR-b7ba15352c0c47a797767edfa07f874d
ACR-21f1df6cce50443fbd763884a31d8a53
ACR-9f0e60a80c064658a08efcab714d5426
ACR-9efdd8bdc6b144e2825285cd10fd13b4
ACR-523aaa58f09d4319abf1db13cea8e309
ACR-7892d9dfd5194693aa238c31fe46fdb3
ACR-4cc57a84b7f5484281f133a1d7fb11c3
ACR-ec89def0ee2742dcbfb00688f957fe55
ACR-19ad6d8f6ddb4798bdf2d938da2aee60
ACR-87331941328a48f09166becfd8d0df16
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
