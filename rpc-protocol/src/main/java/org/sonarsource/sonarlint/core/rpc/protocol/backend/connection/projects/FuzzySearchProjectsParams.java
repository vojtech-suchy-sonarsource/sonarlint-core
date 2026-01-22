/*
ACR-7c8376fbb0e04a14ac1b134950ee2b4a
ACR-8b43aa7114fc427eaf54f3f1eb0c6412
ACR-7f9f9d5901dd48cb91f249059f87530a
ACR-c303b53bee6b46b5af9d7bb2a902ac03
ACR-736c5d52079a4fca88f1057d9b273c92
ACR-066bb957ccf34402bd9b04ea83afed5a
ACR-4d6ba11d0f2c47f49f49aebf38242eb7
ACR-0a15dfd5f26a46b681a31a28c0859d09
ACR-ea226832cbf74b17905669d01dc681a5
ACR-c512ca4bd10642b396824492a08cb7ae
ACR-966de14471a9467cbb07c7087b242f56
ACR-4ba2f9b7b54b4e4bb5a7b680b095d9e0
ACR-9599b73723a947f38ea3cf7e76a3ab87
ACR-2ba97f3ea0a64fd6ae72279b2041398e
ACR-ac0b122707d2498cbf5d3f1882775cc3
ACR-82c998e1e06e45bd83e682ddd152f8ca
ACR-10aa3fd8a2174aaab809c1decf9cc2c6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

public class FuzzySearchProjectsParams {

  private final String connectionId;
  private final String searchText;

  public FuzzySearchProjectsParams(String connectionId, String searchText) {
    this.connectionId = connectionId;
    this.searchText = searchText;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getSearchText() {
    return searchText;
  }
}
