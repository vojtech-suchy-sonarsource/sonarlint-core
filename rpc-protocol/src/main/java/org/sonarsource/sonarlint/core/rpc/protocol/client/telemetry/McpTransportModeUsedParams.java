/*
ACR-b80d0641f4ef4f2a92d8f8c4825e8ee2
ACR-20f5e2e12aea480f8ad9798841e5c250
ACR-e7afe6d0be4743029271378bed0a7fa1
ACR-9f720405502341c6b06039ea2fd78a38
ACR-b867006ef0cf4d19916272c86bffdff4
ACR-941ede917c3a47a3a05da606bd0766da
ACR-1607906770db46c8b28f906245276d6a
ACR-e6ab4c60f1b44597b1e6c11c1a76b14b
ACR-22318d1ce5ba4ec8850a6925aed4ab3e
ACR-c240cf024bbb4d509d8a522eb161fb6c
ACR-78b676dbbe4f4c37b26162208e3776f5
ACR-abed41c86cd2404c95c58cda6bb22b68
ACR-88e11deaae0b4fc8972983bae62405e8
ACR-66e554bd18434506a6728b28f55e9f13
ACR-c38f32c559d84165a7ded7dfb8c6449b
ACR-0cb7114e5c3143c5b4394a72de94512d
ACR-c5c59c84a77f4269b2e27dd0029f4f6e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class McpTransportModeUsedParams {
  private final McpTransportMode transport;

  public McpTransportModeUsedParams(McpTransportMode transport) {
    this.transport = transport;
  }

  public McpTransportMode getMcpTransportMode() {
    return transport;
  }
}
