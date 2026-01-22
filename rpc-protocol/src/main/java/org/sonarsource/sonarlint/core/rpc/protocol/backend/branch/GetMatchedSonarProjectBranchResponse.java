/*
ACR-a756aa3f029347ceb5c313400478e16c
ACR-e7aab56f4ed340b58e6b57c78649b457
ACR-fb6cf6e9d1ed43debd4aeeb9311081dc
ACR-ca8581d5aee44d7b8d5b291fe935a64e
ACR-fe1ed7244b6d472eb08a4d4ce9e0379d
ACR-4af97fdceb594718a9d23f2a070b768d
ACR-52aa70406c744664bf5eee8e970e4fe7
ACR-da95a01019fe41b9a76872890c70f46a
ACR-5eeb96fc8e1d4732b1cd968b75a98231
ACR-1dbc1c7a95324a8aac771490ecf59d62
ACR-021130a667744dbeb1ef79668dd678b4
ACR-9564f40953d8407885e77e8e620a0d2d
ACR-e1597515f7784801bda49d9d1ad47f3a
ACR-5aa3a520b3c04d7b8ac9dc2ec649f186
ACR-28f2eac3ecd446dc85c521047ab40062
ACR-d5473c1f895647d9be900862fed70e0f
ACR-297ec200d17740088aa34068e583a2ed
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.branch;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetMatchedSonarProjectBranchResponse {
  private final String matchedSonarProjectBranch;

  public GetMatchedSonarProjectBranchResponse(@Nullable String matchedSonarProjectBranch) {
    this.matchedSonarProjectBranch = matchedSonarProjectBranch;
  }

  @CheckForNull
  public String getMatchedSonarProjectBranch() {
    return matchedSonarProjectBranch;
  }
}
