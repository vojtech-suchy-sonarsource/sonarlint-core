/*
ACR-9ba3805abd724407b6aa1fd6eded12f5
ACR-10a4921de23f4b93a0c41b8b40097552
ACR-7ef335ef1fd84ab2959720b14e4903e7
ACR-10899fa0523d48baa4d6fd5ec87f895d
ACR-52b99e093b6140aa88c1bcdfafaec1ce
ACR-a35cd0117a9245ba9cd558a2079dd9ba
ACR-6065b93bd3834779b646dff2d896b039
ACR-3b6e13f210dc42a8bb9aa73e9e0d0604
ACR-41ecb05b6a284db297308eac3ee4192d
ACR-0d93100feb254df19dbb5ccd55d15c6e
ACR-bda7c4f8041b4b6a8ce6d0c024b376b5
ACR-75fda40a88484e8eae1ac185a7a09f40
ACR-58ced0ec3cb74238a1de3d202b86fcca
ACR-08d19cc043d1463891815bfbbba16d76
ACR-43ce14aaa35949db9b4428156d2ff86e
ACR-570d7d3bb62345cda1a494516cd5c59c
ACR-95a022be1c554a4fa460ca6d342bff8f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("rule")
public interface RulesRpcService {

  /*ACR-a51bf42de2c5461b8c247ce3ad0bf846
ACR-3945ecb6401a4439b8d5fc68ab605609
ACR-d3cb97bc18f44604a22967c78473cbf9
ACR-a20e78b2b6e743a0be310ddcd31cfc21
   */
  @JsonRequest
  CompletableFuture<GetEffectiveRuleDetailsResponse> getEffectiveRuleDetails(GetEffectiveRuleDetailsParams params);

  /*ACR-8c5f5f13827f4c4a9dd971b2be761f80
ACR-f4cde366206f4fd993fe79f82bcdc5fb
ACR-d750e045b3794bf8a9922c1b2c9fa638
ACR-73e6f0e9a34e4573aeed8b78390f10b5
   */
  @JsonRequest
  CompletableFuture<ListAllStandaloneRulesDefinitionsResponse> listAllStandaloneRulesDefinitions();

  /*ACR-ed872acd83ee4334b19a486fc64d8344
ACR-29b0e17f5d134b6ab21687e2b6f604ad
   */
  @JsonRequest
  CompletableFuture<GetStandaloneRuleDescriptionResponse> getStandaloneRuleDetails(GetStandaloneRuleDescriptionParams params);

  /*ACR-c3a58f9c390446b9947520ff2ae97f90
ACR-8e252e778ed249daa7f1e655ccdf7df8
   */
  @JsonNotification
  void updateStandaloneRulesConfiguration(UpdateStandaloneRulesConfigurationParams params);
}
