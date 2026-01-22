/*
ACR-614330e8faae42329e48aa29a3e35fc6
ACR-5dda08c491b94395b9eb7597e71a8507
ACR-6b77e53cb1244886846a10ec6bfa3440
ACR-688e1890d0cd42088a58932f9dd28a7f
ACR-b4602412abdc4aceaba908b2d44022cf
ACR-ffb4c0c22a1f4adb8d9907fefe258805
ACR-0b1127af01094a9b82385b2d5bddc7d3
ACR-ace28131e69541a5abb4b241414a4be6
ACR-351b90b057e643aaa1e7467279edcc80
ACR-7c02edcc53324fdca6a7df8974668edd
ACR-550e24c30e1744b6a22a8a40b55415e3
ACR-a42e7d0103984400846d7f1db553553f
ACR-f3eb8b7d4eab4cd0b7be0adedefce36f
ACR-ddfd81dcac3f4686bec1f9f63b9a946f
ACR-1d3a8ecb5972464b89b556b7d101c7a0
ACR-46b7f09740fd4419be580cbdb4617798
ACR-85ecd5727f854ac9b725f6809fc69270
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
