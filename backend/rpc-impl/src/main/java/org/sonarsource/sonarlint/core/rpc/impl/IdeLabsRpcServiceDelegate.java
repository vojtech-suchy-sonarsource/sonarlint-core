/*
ACR-38b081186ddb410896f377a2bf38581a
ACR-013b197749c440a9a0deaf80d529d6e4
ACR-bd174e7ab56b4262ab95fb25d6ebc66d
ACR-cf6cc499c855478a8c607128999abf89
ACR-975df58ab9944112afbb2419a41fc9e8
ACR-4a9fe340d897421490ee40e4ffaf4648
ACR-77eceff464e14c8eb57db5d2d48245b4
ACR-a43a3ef8735746aa89e8b7a267557892
ACR-8193fb68a6904c95910c3882513722e4
ACR-fa3610ff71204f5b9c461e9821f11f8e
ACR-ce20f6bc32a4426e917b7a891a3401e9
ACR-bdc63d847ad1459da284e180f2ef3450
ACR-494644a3621043bf944998bf6c60cf17
ACR-4037d20d1e284ae39e5b56a9713992c5
ACR-b49d972394d24cc4be18688ef7ee2582
ACR-9d0a02269f554f20b5901ff847a121a3
ACR-70a12f13251d449e83a751ead2342735
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
