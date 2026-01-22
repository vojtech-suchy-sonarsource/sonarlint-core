/*
ACR-1d62e3185f2443c0ae2150170b66be29
ACR-06bdee914c8f43139cf669e448400fc5
ACR-81b83b55224b44068f51d692315fc7fe
ACR-fca0f727daba40f987ea57418e0a4bde
ACR-b4865a149c1c4ef3948d845a5097a4e2
ACR-d41a75bb841844ac9ffc53db603996a5
ACR-9328878a02b14d1c8b054a135aa475c6
ACR-54ae7eaf2704457687671de783f0ffa0
ACR-46fd7af25ccb425d8acb6f022f6e237a
ACR-83bbc5c66fd346a6b3f2ef29846ab0fa
ACR-1c4f386cbc744dbeb7362bfc385eb065
ACR-a8d17283257e480482464e8d0f9fd8b5
ACR-f14c83e58eb64972a6b5d757725a0f7e
ACR-5d73388d1c8549ffb5f4a60fadb76788
ACR-f289a4d41b9e418f86ef4f214f56a610
ACR-5345b8323533484ab9306cf5a98ab1e7
ACR-5992ce6ae09242fe950ebbc36ee915c0
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection;

public class GetMCPServerConfigurationParams {
  private String connectionId;
  private String token;

  public GetMCPServerConfigurationParams(String connectionId, String token) {
    this.connectionId = connectionId;
    this.token = token;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
