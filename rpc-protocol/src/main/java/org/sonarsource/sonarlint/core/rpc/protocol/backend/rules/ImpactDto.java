/*
ACR-3a6aca02b44d4dbe80a603e7fdc5a595
ACR-f70454123dca4bee9840fed90386d581
ACR-5fd2779b73454c6f9e5cc7efe57928e3
ACR-a2cb7105c8b348e0a0a8eb02bc54baf0
ACR-93c306f63cb94fb28d5c81fe6f9a3e3d
ACR-c9566b3f0cb34bc3a5723c8d04520f4c
ACR-a4de926089204db785601a662ff325ca
ACR-d2496735e6fa45be9037174a3eb739d9
ACR-8c04f73751244acdbbe826c7a1964be8
ACR-f93ef32a25814f1eae40fdc1248cc06b
ACR-1022e7e1332c481280aa5a061cd975cc
ACR-90593368c41443b9b31303e6779c224a
ACR-daa55064f5b34d2ea0b7979f05bc3650
ACR-5a3799eac1e24c4091adb938101d3c46
ACR-672758926d524e16ab26f956f21ca56c
ACR-34158a5d15d249e5b04476d0f8216153
ACR-cb122f247a3b42bf8ba48fd9c7c8751c
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
