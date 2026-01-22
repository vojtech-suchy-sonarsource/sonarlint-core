/*
ACR-dcf853ea0d18478780932839cefabda8
ACR-17974f7f407a4d508093bdb1ce355702
ACR-75ad375baf714c5ca9eec9bead4eb1d3
ACR-1b9995c0a9b54e548a9931d355171c3d
ACR-c7f3f8ffb36641cabde833d5278638d9
ACR-80d110a6079d44b2be64ac20f2a7aae4
ACR-dbae102a44924373be87629ae2ef8538
ACR-aba30c5fe6da405b9c8d71afaca15d13
ACR-f6cd30f964934e11a0117f7f7cd30af3
ACR-e3d47f2b81b84bb1b4ba526b8500709f
ACR-d1899398d8be44bd947588ff16a47cf8
ACR-77cbafc69fda43e0bf8adffffd0d456b
ACR-35b5793ac1c14313b1dc7c93e42f83c1
ACR-0ed0827139d0405288d3766495ce06dd
ACR-c883312ddade4cc9a05a018dfa45bc34
ACR-ce7b9343003f459c94b5c143ef4a5763
ACR-eef2a433ac0c40d08ece9d5238ab2602
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class DidChangeAutomaticAnalysisSettingParams {
  private final boolean enabled;

  public DidChangeAutomaticAnalysisSettingParams(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
