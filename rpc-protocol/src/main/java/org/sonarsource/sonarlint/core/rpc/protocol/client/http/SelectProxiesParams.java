/*
ACR-d25d4e57945849e7b6f10676dd9c36b3
ACR-532d4b3df4b5424fa46500dbf98e14f8
ACR-5d4f6582c55c412d8915ae3c305f359f
ACR-0affe727b9dd468abd8b6112e0e97e4e
ACR-605bd483282f424280635a536424cff2
ACR-0abc68fcc87145d3be895531bbf54635
ACR-9c5572d4b7dd41afa2c91d2b97207baa
ACR-719fba22fc254386b50819187a403bd1
ACR-563c1ef0948a4dfa851a61dbe4b883ce
ACR-c58425dba54b4b12b70811808dcb3737
ACR-5d6f9ea6131e43deb5ee3c28cab0263d
ACR-8239a7684bba4daba0038dbc02240b2d
ACR-19f399ef94a141fbb8bcf9ce5f1cd150
ACR-513f09baa0b34f57a6ca4c2929402155
ACR-d318038cd5dc4507827b8211978deb94
ACR-f6ecef3bb0b14c24b521a0e96491d9e4
ACR-659bd962f2a34cb0a5542a454e215164
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.net.URI;

public class SelectProxiesParams {

  private final URI uri;

  public SelectProxiesParams(URI uri) {
    this.uri = uri;
  }

  public URI getUri() {
    return uri;
  }
}
