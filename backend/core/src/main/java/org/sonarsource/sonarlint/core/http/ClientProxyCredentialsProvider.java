/*
ACR-5eece61b695b4aba878ff7499267664e
ACR-fc9561cb82844dd1a08c4e9b5f8f321f
ACR-9bd4168d5eb64fadb78496c4866350ba
ACR-dd25b108463b4dc9b3a9f0fd679ad380
ACR-48a58db68a264ff6ae6ee00e40eed07c
ACR-6ede68092dae416e8c0c27ba895dd039
ACR-32197b47615843258f19ab2d3ff7e96a
ACR-2f25c44a9e614bf2b39ac0e7af6914a6
ACR-5d6f7c753bfb43b9bbbc55535ba9c4e1
ACR-ce33adafc25244788a02dac842187bd0
ACR-75c89b234b084b439054cc2a2964a23f
ACR-875ae82ff55e4103b49e69ea0dd24db8
ACR-a5bc3d261abb428da937b08b9dc154d2
ACR-5486ccef760946ffb880860c25d58d39
ACR-1a5488172b56434f9212d997237bd041
ACR-bf6b72adfec84195bab44eba2c153b39
ACR-35fcef81ecad42419044e7296e138948
 */
package org.sonarsource.sonarlint.core.http;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.GetProxyPasswordAuthenticationParams;

/*ACR-b1eb2c3a138048559ce04a87c85d65fb
ACR-9c193f6b2e3f4f9b80a9f3ffe66c1f71
ACR-ff881e8e419b4b38a41e2966ffb6a3be
 */
public class ClientProxyCredentialsProvider implements CredentialsProvider {

  private final SonarLintLogger logger = SonarLintLogger.get();
  private final SonarLintRpcClient client;

  public ClientProxyCredentialsProvider(SonarLintRpcClient client) {
    this.client = client;
  }

  @Override
  public Credentials getCredentials(AuthScope authScope, @Nullable HttpContext context) {
    var host = authScope.getHost();
    if (host == null || context == null) {
      return null;
    }
    try {
      var targetHostURI = HttpClientContext.adapt(context).getRequest().getUri();
      var protocol = getProtocol(authScope);
      var response = client.getProxyPasswordAuthentication(
        new GetProxyPasswordAuthenticationParams(host, authScope.getPort(), protocol,
          authScope.getRealm(), authScope.getSchemeName(), targetHostURI.toURL()))
        .get();
      var proxyUser = response.getProxyUser();
      if (proxyUser != null) {
        var proxyPassword = response.getProxyPassword();
        return new UsernamePasswordCredentials(proxyUser, proxyPassword != null ? proxyPassword.toCharArray() : null);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.warn("Interrupted!", e);
    } catch (URISyntaxException | MalformedURLException | ExecutionException e) {
      logger.warn("Unable to get proxy credentials from the client", e);
    }
    return null;
  }

  private static String getProtocol(AuthScope authScope) {
    String protocol;
    if (authScope.getProtocol() != null) {
      protocol = authScope.getProtocol();
    } else {
      protocol = (authScope.getPort() == 443 ? URIScheme.HTTPS.id : URIScheme.HTTP.id);
    }
    return protocol;
  }
}
