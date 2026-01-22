/*
ACR-a29656b93fe34f4696b53ce11eb192b0
ACR-b2090d6d99fd45fd8ff23739c2eab867
ACR-4b81265dbd744138ac4990a1d3a996fe
ACR-76f77f5dad4b491d9001f845fbb2c8ba
ACR-d976695f9be64a73aa8c8181957d6afe
ACR-a8e401e04bb34d148a78c2d9687d16bc
ACR-7948162c7b7b418e8b379bc98124fa98
ACR-bcdc36d2252c4c94a5f30e65879b027d
ACR-64edfce846df43ff81048148ba50bb0e
ACR-1b7f36b11d7d4050bac327aedbd58dc7
ACR-85c9a9f089314fc18151342c0d3c900c
ACR-a0cdb0d55d8d46cc8872f8e88ad140e3
ACR-06f0066f4fed401bad4e53f8f178f0ca
ACR-8f77dcb6a8244ce39c7e94d9c6cc1494
ACR-4666253ea8d0492e9ae2bbf000682860
ACR-68c776e1a2f8495d9d6146bd0a49e925
ACR-c63b99ac48ea4af6b84ea8a308b9805d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.Map;

public class StandaloneRuleConfigDto {

  private final boolean isActive;

  private final Map<String, String> paramValueByKey;

  public StandaloneRuleConfigDto(boolean isActive, Map<String, String> paramValueByKey) {
    this.isActive = isActive;
    this.paramValueByKey = paramValueByKey;
  }

  public boolean isActive() {
    return isActive;
  }

  public Map<String, String> getParamValueByKey() {
    return paramValueByKey;
  }
}
