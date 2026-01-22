/*
ACR-f83b6afee13e4efda0a7778e813929ff
ACR-cff8b967260c4f1e84feffa5b2a87588
ACR-ce4a1ebe34d74a2e87523755ca6fc7f8
ACR-fc6d63ce2d8c4dc29e11da45934d8d75
ACR-b5a70b7547bd4b24a502ee5537a3d513
ACR-5aa9c542efe243f0be240a15c2c0ac8d
ACR-aa947d2893ee4ef7930d87365a013491
ACR-2968b0b23bea49f99c72fdc9a968cf6d
ACR-ee1f1265367c45378bad975d5e9dbdaa
ACR-089c393e3e0d4fb5a23abb7856d45ec0
ACR-9e007f3c06b6484cb54013d1c8ac2bc7
ACR-dc3799073d0a450ea25a193fc90a1fbc
ACR-03c16058f27a4c2ea083cea26e14912f
ACR-9573c632478f4a68a60f0765dddf6fe5
ACR-92cab9f2c683431a8a58214e4ec1d3e0
ACR-2ef9466c1c26410aafe966258d793656
ACR-5c86e5b4fd544aff827402d2908e19eb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class IdeLabsExternalLinkClickedParams {

  private final String linkId;

  public IdeLabsExternalLinkClickedParams(String linkId) {
    this.linkId = linkId;
  }

  public String getLinkId() {
    return linkId;
  }
}
