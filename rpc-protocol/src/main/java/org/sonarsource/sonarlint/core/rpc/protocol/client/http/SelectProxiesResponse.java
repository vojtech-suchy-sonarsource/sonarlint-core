/*
ACR-efcdd4176a124e13a0ae2bb0725151e1
ACR-4557341a3afc440786484faf43d6bbda
ACR-d3cf5a45ae6b4908b213d1829635a031
ACR-bddc910393e7463080dbe7a2271cc584
ACR-cf14390a45ca44d28fa5d20ec20afafa
ACR-ace789a064ca45889ec9f13837438176
ACR-709eb2a8fc9f4eeea4c9203d685fc7c0
ACR-119b11e8974842d59d3f5663ebd46787
ACR-1a0fc1f104274278bfb4b360d0e57247
ACR-9edca8e93a1e45eea28f0d836741464a
ACR-beafb02148444e109b114a4d8a7b9d07
ACR-8fe8be7a63e849a48ac5c48020b9d64c
ACR-250ccb0e8fb74a9680624660c01ee1b5
ACR-9f32082c482c4ce581ca0786f47bc6fe
ACR-07ef6db6dd0b40de8a83dfd0881a69bc
ACR-29a9cdc2ee814349b779336e9399f05d
ACR-65f4226a5cdb42fc9a2fd2869973662e
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
