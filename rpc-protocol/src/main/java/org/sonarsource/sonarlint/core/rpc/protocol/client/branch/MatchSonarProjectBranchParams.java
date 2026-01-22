/*
ACR-1842532a98954d42bcbdaeb763fe3ae4
ACR-7f91ed5b702043c9845793004df9d3b0
ACR-ab7dffcae3fa4898803373eb8f84f1ee
ACR-73dcf458b5564b5ab0d9aff31d9ba33f
ACR-0712e1b784244f20999820ec7cdd91e0
ACR-4158d504940943bd99e5897beb719f68
ACR-e98e5e5889e846f1858f56134bf0f2e3
ACR-5058f0ead5934b088247805a2e937710
ACR-af90e7829f5b4337b242df5a30898f4e
ACR-31f2e0e5dd224ced871c3c06f94d24a8
ACR-2fd6bc0133384897950f169946cd40be
ACR-4e5ebaccb2c74800b862eca04e68b47f
ACR-9053deea94ee473d8892b275f6faa347
ACR-9de40398161d42a1a346ae933fa0a279
ACR-35ce0d6f237f4952bc362812843c6c9d
ACR-93808a9281474ae091990184817b7687
ACR-83f84ec3d5014020a7f422d92ed4016b
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
