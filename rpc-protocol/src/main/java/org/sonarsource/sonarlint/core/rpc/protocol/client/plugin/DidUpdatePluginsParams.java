/*
ACR-472c802d6b43417a90a3f5d58f954e0e
ACR-a14b217e70b44883bd0ec82e18acffe8
ACR-cdc9281e84de466fa49735339b6a000a
ACR-78717abd5ce2431c9b0ced2af591dd81
ACR-e43f4a8def55439cb161e42ec351e75f
ACR-445e3d1b236e4eb7a3098c6f98d1a364
ACR-737bef91ac404efaada5477841ae234a
ACR-aeb7f3c1ff4e41b381fd3ed5ba7f5183
ACR-ecbb39d3e3724169bbb5a4a104d08e8e
ACR-b77978eac82447f085d9de014c84db0d
ACR-0ee5e0cbd0ec4b609d52b5bf2dc314dc
ACR-474556fd5663482085b1b31edda8d4e3
ACR-fb08ed06dafe4db2bc2fd085405c30c0
ACR-3241b2a1ced34bd89eded0ad25b4826d
ACR-ab7d6c65e51f44c9a59a1ff8c325f338
ACR-633a0635eeff4ff2b679da63f68542e1
ACR-cc2cfe12242f42ed8e081be9beddea1f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.plugin;

public class DidUpdatePluginsParams {
  private final String connectionId;

  public DidUpdatePluginsParams(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
