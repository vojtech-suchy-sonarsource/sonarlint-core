/*
ACR-e32e484fd7ea4d3f8d5382bec62f7e4a
ACR-b4d9e4c0feb34fd9bb9afe5ca31ea760
ACR-e12a5a5f577a408e9b5fccc7ac2a37fd
ACR-03eae97b2f0842eead6d27a4d1640f33
ACR-57ef8348701340fb8919ce95a41821c0
ACR-5390d41b07694c4986fc3071b9058e40
ACR-5fc60ea3e4e3472db8c727b4a11aba0a
ACR-43fa242d4a2f43fdbde89304f0d83176
ACR-42f7a8a640b84626a7274cc9403ce364
ACR-82644632e1ee45f998f121fb1efa6ac8
ACR-4faaca0db11948a094175a5bd2aadaa6
ACR-25bf17e42a494a71b03ddd7652d9fc7b
ACR-4b10d0febe5d46d985ebd6fa219a8295
ACR-a866ce7283194a76af2f62df51d314b9
ACR-2a011bdc4d5a4dabb7cfd4db728fee0e
ACR-225c515a582548baac51edc963a61eb5
ACR-d23e6c9d530447c8b67554961d154f56
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
