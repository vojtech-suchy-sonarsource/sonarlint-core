/*
ACR-e7593a41e7d146a389dd8a5c1fcffa3b
ACR-5778d513276c4451b986ea88b4268db6
ACR-3153443b929b443dbe899a0d480b01d4
ACR-2ed6847a162c45818fe916394bb403ca
ACR-18adbb64eb2043039632e2c05de6551e
ACR-2fda7b0dc0ce4e99b71d24f30d994a0b
ACR-77cd91cf0faa4dea818407f95b82ed43
ACR-053e03e5b14040b2a83e97df60f6a12f
ACR-7bc52c2408dc4989b5b30a3762af14aa
ACR-a230df172c9e44a885102e63beb83c17
ACR-2c1241d44d644be5b00cd855e366bc24
ACR-afff7e246f9346b3aa3f051c72973e5f
ACR-94a1061925e9491a9ccaad33598adcba
ACR-dcb13de4b5dc42309d13739e4743e9d3
ACR-5ca446db8ebb422caada7088cb21c5d7
ACR-e017fc82f6b040be8585828534a13747
ACR-0341cfe9c6b5455691a257f91fe3e00c
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
