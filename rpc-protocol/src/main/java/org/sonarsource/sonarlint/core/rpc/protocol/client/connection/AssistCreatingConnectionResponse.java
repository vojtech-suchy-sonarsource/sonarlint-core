/*
ACR-3bdb2f6c4ae4400d8b04fab1784fcf74
ACR-2e28e4372e784b1f9d8e99beaa3e265e
ACR-394e618f2cbf4bd089ea21e0b3e75799
ACR-63baf4d8374945cbbda4a14eb50497fa
ACR-2b256eed640f48b1bc6ba97dfe7c5182
ACR-194b268e946940ae8e85e48efce60095
ACR-62708af635f74fc5adfcd33704988c58
ACR-374a942af5f74274965c44022e54f906
ACR-62062c7edd1942218c4cc4f1f9d81421
ACR-eb8d79614b544d77bcddf5aad9138231
ACR-0dd99d5b9f584719bb4bc754b8050d89
ACR-80f503a26d604a6e933528caf7843819
ACR-3a697b590be84a70ac44f4035a973a99
ACR-c51d3d3ea90943e9981fe0a7afdbd43a
ACR-d26eb11ddd784c6b890a82c785202c5b
ACR-5b6c73a45b4c480eb81b6b569d0b23bb
ACR-f5bc566d0aca490c9bce6337acbe64df
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

public class AssistCreatingConnectionResponse {
  private final String newConnectionId;

  public AssistCreatingConnectionResponse(String newConnectionId) {
    this.newConnectionId = newConnectionId;
  }

  public String getNewConnectionId() {
    return newConnectionId;
  }

}
