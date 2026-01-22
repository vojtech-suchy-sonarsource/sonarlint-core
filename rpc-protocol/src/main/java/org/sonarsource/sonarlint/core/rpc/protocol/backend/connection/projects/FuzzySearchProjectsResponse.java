/*
ACR-910430b148e547889ee1daa156fedb09
ACR-eceb53eac4d9458d85ac108f53c07006
ACR-319f9379adce4b87b5b484bc97e9979b
ACR-a488e57e8c6940ffa9e9333663ce5cd3
ACR-a932e70465ed47c49b1d2056049adb5c
ACR-7c80114252b8417382386423a0128b95
ACR-c39ceaaa2ed94280a75ad3663568783b
ACR-b58e372ea2094b8ca867c6d86ce83c80
ACR-1e9e75d051114a94ac418076dd4ac188
ACR-ebba4a6c2fb94e71a2531707ded9fa1b
ACR-2f0092e1d7aa4aafa293cd93e2d684b1
ACR-754d1923e4274c2ba3ea568312836b81
ACR-082f6f26a5d14078916b90394e6f2ca4
ACR-df4351a440234764a5088e7bc0b8dc73
ACR-a2cfc0fade0a4a7c866219ecddb6a981
ACR-1195cde1ad994137b4556d2bdad3197e
ACR-41c593cb78e141d6a8e578ede42fa7cd
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
