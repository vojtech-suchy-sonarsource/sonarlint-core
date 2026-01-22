/*
ACR-20dd35e350ff4960989a2a301ca1440f
ACR-79a26f55057949f888288a59450b5f7a
ACR-ad6e9a01413b4431b435115991bf159a
ACR-117b638c2b584fe6bcfe06cbb093e9e5
ACR-cb1127048e1f47be9f481e0866e54511
ACR-4b7fe639f5634c13a72812e192c1a5fe
ACR-2a275f1b67e547d1bc2ba52065e70d32
ACR-2c0b130be2004d65a499b09339b05a50
ACR-e50650f11bc3473d86f16697233d19fd
ACR-af8976412ff34135926cf99300cf6974
ACR-175ca8353bb047089cbf1f09461029c6
ACR-9626d0ba06fb4007bd5c932cd03720e3
ACR-1493b38657bc44d4951adb6b238d7616
ACR-f17f17cc27f34f829b8c30b8fd459db4
ACR-5be21589f0e547aa9ad6de9254a94d42
ACR-d9227f6dc7ef4ab79830f969739cfbb8
ACR-c117d06356bb42c296e70569c67763b7
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
