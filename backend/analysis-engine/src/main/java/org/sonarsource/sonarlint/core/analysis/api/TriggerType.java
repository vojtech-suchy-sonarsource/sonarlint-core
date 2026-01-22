/*
ACR-c48122a8c8c7441e870ad82777034511
ACR-226ed669d78e4985b01f05245b8a9c3e
ACR-e1a1bfc3e632494f900611fed0de92d7
ACR-4c0e5ad0671242a1be36c780036667d2
ACR-5a5375d1c98f469f8c690b8dc65b5ed6
ACR-26add30edebe4bc7a88cde25c1cbf549
ACR-d89e1a625dfa4a2c89f38961e0b532ea
ACR-a450b8003f84444bb808992c1ec4cb73
ACR-672fd5bc61f54e5ea11ba24d69bf0b32
ACR-b9b26856c6524bf78a6c24d3968576ed
ACR-6295ca4e97b4473f96589856dd985426
ACR-d67d5101b7004ccf87320449451c81bd
ACR-a0e656a202e04e97bfcccfee8b71f29b
ACR-491f9248b8664555abad53e688795a7d
ACR-438dfbb0d70d44b5b069f64d1a8e507e
ACR-3a223ebda2274c1fa42201468ddbc004
ACR-3f16ee8262b94651949bcad9c9421a47
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
