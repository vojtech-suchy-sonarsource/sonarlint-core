/*
ACR-531d6a653a074b20b3229d0d172929a0
ACR-96e52838cd8543ebabe1175e0e7a1a5f
ACR-5563f7e65fc3458a84903bda1d69e15f
ACR-9f31cdbca4dd4535a46597dcbae64e95
ACR-812aedfb23104f0ab8b09c32336d1014
ACR-c1cfbbd0e5e0400481eef100d0d70cf9
ACR-fd8bf3043d324834baccb7efa33d95cf
ACR-8270cdffb4954d30909c8b52916f95a1
ACR-8ce01f5b706e42e8b68a8c51d6e1b2d3
ACR-c81fec741a124c0b9937eaacbaf52c58
ACR-b68ba505aedf4e88a37d3daa132798a7
ACR-542566f0938f48b9bea24db466563da3
ACR-8086df04b90c462b9792401e23024e2e
ACR-3be70784d94a47d28743cf6efd69b580
ACR-d6139871558a4bc7b148c27da0da08fd
ACR-336a47b97ff9462994ec83efbe4c32bf
ACR-09bd500c371a42ac9f097248e7b87e28
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
