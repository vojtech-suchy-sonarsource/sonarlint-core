/*
ACR-4cef0d77463345c8a87d428e6919e86d
ACR-473cd338356a4ae892ff2484a12f34e0
ACR-a1c600792728407f8d56a5635a2bf092
ACR-9b2c9916ff7e480da4f3a48ee362fcb7
ACR-9f1f2146e41a40ae98e44d19fd33c8cf
ACR-0adabc3beff9448491704a7b9fed6670
ACR-4170b2e8d6954466baa8bfbb2b8ac5bb
ACR-f4c5f4074d594e21ae52785a6c42d10c
ACR-86ad704d85bf42f2b5abe13adf423bed
ACR-bf56a7b9d3244813a2d7d93c501084c0
ACR-ce1b91d1c44249a5b27b6d9957d131be
ACR-69dca439ad31438da046341f8dfaeac6
ACR-587186b1360d46048c43e9abb9fd9b20
ACR-40cc8ba60d9346f1a343570c0edc0fc8
ACR-c6b9be9a17fe4e11841b2110cbe3b041
ACR-dfa588dfd93b49b0af8aeed60f1e1b99
ACR-08a4b8d1ed8a444eb4b07a9a615d3e9e
 */
package org.sonarsource.sonarlint.core.serverapi.organization;

import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations.Organization;

public class ServerOrganization {
  private final String key;
  private final String name;
  private final String description;

  public ServerOrganization(Organization org) {
    this.key = org.getKey();
    this.name = org.getName();
    this.description = org.getDescription();
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
