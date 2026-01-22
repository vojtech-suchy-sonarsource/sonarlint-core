/*
ACR-33a3df21d4ec422a8def13c056b36433
ACR-516d26d0067a471f92945664b32d1c39
ACR-5fe82e3fd0a6483d9af6d7dc8c31ac87
ACR-97e28332014c414f9dbc4a466dbaf461
ACR-897a89c56f9b42be9911e55cd680ec64
ACR-094ad612519d4d2aa22009d5ef5e3445
ACR-3278b8ec009346369bf26d87d11ee0df
ACR-4f787ae6fa16447db155c4d27b1ac9a6
ACR-067e6a028af148eba97a93ca0fb3c64e
ACR-8bf7f028e1ce4ff1a5105bac59a9da4e
ACR-c660096ab1fd469597e87c55365e2e0f
ACR-09ab18e9bb8241ee8ff6129c3eaf31fc
ACR-dca9bf5d53674d8587f8dda73196c324
ACR-72807d95ab0541b88aa17e8d11aafa42
ACR-dfde114a53f04bb6bc896d961854c1f0
ACR-1b3da72357da4bd19bd0a608b8d0f01f
ACR-337234c011d940ca9d3d4f0c51c8f93e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.labs;

public class JoinIdeLabsProgramParams {
  private final String email;
  private final String ide;

  public JoinIdeLabsProgramParams(String email, String ide) {
    this.email = email;
    this.ide = ide;
  }

  public String getEmail() {
    return email;
  }

  public String getIde() {
    return ide;
  }
}
