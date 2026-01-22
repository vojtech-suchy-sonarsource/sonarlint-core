/*
ACR-6876851dc416480aaf6a6d971ad642ee
ACR-c7b31739bf06479eaa45744ffc500af0
ACR-7deef4ff999e4ac1ace81151f157303d
ACR-476075df3d2c4679be6de9578b838a48
ACR-63b38fde43d646188d9c0e21f1d66ecd
ACR-64155a91c3844c0898e5b2225be678f7
ACR-d879f3d966844502835c8c884b9233d5
ACR-a10f00dc40b749f99c5a9ae6792d1a3e
ACR-99e52c9094aa4ea1bbb61f5ffee7b2d3
ACR-14377b5c52c84ccf8af934775428017a
ACR-4e154712d54747b687117025cac5cf59
ACR-1e1fb6f720794afe895eefeb1c92c219
ACR-beab5e057c19448bb86bd329d29d66aa
ACR-2dadaf3a9898402f8b797a778b1c976f
ACR-d54105d6944d41b491c6c6371fa3a05c
ACR-607f498ab41241f78726e9dce38a76d8
ACR-087d5f7769f245ac98b44aab8a32e009
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
