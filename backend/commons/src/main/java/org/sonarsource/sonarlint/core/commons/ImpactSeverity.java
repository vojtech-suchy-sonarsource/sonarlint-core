/*
ACR-d98d7c3fb2f14b919326b97214925764
ACR-d27d7ee8c5cb4d5c8d8fb466d56e3ffc
ACR-6c954cc43a554d41bccff6e414f8e1d2
ACR-42dfd8a068b247ec919745e5f952e00b
ACR-ee6d5032b2f847f2a7302d20e838817f
ACR-e2fb453e50fd4064bd24f517cf3d3804
ACR-650c5140ddcc47ef8e26f7e69fda97ff
ACR-42f33d8b63c0456ca7a09bbae748fc09
ACR-3b247972d3ef42838266c1926c0f1eb1
ACR-d7bc514429574780ae04ecb872ea8ef8
ACR-eedb35193cc24875b5e6863bf742cd0e
ACR-8f658d31bd094919b6c7a7401b5e5a73
ACR-5f09f3fa4ea94a189240ea9be06b20d4
ACR-daa0f262c1ba4518afbe371ef71b2426
ACR-d00bac03f9794927ae0aaa4699f3b348
ACR-493b93a4db954a7395ee8ff08a6383d0
ACR-a309e608e9014d2e8a83d5fa1315313a
 */
package org.sonarsource.sonarlint.core.commons;

public enum ImpactSeverity {
  INFO,
  LOW,
  MEDIUM,
  HIGH,
  BLOCKER;

  public static ImpactSeverity mapSeverity(String severity) {
    if ("BLOCKER".equals(severity) || "ImpactSeverity_BLOCKER".equals(severity)) {
      return ImpactSeverity.BLOCKER;
    } else if ("INFO".equals(severity) || "ImpactSeverity_INFO".equals(severity)) {
      return ImpactSeverity.INFO;
    } else {
      return ImpactSeverity.valueOf(severity);
    }
  }
}
