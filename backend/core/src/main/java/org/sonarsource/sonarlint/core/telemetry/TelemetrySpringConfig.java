/*
ACR-625b349bb7a8463f8e7f906687a8f889
ACR-0f1ed6e32f074f3fb1ade716794d9504
ACR-228b9d06579c43858ac59fe1058b2156
ACR-f0d8675ab0944d76ac1864d3a1ee5eea
ACR-0b13c4ae2aff484ba07f58e406d16efd
ACR-63bd36f684744d108c8618a750bbecbb
ACR-bfe6fa69815d4f8dab8b3eb3dce75614
ACR-5c47378ba16e42edb855bee77c4b8ff1
ACR-ba09648d4ed04f96a9a03f5e6d62caeb
ACR-94806a6c125e4adea6037f897846ddeb
ACR-3870a81e0cc04f658500e06e4a395e82
ACR-0a74f29be3a14848a339d2f2653a72b8
ACR-4dba442e996c4b88a4b36e1905c0732c
ACR-51bc2f84e159496ca31bf421d4dba9db
ACR-5ef6cdbe049d4d34a460dcbf76a4a3cf
ACR-02fc30aaa6884e72816f9341e8586e91
ACR-802f8edde13f4aff990da417f90c6533
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
