/*
ACR-101fc737849b415090f93f0c08e92462
ACR-af1fcee30d644fc4a375b02fa2531502
ACR-fe290dd16304495d8e06a354932b76e1
ACR-a0b46730786e41b98f3d14142435d643
ACR-ce041b3f109848e6955a7d1bff53de32
ACR-7b2fe909399241cfa06a98dd19779eca
ACR-98442839502b4839b80e3036a8e2c3b0
ACR-5d86a5ce081047b6adc44716ba4767f0
ACR-6e1118ee4f9f47759a2f19586056ac4d
ACR-17d226c2784a4b55a4ec8310294de89e
ACR-782380313f6549e0b57a3e72f0f8c3dd
ACR-4e6dc60df3044151b038271b5752a051
ACR-9ffc183f3e4242fda67e1e2cbfae5108
ACR-5ab698e7d25b45ac93d322b09ba472ff
ACR-8560e63861ad48db877f549d68c5b5d9
ACR-4042321196ef448cb664421512377a3a
ACR-23bdef2022c34e3691051c8b54596157
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
