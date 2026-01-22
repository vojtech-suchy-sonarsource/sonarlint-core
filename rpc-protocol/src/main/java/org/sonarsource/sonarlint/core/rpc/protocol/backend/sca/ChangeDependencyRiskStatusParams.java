/*
ACR-93b32580df2e43b791962a88cbd9e73c
ACR-b534dab078a34b85915ced505cc8ed1c
ACR-5bda7d644bfd439e97e9360432fbbaba
ACR-d88e5b604b95435294dcb197b473ae03
ACR-3dac907f3efb4403a0937fcd8582209b
ACR-e9367771df5345c6bcab0538b9e0404b
ACR-8c5239dc874c41039fbdf485ffbe91c0
ACR-ee3cff0a34624f17829d7908bedafe1e
ACR-7cdabf7befb1487b988a5c9d37bcba53
ACR-98ee5fa5cb7c4e87848e6b65bdd2566f
ACR-2abd5287667f412ebafac275a8ab7b30
ACR-b762541bb84b4853ac1be184a5dd018c
ACR-9329ccae52ce4145b61f0d4e19626539
ACR-ebf399f5e0cb403ba5f7b85510052c10
ACR-e95de124e58947a7bb64db9135c42c73
ACR-8742299cbb554926a3b862fc5a73bb15
ACR-bd12bddd6cdc46119e82251bf95c3f4c
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
