/*
ACR-78c5c9bd69c84db6b2bfc020b1423a7b
ACR-17f92dec5eb2470ab507c010295d02c7
ACR-b7fff5846b134d1096f5133538edbf00
ACR-9151361b80194482b6bf7ae49c4f15a8
ACR-8ee8caf0906b42e7b18991eed3d06cc6
ACR-590f21d9bb1b4075849cf29fefd858eb
ACR-b2ea52ef91f94e0586e8552be6f421c8
ACR-62d735ebeb024657b0d5fae418158232
ACR-8efc9a6783b14369b063c68c0777704b
ACR-c7e10011147440b18cce3fa18f00e4f7
ACR-afb3a50378c340999f3243194ecc84d3
ACR-e55b7b460a3142d5bd7a448aeed681ce
ACR-5ed76decc75c4d8f94e299e845d4268f
ACR-c2e317fba0a746fdb4db446574774336
ACR-e1dcb8685bd04b9a838ce1f93185b341
ACR-0b19608e54ac4d99b023cd6b2778d453
ACR-c67a11d06f5240008dc6fa2b83bca3ac
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.util.List;

public class IssueFlowDto {

  private final List<IssueLocationDto> locations;

  public IssueFlowDto(List<IssueLocationDto> locations) {
    this.locations = locations;
  }

  public List<IssueLocationDto> getLocations() {
    return locations;
  }
}
