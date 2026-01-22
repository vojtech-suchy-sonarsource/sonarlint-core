/*
ACR-08797d64a6164b41966d39c501bbfb6d
ACR-74f5ab62665d46e28b5bfb18f0fa3504
ACR-e4300390a7d34e1bba32aecb9d02de92
ACR-752ac809391942d2b53fb9f26bd702ae
ACR-d693503e922148b8bd3dbc0b1de851ad
ACR-d7cd189451c241579e4a9ffb6fd669d8
ACR-80bf7f7dbb07420f8cca798046a618e6
ACR-957c0ebf749c48e1a663ebb1a71ea721
ACR-17f0baa0f42b4259892597126d303833
ACR-4e20bd9bf2514278a6180e0709b172b8
ACR-874940ca00ec4f47bd8c8859befaac48
ACR-1b4b2a63be0f46569733d1e86a3a39c8
ACR-20ef91279c094daf9656ecb2a203649d
ACR-372913daff1448908beaf1df6bf09240
ACR-26b01fc3900e41919a4aa46f5fb05fd4
ACR-ed573c96cfbc40f9b75685e5c278f682
ACR-ee795529e72f4086a2331960542b75bd
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("ai")
public interface AiAgentRpcService {

  /*ACR-e7d2bde4136b4d38aeab44eca1d52ac9
ACR-eb28c1d2f4a848b4b0fd78864050eb6a
ACR-711ea978c52d459799b4dd1e043c5ee0
   */
  @JsonRequest
  CompletableFuture<GetRuleFileContentResponse> getRuleFileContent(GetRuleFileContentParams params);

  /*ACR-9923fc1e6cbf495995bdd6dddc2567cd
ACR-15f404fe888043b09c3cde4f6c87a02f
ACR-2a60294cb28649a89bc025cc1ca4a64a
   */
  @JsonRequest
  CompletableFuture<GetHookScriptContentResponse> getHookScriptContent(GetHookScriptContentParams params);

}
