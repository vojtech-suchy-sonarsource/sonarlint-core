/*
ACR-e74789c9ba9b4cc8bea066cba24913a6
ACR-1c131802c6a341f1abf5fdd20147ed5f
ACR-9bbb7d6debbf4feebd8a96b746d5a70d
ACR-e3f3016263b54bed9c41866c0dfd1538
ACR-4b91eb9fb68d4b32919b8323cbf0413c
ACR-445e048650cd4dcda01c27546f4738b7
ACR-c68b5db44be34049aae1b1211e4b4c7a
ACR-0470500b2ae546f5a317c03509f47b4f
ACR-df7076441a4a443fae9ee1032b0e0db0
ACR-3168f2f458cc4a7690dc61fc71f01068
ACR-5a0d1136fbe04714a862c1068d5c97d3
ACR-fbb23e57e54b4c76930528d20085ded8
ACR-87fc62d3071643788909c134ced1ecfc
ACR-7d4dfe1bbe6d4ea9897cee523f8d70c0
ACR-3241bce8eaeb4291812c2b876c45840d
ACR-c5bbb68f979b421da8b2554b793792c9
ACR-f44bd999443c4288bfcfa2ef73455600
 */
package org.sonarsource.sonarlint.core.promotion.campaign.storage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.sonarsource.sonarlint.core.commons.storage.local.LocalStorage;

public record CampaignsLocalStorage(Map<String, Campaign> campaigns) implements LocalStorage {

  public CampaignsLocalStorage() {
    this(new HashMap<>());
  }

  public record Campaign(String campaignName, LocalDate lastNotificationShownOn, String lastUserResponse) {

  }
}
