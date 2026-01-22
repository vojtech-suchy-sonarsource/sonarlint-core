/*
ACR-0af4af608101446088d949f5220af8b1
ACR-70876fdbcf244e87a585c27b71847406
ACR-48c345ef43f6403b8ac02e5ea086337c
ACR-ef9b533ed1aa4ecb8f7afcb1abf02568
ACR-47c4ba4cc3ba4106adeefde1a15f592b
ACR-47c0025f88ff44b0971c9a97506e854d
ACR-24eb720940c0408eb9ae6568c9fbcad0
ACR-ff12fc3786b4441593ddaf7d6555eba0
ACR-93306d11b71b48b38699f6741756c52a
ACR-73dc85f95c8f4645b4d852dc2766d542
ACR-da7cdda6d5b149f0812bfa7b70ce19fa
ACR-fc084dfe3b5b4c48875052178c1370c7
ACR-72d608e90fc44c3aa7e6ed0293e2a905
ACR-10de3c3d45f145caaea0cb161fe7c47e
ACR-906862e4535342c396a51e8545b0b10f
ACR-71e5ba620a9740bd841e1cc50b1e3b1b
ACR-db1e9fc9ffa748e8ad9a55b89efcfb89
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.nio.file.Path;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
  TelemetryService.class,
  TelemetryManager.class,
  TelemetryLocalStorageManager.class,
  TelemetryHttpClient.class,
  TelemetryServerAttributesProvider.class
})
public class TelemetrySpringConfig {

  public static final String PROPERTY_TELEMETRY_ENDPOINT = "sonarlint.internal.telemetry.endpoint";
  private static final String TELEMETRY_ENDPOINT = "https://telemetry.sonarsource.com/sonarlint";

  @Bean(name = "telemetryPath")
  Path provideTelemetryPath(UserPaths userPaths) {
    return userPaths.getHomeIdeSpecificDir("telemetry").resolve("usage");
  }

  @Bean(name = "telemetryEndpoint")
  String provideTelemetryEndpoint() {
    return System.getProperty(PROPERTY_TELEMETRY_ENDPOINT, TELEMETRY_ENDPOINT);
  }

  @Bean(name = "initializeParams")
  InitializeParams provideInitializeParams(InitializeParams params) {
    return params;
  }

}
