/*
ACR-05de162a19d745cdad6298acc4ed83f4
ACR-0054b4c682be42c4a050b36406cb15c6
ACR-6323f6eccc4b4098a5078ff135137074
ACR-c8b0347e356e41ffba65c6322b2bd118
ACR-7d7b5f8b1b164f6792b17bba9dd92648
ACR-11ee9ca332ea4eeeb853d3d5b393331c
ACR-cc45f4f06dad4076b11e8be1cfbd026d
ACR-a0733f24445c49afacc56681586e3255
ACR-523611132498405e8234daf8ec78dcc2
ACR-a2b42f92a0eb471e8806aa71ff39c302
ACR-a01fb676454547d59ff4442f0213352a
ACR-81dba379eaa94e4ca4ae2ba9abef166d
ACR-6183a3caf43a46378537b702b8ad6d6b
ACR-6f4f26aa9fdb43a3b7a8eeb5f0589f97
ACR-06dde96c35174ea59826d187c4b94210
ACR-fd19062f5fa2481a8d81bd3c9e241420
ACR-0aeb7cc3128e4e6da322682bb4aa6e82
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
