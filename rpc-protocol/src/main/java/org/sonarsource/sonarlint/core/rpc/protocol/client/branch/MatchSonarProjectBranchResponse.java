/*
ACR-42e2603781a84f0f849824da424e31f1
ACR-53dc1782fffe47e68db4f53c319d4e38
ACR-307da124def042a1a94aa1930878004a
ACR-280b17abc46f402b8fc94a350be6a3fc
ACR-72e0772bb9814937aece681f6337b5b0
ACR-d968dd3da22848ac97b404506083e83e
ACR-b17cb49c1d68461e971b5bb54c7b4431
ACR-31913340c4114473ad4822b7f28b0d49
ACR-dbb57a7908d945e5891f89ccb52a24e1
ACR-4f91cc7dceee4254b98c6acc8df9518d
ACR-00459a2e5c014817a0bdd8ef6dcba7f8
ACR-c2448e82034f411e81d987ddc7d5f9bd
ACR-0c0411aaade3474fa4d473d800873a14
ACR-660b3b5c7efa46fb9603b7bab954893e
ACR-a2a613caafc143b9a474680a421fcd15
ACR-4809d8436a714c428b34bdd79f798c84
ACR-339af3ab99a64a6086280d98d4777fdb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.branch;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class MatchSonarProjectBranchResponse {
  private final String matchedSonarBranch;

  /*ACR-70318764c7d44d69b45551f71d89f75a
ACR-28b067c82ec740cf959fc23b9fdacd5b
   */
  public MatchSonarProjectBranchResponse(@Nullable String matchedSonarBranch) {
    this.matchedSonarBranch = matchedSonarBranch;
  }

  @CheckForNull
  public String getMatchedSonarProjectBranch() {
    return matchedSonarBranch;
  }
}
