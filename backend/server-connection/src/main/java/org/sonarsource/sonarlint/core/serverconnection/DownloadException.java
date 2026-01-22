/*
ACR-e76c40018ae848d3b1dd21a76b240c00
ACR-c2b0472d59ba4c99b8f0757fa9b06da3
ACR-87d3a051e7254227ac9c7daeaa90d385
ACR-acd47e54065b4ef680f09f025dbf8837
ACR-85a126e2ef7d4c0c8ba328a6d53ffbb7
ACR-894bd06fb0cb4aa7817bf306570006ad
ACR-7339987effc7410d805f7710487f73ea
ACR-21441e3f6b034996bc83d5315ba45f90
ACR-152782b95ca24f0ea79857ba8f7bf51c
ACR-06858d3e88ba4a58be3dbc8a2bef1390
ACR-2b9fef2f0b38494dab9f51df9f4d6444
ACR-eb70312bc48a42fabb75f770b8a21614
ACR-24649e2942524c019f5aa63ddd0d6d64
ACR-93e86a6b6b76440981eeee40da9bb9f6
ACR-be1e743f54c041cc90923366e02ca3a0
ACR-a4764922848b4f8ea7c85c767efb9ec4
ACR-5c9d2c1afa7348339f8ebb1af61734eb
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
