/*
ACR-6ca537b89fdd48a9a78c99c8bff9a4b2
ACR-874650c1796740e1bc861882de052fba
ACR-90a74477403748b49fa8000bc246a3f9
ACR-42c6fcb34438426f9febd5e035bb879d
ACR-41a70175f22a4eab8495283cded55ceb
ACR-e2d72997c4f0412f9e9a9c5b768c2d69
ACR-ecb1e7cccabe40699928b15a1f027fc5
ACR-e3eab5894901497182649d0c93493d77
ACR-ca246eb187f14765baf7860f4b8b8d40
ACR-ec46f1943948447b821db52284358257
ACR-14d368fc1f5d4c6d90e5f6cbd6187466
ACR-07b1299d193c436589411a61bd5935e9
ACR-8dc4396af77746fbbfc71b3bd53ab0b5
ACR-2da83b9446394d499cca9ec312399d62
ACR-734ea032dba34f999e6308fbc3eec147
ACR-be0d324e0cfa4f1389fe499684db3f8f
ACR-ed1c3fd15fc94b6fb43221506160c3f1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fix;

public class ShowFixSuggestionParams {

  private final String configurationScopeId;
  private final String issueKey;
  private final FixSuggestionDto fixSuggestion;

  public ShowFixSuggestionParams(String configurationScopeId, String issueKey, FixSuggestionDto fixSuggestion) {
    this.configurationScopeId = configurationScopeId;
    this.issueKey = issueKey;
    this.fixSuggestion = fixSuggestion;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getIssueKey() {
    return issueKey;
  }

  public FixSuggestionDto getFixSuggestion() {
    return fixSuggestion;
  }

}
