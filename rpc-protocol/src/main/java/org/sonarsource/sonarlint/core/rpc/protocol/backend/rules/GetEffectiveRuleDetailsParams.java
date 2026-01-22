/*
ACR-96317942120d45beacdf3a502f3b1ab5
ACR-56f25f3f50724215bfb5c737a0872c26
ACR-d4069468ed1b4ffc84e8b663d362315e
ACR-bb05fb464a8b4da1b0089d8eaa237ebd
ACR-b32fbb212c95475a8b707c91b938ba78
ACR-66406c9277724bc9a6876170b9fe34d0
ACR-e06b65872159408ab77ace7a3c6524db
ACR-28a752dc5fe54475bdc295bea87d7a7c
ACR-5cd5ab10fba04760a1ec2e32778336db
ACR-ae5c2280c6f3407983ed397511b3b63f
ACR-a14424f6a2f243a5ba7dd04c6de04b98
ACR-612d3cae18234500a64747ceb7530782
ACR-cdab132f50d44701b5f49f5acb12da9b
ACR-7da5e29ba08a4b679ffd57c7b448c140
ACR-ca5d0de9705e408995510f12ca35b005
ACR-aa443894f20b484ab39e796ce7e19830
ACR-57e19faa8afd402fbd4120bfc0afb762
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
