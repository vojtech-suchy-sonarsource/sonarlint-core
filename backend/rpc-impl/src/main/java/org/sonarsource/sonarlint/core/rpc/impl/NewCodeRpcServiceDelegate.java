/*
ACR-0b244fd151814faa9eeaff2ce2c9644c
ACR-a2b97c9e258e41238b8f0186fbf08c54
ACR-0a0eb11c28e640c39f54eb7ff4c15d50
ACR-638258d3769545709ecb9baa7e79d3ac
ACR-2f9d911862844933bb604858397154a4
ACR-e0d130c923324bc29e6f4367252a107a
ACR-953cfa839b854022b184cf233cc797d4
ACR-bdc1d7750b1a4aa1bc5b99f9737577cf
ACR-7f3aaf66c37a4bb2ab8be18195d4f2ea
ACR-5a344172774142f4aa7c26b9a1a24f01
ACR-8d0e50684b184d2ba78c886e21851a74
ACR-07f46d39367947bd9417f76d3986315f
ACR-b7fa765a685c462294448d59f93f8b14
ACR-cd3d3e35535549e5828e5ff02621c57e
ACR-b23e01f22e764b4e84b43176ae596d78
ACR-4fb83a7a15134902b179b7f267570567
ACR-e629e8e4b3294908a01a9fa9f3211b0a
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.newcode.NewCodeService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode.GetNewCodeDefinitionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode.GetNewCodeDefinitionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode.NewCodeRpcService;

public class NewCodeRpcServiceDelegate extends AbstractRpcServiceDelegate implements NewCodeRpcService {

  public NewCodeRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<GetNewCodeDefinitionResponse> getNewCodeDefinition(GetNewCodeDefinitionParams params) {
    return requestAsync(cancelMonitor -> getBean(NewCodeService.class).getNewCodeDefinition(params.getConfigScopeId()), params.getConfigScopeId());
  }

  @Override
  public void didToggleFocus() {
    notify(() -> getBean(NewCodeService.class).didToggleFocus());
  }
}
