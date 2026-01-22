/*
ACR-1912cd41021640bd9d4d50b147ecb9a3
ACR-4532b99c283b4efdba8c2c278d98664f
ACR-d185047360c84bf39fc21f7d6b5a755f
ACR-98d1771dadf7487c85c9a52e7c4f17f2
ACR-bcb33a83eb624095b67d5112b7b7a522
ACR-59f9fbbd84e940c4b7098a57a2e3ad64
ACR-351fb683a6f1419a93401dd429515fdc
ACR-6fa41fcb7c8f49c8a6389146cace4517
ACR-272b0eab9ebb400d8701a32a5920af36
ACR-83ed6d55d3db4c8a98d1c58f2ddee103
ACR-57d348820b2f4d67a4009a957f866b67
ACR-2ea9c1af76b64fa195f83976b5961fd4
ACR-b36738f2693c47dd8a34ab8d1d2ecfcc
ACR-dee74bedb24f4307a39c4a14550df235
ACR-9e1933d4b2eb4c52bacc0b5b928c3503
ACR-7b5f333ca8a04e5586ef33db23aee4e8
ACR-cf45ad779f05418a80d81de2ac5d7217
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
