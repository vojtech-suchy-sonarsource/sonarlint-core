/*
ACR-0b29643b77744169afc7712b3752981a
ACR-04be183e36f243d2aafc4f61f5856bc7
ACR-6bf05ca459e24f9d8ce224322647253a
ACR-7bb5e4ab6a874d0d9f221bb5ab7bba05
ACR-ba738a5cf59942ffa5a84a6b2b774909
ACR-c12e8d9672024b35883cc0ef8ae49a12
ACR-2304ccfba0e84a08adbb816e9b996a10
ACR-fea38d6f35be49f09ed08dc0210229d7
ACR-cc1825aba4b0426ea748831d6501040e
ACR-d8a97a813bb746539eee4961db797b1a
ACR-3eb5d54c6bff47f0b6ba65c28cd9333a
ACR-aec3576d35cd4e10b9e4fd8a0b2138d3
ACR-bdc2ee650ee340ad91a52e291c02d4a6
ACR-53a42cfafc9c4af1acf557ae6124de9a
ACR-c9a3d5b9ed3942b68b1328b3f520a233
ACR-cd53be12298740a3bdfc5d2e2127dfb3
ACR-b6aa4f41e4604fd5b1ba4b8c759ec142
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class SonarQubeConnectionParams {
  private final String serverUrl;
  private final String tokenName;
  private final String tokenValue;

  public SonarQubeConnectionParams(String serverUrl, @Nullable String tokenName, @Nullable String tokenValue) {
    this.serverUrl = serverUrl;
    this.tokenName = tokenName;
    this.tokenValue = tokenValue;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  @CheckForNull
  public String getTokenName() {
    return tokenName;
  }

  @CheckForNull
  public String getTokenValue() {
    return tokenValue;
  }
}
