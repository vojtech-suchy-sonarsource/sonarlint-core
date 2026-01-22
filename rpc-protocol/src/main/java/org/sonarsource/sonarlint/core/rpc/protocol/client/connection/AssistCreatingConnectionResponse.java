/*
ACR-cf71887a10d9473cb1a316b023d3e002
ACR-faf9436775844c5c8cd1bf3737e68036
ACR-45840948bb504633a7828c2dcb385abf
ACR-5d2dc7e5bc7547c6abec0bf60dbb8752
ACR-2de2e90c4f67496dafc4ed65b26121e0
ACR-d67cb1f2429e4468a8dc9d7e80261d9f
ACR-08c2bf3b73c64dddbfd4224bbe6cd6ab
ACR-c1e7d08064eb48afa6ed90f36f486d5a
ACR-57a5daad905c418a8c0e606aa5c85ff4
ACR-18acc658ef554fce954277828606f9bd
ACR-dc08a593856d4350a9060f8301625ac0
ACR-382e1c67a0cf46e998890b07eea273d3
ACR-9d4f4d622b1e4dedb4148693229d49ab
ACR-53fcfccdcf2b491283d4da1c3e570557
ACR-e86640fd3b6443a08b35e04af0cd6bf6
ACR-78233d9f8c2d44da8087acf93ab331aa
ACR-0a696ff4b9b44821aac0b29d8654cf06
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

public class AssistCreatingConnectionResponse {
  private final String newConnectionId;

  public AssistCreatingConnectionResponse(String newConnectionId) {
    this.newConnectionId = newConnectionId;
  }

  public String getNewConnectionId() {
    return newConnectionId;
  }

}
