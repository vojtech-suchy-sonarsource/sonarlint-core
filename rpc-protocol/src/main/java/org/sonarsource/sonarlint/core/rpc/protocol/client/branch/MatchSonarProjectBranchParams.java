/*
ACR-3c63ba7b9df24902bff3d0c73c294ec5
ACR-f421d6018dfa4b5caf569b59f10b79b4
ACR-6d25a68e2ba7474d9616969ce336a809
ACR-5accdb0ec13944a5a37cb27017e79cd9
ACR-9da963e2fe4f4bdb89fd51de91cd5743
ACR-7b2415430d6c427a8be5601636f461d2
ACR-42d3f70d52ed49f7817db5b783378175
ACR-57433fd41cf949f39d1af75cc82cd5bb
ACR-99ff390b9348457b9771afbb037867a4
ACR-ce036de6a51c45a9bb99003d5a5b594c
ACR-96e9174ef8064a8ab74bb15e09dcf899
ACR-361752209dcc4574be444c62c62d627f
ACR-202f70bda18d43dfa975ec2191f9b8ac
ACR-85d92656e2444c95847f638b4d45001c
ACR-d90c89cc77ca4a75b8d3183a5da2c0ba
ACR-20dff044adee4e4981e2a6bebc43b419
ACR-b2053e6baf1344cf837e59beb602a545
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.branch;

import java.util.Set;

public class MatchSonarProjectBranchParams {
  private final String configurationScopeId;
  private final String mainSonarBranchName;
  private final Set<String> allSonarBranchesNames;

  public MatchSonarProjectBranchParams(String configurationScopeId, String mainSonarBranchName, Set<String> allSonarBranchesNames) {
    this.configurationScopeId = configurationScopeId;
    this.mainSonarBranchName = mainSonarBranchName;
    this.allSonarBranchesNames = allSonarBranchesNames;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getMainSonarBranchName() {
    return mainSonarBranchName;
  }

  public Set<String> getAllSonarBranchesNames() {
    return allSonarBranchesNames;
  }
}
