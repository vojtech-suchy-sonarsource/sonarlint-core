/*
ACR-e0da8f5681b047c68fe0abdcdffa1948
ACR-512fa998473846cc83afe32a16899142
ACR-fc37960afd0a4dc084f6af82b087c367
ACR-261edb8d562543e5b1a678c5c7af6431
ACR-123d46db61c240538ddd6119563630cb
ACR-7ce5b5042b6a4738ade01e1475558039
ACR-e9ce38dc7cf043f088beaadc969cf2fa
ACR-3da385e8bc1c4e6a94110ed438e83696
ACR-e3b42af5956f4da59ca6512a081a70a4
ACR-a451d9208f7c40ea9731e5c3a7ad9a00
ACR-7e5a78e76b754dba9dd126691c506bfc
ACR-f7b376b90b4a4341a903ffdaaf929c04
ACR-ad263c0906e440ca946dfc0b657d3222
ACR-facc379ba29c4203b9022602d0952017
ACR-757b16e9963b49a6a2821b2b0f2d26fa
ACR-d48154761b1f42fdb9e9b23184373425
ACR-650c8d72a2b2416b8d955d4e77e78443
 */
package org.sonarsource.sonarlint.core.branch;

public class MatchedSonarProjectBranchChangedEvent {

  private final String configurationScopeId;
  private final String newBranchName;

  public MatchedSonarProjectBranchChangedEvent(String configurationScopeId, String newBranchName) {
    this.configurationScopeId = configurationScopeId;
    this.newBranchName = newBranchName;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getNewBranchName() {
    return newBranchName;
  }
}
