/*
ACR-724cf02188f4482a86a016d1a0447497
ACR-e324bf756f6a47158de98880411af695
ACR-99536c49ec7948828c57509cc88230c4
ACR-5d56f1d5e13c459eba2acd5b97e2e90d
ACR-336fe8e9271d4e44813c015d5dbd1418
ACR-65bf87ec4c1547c7b888c7dbe0e978c0
ACR-79e2027d9162491086561b284e9b9b0d
ACR-79db049a6575429bb22c95f1cf5f197f
ACR-1f26d2d956d54500afd4404cf520055f
ACR-2154d10f31384a749abd205098551a39
ACR-f985a44523e94a3bb1c040e671e03db5
ACR-41a9eaa1535c4a4a9265b5256f1b2138
ACR-649e7c9a424041fcae1c7c49a7f3984d
ACR-8528a499de6544b586fdaceb25e4860d
ACR-669d44b1ad0c415884efc3831586d6f3
ACR-6969e8ae5b254b4db55c97b985a53887
ACR-8bcada011b024516a52e7258a89f215d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.URL;
import javax.annotation.Nullable;

/*ACR-20cd4ca6809e4f1a8d672fcc80e92745
ACR-fabdb1641a424ae4828d301bf1904a91
ACR-fb9a38a67f3947faa02957a71e4ecce4
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
