/*
ACR-602d9a80201c48f490049c6b0eacc6db
ACR-6e42326e70f94c17a0e92bceda258c2b
ACR-ee8c4ab079be40b5981ae2a90ff08204
ACR-03fab6acc90a42fcb846f3ed078165ee
ACR-b40d4c26996944d98ac4fb9618598291
ACR-7f25afdaa28d434db18dc70dd45b27f8
ACR-ae712b83e8f24e5b891ed939b768d686
ACR-1300183687d24212a2ebe199d6795e48
ACR-9b81741e25044e89a65a25dc2a2e3e18
ACR-3f5cca2e4eee4ff5bc33eb80ba1d4ced
ACR-831f7e72b01c4dbba94cffc39eb0b0af
ACR-a29a8e74730144a09ef296ad762eb10e
ACR-5e2e69a9e70a4132b01d79d5b5204169
ACR-7ee8ed4ac7ff44e68f73685884ecc3f1
ACR-5be601a2b51444a9aaa80813bf55c687
ACR-b641cb04ff214f57b639ef6175e55b53
ACR-c140aa5d9ada4aae8983d141ab450456
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.embeddedserver;

public class EmbeddedServerStartedParams {
  private final int port;

  public EmbeddedServerStartedParams(int port) {
    this.port = port;
  }

  public int getPort() {
    return port;
  }
}
