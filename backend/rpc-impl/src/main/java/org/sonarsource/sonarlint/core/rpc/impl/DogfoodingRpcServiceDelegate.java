/*
ACR-74b34eb8e9da44aa9830d84104681167
ACR-0a270a6261f043fbafffe4e023f2bd7a
ACR-e0d700fa090f48549511f0bdeef47905
ACR-1a457299086b46aa9ae0315a6c0fa7b5
ACR-bc8e7f98b86743e1b2749bd9e97f0450
ACR-d43bbf8d814e4545a630ceabac332c18
ACR-f06218c87e0046ef92ec1ed2c3bb99ae
ACR-df5fa1d98c574059b06edcc1b36dfd3f
ACR-49bfec9f03bc43eb8a1dead13ad40d80
ACR-49548946e5b74ab496cca8ec3a06fe51
ACR-56f7517582f54ebf8e80128f6f4518d9
ACR-3c34795c4b6842de9dd7aaba84092126
ACR-ca8e58ba2e914dca8a182347cc6ea0ce
ACR-67ba635ba7e64f049bb2372a574c4327
ACR-176b8ec11cbe46b4acd93906ae115634
ACR-68c52fac2b7d4b56a646dd3a00e06090
ACR-7833f03116304d44bd7a2c1cd51cd6ff
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.commons.dogfood.DogfoodEnvironmentDetectionService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.dogfooding.DogfoodingRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.dogfooding.IsDogfoodingEnvironmentResponse;

public class DogfoodingRpcServiceDelegate extends AbstractRpcServiceDelegate implements DogfoodingRpcService {
  private final DogfoodEnvironmentDetectionService dogfoodEnvironmentDetectionService;
  public DogfoodingRpcServiceDelegate(SonarLintRpcServerImpl sonarLintRpcServer) {
    super(sonarLintRpcServer);
    this.dogfoodEnvironmentDetectionService = new DogfoodEnvironmentDetectionService();
  }

  @Override
  public CompletableFuture<IsDogfoodingEnvironmentResponse> isDogfoodingEnvironment() {
    return requestAsync(cancelMonitor -> new IsDogfoodingEnvironmentResponse(dogfoodEnvironmentDetectionService.isDogfoodEnvironment()));
  }
}
