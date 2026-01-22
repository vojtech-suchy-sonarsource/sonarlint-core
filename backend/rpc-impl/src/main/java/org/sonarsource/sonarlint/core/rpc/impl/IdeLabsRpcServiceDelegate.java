/*
ACR-24a4170ace3f416c9774726103b238ed
ACR-d393105125f84f10a00e8e2e193ad21c
ACR-90c793f7ec9b49d0ba71c7f2b6522a1e
ACR-a9678f4311a3411b980b8f1268a71417
ACR-388c08ed55bd4a95a306195caf43954a
ACR-9d58e2c1b5444d0eb6c41c17d92acf47
ACR-2e0fff37e22345dc8f96f1bdb37624e8
ACR-ab0e3ffa1df74adfa223026800b85728
ACR-c2d0cc7e8cca4b7ab9e125bbc118f6c0
ACR-ccc33e2caf6844d5926de21cd18a5ec5
ACR-6443f700707243ee966c2853952705fe
ACR-127d1d8c22fd45c2b5f1588a48f6de2d
ACR-54a9717ba045444194ee64bd826f0db6
ACR-9ca6b89056ec46dc92c5e8309fab58fe
ACR-af8e8e6422fc4d68827966cb2a1298e6
ACR-4d4abd2739d34e51a33d2971ae0afe5b
ACR-234550abfe7140b38b47df624dee9ada
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.labs.IdeLabsService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.labs.IdeLabsRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.labs.JoinIdeLabsProgramParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.labs.JoinIdeLabsProgramResponse;

public class IdeLabsRpcServiceDelegate extends AbstractRpcServiceDelegate implements IdeLabsRpcService {
  public IdeLabsRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<JoinIdeLabsProgramResponse> joinIdeLabsProgram(JoinIdeLabsProgramParams params) {
    return requestAsync(cancelChecker -> getBean(IdeLabsService.class).joinIdeLabsProgram(params.getEmail(), params.getIde()));
  }
}
