/*
ACR-79af76c106594073807971b611b84ad5
ACR-b5254338288844f6beb81f61beb1b093
ACR-de59f34d5e2d406c80ab01e8c6ceeb9d
ACR-467b31125dd74c8dad4ad39180d4fc23
ACR-42d754a6495c4ebc958230ffc61d256e
ACR-19911b04f74841f1914276c54e1ccaf0
ACR-c73074c356ca4e9ea20a811f4537feeb
ACR-723002ceb54545aba7b0f056144bef87
ACR-9496aae81df3463e83c3ebf3aaf9ec68
ACR-d76ec1396395443586cd5fe4fb81d68e
ACR-4cc2100f51fe4f599083a37e3cf1fb78
ACR-036a3314dd7d428a89c55c0f069be52c
ACR-c70be2f805574436b4c27848e8df5a12
ACR-492faf1effc64b8bba6f0ddab312838a
ACR-4354c998989c4ec098facce7936cc1a4
ACR-d39b26a1e13244a7b2ce012af14b2090
ACR-21e6d16451634476ac3c682ee4a26128
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class GetEffectiveRuleDetailsParams {

  private final String configurationScopeId;
  private final String ruleKey;

  @Nullable
  private final String contextKey;

  public GetEffectiveRuleDetailsParams(String configurationScopeId, String ruleKey, @Nullable String contextKey) {
    this.configurationScopeId = requireNonNull(configurationScopeId);
    this.ruleKey = requireNonNull(ruleKey);
    this.contextKey = contextKey;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  @Nullable
  public String getContextKey() {
    return contextKey;
  }
}
