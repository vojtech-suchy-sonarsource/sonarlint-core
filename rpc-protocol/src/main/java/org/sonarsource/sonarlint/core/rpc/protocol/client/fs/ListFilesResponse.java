/*
ACR-b71564e1fe394256ad45af19d7aa142a
ACR-cbe24df38d3a441c9496b94e9b853216
ACR-268f3cf989044d08b4cedc14619c60e9
ACR-2625aef519b44d54851c5b3c07b2d1f8
ACR-7dc08854c631418b96a31fa02025d144
ACR-1b6e1ec446654fef86e170bae067f455
ACR-f61ad9f17b434106b91e7c3ed3007cda
ACR-a8719739aaac4e90a490048904ee477d
ACR-35dac743fed041bdb8c81d73c43b7709
ACR-065d69dcf20f4d80acaf576fbee66791
ACR-267bd0604ea04636bb847393355462c2
ACR-01052304d31e41ddb3da6efc964251bc
ACR-3619c69c86c244ef86c7eb8d6ce72ea8
ACR-081666222cd24739a2278e2bba06ff03
ACR-f64d633ba1e5497f85550be57dfc15f9
ACR-3691c5b0878449f9b44e6d14b1062bd3
ACR-416b73fb333146a088b8a12833afb3bb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fs;

import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;

public class ListFilesResponse {

  private final List<ClientFileDto> files;

  public ListFilesResponse(List<ClientFileDto> files) {
    this.files = files;
  }

  public List<ClientFileDto> getFiles() {
    return files;
  }
}
