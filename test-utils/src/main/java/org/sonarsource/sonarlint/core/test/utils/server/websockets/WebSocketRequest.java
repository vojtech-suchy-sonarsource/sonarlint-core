/*
ACR-349c8edc1d3143e6b363de363b098e9c
ACR-ba02aed8fde043c395a2a1df2b7a4158
ACR-8a1a2fab19e741dbae9e3c76ce25c15b
ACR-81cf0a5feb5342a8a12b41dddb841e86
ACR-d4c84d3163834374962f8eb3649492b4
ACR-429c6f6b8e5540d2b3707a88da4d2da0
ACR-cfc3760f6c3f4742b85af84ffa21f589
ACR-786818edce334d05ba6176714cd41674
ACR-42d57b5d9ae742bea54188a88c432754
ACR-1cb88758649d4f1a9c002ef157dc8049
ACR-74e8b04c881843699519ca2b42276512
ACR-1a6f1566a2f646b29ffcbc8ed361bab2
ACR-3d144dbed0cc46138e57aa9d6faaa156
ACR-026f0058aa344f28b6e7a6d6a244c74c
ACR-5dd3aeacaf3c45a9871270c752812fb7
ACR-350c754c4a374db0a87fb764f7d81176
ACR-b109955929c748388dcb397c491cab4a
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
