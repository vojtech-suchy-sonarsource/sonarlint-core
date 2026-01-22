/*
ACR-238154f55f5e42f3aec1ecc0a40c54ce
ACR-1d62908fea694dbfa4fffaedbce377e4
ACR-9377d1473b3048cdb53ed1b110641ad0
ACR-d3af6ec2ada74d2ebe0cf31ad0ef87f8
ACR-d788d63b13e04223ad0acfa07b302840
ACR-eb84d775f641457485b39709ea683723
ACR-ad7df41c5ef44f09bc544cd5e5659f4d
ACR-f5862125cbee4d59a687f4087e15fc54
ACR-b2b9711b0f3b4884a9e0c643b53d8302
ACR-7cd6cf28fe614dc5904d59d49637fb63
ACR-43ae767df7c7411c8fa9b157fc813c8f
ACR-c10f566c03d14111b4904420792ab996
ACR-1b6b53f9d4cd47d1ac363512fec909cb
ACR-2abff68f3f46484b97433cab492db9b6
ACR-f09ef35a1cc9456384c166cc62a517d8
ACR-01826f099014479cb9dc0d8b99b5bb34
ACR-ceffba626a664b3884fa18d505712c6c
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

/*ACR-db0cff4693964f6e9ba57c0acdaff374
ACR-cbfd77cfe40a4ddc9d225acb821498d7
ACR-38dcead12f6f44bd9d2fe4eafd903611
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
