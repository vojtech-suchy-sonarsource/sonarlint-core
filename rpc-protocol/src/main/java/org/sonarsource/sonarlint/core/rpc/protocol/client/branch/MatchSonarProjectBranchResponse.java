/*
ACR-f380fb9d69c24ec184695b10dd5c4560
ACR-503015020c644be69208d9d068e50f96
ACR-fd3d9f88c5df41ffb1ca42ea87e8e3e1
ACR-bcce4cbe316f4b2ab4649e4092cd4430
ACR-9580e78db21f476cb6d126779f4c957d
ACR-635bccc96bb6456daf970d5133c98660
ACR-5ab0fc2498e248299abfdadefe8172f8
ACR-1f071554be5b4397a8706cfb6d7ccc5d
ACR-a4a7a5169428476da4ddb70c789b0f7b
ACR-b5796063e7b54ba981fca84b8859f9ef
ACR-ff6fa6ec8970438dbecd6098a2098730
ACR-4922f974bdfc47f28caa8c06202d3612
ACR-e35c744a1d214c159a3278eab3d88abb
ACR-bcf0f62eb6f046a49dc81f97100f5218
ACR-cfcc72f43e6a4d21a066fb9419e0ec13
ACR-786e0dfa69ad48719d7fbb1359ee0d14
ACR-0961fa4934cc47e0af8e75057c773fb3
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.branch;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class MatchSonarProjectBranchResponse {
  private final String matchedSonarBranch;

  /*ACR-689ffe9646d24021b5cc6f866eab08b7
ACR-5b09ebd824c3417da091a9c0ae29521a
   */
  public MatchSonarProjectBranchResponse(@Nullable String matchedSonarBranch) {
    this.matchedSonarBranch = matchedSonarBranch;
  }

  @CheckForNull
  public String getMatchedSonarProjectBranch() {
    return matchedSonarBranch;
  }
}
