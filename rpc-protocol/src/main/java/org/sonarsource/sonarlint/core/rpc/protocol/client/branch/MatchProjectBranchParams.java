/*
ACR-247c1636d03c4559b0558aa9e1718173
ACR-6e94054cbe4b46018130f45c3e811e3a
ACR-66796bb5d0234d16a230a7438eef6300
ACR-e205186fc9524c6baff8b9e4a2ecac9a
ACR-1d733b586a914f07bd501832a59a071b
ACR-0e4076ff24f442cd86d5c6a1da01a72d
ACR-723b73fef178448aabb157864839576a
ACR-47295487fab64e3399cdfd2c47fde861
ACR-7abb36e7f68243b3b376b0f35c67eb3c
ACR-d4651504a664442e9f06137636898933
ACR-16d371d1a82e434299bdc73d6890276b
ACR-b86fb567f7be4c78b81c5be5657a725f
ACR-2e894ac8786e404fad24e6406ed0d74b
ACR-73677557a320497785f6676ae0e662c0
ACR-c83f594e61e7459aa85d5e358edc05c7
ACR-dffd0ec1f94d4e33bf5809f6736b0d5f
ACR-b04a2993d99b4df68edebd3d563d653a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.branch;

public class MatchProjectBranchParams {
  private final String configurationScopeId;
  private final String serverBranchToMatch;

  public MatchProjectBranchParams(String configurationScopeId, String branchNameToMatch) {
    this.configurationScopeId = configurationScopeId;
    this.serverBranchToMatch = branchNameToMatch;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getServerBranchToMatch() {
    return serverBranchToMatch;
  }

}
