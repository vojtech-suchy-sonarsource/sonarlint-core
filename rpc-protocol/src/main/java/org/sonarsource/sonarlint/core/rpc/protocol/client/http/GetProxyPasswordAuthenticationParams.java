/*
ACR-30bb6db5cd2e45bbbfa11ca2688aed92
ACR-6b6678b7694045d78287b6963582475f
ACR-1ca9f7d45bf74e5fadefdb73dc0d0f31
ACR-4842759bb20748ce8788149eb332832c
ACR-e329b265a503449a9a0c2cd74c710cc9
ACR-93c10b4237e04e11a86127ca23236dd4
ACR-d5249394a3284ebd95ace1c2621b1f8f
ACR-c757b9b2fda8492f87257eee54da0ce2
ACR-21e7d27be65e458db4ee729db1af65c3
ACR-8251f5735db04cfcb24b143ac99b2c94
ACR-4dc811a11c694958a74b63eb8c6587a1
ACR-cdbce64e14a74fec9b8271a464735a89
ACR-c4a6989eb99f41c08e8ecea14da50673
ACR-233b70ff953b457c98d6709398b6386d
ACR-0ad88932ab664872bc256691fb89ac95
ACR-a606606d0d0c430a819593f7ac8a2617
ACR-949a23f9dc804f9a9df06a1d993484b9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.URL;
import javax.annotation.Nullable;

/*ACR-be8280d72ca64597a8bded87b55523e3
ACR-6e5fa3ffd42247af80eb929d345afdc6
ACR-38050afc722b4c04b195cb7e16b2d1ab
 */
public class GetProxyPasswordAuthenticationParams {

  private final String host;
  private final int port;
  private final String protocol;
  private final String prompt;
  private final String scheme;
  private final URL targetHost;

  public GetProxyPasswordAuthenticationParams(String host, int port, String protocol, @Nullable String prompt, @Nullable String scheme, URL targetHost) {
    this.host = host;
    this.port = port;
    this.protocol = protocol;
    this.prompt = prompt;
    this.scheme = scheme;
    this.targetHost = targetHost;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getProtocol() {
    return protocol;
  }

  @Nullable
  public String getPrompt() {
    return prompt;
  }

  @Nullable
  public String getScheme() {
    return scheme;
  }

  public URL getTargetHost() {
    return targetHost;
  }
}
