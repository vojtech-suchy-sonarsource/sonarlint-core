/*
ACR-6f885c2cdcc9462ab9cf541f7b8b1216
ACR-644088ce02324c0cb675ef1e1e0307b3
ACR-8e5ffd0d9932495db99e6530382e8620
ACR-e22196609dbf4859bcc9247a6fa6c58b
ACR-2bee15258491454a849d93ad2c9433f8
ACR-1cf1d8a48d9947b5a7ea3d7e76a939a6
ACR-2ab3d947e39a419e9b9238ddc9cd63fd
ACR-e80f735d6bc741838559ec8cd55b3c80
ACR-58a2fbce0286458396e5128518869ea8
ACR-35ce5f2596b7430c90da057ef1a3741e
ACR-268d2eae6b9949b49d3f0cf96341cf6e
ACR-068e6e71f3ab41e4bc79eb465b730ace
ACR-e02e69ef07e346d985ba385c75e91b53
ACR-d0dc044ef62c4128993e719c2f019f07
ACR-5bb5ad3d2d60486aa3e0ca6e66d303b2
ACR-144c97d13b4c4fbf962082a3f9d54024
ACR-cdf6e142df4d44c4905807fd7cc245a5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

public class GetCredentialsParams {

  private final String connectionId;

  public GetCredentialsParams(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
