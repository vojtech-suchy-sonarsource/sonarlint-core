/*
ACR-7b891206b4a0465184fe00e5c8e550b1
ACR-da986076daef4a60b82589e598e25130
ACR-117532643988454eab18f82d94bb6144
ACR-9fdb695c3ae0437cab165af44c236767
ACR-7ac91d7665c248d2bbdee12ca95ae348
ACR-8ed78743fe9348f9a41312a1b3ef231f
ACR-474a49e804ee4cf395a2cf4c8f1beeda
ACR-92d05f77ac3f4c7e9530ea67fd63f533
ACR-b9a0484d717949bc8992ee7b978dd8bb
ACR-9617e89690cc4e6c8bc617dc56beed3e
ACR-64942a600db44a779eed1e0eeca45b3c
ACR-cf49c1d84b624a1d9d01788b06e45191
ACR-148fbc6ff00841aca9a70900e7947823
ACR-2def5e2dc3dc4e8facafe0a16dd89f8b
ACR-f20ee534c9d24b418247fc218f470f7c
ACR-6bbd381b727041a98089559ca6385b7b
ACR-fd097323c61b4d5cb94672be19079d2e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

import java.util.List;

public class FuzzySearchProjectsResponse {

  private final List<SonarProjectDto> topResults;

  public FuzzySearchProjectsResponse(List<SonarProjectDto> topResults) {
    this.topResults = topResults;
  }

  public List<SonarProjectDto> getTopResults() {
    return topResults;
  }
}
