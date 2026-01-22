/*
ACR-b11ec40c9f0c43cdaba71d963303149e
ACR-cef3dc16a8b94dc4a24daabebdc7d843
ACR-54d85dfd8ee84d668282febf949f2124
ACR-9b33287ea39542be8a735c6373065d52
ACR-0506da2eebda4e54b0c4b89a543943cf
ACR-7f12d21f0ab8402fb58835f2efd0cee8
ACR-2a2858ed46fa4d62905eb21a4fce9c4f
ACR-e4b4d2fbf6b64848996c1880f3735fef
ACR-670cb59953034e6198dd8147c7e4d030
ACR-9806583d282a4ec9bb4da6c817e58ef0
ACR-665f8d52c77c4b3fb379e2d18992004a
ACR-fcf5bff77d9b4c42ac4ea3cace3c8359
ACR-b0b20aa479bd46f7bea7b1abbb90a9e9
ACR-9c847bd2955647018a684a66d260a59f
ACR-2177bfde00b548568b0e45d0548eccf8
ACR-950777b2ff344aec93d7819e2898fc9e
ACR-99d41124f1634dc4953109f92847fa95
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
