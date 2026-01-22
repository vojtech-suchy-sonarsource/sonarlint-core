/*
ACR-05535a2e28b0422c8b0e0967f3294eb2
ACR-ee4f052e3c66478a9988059a5146e678
ACR-02e9965e7774449a8f3e2786fd9a08e2
ACR-1eb78d2946c840bbaedf65c918aa7084
ACR-1054ef892c1e4575b0c08ad154653cae
ACR-3ae9c03a68f94c8194a83981ef0622de
ACR-98d0ed2ddfc0415682dcbc45586f61c6
ACR-dd8e047435d94f998e7f831750de5089
ACR-38d01d8b1ce64150aae5a64e2bb411bc
ACR-f1c5bd842031437faf754e512d67c248
ACR-0de81e86cf9248318b7700de76bcb58e
ACR-06cde97bf0b144a6a19934ec49623542
ACR-e58d6dde41e04a4db3fa5465bc55b58e
ACR-78660f668798452c9d3572770a06433e
ACR-9da92fcb23434ef28224f2456899562f
ACR-5d45f1bc89694509950720fd6209b749
ACR-5c3d9eeec6e9489092a6162c284275b3
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
