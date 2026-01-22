/*
ACR-dfa2fd2187634988bdb0383aa88e9373
ACR-374cdb8cdc4643e9825349072b2296e8
ACR-5c5f35ab6573409592fced6f2449d323
ACR-922c7c841aa7453f93540b8364fd68b3
ACR-3c4330ecdc124a11a424ea86e7c24844
ACR-14188213a3104990a914c146286de9a6
ACR-369f2cfd51a44508b6432b50596b7786
ACR-60925c7b7007497a828fb9622db21074
ACR-6ffd1be7da3d426991508e930aa110aa
ACR-6b5ac99bd7d74418ab942849654e1753
ACR-3a4dd1dcfe95447cb4f82d2510817573
ACR-b78c53e1f3264951a861affdd21dddd3
ACR-5b60b3b5db844296a4c8786531a9f896
ACR-2530a0b18cc94a97bd7fd1a4ec847ab1
ACR-0ef5cee09b4c45b4b4f4db0b20b6520e
ACR-fe59474f82fc4da78d5fbddc227735b5
ACR-36d2ee09a60445528ef5bcff4cd18368
 */
package org.sonarsource.sonarlint.core.hotspot;

public class HotspotStatusChangeException extends RuntimeException {
  public HotspotStatusChangeException(Throwable cause) {
    super("Cannot change status on the hotspot", cause);
  }
}
