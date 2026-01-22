/*
ACR-fdbe72c7f21a4e01872574bdfa2c0637
ACR-4cd5bdb1363c463a80110263ce365984
ACR-33a66cbae27948679d2f334563441933
ACR-ddce9a7056bd479c97750a6b8d9535d1
ACR-ca756a61f2f241f5961db0392f7d3f07
ACR-0f755d5981fb48e594503b5751ddffd7
ACR-705b2ff54ec84e05b71aee0e1a9567d7
ACR-7e18170614a64ff781924b775e8a04ee
ACR-19bb175fe45b4d748a47d9df3f52799c
ACR-c309c00e0adb49b88ad8ba14cc973ba9
ACR-aaf89da8bd7644dab88d51d520b861d7
ACR-5d6b9e77471c40019dfdd87a3ea13315
ACR-eae90cd54736440d8ce4e9ebb5511e5b
ACR-4927df75de494282bc22f79985cab0f4
ACR-6232a1ce23c94449a2a3011ac8b86007
ACR-f9c20ec1e80f4508bd2305535f52d635
ACR-5396c9b7248847698d520dedc2299ce4
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.sca;

import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;

public class ListAllDependencyRisksResponse {
  private final List<DependencyRiskDto> dependencyRisks;

  public ListAllDependencyRisksResponse(List<DependencyRiskDto> dependencyRisks) {
    this.dependencyRisks = dependencyRisks;
  }

  public List<DependencyRiskDto> getDependencyRisks() {
    return dependencyRisks;
  }
}
