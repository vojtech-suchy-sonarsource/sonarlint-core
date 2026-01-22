/*
ACR-5f3573628df24c5c953ba0213ca6b855
ACR-471195a30ba448debdc46febc1fc3193
ACR-69c9a7322b9c41b58c6c76187f76df77
ACR-78027eb28dc74c97b6ca01d6901e8aff
ACR-249f9a5e3a214b2aa785613c0986fee1
ACR-daf733029eb241b9b8874b6769ab57eb
ACR-c42ad1f36fa04e01933ad2df02a6ccde
ACR-488916fbcd8d467ca14d1f0feb931b3e
ACR-c52070e91340485d9e4453e8f3e8ec03
ACR-81c8a8414b654195b943950eee96da47
ACR-fc244fd637da45e0854d28aa20bdaca2
ACR-ba127863452f4e288df4a54d14fdd1c0
ACR-7a9c53e7cb5843a3844cecfca8bfa0f5
ACR-88c6d42ff0c94e4eb143e91a1ae59a39
ACR-18f602ffe0304a0a973424466ad03ce6
ACR-07beb4ee32e84299a0791c0aba88ebb8
ACR-e6932cb51e844ce2b7b4d3618ae49a9f
 */
package org.sonarsource.sonarlint.core.telemetry.gessie;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.UUID;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;
import org.sonarsource.sonarlint.core.telemetry.common.TelemetryUserSetting;
import org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieEvent;
import org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieMetadata;
import org.sonarsource.sonarlint.core.telemetry.gessie.event.payload.MessagePayload;

import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.GESSIE_TELEMETRY;
import static org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieMetadata.SonarLintDomain;

public class GessieService {

  private final boolean isGessieFeatureEnabled;
  private final TelemetryClientConstantAttributesDto telemetryConstantAttributes;
  private final GessieHttpClient client;
  private final TelemetryUserSetting userSetting;

  public GessieService(InitializeParams initializeParams, GessieHttpClient client, TelemetryUserSetting userSetting) {
    this.isGessieFeatureEnabled = initializeParams.getBackendCapabilities().contains(GESSIE_TELEMETRY);
    this.telemetryConstantAttributes = initializeParams.getTelemetryConstantAttributes();
    this.client = client;
    this.userSetting = userSetting;
  }

  @PostConstruct
  public void onStartup() {
    if (isGessieFeatureEnabled && userSetting.isTelemetryEnabledByUser()) {
      client.postEvent(new GessieEvent(
        new GessieMetadata(UUID.randomUUID(),
          new GessieMetadata.GessieSource(SonarLintDomain.fromProductKey(telemetryConstantAttributes.getProductKey())),
          "Analytics.Editor.PluginActivated",
          Long.toString(Instant.now().toEpochMilli()),
          "0"),
        new MessagePayload("Gessie integration test event", "slcore_start")
      ));
    }
  }
}
