/*
ACR-2d1447ce9ecc413da3f7581b85d79fc7
ACR-3e9479397313456684fd3b3da16d4fd2
ACR-fa0c4ff5e8354bc5b9ea0a80e2093e89
ACR-778449b655174be2873f0bc73ebd24fb
ACR-fa7d339fce054acaaa8d5caeab1dea67
ACR-dca847de3d44458390c24f02a3a00e43
ACR-8db1b28446614f9eb86dcb8cfe7184ec
ACR-02e7f5e554be4e5b99d44fb075904bd7
ACR-60fc3b2d93d04b7da0ff3c9462073461
ACR-62f73b86988c421cac0f91b67a189550
ACR-a76638d82c2048cd9b91c14a0d2ccc39
ACR-3b83c3966840401780d100c7e36ac0a4
ACR-4beb1173ac79408c81b1d4bd27f128fd
ACR-ff626dc0c8ec40eb9d88e45d6c04814d
ACR-5c8092bc3cd947c092f5749f59f153a4
ACR-971eb2a57d3a43d1a396fda63ef0e5b6
ACR-a774ed2ea29a4dabb0b79f1bbcbfbd6c
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Set;

public class ProjectBranches {
  private final Set<String> branchNames;
  private final String mainBranchName;

  public ProjectBranches(Set<String> branchNames, String mainBranchName) {
    this.branchNames = branchNames;
    this.mainBranchName = mainBranchName;
  }

  public Set<String> getBranchNames() {
    return branchNames;
  }

  public String getMainBranchName() {
    return mainBranchName;
  }

}
