/*
ACR-686c6029faa84f90bb7ccb683b6a1d94
ACR-e3c8a011ddd14e54934d436ed42920ad
ACR-dd4b56f8e2254f6297318dae2c9de634
ACR-e403cfde545d44188097625292cfd2aa
ACR-f03481663cff4f6cba3a3f5513bb7676
ACR-c392f28a730d4a6f8c38b817835d97fa
ACR-49a8865e34cf4b59acc9a0b2f7be2ac0
ACR-ede2877c22d344ccbd65c63a1bfc8ada
ACR-2df5e420487b49aaacba88329732b9ce
ACR-feff77b5b15f45d3b9b648fa792efade
ACR-936e18002d434d49bafa49301d598ea7
ACR-ea1eef92e3e7491cb5e489f1694383d5
ACR-a01bd96140d24a27ac882f98da497fd1
ACR-1b809a2ccc7d4cd4a47385e6c2e3e80a
ACR-4cacf69c02cb4861b1193142aac9e6c1
ACR-cf36c8219ac14974a4afe8b50e1cceaa
ACR-319656832aab4f8caffefd0474f92de3
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

  /*ACR-a7d3289cbcb543629b7e6ea1b762884d
ACR-6bd175142d18491abb3a101aa2be0396
   */
  @JsonNotification
  void didToggleFocus();
}
