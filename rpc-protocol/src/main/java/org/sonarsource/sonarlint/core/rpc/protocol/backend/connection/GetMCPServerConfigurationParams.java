/*
ACR-ce45e133541f41128281ae4dcaf2ee29
ACR-743815b719434f9ba3c08937b9f5ef25
ACR-3f2d5af72ee24f46946c1ec101a8895c
ACR-d193316039d5421c9cf78e3a148187a8
ACR-0fda2f3667ef488bbb2d1952285a93b7
ACR-efe9bb428ad04bbd8e658b746305d1f0
ACR-8090f0a2712a4166baea3b90225337de
ACR-636b8b038f6d4ef2b56c8bcf642ea729
ACR-63a199197d6d4a58b56fac9e49109fd6
ACR-a1b038e1b5804bfda37efb49ea327841
ACR-175a7d2883a743cd9c455c3f970215b7
ACR-c72534044c454f9db162b32a3c5c7b29
ACR-9c04cfdc684946c3943784821cfa8adc
ACR-273531356bf34e458c2cb4eed8a34292
ACR-ecdfab31fbb44e749602734f5b826208
ACR-7e7f0c45725c44899b415086d6d68f12
ACR-b9cd3d7d48374b228af267832ca4e372
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
