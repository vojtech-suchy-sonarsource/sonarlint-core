/*
ACR-7f3da85ab1624e3a975de34083e84bf9
ACR-adbd3d9c640f43ab90773a90a57b2af5
ACR-dcc32c3d36b145bf99af497d58ae1006
ACR-62a3514c3fc046c0ae555b56af283027
ACR-5da80920033d46cbb6c819341608b2ac
ACR-6909b8e66aa24e62992fcb4e99017c54
ACR-2642daf76f854e38a621880062a8b5f7
ACR-3fd9a7e4a5e043498f4234dc7140d2d8
ACR-80036d2f84a34220940ba0d79621d658
ACR-97f59dff2a184f35afbfa7846ebbbaa9
ACR-ba92ba90c0504f9fa8aef661a8d88e9d
ACR-7bfae3ed62594300b96b824fd0492696
ACR-f0f885547c8c425ebbfc09585e11edda
ACR-2932f570067144eea852f7acabafea09
ACR-2d0bdea81a0649a78428455da1051b0f
ACR-ae0673ebb4474a92a9c97b5a3b7be26d
ACR-78f44b8757b44874848efa5621d0e47b
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
