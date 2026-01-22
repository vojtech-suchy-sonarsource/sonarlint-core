/*
ACR-c5ed9b60d33945ee833d214e8436d99f
ACR-c7a388ea255d46328ec805838320cfd7
ACR-89ae4db7a2b941f8822dd039f78644d5
ACR-53ee854d23794b55aa01cff24a6f8f01
ACR-2b71738bb5a949ff96f93b822c06a275
ACR-15bb62716d944bc88160c7d24d43a837
ACR-0929623f9c6349bba0ff5970c8b5d04a
ACR-86bc7a91ed894530a7999e6c5862cc59
ACR-6d79cc15b2a54aadb163df1e2390ec4a
ACR-37dc456764fe46bca4701e8bbaf2f0ed
ACR-ba25510634284a67bfc709c639d1cdd7
ACR-b332917fbc4c418b9eb032dfe7d1d987
ACR-e459f6e92dde43fe9cc0721de48b3235
ACR-e1be93c9c08641c7abd8b427acbff5bf
ACR-f30c3f81f5004a36b07dc95566004965
ACR-db860761068343a198e7b62a25476f36
ACR-47a32b2df2f24e79b44f69012662ddd3
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.sca;

import java.util.UUID;
import javax.annotation.Nullable;

public class ChangeDependencyRiskStatusParams {
  private final String configurationScopeId;
  private final UUID dependencyRiskKey;
  private final DependencyRiskTransition transition;
  @Nullable
  private final String comment;

  public ChangeDependencyRiskStatusParams(String configurationScopeId, UUID dependencyRiskKey, DependencyRiskTransition transition, @Nullable String comment) {
    this.configurationScopeId = configurationScopeId;
    this.dependencyRiskKey = dependencyRiskKey;
    this.transition = transition;
    this.comment = comment;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public UUID getDependencyRiskKey() {
    return dependencyRiskKey;
  }

  public DependencyRiskTransition getTransition() {
    return transition;
  }

  @Nullable
  public String getComment() {
    return comment;
  }
}
