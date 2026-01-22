/*
ACR-c589711c55dd41e7962e80b8d1c2b1ac
ACR-bf8898ed0d2a405f844bb96e1bf196bb
ACR-80d2748767b341029d48a9e2b476c997
ACR-eea96aadd4684506842097c4ba474d2e
ACR-b2544f7082d74453baba21d0bd89a82f
ACR-2105e9281f75458394c094f100ed96fa
ACR-dd8197ecc51d41849b6d6f05a604afda
ACR-51909f33d130455e9db2bae111215788
ACR-e19217a0bc424475aea959c29a7a9403
ACR-aa890beff5e242abaeea29e6ed88add1
ACR-13e12f91a2344f16a17c26639b2c874d
ACR-2586f3fed0404ae3ab5ace8554cfb923
ACR-ba3ce496c00a4f748b32ab80feb40a01
ACR-bdc0d581a12a4fcaae694cff748aafd6
ACR-dbfd4c4455b942d684f75228f8356c51
ACR-97609e41a7384eb1874f48948a82f52e
ACR-688c4cfe5e6e4044ab9a6b6c8cb760ec
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.branch;

public class DidChangeMatchedSonarProjectBranchParams {
  private final String configScopeId;
  private final String newMatchedBranchName;

  public DidChangeMatchedSonarProjectBranchParams(String configScopeId, String newMatchedBranchName) {
    this.configScopeId = configScopeId;
    this.newMatchedBranchName = newMatchedBranchName;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public String getNewMatchedBranchName() {
    return newMatchedBranchName;
  }
}
