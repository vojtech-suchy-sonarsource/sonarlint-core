/*
ACR-dde26aa434884af781e8295bde11db85
ACR-b34d458ac952473ba5fc340c6f4dec57
ACR-eeb9624a9ffe42fbb895ecc744f04dd3
ACR-c1f7c9b23a784877a70970e265a45b2e
ACR-b510c7e445404debbfca17550274b784
ACR-9862323fedb143b38b6bbe2304f3e18e
ACR-f0db506f7c9448fa89959f7fce28b545
ACR-1b64a496c0604efea9d77c6103022995
ACR-df83e6bedc2a4356b7773a9f03d6f29a
ACR-9483b4325f3045fdb3e6b5f0ae7f5b6c
ACR-1688348af3b747ef909e9967ffe159ca
ACR-c7f8c1c2f2944044a3416a0a02574504
ACR-cc2262e6804e4ba38517bc95c9b0224a
ACR-71d1742a9d744a42beaf408bb90232a7
ACR-fcb74d3fedd647e58e64216d2ca027da
ACR-29ebd22ee1b2429eb3f6b0e9e5dbae83
ACR-36fd1b39570b4b36a3d56f07a524222e
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
