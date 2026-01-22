/*
ACR-e197ff5a87374b1499670c1099b6203a
ACR-b9dd8d85a5134d238e70cac5632b125b
ACR-6d3892d9cc1c4630a1acbd883faa460f
ACR-ea7fa02da2fc4c2aa7784e6335a3123a
ACR-f428486bd4d845fe810546aac98da345
ACR-a0f375f215c4408193945e4ebef4d5c4
ACR-077efe31a2c242d6b5b14999d2757076
ACR-33cdc4ca608546bd8762e0ff0fdcff4b
ACR-33663b7843974ab5b75dc22989ad20fe
ACR-77d835ea070f40039b5b441ba2568a79
ACR-2361e0b872f4497784c584c93f058f3a
ACR-0e53424c18044035b34372f5979b6981
ACR-18bc59411e3846809268164a0ce52a51
ACR-b917aebc0525494aac5089506ec2d755
ACR-aa0af2ac413b45c9bd81c17619ad6691
ACR-766a1bde0e20433db7d3b764c4ece3e4
ACR-cda0ca5bd7cc44bea1f1c6bc711fff55
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection;

public class GetMCPServerConfigurationResponse {
  private final String jsonConfiguration;

  public GetMCPServerConfigurationResponse(String jsonConfiguration) {
    this.jsonConfiguration = jsonConfiguration;
  }

  public String getJsonConfiguration() {
    return jsonConfiguration;
  }
}
