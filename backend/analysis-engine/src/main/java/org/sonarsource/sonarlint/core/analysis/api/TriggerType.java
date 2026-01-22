/*
ACR-97bac0fd9b2f4813a36799724e2e8801
ACR-bb4981a3aedb4f20a0c3c6a535736a17
ACR-06f827e313b3480aaa1d60577827ba34
ACR-3a1f25e9283a492e81a74f82e46ee7ff
ACR-0fae442e554542d6acdfa000a849b358
ACR-8072fb69013d411bac400a0496be4670
ACR-61a1baad20344defb5d728e09ad2eafe
ACR-d9038a12aaa844c4bb35d1f2883533fc
ACR-59ac6b4964c44e9dbed80b6b746dcefc
ACR-b357bdf1efb243cf892d4df70bc207f5
ACR-7590a190d1c046958dd4c160831ce056
ACR-aa036ec0c2b040e1a1d8976852dc1cec
ACR-7c7d690d0cc44a9089037c598785f339
ACR-44509c7a696c47a8bb2c70863114400c
ACR-90d2ca79241b4dd0ae6502fa22cea398
ACR-3dfbf54d14a0411b91f23da22afa1d90
ACR-e48ff965d7fa4d7f99323251b1341baa
 */
package org.sonarsource.sonarlint.core.analysis.api;

public enum TriggerType {
  AUTO(true, true),
  FORCED(false, false),
  FORCED_WITH_EXCLUSIONS(true, false);

  private final boolean honorExclusions;
  private final boolean canBeBatchedWithSameTriggerType;

  TriggerType(boolean honorExclusions, boolean canBeBatchedWithSameTriggerType) {
    this.honorExclusions = honorExclusions;
    this.canBeBatchedWithSameTriggerType = canBeBatchedWithSameTriggerType;
  }

  public boolean shouldHonorExclusions() {
    return honorExclusions;
  }

  public boolean canBeBatchedWithSameTriggerType() {
    return canBeBatchedWithSameTriggerType;
  }
}
