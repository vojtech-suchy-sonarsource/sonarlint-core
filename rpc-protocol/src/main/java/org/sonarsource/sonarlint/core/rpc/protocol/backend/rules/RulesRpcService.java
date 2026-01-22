/*
ACR-d06c4a72ced84be8863fcd1d67555b47
ACR-384bcfaf654b4494b05c268c6368a1c3
ACR-fd8e77e2528048eca31638d2234c406a
ACR-82208d9272ce4d8cb3afbe196cc417fd
ACR-ebad71de072346f994eb23e26cc6132c
ACR-beb78cb8b6804e15baca25fb9a20fc75
ACR-dafb30ab4f2543999068bad8c400e165
ACR-cefc2115236f41b2bf7c805fe640bb32
ACR-c986cf47826a43e28f843ee658651e03
ACR-874d7ac74ff6469496bb1520d89333f5
ACR-e2eea00411f241eebc30670caff279bf
ACR-907a3008e7ae4f319fc2a7172abaf0d2
ACR-d07ea854640746ec860b5003d4775625
ACR-15d22181773040ddbe3c659f6c77ccd0
ACR-6163c1de31a54721b178e75c05a7aca4
ACR-819b47171f0c451698e148d4a5e170e4
ACR-ef146a5f5f6a4b449005c3f663f79c3e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("rule")
public interface RulesRpcService {

  /*ACR-274a1ca504244525a15fd05a2301b04e
ACR-ba48bcf0542c498d830b0237cfb3910a
ACR-2c2a051ee43d4b098abac817975ed473
ACR-0df8014fb99a4f7c996487af3d6d96e0
   */
  @JsonRequest
  CompletableFuture<GetEffectiveRuleDetailsResponse> getEffectiveRuleDetails(GetEffectiveRuleDetailsParams params);

  /*ACR-fe41acc41e1e4911a2677222c3b0d0e6
ACR-4205933863f04d9ea2b8e04bb847af49
ACR-736c8c3a4e0849cd8d4740d17990ac5f
ACR-4c6f61eff92c4cafbe927120b00ed69c
   */
  @JsonRequest
  CompletableFuture<ListAllStandaloneRulesDefinitionsResponse> listAllStandaloneRulesDefinitions();

  /*ACR-2e8f3765e9e54daab5bb573f346d5eb5
ACR-6ca20f15e15d48ebacef10a9255f5854
   */
  @JsonRequest
  CompletableFuture<GetStandaloneRuleDescriptionResponse> getStandaloneRuleDetails(GetStandaloneRuleDescriptionParams params);

  /*ACR-a77710a6a52d437a8812ebf33aa9b931
ACR-2c4457c15c764e87856ecdfb2268a6bf
   */
  @JsonNotification
  void updateStandaloneRulesConfiguration(UpdateStandaloneRulesConfigurationParams params);
}
