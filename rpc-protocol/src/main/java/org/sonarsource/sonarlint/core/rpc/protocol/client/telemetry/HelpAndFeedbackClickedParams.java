/*
ACR-8d88424ecc0e45648b8bc975c00aa95e
ACR-fd95679fafb54cfbbe563dfac7b71c2c
ACR-61b0a95735d645bbb8b67cf2dc31655d
ACR-1206db083a5b4c1fa594a82fada2aae1
ACR-95fdf368baec4349a593e00b8952fe75
ACR-9339c493f1dd4b10b3c87935d84989a2
ACR-09e681681d3a46a0b95d9b007f3f69da
ACR-a1b5d7ecb2414ca496072e31e9846875
ACR-7a294090b6d746a4baf65d4a71b2986d
ACR-f96ff205e57b42c78436e3633ac6fd60
ACR-e0da3e04b5304f0cad28d9b07c57224f
ACR-438cb6fc3ff548269c5a5f31af518d24
ACR-d6c959a802c940b3ada43ae39a0af249
ACR-0bef4c70f2834b05bbc5ee3f844175a7
ACR-d101e9ac0157454695a8a8fae0d2b5c4
ACR-bebd700af5824d24ba65baed2b64490e
ACR-e29f7a94493846a1b7a04b0a358a14e2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class HelpAndFeedbackClickedParams {
  private final String itemId;

  public HelpAndFeedbackClickedParams(String itemId) {
    this.itemId = itemId;
  }

  public String getItemId() {
    return itemId;
  }
}
