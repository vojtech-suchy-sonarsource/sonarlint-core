/*
ACR-c43a4397188f437b917b867ae5201a12
ACR-fac3f9810977465fb33d4c278d67027a
ACR-917c2152d9954eeebbdd11c11b765ce2
ACR-e232d47f1d40464187f10f2775bfa52f
ACR-b32099cc7f4a4c3381dea611890535e7
ACR-2f27486a0b5e4cc788b2400535c26873
ACR-45683a6dc5f945da8f8484162e34cc7b
ACR-967eafa9afd145188897c2b99b8af5cb
ACR-8135517e88f941ba8f079b5d8727712c
ACR-3851b68e00254680aeb917d3d3430b74
ACR-2229c51add9a4ef282b93d3a7102f36d
ACR-716fa7b91f6c490caafa70f5f1550f30
ACR-c03d8c4a18e44dd7b165c92584a827d4
ACR-4689b065c1be4053a3741eb0efce64b1
ACR-56896e8dca7b472482f34fa05a242fd3
ACR-0e3d260c20a242d091fcca5f6ce673c3
ACR-6fcf5679ce674b46be9a58b4712e122d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

public class LineWithHashDto {
  private final int number;
  private final String hash;

  public LineWithHashDto(int number, String hash) {
    this.number = number;
    this.hash = hash;
  }

  public int getNumber() {
    return number;
  }


  public String getHash() {
    return hash;
  }
}
