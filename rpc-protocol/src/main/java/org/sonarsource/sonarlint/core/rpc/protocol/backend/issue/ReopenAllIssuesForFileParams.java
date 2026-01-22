/*
ACR-ac24a6eb6fd64f15bfcd79990f38df23
ACR-0940f93d2c12469183bdc595a0c43085
ACR-ee2229c0ab0b439dbaff7ece843adbe2
ACR-23b766a2bab94e89a62d99208bba9bf1
ACR-002e8a52d03b4e78b439880cf492fcd6
ACR-bdd817f13b4d45a7bd69ff2f18eb3fe5
ACR-bb11deeef81643aab57555ca8615538f
ACR-fbac758029bb40aeb274ec18471c6afe
ACR-1523286538a247ec9fedfbe1be8107d5
ACR-397453056c634761b499db144f17e435
ACR-707eee16037a4fe59732fced47fba9dc
ACR-34ff719edaf443ecab65b0689ba2b7b3
ACR-52f0e23a2f5547f28058280488408d93
ACR-b087fe9794ae415bb441b4ef456c24a6
ACR-946b7bdca69245e5ac060c08d01c7b12
ACR-15127d2b8aba4f5a937f413d6f321b06
ACR-e92b72e9afe3424bb26f35866135e196
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

import java.nio.file.Path;

public class ReopenAllIssuesForFileParams {

  private final String configurationScopeId;
  private final Path ideRelativePath;

  public ReopenAllIssuesForFileParams(String configurationScopeId, Path ideRelativePath) {
    this.configurationScopeId = configurationScopeId;
    this.ideRelativePath = ideRelativePath;
  }

  public Path getIdeRelativePath() {
    return ideRelativePath;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
