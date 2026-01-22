/*
ACR-6b86ae9c0155445ab3aa7a3320644382
ACR-b0dbd4380c7b4446b9f2def622301302
ACR-da70c5456def46d5bd8bc71852107da3
ACR-06d3760ace75477ca491513e95bcb00a
ACR-0ecd680c35034600becd85e9dad915ba
ACR-25275b2437c34d3db63ba41a6807c8e6
ACR-3e2717e977d645c0b2bf70eaa8412c5e
ACR-460e0cc66e74457d9997024d1bd1898c
ACR-4ffc2a8226ea4392b38c2e44546a4799
ACR-549aac736402475abd3310a2a4e94452
ACR-10a4175143c04e9c90e55af502fa14f1
ACR-11ee886b82df4a6eaa6b3758088381bf
ACR-af93047fee714c33bdd8f966fc9210fc
ACR-36f6444c7b42441cb7eb603876760e61
ACR-017f16a6a22544d3b2aa78c803f73543
ACR-4ea668b531c44a778ad9492cd6d989f1
ACR-1720d82168cf4f4db69b7a3513ea4d0d
 */
package org.sonarsource.sonarlint.core.telemetry.gessie;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
  GessieService.class,
  GessieHttpClient.class
})
public class GessieSpringConfig {

  public static final String PROPERTY_GESSIE_ENDPOINT = "sonarlint.internal.telemetry.gessie.endpoint";
  public static final String PROPERTY_GESSIE_API_KEY = "sonarlint.internal.telemetry.gessie.api.key";
  private static final String GESSIE_ENDPOINT = "https://events.sonardata.io";
  private static final String IDE_SOURCE = "CiiwpdWnR21rWEOkgJ8tr3EYSXb7dzaQ5ezbipLb";

  @Bean
  String gessieEndpoint() {
    return System.getProperty(PROPERTY_GESSIE_ENDPOINT, GESSIE_ENDPOINT);
  }

  @Bean
  String gessieApiKey() {
    return System.getProperty(PROPERTY_GESSIE_API_KEY, IDE_SOURCE);
  }
}
