/*
ACR-0984f926c7cc42308071fd746b9127d3
ACR-5e8e52a6560b4f8f8aa66815fd3a5299
ACR-0d680b5e021648eb92b5c06bdf13ce2d
ACR-d5c09a9b855f4e46a7fd12aee49ffb25
ACR-2fd951210de647f4be114b4a186a9ad5
ACR-c4a1e17ddf734153b0362a11ccc6ddb4
ACR-3b2b9ef5d69e45f191a1dbe51cd81d70
ACR-6daa51ce34e84bacb7408cd1c3b64550
ACR-fccbf2899a7e4e32bb5fc9b42e8edfc8
ACR-452f3fac68314da6a9152950a4c39eb8
ACR-a54c14fc5ea741a3967e0da60f2a3123
ACR-78f5feee08e94df6b2f12254c44c7f22
ACR-9ba7af996759473c8aad6203983eca99
ACR-3843a474b2a6455c9c2c09303b096d79
ACR-b9d920fd53354d93ad9d1422e84212b2
ACR-133d18fb163e404cac3b1bab3884f751
ACR-4bb560b28c7b463ea567e83b22d87447
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
