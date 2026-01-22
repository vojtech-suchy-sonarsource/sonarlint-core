/*
ACR-12bc89e813d3484299cfdf17ef599bb1
ACR-3e98cf20bd104d89a6f462187afc7ef1
ACR-10e4ed45f51e4e2590184a8da7a98bf6
ACR-41e7b53dca1d4580a5cb1e9103966944
ACR-63ee3718aacf40e7b154f181de0e8ec8
ACR-9d4f52ab20fb4661917fe9a5c9685b09
ACR-f8af463d468046fdbe8c5dab7e4aa134
ACR-4f96866db8fd4b9fa5b7586f8af3eb22
ACR-a149e5d18fc9417ebde2becfb626adf0
ACR-707ecaf258a44a4cae573b5918c5a330
ACR-f4cd3c4c01214b3f82e38e011819fb8c
ACR-ac0472556fe843908f3ab4b940428c78
ACR-dd1d4df172e04ba3b651f443ddb2e8a1
ACR-94708a85fd504914b4e00ae54b1e14d3
ACR-f2663c9872874422a1a05698c973376e
ACR-477bca66916c4ec9ae0f1ae40cd1c333
ACR-e83e89b871f44fb78e8154e8efdbc517
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum ImpactSeverity {
  INFO("Info"),
  LOW("Low"),
  MEDIUM("Medium"),
  HIGH("High"),
  BLOCKER("Blocker");

  private final String label;

  ImpactSeverity(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static ImpactSeverity fromDto(org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity rpcEnum) {
    switch (rpcEnum) {
      case INFO:
        return INFO;
      case LOW:
        return LOW;
      case MEDIUM:
        return MEDIUM;
      case HIGH:
        return HIGH;
      case BLOCKER:
        return BLOCKER;
      default:
        throw new IllegalArgumentException("Unknown severity: " + rpcEnum);
    }
  }
}
