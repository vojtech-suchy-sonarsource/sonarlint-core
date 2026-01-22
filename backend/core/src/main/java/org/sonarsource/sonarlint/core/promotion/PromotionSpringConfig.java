/*
ACR-f0a803eecfdd42969766537b759fe967
ACR-4115896bbfee48768440bacca0740c3a
ACR-165569a514e340fa977e700253927007
ACR-2b699dabd9834b29bbe15d0b2a5da92b
ACR-63c718a3f1984a0d84c66c8d031428a9
ACR-7e30401b3ea144a0ba7d9997e8c4d6d6
ACR-093144cb7068428d9c10a6419c46f0d0
ACR-b8c23d204ed14f44a70037ddd75f43b0
ACR-0bea1e20d53247209a7d7d1205e7c581
ACR-637301c2d9ba42cd92f1e16b0fd9bdd2
ACR-7e1501264cc544249d7b0a0c1c7bea62
ACR-945ef8b1bce34acbac582f0819038c05
ACR-7d9830f1ba3a40b386a11317a1ee7de0
ACR-e085a574a99048929556b52e69fc4751
ACR-135f8093c75242648a527360506e303c
ACR-efa977afef6c4b009a69df7d71aa8f84
ACR-ef30569201a641bba8adb1d96d8a1b3b
 */
package org.sonarsource.sonarlint.core.promotion;

import java.nio.file.Path;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.promotion.campaign.CampaignService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
  LanguagePromotionService.class,
  CampaignService.class
})
public class PromotionSpringConfig {

  @Bean
  Path campaignsPath(UserPaths userPaths) {
    return userPaths.getHomeIdeSpecificDir("campaigns").resolve("campaigns");
  }
}
