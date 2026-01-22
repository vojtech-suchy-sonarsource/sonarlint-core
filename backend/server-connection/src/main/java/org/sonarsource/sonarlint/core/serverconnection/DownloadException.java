/*
ACR-d7ca4b9419e74c518f7d88640ad17e9f
ACR-ddfea84a96224de3aba464c42e482e58
ACR-5628e743ac8b40ffa78c416924f572df
ACR-ffefb2e296a04719a5c2349cc61ef9b5
ACR-1466ebc6d5f742a3a85ecf1f3d636722
ACR-8997586ebfa745aca647fdcc860d841c
ACR-dd49c91b1bc043ec86b3e4990d569e3a
ACR-71468284cd084a7a8eb2593c4837d0fd
ACR-377bcaa8a3674e759650ab54afaaaa58
ACR-b13715cc023249ffb1cc4354ff13fb8d
ACR-2065a87dda344ad48a227e2369a46661
ACR-f8d8b8131c1240cf92d816d69d0e3de2
ACR-e8c7eb3ffb53423190a9af85006e5ca7
ACR-3ec993f2be114c59a4053ec2625d0995
ACR-331e2a6243224dd3925dcb89b5ba0c6c
ACR-6e64f133fc8348be9671cc092b3e378a
ACR-571b4d63bebd4b769ca3cef1c6fe3e46
 */
package org.sonarsource.sonarlint.core.serverconnection;

import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.SonarLintException;

public class DownloadException extends SonarLintException {
  public DownloadException() {
    super();
  }

  public DownloadException(String msg, @Nullable Throwable cause) {
    super(msg, cause);
  }
}
