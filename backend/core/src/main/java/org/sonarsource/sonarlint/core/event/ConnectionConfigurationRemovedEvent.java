/*
ACR-16f48e587ea943428404ab27ee941496
ACR-d4d1e18200934150b35f3cf4605f8cbc
ACR-4f1c7b8e582f4211a41cfe733e42cdf5
ACR-c4b8531b533248b48f44e482f3cad34d
ACR-82b01de96af34d35b376ee3979655dd5
ACR-fde768e36a514eb2bbe70a2dcc6723ed
ACR-3902909f665d4f60911881d6d6576fbc
ACR-b7d7a4cf100d4c898a4ce0e7c0dd55a5
ACR-b6e9258321de42d9abe7948333cd09f9
ACR-9b21152fd62a46daa221e55500d12db4
ACR-5c3f9325a4b24d199bea3f05d48716ca
ACR-39693f5ea3e1452aae7603d013dff38c
ACR-a6a4cc6d3c4c41ba8339c078c2c74f83
ACR-4921e8392557449e82f43cec924a747d
ACR-ab3a3145240f4c57b20fba67bb1263e4
ACR-3826ba82996b4add90260bbab30fdbf6
ACR-be56a1c8614245909c6144e1a98f9221
 */
package org.sonarsource.sonarlint.core.event;

public class ConnectionConfigurationRemovedEvent {

  private final String removedConnectionId;

  public ConnectionConfigurationRemovedEvent(String removedConnectionId) {
    this.removedConnectionId = removedConnectionId;
  }

  public String getRemovedConnectionId() {
    return removedConnectionId;
  }
}
