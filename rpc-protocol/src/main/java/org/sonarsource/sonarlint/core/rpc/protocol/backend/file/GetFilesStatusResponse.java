/*
ACR-3e632120875448708f0a5e4da3a6987d
ACR-e499daf439ec4b858b144eeaca87ace0
ACR-b201be0b0521406d9eb519c8d415e0f5
ACR-565f5b55cd41423b884cb95cc750c1f6
ACR-3040b51873424eb59999ec2670903e6e
ACR-6233b4063f3d4b7da61255cd440e7689
ACR-2ea8423dd664494d9536003b54c0d4d2
ACR-3a277fc90ce94e45a3edefc33a8414f4
ACR-800e7833a9694eb8b377fa4e913e742c
ACR-1012ecace6ce440a83bdc0be69a6b253
ACR-491ed864a49b4b0196cba96752866ff2
ACR-e853f3de5c744c8f9b093501cfd71b09
ACR-b7599932c2424c09b835f0fb82da92af
ACR-cea3711193e8416f9bdff3a259accd82
ACR-f564762ec2064faea90ee20ec36693e4
ACR-07f888d08b3d4328b45f54bff86d198d
ACR-8446ccf7cdf44fe48aa73187fc47f8a6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.net.URI;
import java.util.Map;

public class GetFilesStatusResponse {

  private final Map<URI, FileStatusDto> fileStatuses;

  public GetFilesStatusResponse(Map<URI, FileStatusDto> fileStatuses) {
    this.fileStatuses = fileStatuses;
  }

  public Map<URI, FileStatusDto> getFileStatuses() {
    return fileStatuses;
  }
}
