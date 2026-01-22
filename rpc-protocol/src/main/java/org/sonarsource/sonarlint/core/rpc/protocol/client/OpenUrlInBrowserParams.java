/*
ACR-0f569cfcb5824103b10c51a8d67948ff
ACR-c13af09150894aa79348c5f84d142555
ACR-14c84c76f8cb4d408155559905076b6f
ACR-a87c81a85ffe475ba957a95565dfd48a
ACR-ea1d834ca08f4ed69e6fff8fd8a0648a
ACR-0f0c3e7fcb0a4c85a5ebbfe6d25003bf
ACR-c96f55945a7d421299f3c313b3a352c3
ACR-2188f5bb1e034ed7ab3caef2d1e3f438
ACR-3e9c3eebbfc0495fb3fe7a747de3d9da
ACR-648fe916c230424a9cfd446fba778e9a
ACR-4e6a4cb3f88441cc9166e71c11967a13
ACR-02374bc33bda455b88031d367374e566
ACR-0dd13183846f40a0a68fc2884325d258
ACR-e6a4aed836a84699bc213688e69b8ee6
ACR-efdfb4cd19e94c3c913c601d62985923
ACR-3beb9541ec494607a56bd493c15db2ba
ACR-4aaa89fffb8141f9898184e27c85a3ba
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client;

public class OpenUrlInBrowserParams {

  private final String url;

  public OpenUrlInBrowserParams(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
