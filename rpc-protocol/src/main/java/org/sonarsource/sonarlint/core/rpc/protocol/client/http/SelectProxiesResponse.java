/*
ACR-30b6da71387845e9b1dbcf84a7ab326f
ACR-79fac3c1479342b59e57e21644264a3c
ACR-d87e6ee53dcd42508d29ad9b39139eb5
ACR-095e27cd3d7640abb195b54427e67657
ACR-4021d4ae44d14f13ade6fd88cec3d78c
ACR-f19b4cb06e954b339c9a84436619d7ea
ACR-1c2b3cc5411145b49b95177a7c35c806
ACR-2d862ef42a3d489f931b9dbd88ca3ee7
ACR-b0894678ba1b4cf8b115bfdf43e886e6
ACR-b78fc019e68747c5bd1eb157e4bc450f
ACR-bf4d2dea42b24a5e8720f7572c61b0f5
ACR-657c9120327f4923bc5d6ca6ba1e6cfb
ACR-ceac0e00776047db87f1c756e5fb4730
ACR-be14e6cb573440faab156b6d4e2b11cc
ACR-4c5c210228ce4a549baef23d7e5162c8
ACR-76664a71fcf0417e9577527c45ff0444
ACR-4823ee4c8f3149e79b0fc9a58e5db256
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.util.List;

public class SelectProxiesResponse {

  private final List<ProxyDto> proxies;

  public SelectProxiesResponse(List<ProxyDto> proxies) {
    this.proxies = proxies;
  }

  public List<ProxyDto> getProxies() {
    return proxies;
  }
}
