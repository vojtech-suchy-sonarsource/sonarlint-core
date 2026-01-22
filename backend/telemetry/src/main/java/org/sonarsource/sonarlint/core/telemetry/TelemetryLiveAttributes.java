/*
ACR-c3cd35ec6e614af4a013e1bd34bfed5e
ACR-3862d9f75cad4cb48e4ffd6d2f7cd503
ACR-461efa31579a43c5916a160a034a4e5f
ACR-4ea0c67944ce41d19f908c0a47f5db2c
ACR-df55cc402da2412d89ab8feaceb42b12
ACR-9d25a3d3766d4a91a8227525312cbb02
ACR-d6e8876ab3514ca09a2ff6e3021330a0
ACR-2f2a609298324574b139f19cefc1f902
ACR-11eb005f328649819e21c2c996d6bad9
ACR-d56a6502147744de8ce6bd72e2d5ca95
ACR-9d5ccdc7c4fb41a98444ff10210a83d4
ACR-9b2682394a3d4299a4435025418136f9
ACR-b99cf502280742bbbfe8f53e412f5fa5
ACR-fd32e7f0ff0942c0992584ebf21663d9
ACR-355457a08492434d84864f99d5e1b586
ACR-71fcfae745dc43878c1184f2008c1f3b
ACR-dadd0d886e4243fbae2b6846d2d99961
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
