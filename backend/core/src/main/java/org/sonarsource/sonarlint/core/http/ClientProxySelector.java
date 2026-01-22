/*
ACR-51f1c0b88c2f46ecb6a812b5aa167b84
ACR-a2693c3eaf9b4f078b3a424c91f60af7
ACR-7172b6dd0c744d7aaf1b51c1b053167d
ACR-863f05ba3b5345fa87a32280f234c073
ACR-efd2d1fd2a4d4ff2ae66f66df7a5a553
ACR-29784288f76f49baa3d0f760af1061e5
ACR-2fc971a252b64ca9a15141b3a9e7ce96
ACR-af6864631d0b4863b51d02af62a048a2
ACR-837a2493d83d427c834a9f109cbe8a1d
ACR-2238ae4030a845c6909f4596654c33c3
ACR-0e435c39f6eb4d25a32c4ab58dc0e929
ACR-cc6e555ffc5746cab07f58ce4629ebd5
ACR-9849696b97d04f38bccec7d3ab8fa483
ACR-af1465d944ea4042a0ec775e691eff61
ACR-9dd419401ac54a3aae4af23d2688cfc0
ACR-ec063e32ac094e4592b48c0ae3c88164
ACR-b5a228e772a242908cc6b46fcf9bfcfc
 */
package org.sonarsource.sonarlint.core.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.SelectProxiesParams;

public class ClientProxySelector extends ProxySelector {

  private final SonarLintLogger logger = SonarLintLogger.get();
  private final SonarLintRpcClient client;

  public ClientProxySelector(SonarLintRpcClient client) {
    this.client = client;
  }

  @Override
  public List<Proxy> select(URI uri) {
    try {
      return client.selectProxies(new SelectProxiesParams(uri)).get().getProxies().stream()
        .map(p -> {
          if (p.getType() == Proxy.Type.DIRECT) {
            return Proxy.NO_PROXY;
          } else {
            if (p.getPort() < 0 || p.getPort() > 65535) {
              throw new IllegalStateException("Port is outside the valid range for hostname: " + p.getHostname());
            }
            return new Proxy(p.getType(), new InetSocketAddress(p.getHostname(), p.getPort()));
          }
        })
        .toList();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.warn("Interrupted!", e);
    } catch (IllegalStateException | ExecutionException e) {
      logger.warn("Unable to get proxy", e);
    }
    return List.of();
  }

  @Override
  public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

  }
}
