/*
ACR-18b5c0e650dd4486bf7be85501c292a0
ACR-bf389e8b951d4bebaaea6521f7ee3153
ACR-459f92430d3e4608a32b41ae13ffbfd1
ACR-5340c52e83634d1789c6091d7df76aa9
ACR-ce8e8ab60df64fe2b5d2fdd4da5376bd
ACR-d32f755e9ce844aaa0ece830d999096c
ACR-d49bbe7256bc4b42835689269ec261d2
ACR-bc2aa96a7308481a855f21969d5ee5d1
ACR-1ac357d8b68d4c4bbcdfe26acc0a4d14
ACR-d1555698161b41169ef187c335a2dfe6
ACR-80c5f37c30f74c53a8d9970158b04fc7
ACR-d374a33567eb4b69b32bc12d5c913e2c
ACR-cbc56f50b05744c2b0d2c391ee205e38
ACR-80cd67fb5f504819afad2ba8a4b7456a
ACR-88b3ab5763c644089cdaa14030f04d19
ACR-75dd90d879f74c92b82e998a0918a6da
ACR-999187eadf07424e9d4836681bdd4b05
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config;


public class DidChangeCredentialsParams {
  private final String connectionId;

  public DidChangeCredentialsParams(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
