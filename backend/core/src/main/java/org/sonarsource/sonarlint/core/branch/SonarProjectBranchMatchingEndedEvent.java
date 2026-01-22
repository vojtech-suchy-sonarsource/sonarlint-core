/*
ACR-49a1496970334d8c9f047f3595b395c8
ACR-834bcc640d814f059d30f8de53791664
ACR-98b0393e06404b30a67f97f0a0191dce
ACR-258030fa11764835aafd02bb886b6805
ACR-56da7c31ecfb4edfbd86e005f8febcc9
ACR-9904a018aa9f4a4db3df530c821e065a
ACR-b3b9f33a46254ef2a8d18e3cf5296c8a
ACR-4599bb5da8cf493baa2c6ce8f1988c8d
ACR-a579b635667e4ded94c65b4cb94c948e
ACR-b14e1a916df6476db1a2edbbc80e210a
ACR-1a0840a7883b4932a62faebaac63b16a
ACR-f4eaf34c05304e2d89d721e8dc1953c3
ACR-0e17d38f407c4e3c9ff3c195adbc4fc3
ACR-48dd30e1534047bda092db3dc699c6e3
ACR-5f806e9a7d3e4b319ba1f4c3fffacb2f
ACR-d8888938bdfd489f83538ec4e98d9b87
ACR-8eb5565cfa57487493af1219a9bbc1cf
 */
package org.sonarsource.sonarlint.core.branch;

public class SonarProjectBranchMatchingEndedEvent {

  private final String configurationScopeId;

  public SonarProjectBranchMatchingEndedEvent(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

}
