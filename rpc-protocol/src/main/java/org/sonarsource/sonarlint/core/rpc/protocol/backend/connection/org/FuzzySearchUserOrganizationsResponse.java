/*
ACR-75f0cd5837c94a44abfb800ba13063b2
ACR-8f3a1822cd474a2ca22cec4b2cc41614
ACR-51dedc3783894adeba5b794663399537
ACR-b8b9a77a949449b4ac0ce023242fdafb
ACR-23c046d5aff14e3892bcb6acd801e7c5
ACR-3495a2984fdc48ef8c9ca6c8ca1cc446
ACR-878cfcabe1ca467381e62ec5aaf1f4c7
ACR-bdb96fe27a9c429fb5834b86e5919c38
ACR-521bcfa9204e46628a63d987b347fdce
ACR-3cb82b2d32c745d68ff08f2666c78744
ACR-fb0972313e4b472288e5b8a0cd556956
ACR-c5d6ebdcf23546dbb181515bbc953c41
ACR-8b4f0f1090c748b788d946f5942b6cf4
ACR-d7ed2e9948ed46b0b293d351a82c0001
ACR-ad48665713e940828ef34bfc738c3e69
ACR-357ea35467774399ad96469d5b450d55
ACR-a9dbff925b0d4573877d5db2920df2d6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org;

import java.util.List;

public class FuzzySearchUserOrganizationsResponse {

  private final List<OrganizationDto> topResults;

  public FuzzySearchUserOrganizationsResponse(List<OrganizationDto> topResults) {
    this.topResults = topResults;
  }

  public List<OrganizationDto> getTopResults() {
    return topResults;
  }
}
