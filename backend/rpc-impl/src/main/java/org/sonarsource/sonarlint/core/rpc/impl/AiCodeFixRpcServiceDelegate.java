/*
ACR-11aa42931550484baee6a8792e8a3c69
ACR-111a8d730a754f9b8e2e3ba196d26c60
ACR-574f119b3ddb49ecae9522ccd49338ba
ACR-6810deb754714d508a15b191c5f9812b
ACR-7e66902ab376410e87158c13cd6806d0
ACR-0e0d1793e1f2435ba78f88fb6a6ccac9
ACR-c2a326568ebc4485b1843e12e3518446
ACR-1dd7b4ebedf9482a8616963eff5c1982
ACR-06a83746e16c463c91f14e5f96744527
ACR-ecfa46f8466746e5b55f30182710ee64
ACR-b1948b7179b549ccae92861ea755a282
ACR-b8d7a64933554f588b2944419af38266
ACR-1126d6563f88462381d91496fd6196c2
ACR-6de10117c43d4662b5be3137f7e0d0ac
ACR-87adbe7fa3da45feb402ffffb7798fe6
ACR-261e534daa5044cf8e256cd3863836f1
ACR-83bf39e548cf41e5b2e89ea0ac2e4707
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.remediation.aicodefix.AiCodeFixService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.AiCodeFixRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.SuggestFixParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.SuggestFixResponse;

public class AiCodeFixRpcServiceDelegate extends AbstractRpcServiceDelegate implements AiCodeFixRpcService {

  public AiCodeFixRpcServiceDelegate(SonarLintRpcServerImpl sonarLintRpcServer) {
    super(sonarLintRpcServer);
  }

  @Override
  public CompletableFuture<SuggestFixResponse> suggestFix(SuggestFixParams params) {
    return requestAsync(cancelMonitor -> getBean(AiCodeFixService.class).suggestFix(params.getConfigurationScopeId(), params.getIssueId(), cancelMonitor));
  }
}
