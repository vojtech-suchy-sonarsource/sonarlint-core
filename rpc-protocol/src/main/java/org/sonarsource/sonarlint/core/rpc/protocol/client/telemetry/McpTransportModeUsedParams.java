/*
ACR-0760c16b66ff42828b2d9990c966f9a3
ACR-f6481a55039447c4afe147e499d5dd63
ACR-0afe5d2335804feb82e66790eaa669fb
ACR-e2d383c09b10428a9c16936fdada104e
ACR-11fda88333c14dc7b05e771d3f28bcc7
ACR-549054ecd89149b0b1860255c35a98f9
ACR-c72928ec706d4982b242a1822c011b71
ACR-9d06969ebe3b46649aa59bc269327064
ACR-c2e32cf7011e496396cbd0587681074e
ACR-b95e40ea8a83445598c640be8e666a86
ACR-7d871926938743589fef7b4cb46442cf
ACR-937ec4c5d561450a99109e881186890a
ACR-8ea3a62efdfe477dbcb6b598aca518a7
ACR-1922104d16e54a059072868e226c7aca
ACR-4cc5fe801d3e4078b367476399232465
ACR-e671ea623ad749fe8df6729985a75d5a
ACR-86c0c0075d944574ad66c48f87bceb36
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
