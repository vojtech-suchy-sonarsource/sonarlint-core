/*
ACR-3f8314cc80984ae7a4044e04ea7f6ad4
ACR-02aab244d9d34164bd9863eb082dd145
ACR-c9327a2878134cf1b81861acc4c4592e
ACR-a7c13172eaea49cdaef00e7b91edf561
ACR-2b33a6a34a79416ba95fd4fa2053b773
ACR-f2dbfa9aa52a4c4ca95a172396f62a2c
ACR-d247d0713d38450fadca36bf7eca9c35
ACR-c68181b6d2ca4d1ea79e58abd17c5e86
ACR-27fd9e08a6094eb4b32a4d74b6b64f48
ACR-0fb1601b147842e886bf69ce54843ecb
ACR-e4839e4efb6f42f38a8d3f934b408e84
ACR-f51364ac37b9431ea3410105279cf700
ACR-0ee9404910bf407e851fcd3dfc3ce256
ACR-a133abe9215a459194244def2c4ebecb
ACR-5fd499581b4841c1814cc33a5ac14b91
ACR-8ef7079d5fd24c2f812b5dfc8a267462
ACR-f2658c60fa6646119ee93c39cc91d72f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

public class GetRuleFileContentParams {
  private final AiAgent agent;

  public GetRuleFileContentParams(AiAgent agent) {
    this.agent = agent;
  }

  public AiAgent getAiAgent() {
    return agent;
  }
}
