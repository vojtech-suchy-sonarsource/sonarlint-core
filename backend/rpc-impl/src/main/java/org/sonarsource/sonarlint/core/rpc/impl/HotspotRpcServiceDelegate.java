/*
ACR-a7ccfe9bebb3433aa434448e4be521be
ACR-cbf12e613bbc4c70b97dfbb610c64eb9
ACR-e72c2146a79a48cdaf974d31d3cddac6
ACR-81e3e9b2e1414c26b7ea95e1cab6b29a
ACR-4725d1ed7a924f169407ab98afe6dac0
ACR-400a5812fa064310ab2b3bb7c952df73
ACR-de9e8d88f0dd45b091b918711b743386
ACR-111e3f150cfa447494ffdee5a98d867b
ACR-095e8fad1ec54e3da6e27754244665f0
ACR-62e66ecfe16c485ea624cd0d3afe5058
ACR-7eead79d29cc461cb2642d1cdc56c10d
ACR-964c5b8a6daa42a98ceebc046d51835f
ACR-929ad34323904599b93423ce876a1488
ACR-703c3874b40f41c3a839e54048f64385
ACR-03c274cc34db4f53afde94e5ed7f30ca
ACR-747ecd0c592e43cba2a7f125ec464fe3
ACR-864f5b2214c24ef7a2cede26a2f15edd
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.hotspot.HotspotService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.ChangeHotspotStatusParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckLocalDetectionSupportedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckLocalDetectionSupportedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckStatusChangePermittedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckStatusChangePermittedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.OpenHotspotInBrowserParams;

class HotspotRpcServiceDelegate extends AbstractRpcServiceDelegate implements HotspotRpcService {

  public HotspotRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public void openHotspotInBrowser(OpenHotspotInBrowserParams params) {
    notify(() -> getBean(HotspotService.class).openHotspotInBrowser(params.getConfigScopeId(), params.getHotspotKey()), params.getConfigScopeId());
  }

  @Override
  public CompletableFuture<CheckLocalDetectionSupportedResponse> checkLocalDetectionSupported(CheckLocalDetectionSupportedParams params) {
    return requestAsync(cancelChecker -> getBean(HotspotService.class).checkLocalDetectionSupported(params.getConfigScopeId()), params.getConfigScopeId());
  }

  @Override
  public CompletableFuture<CheckStatusChangePermittedResponse> checkStatusChangePermitted(CheckStatusChangePermittedParams params) {
    return requestAsync(cancelChecker -> getBean(HotspotService.class).checkStatusChangePermitted(params.getConnectionId(), params.getHotspotKey(), cancelChecker));
  }

  @Override
  public CompletableFuture<Void> changeStatus(ChangeHotspotStatusParams params) {
    return runAsync(
      cancelMonitor -> getBean(HotspotService.class).changeStatus(params.getConfigurationScopeId(), params.getHotspotKey(), adapt(params.getNewStatus()), cancelMonitor),
      params.getConfigurationScopeId());
  }

  private static HotspotReviewStatus adapt(HotspotStatus newStatus) {
    return HotspotReviewStatus.valueOf(newStatus.name());
  }
}
