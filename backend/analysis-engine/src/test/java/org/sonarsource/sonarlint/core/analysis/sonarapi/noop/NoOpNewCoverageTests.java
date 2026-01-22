/*
ACR-01bffbf0e697481abb55a4bbdcfac5bf
ACR-189cbc3aee904bbd850409f13885797f
ACR-789a360724624eb29ee604ff9e8f1786
ACR-58f9f9794776471da89bcba15a3cda13
ACR-8ccca50291844ceaa23f34b7893aa1c7
ACR-2053f527632e4d7aa04f02e912b6909a
ACR-42ec9495116b464aa5bbb62736bc9120
ACR-c80f380de49843eb969b9b98fd0e8ab4
ACR-cc07f3d5009942058d1ae5799d96c850
ACR-933aa078e24746b88406e4535754d967
ACR-999247e0ebf8474da7d200b5e7ae8884
ACR-85766b61fd65498894e0ec1e815a7237
ACR-ce4325bee30a45d29a943fa2d866b606
ACR-98b8fc47aacd496c9357204535ea8fc5
ACR-02c3f766fd6f4e6aa716815122e02925
ACR-fa920ba5744549b39137e671b4845751
ACR-ce9a771756f546358dec76f8cc7df56e
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.junit.jupiter.api.Test;

class NoOpNewCoverageTests {

  @Test
  void test() {
    new NoOpNewCoverage()
      .onFile(null)
      .conditions(0, 0, 0)
      .lineHits(0, 0)
      .save();
  }
}
