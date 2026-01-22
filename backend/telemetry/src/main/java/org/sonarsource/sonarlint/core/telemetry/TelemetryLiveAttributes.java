/*
ACR-6360aec3caa24e78abc4f514d4322c7f
ACR-818ae1a06c394d99b404d7eb1e0724f2
ACR-e3ac5d80be154cb389e1df07ff8aab9f
ACR-7c6522c0ec414ec7a74eb59a2434d9fe
ACR-ada1af9106de49fdb74cac89916df894
ACR-6de364df4391426cb7c39157d99b05a6
ACR-b00c07c2767c4edf89b9e6ae6cd64bf0
ACR-9038bfaa45f04ba9854eabe9023c529f
ACR-c959c41514254c01817d380294682512
ACR-35edb0b8bd224b45ba458472cdccd8b2
ACR-e65193005b0c439eb94bab5f8c9279be
ACR-b9f13a16ce0546b099af7176783b9d53
ACR-22496678580b4f96b3b4a0bbad3b45cd
ACR-fba566a5a2de4386b9cf688c02761178
ACR-39263408f0c94a33b3e85ed13fa88f51
ACR-da6ccb44c8de42a8878f33643a9b38ab
ACR-3664aafbcac5429c834efb10b5a0dab6
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.TelemetryClientLiveAttributesResponse;

public class TelemetryLiveAttributes {

  private final TelemetryServerAttributes serverAttributes;
  private final TelemetryClientLiveAttributesResponse clientAttributes;

  public TelemetryLiveAttributes(TelemetryServerAttributes serverAttributes,
    TelemetryClientLiveAttributesResponse clientAttributes) {
    this.serverAttributes = serverAttributes;
    this.clientAttributes = clientAttributes;
  }

  public boolean usesConnectedMode() {
    return serverAttributes.usesConnectedMode();
  }

  public boolean usesSonarCloud() {
    return serverAttributes.usesSonarCloud();
  }

  public int countChildBindings() {
    return serverAttributes.childBindingCount();
  }

  public int countSonarQubeServerBindings() {
    return serverAttributes.sonarQubeServerBindingCount();
  }

  public int countSonarQubeCloudEUBindings() {
    return serverAttributes.sonarQubeCloudEUBindingCount();
  }

  public int countSonarQubeCloudUSBindings() {
    return serverAttributes.sonarQubeCloudUSBindingCount();
  }

  public boolean isDevNotificationsDisabled() {
    return serverAttributes.devNotificationsDisabled();
  }

  public List<String> getNonDefaultEnabledRules() {
    return serverAttributes.nonDefaultEnabledRules();
  }

  public List<String> getDefaultDisabledRules() {
    return serverAttributes.defaultDisabledRules();
  }

  @Nullable
  public String getNodeVersion() {
    return serverAttributes.nodeVersion();
  }

  public List<TelemetryConnectionAttributes> getConnectionsAttributes() {
    return serverAttributes.connectionsAttributes();
  }

  public Map<String, Object> getAdditionalAttributes() {
    return clientAttributes.getAdditionalAttributes();
  }

  public boolean hasJoinedIdeLabs() {
    return clientAttributes.hasJoinedIdeLabs();
  }

  public boolean hasEnabledIdeLabs() {
    return clientAttributes.hasEnabledIdeLabs();
  }
}
