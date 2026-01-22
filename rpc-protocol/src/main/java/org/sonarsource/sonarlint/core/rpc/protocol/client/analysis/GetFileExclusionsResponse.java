/*
ACR-7b70558130524ccdaecddb5b0641d475
ACR-a97a286b4ecc4eaebd4d67adae42b303
ACR-3e59d3cfc216491dbbce62c6a5c7642b
ACR-6aafa76677f64fcebdb67b9d7316fbf5
ACR-6a628a99698a4a6faaa5891975044528
ACR-a12f78547d434b7bb8fb378993a09a3e
ACR-37e3cedb70444053b053b1f1d3e00caf
ACR-9d0f686043b3465eaac92d80513304ec
ACR-a4c197461c014f13850eb638e2d23038
ACR-ae050425d92b43679f84c77c60e5a921
ACR-7ef82e204f604cbeb121241eb8a4d2d2
ACR-adb23959d19a44148aedde229da031cc
ACR-9d2770dc52f9476fa00338d39109c1f4
ACR-e05ee890bae64e569854e6830d4f3d8a
ACR-a9e0ae8a35e642e3b684850e3e17acd7
ACR-da9ef7ec9a8947e4b982198645ad645c
ACR-8cbf701acbef425c811e59fddfc87410
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.util.Set;

public class GetFileExclusionsResponse {
  private final Set<String> fileExclusionPatterns;

  public GetFileExclusionsResponse(Set<String> fileExclusionPatterns) {
    this.fileExclusionPatterns = fileExclusionPatterns;
  }

  public Set<String> getFileExclusionPatterns() {
    return fileExclusionPatterns;
  }
}
