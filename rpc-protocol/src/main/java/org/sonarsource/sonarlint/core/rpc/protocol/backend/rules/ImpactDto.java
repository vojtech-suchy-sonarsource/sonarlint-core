/*
ACR-da47791753014de6a374e3c8f240f798
ACR-7b109bcfca814d319a49417a3cde8b87
ACR-2f96c9cf9f8c4e88b5efd9ce2778c18b
ACR-c63c7fa60b7f48a29450901df7c6e1e5
ACR-4f0d0fff94d54cc1ac65d7d5c8f2b292
ACR-76cb8f9cb9144ddabd45ca4fe9fd9efa
ACR-c0bca4b55d7a499194c9b480717ac3c6
ACR-5d516266fd96435baf684fc49b13d914
ACR-476c59e26c124a2d9858952e4adc64c3
ACR-46fb531a9d34481b8f6df725bd8a2d57
ACR-c68ed90362f740fe889571ddfb42d211
ACR-4f5a4fdfca46445186e0c409a836ab81
ACR-3e3963fb65ec469299565a8a659be7e6
ACR-da4fb678e5ef4ce9b78600555885d6e4
ACR-ffee4dd66c34472581057dc3c25b5ea4
ACR-6a422335368e4f8f82739916caf5f859
ACR-74063dc9ee3b41639c978067c81972bb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality;

public class ImpactDto {
  private final SoftwareQuality softwareQuality;
  private final ImpactSeverity impactSeverity;

  public ImpactDto(SoftwareQuality softwareQuality, ImpactSeverity impactSeverity) {
    this.softwareQuality = softwareQuality;
    this.impactSeverity = impactSeverity;
  }

  public SoftwareQuality getSoftwareQuality() {
    return softwareQuality;
  }

  public ImpactSeverity getImpactSeverity() {
    return impactSeverity;
  }

}
