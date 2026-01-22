/*
ACR-92842bab91a84dae9406c5c1a574404f
ACR-a3160309f30c4ad3990153d80f1efb6c
ACR-333790475eab44348e6acfb2887da712
ACR-49bf92b2e58242878b118183d689d87d
ACR-6957763c23264f66a24b3b2811e1e115
ACR-fc2e277d2284408187170976cb692e63
ACR-d17b19b620854c0480ceafbac54bcbcb
ACR-c944cc9d006d4cabb6a76a9c0cc830fb
ACR-3a86397b9df74c19933df6cff9ed8d8b
ACR-6d50a46ef24747f483df37d338d77fac
ACR-301478f337c2491eb7aa9cccb18bdc6d
ACR-b1079b7f4e374117a1c4cc41decb1aa4
ACR-57102a85f16148a09f20829ad2728569
ACR-0b50a5a5e5ab462da66605e499dc5eb1
ACR-4c4a169268564ac28ec93b1a6e0c1f4e
ACR-80b281f475f94f9b99007a0540fb537a
ACR-b1bc71e3a9934eaab57a4eab30c6c0c6
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
