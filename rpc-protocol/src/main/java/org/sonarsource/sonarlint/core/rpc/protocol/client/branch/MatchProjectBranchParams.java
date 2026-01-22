/*
ACR-c94e398cd9654648843d515c2aad0958
ACR-8783645c051746c6a90bfa2a737c2d5d
ACR-7d068328c2454c09865f01430d7a21f5
ACR-5d9a905cdfb7490c8ba3afd27b3d58ee
ACR-7220bc6bf4294222adb069156d7cec31
ACR-1e98104f77274d20b86d9ee75a58ecfe
ACR-31b5c42611d04c81a9a9434b75cb79d4
ACR-4a17d9d4d43846f19a13bdd67d4bb0a7
ACR-a1442db2ca6d4b20b7a29109457478b1
ACR-b4b087e76b2b42eeb3ce93cb9923afee
ACR-3c471571d4c94a8a966c8dd735e1b513
ACR-f704c2fd19124cd195274a78960ff1c0
ACR-003dc200f9e842589d14c618927f0fbd
ACR-9e378b8dca844fb6bd749b35dc711659
ACR-251b095b5d45435dbe7c1bc79abda099
ACR-626b039cebcc432bbd66c8afb01d415e
ACR-806c736f453f43f5bced5405b3bbe74e
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
