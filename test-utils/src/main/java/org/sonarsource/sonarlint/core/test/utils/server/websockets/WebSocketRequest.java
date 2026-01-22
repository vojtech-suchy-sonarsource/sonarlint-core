/*
ACR-c9f363039a0140998fe5712e0e439dc6
ACR-1a5cb50dfbdc416681f848415e5dffdf
ACR-4d729a1e58ad4129844da235f51810dd
ACR-12b01b333dcb451ba96f0acaafe3913d
ACR-007edaccdd9a446d84ed49b74b73d2bf
ACR-226c5979c9624dca8fb97bb8eb7565b2
ACR-e7531c1e280b4758b2a87aff02e7cdad
ACR-72d5a411bd0a443980dc2c279a726cb3
ACR-984c6d2a5d824ab0bf69e6b83fb63f50
ACR-79bf9bd3eb914e86be1f11574024045f
ACR-502429edcff64aae9f9b94ec5eaae9de
ACR-a78ac5195d2440db8664ea0f2b0a7c53
ACR-573625d4c85e41b782f4f3a187fa6c21
ACR-b8e1e5c334b64df1b05d542bdb1a870d
ACR-2673da5b6b1346b18858e87f2254c4dc
ACR-65763cee3a144fd9b1af5bdfecb06231
ACR-30e9a7b921b94abb9e04039a96c8fa93
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

public class WebSocketRequest {
  private final String authorizationHeader;
  private final String userAgent;

  public WebSocketRequest(String authorizationHeader, String userAgent) {
    this.authorizationHeader = authorizationHeader;
    this.userAgent = userAgent;
  }

  public String getAuthorizationHeader() {
    return authorizationHeader;
  }

  public String getUserAgent() {
    return userAgent;
  }
}
