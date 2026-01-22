/*
ACR-aa852ffa1a0048b599c8dc4ea0c211c2
ACR-4d42e32923624e84ab513b8ec5203855
ACR-9a7ff0b644734ce4aff49776427208fe
ACR-a89a982e03d742ba847ac1a720f9e3d7
ACR-084156ef05cf4eadb0fee2b46d5a94e8
ACR-9ea05ecfcc6143bc9b5932c4a4c8e955
ACR-d0f816d5ab764c3895f7f843fdb9c43e
ACR-9b36cfa9836d407ca951510577f35da1
ACR-a654506ed3bc4469bdb44b786473bfb8
ACR-f45fab5f7fa740eaac5b78ddf3b5c3d7
ACR-545ceb1cd3e84af0bb3468b55740ad43
ACR-b32978ad33524087946af98e940987e3
ACR-08ca66fd737b4e1cacec659ba2bc7e98
ACR-bcff88dde6c84a38b77f887f93485395
ACR-bcd4c1ace04440b9b435245250a0bb75
ACR-0011a0fac8e54caea8f89cbdc0fce34e
ACR-643db44ffd8a47998c99b4ea9b1f2a12
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("ai")
public interface AiAgentRpcService {

  /*ACR-e05e63f58f514164a1fd618469d90149
ACR-74b56a8c09d445ddaa1ca963c61c226d
ACR-c9d28a50eb29410fafec93528dd866a4
   */
  @JsonRequest
  CompletableFuture<GetRuleFileContentResponse> getRuleFileContent(GetRuleFileContentParams params);

  /*ACR-c8ddfbda0b7d414fb3a5cf86af09884a
ACR-356bb317ef4447ccbe246002ab14cbd3
ACR-237b2eb8cf7545089a3711fe466357f1
   */
  @JsonRequest
  CompletableFuture<GetHookScriptContentResponse> getHookScriptContent(GetHookScriptContentParams params);

}
