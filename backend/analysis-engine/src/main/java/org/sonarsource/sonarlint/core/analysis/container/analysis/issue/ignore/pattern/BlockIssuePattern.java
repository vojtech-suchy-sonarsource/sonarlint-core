/*
ACR-f8511dc9c56a43d796397608ebe99fa7
ACR-d309d9e3befe4fa0a47b57b27d93f4e9
ACR-6b6feb0d457c4ab4916d0a3030aa257d
ACR-b1af031105674a0dbc81da5d02b69063
ACR-97fabdd624f24b29a7ccf5001b56adea
ACR-3bf14bd738874c9bb1e162bc98dd0dbb
ACR-2c47a2a39d3841c7a29018221e92d8a2
ACR-c65ae72a9b6945c19c40866208075d56
ACR-91637489489c4c6dbe03a9afb3c3c994
ACR-6fa67ddde30d488082136e5445d50fdc
ACR-cfb181af4ddd4825aa7bf2890003009c
ACR-36bf56e7e60c4789b70fd5b7142bf4cf
ACR-2ffac43cb4914ea989bfb791f8915eea
ACR-005a62dd12cb4af2badbad9e465e845d
ACR-4c452dba178a42a4b0f9e0e40c86d8eb
ACR-11d7a9996134463ebfbd7d77145ed242
ACR-90b6f044dbd64325ba0e788e1746c97c
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

public class BlockIssuePattern {
  private final String beginBlockRegexp;
  private final String endBlockRegexp;

  public BlockIssuePattern(String beginBlockRegexp, String endBlockRegexp) {
    this.beginBlockRegexp = beginBlockRegexp;
    this.endBlockRegexp = endBlockRegexp;
  }

  public String getBeginBlockRegexp() {
    return beginBlockRegexp;
  }

  public String getEndBlockRegexp() {
    return endBlockRegexp;
  }
}
