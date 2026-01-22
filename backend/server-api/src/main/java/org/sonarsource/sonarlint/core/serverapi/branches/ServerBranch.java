/*
ACR-97cb83e1a1464c8cb05cb192e7e83fdf
ACR-a7003ad0d3224142aaca2ef914caec99
ACR-584ef520211843b88e7d07eae5f5acd0
ACR-bb0beec05716405aaecb71e4077071c5
ACR-fc1b97adde314e2ca1e66ef4ebffec7c
ACR-bbfa8a1b53974886b25847e4e1919237
ACR-70560acd944c46afa98c5b5b11634c9f
ACR-6e2e2ca0f9cb41a581b93bffaadda617
ACR-85cd0bf68ceb431391afa9ee9600ef05
ACR-bae300f99ae64f59919c0fe408bbbf69
ACR-d04b21e0bb9a45bab5993d1dcdf40e64
ACR-9eafaa5251de44d481270ce1735fd42c
ACR-4dd2f9e3f00b41abac116d17ebf786d0
ACR-01857ef7f7fa4c60a52563789d52274f
ACR-40e9ee4882c04bca92afa175f5534b39
ACR-4ad4209197ed4e6cbd39b5b41f965fdd
ACR-1b7601d9a29c4a8fa1ffe5c8c0bd3f7e
 */
package org.sonarsource.sonarlint.core.serverapi.branches;

public class ServerBranch {

  private final String name;
  private final boolean isMain;

  public ServerBranch(String name, boolean isMain) {
    this.name = name;
    this.isMain = isMain;
  }

  public String getName() {
    return name;
  }

  public boolean isMain() {
    return isMain;
  }

}
