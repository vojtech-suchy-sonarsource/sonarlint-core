/*
ACR-f871a582bf944233a2c62e87f9470aa5
ACR-accb6587d8ee4027b29f58b47ee1bb1d
ACR-d1b0f904aea44e9294be057d8364dc23
ACR-fef6b060bdcf4587a8bd8af53558452d
ACR-494e36b717dc48f5a859f975c1949f55
ACR-a80f2310fed54f42ac4ffb22a7b85c40
ACR-448a018af6434f1a832874bac98264fe
ACR-051f258187ab4f8bb6c323492af322d3
ACR-0a4a4cd69c5e4d47a5cfeecb229db165
ACR-eb087a0cb27c4a5283a9c4652884a579
ACR-fc486075d47d47ec93a132423ff25e80
ACR-f6cd26ceb92a493aac6d69bb89417fa9
ACR-58d6e6d7eb254d4ca46b91949082abcb
ACR-d832a3acad5a4587b03f8b094c750609
ACR-bdffe03f9d70446fba713abbd84f5be3
ACR-14efa39ada884c349369379e40d5d785
ACR-75137bb78f244ef5b133e6835c5325cd
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.branch;

public class MatchProjectBranchResponse {
  private final boolean isBranchMatched;

  public MatchProjectBranchResponse(boolean matchedSonarBranch) {
    this.isBranchMatched = matchedSonarBranch;
  }

  public boolean isBranchMatched() {
    return isBranchMatched;
  }
}
