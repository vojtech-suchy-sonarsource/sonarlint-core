/*
ACR-f93c60f959bf45bf82889fe1d8f5c7fb
ACR-590051c6aaf74308ae1e0df967bd3ea3
ACR-5131f45d826f4b0aa7c7c68d1a6ab1d8
ACR-c8ed10a3b63d4277a36e1f0222fac45e
ACR-eeab71e458864ccbaeb8370066c86a3c
ACR-29c92097dbe34cf4b2f2e7ded620af7b
ACR-b16dbe17afe547e79853f78d86221600
ACR-7f14ba05c5a4425aa94331d46959713c
ACR-63f6a007434d43239a50c6c9746ed184
ACR-bc14df4c3eb64cf880d23d0b865f1e17
ACR-665a5bf96b1344579315d517e5052fae
ACR-669182494e0046cda8fbaf2b147192ba
ACR-484abc28caa947efba985cd27c286f72
ACR-2fa0e78cca844793aa86e68143ff89f8
ACR-70b390b840b4493aab48ac2dcb4e3027
ACR-bcdcf385ba224527b2661bbdf8308a2b
ACR-21da44df292442ddbcd0ac41b53095cc
 */
package org.sonarsource.sonarlint.core.labs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({IdeLabsHttpClient.class, IdeLabsService.class})
public class IdeLabsSpringConfig {
  public static final String PROPERTY_IDE_LABS_SUBSCRIPTION_URL = "sonarlint.internal.labs.subscription.url";
  public static final String IDE_LABS_SUBSCRIPTION_URL = "https://discover.sonarsource.com/sq-ide-labs.json";

  @Bean(name = "labsSubscriptionEndpoint")
  String provideLabsSubscriptionEndpoint() {
    return System.getProperty(PROPERTY_IDE_LABS_SUBSCRIPTION_URL, IDE_LABS_SUBSCRIPTION_URL);
  }
}
