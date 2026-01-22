/*
ACR-5bfb8bfc459b4bd0805596062c82a723
ACR-b13cd43a58cd45439b91f5bdaaa9bb5f
ACR-74baaa92bdb448a58da19b845ab5e754
ACR-3a9b74228d6c45aba4f7f108370387e6
ACR-159ac198e9ef47a1a2b026752f975d3d
ACR-f74bbb2064834cf18c62992f9818afa4
ACR-dda9e65456ff4b26a21e04ef44d92f35
ACR-aa3a3cd48da547ac916453299632db0d
ACR-bff0a37c968240e9a56864e1493123f3
ACR-bb92c0f53baf47d299494a72ef371bf8
ACR-61176a367544481c9620ff9b630a9074
ACR-3ac044fbd489495f9fc4a859901d4e53
ACR-0559a4789cfe41529decb73d5a0e2a79
ACR-4980c8298b064189b87fcb6fcfb32c01
ACR-44301d4b8fab41ca94fe280601bbf99e
ACR-d6b7310b3f974a67885c2ed2db7e872f
ACR-114a473cb63745e98ff15f0e3b8e542a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class DidChangeClientNodeJsPathParams {

  private final Path clientNodeJsPath;

  public DidChangeClientNodeJsPathParams(@Nullable Path clientNodeJsPath) {
    this.clientNodeJsPath = clientNodeJsPath;
  }

  @CheckForNull
  public Path getClientNodeJsPath() {
    return clientNodeJsPath;
  }
}
