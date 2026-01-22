/*
ACR-043754408d2541758cef16ba1562fa1b
ACR-d049969eb4964b7197a598684291f8c4
ACR-e7ca9a6b8073402ebd83b6861ae4291c
ACR-2405d556099b4b368c9d77602b3914f4
ACR-ba889fc5f3184ac5baaf1aba4489a757
ACR-55ac08d9b88e4e5cbac6eb841e7b9f11
ACR-08e011c580974dbca3ada84858005069
ACR-294350ff91cd4dd1b08baf11681e01b5
ACR-8a1b4f9f2e8e421aa5b93486a634b5a9
ACR-ba98f9476166442c9b587288dc8bbcd9
ACR-e4a0a9a9fdb242c1b4e7f0433242aad5
ACR-4d0c0f99ac834158a6ff1d81abbfbfb3
ACR-9f66114b174f45d1888050b1d3efdba8
ACR-9da1f5c21ae54cf9af4cbde71dcab699
ACR-ea4b2902c882479dbc5eee222e7af117
ACR-9d0a7aecad6f4026b6e80911888eb90c
ACR-184bd68ce21048a5b71bcc208248bd42
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class ToolCalledParams {
  private final String toolName;
  private final boolean succeeded;

  public ToolCalledParams(String toolName, boolean succeeded) {
    this.toolName = toolName;
    this.succeeded = succeeded;
  }

  public String getToolName() {
    return toolName;
  }

  public boolean isSucceeded() {
    return succeeded;
  }
}
