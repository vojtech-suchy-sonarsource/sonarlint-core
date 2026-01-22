/*
ACR-f6ce4ed51b2f4a0784ce9ca057477a10
ACR-d02c7fd979ed4e6893860424bc8f0e3a
ACR-6491a37c9df24e948182275874c06e9f
ACR-d3b1efd8de7747efbfbb02a7447ea74b
ACR-7d409304e7dc410c83cf2839aec57747
ACR-b5e8fb31591b4355b2fb35085465a355
ACR-0b7b4513aa0e4097a0d2071eb04f926a
ACR-2099a9a126c9480281b6358530f2e9a1
ACR-7ac74d03baa84a77a591ba1610a081e7
ACR-093c611830094b538b30d7be385acce2
ACR-47c6348782e844168e7381d0b54b1e6e
ACR-cbdba3d3fa964bd58c102fafe9f8c00b
ACR-e9c1d4f96760488e8888d968b232a0ab
ACR-cef4d5e0d8ad473b896ff35f8a61a1b0
ACR-64a567a38411405d99a0832d81216b84
ACR-e55e6a4a4a0b4750a5e56d387a2e0ba5
ACR-6809a1f18b354ceab1cf13fd810472a5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("newCode")
public interface NewCodeRpcService {

  @JsonRequest
  CompletableFuture<GetNewCodeDefinitionResponse> getNewCodeDefinition(GetNewCodeDefinitionParams params);

  /*ACR-682671a625ac468d847b811981fa8578
ACR-29a6b4c1fd854666bcc49c645ec4a573
   */
  @JsonNotification
  void didToggleFocus();
}
