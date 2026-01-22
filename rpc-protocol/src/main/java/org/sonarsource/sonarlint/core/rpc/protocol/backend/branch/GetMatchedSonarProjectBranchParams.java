/*
ACR-624cdac0e254474888214ccfc66e1f37
ACR-924c7f478c0b45c7a37dcd063f4a3bc7
ACR-55e541de2e5f421ebbd0571fa68b8bc4
ACR-78f2cc143b5049ee9e1a1a90e8911562
ACR-216b9096b7744e818069417c1b0c2875
ACR-ed48431981b54ec698ef87a59bd595f2
ACR-f4487a36d6b54accbb3ad84046b6ac1f
ACR-f83a7c8e417442d7a1db4ce6135644f6
ACR-1f8f5e939f5240268c2e401c895657e4
ACR-fd07414fb411460b83343c0a819545b6
ACR-225b8e1f919246c7975e4f097e1de625
ACR-3f059ddf47f44e9ca6833c90f8b73475
ACR-5484d0c6953e478c854966b566c5c6ce
ACR-2592589a493a44e897986b4c1001b210
ACR-a2fe070dc9dc44f597c1adef9e6324d0
ACR-c4c5cc41778b4ba98dfa2f52db59b5bb
ACR-17821a588a044218b2aa9854eb69730d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.branch;

public class GetMatchedSonarProjectBranchParams {
  private final String configurationScopeId;

  public GetMatchedSonarProjectBranchParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
