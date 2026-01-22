/*
ACR-2869d9f7eb2c4fa2b737ec7fb9f1b948
ACR-8b13225484514380a89400cd2d493e83
ACR-3e01968ca2394438a25460c1e0a2d679
ACR-a8f950b308d14bd59306904ab73e476f
ACR-5c41abf5100b40299c2a4d4733350509
ACR-a1c092b74eff485e85fa1bad3b72ba06
ACR-a8bc380b38794cd79926b419b1bc314c
ACR-8a8c4faab19c44369ed52d828b923eeb
ACR-3944da72200f4190a4a43c71ccc0833c
ACR-bcee25ba3c784663841d30e1242eeb51
ACR-28c0a775ae324c37b2097b522ee18696
ACR-4cc9f4a842da4e8481dc57a779c5bc7e
ACR-f6a56ae728bf4079a3914240cae2d8fc
ACR-273d18c13b8b441d979eb4f12ffadd0d
ACR-9b06ffd83ef946e1bddcb57e1076d7d1
ACR-79f81bdb958048ec912f9020dc6ca462
ACR-a08e76b8e2ef42fb951bca0d9366c9ef
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org;

import java.util.List;

public class FuzzySearchUserOrganizationsResponse {

  private final List<OrganizationDto> topResults;

  public FuzzySearchUserOrganizationsResponse(List<OrganizationDto> topResults) {
    this.topResults = topResults;
  }

  public List<OrganizationDto> getTopResults() {
    return topResults;
  }
}
