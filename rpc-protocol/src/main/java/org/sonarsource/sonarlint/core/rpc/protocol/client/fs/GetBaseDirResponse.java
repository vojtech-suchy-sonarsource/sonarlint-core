/*
ACR-625f5e57dcab486aafd2cf9ccc78af43
ACR-538cc074f0284ed6b1e85a623852c5e7
ACR-3041f15b921840af8185f187aa3718f6
ACR-47e2c41d7c50433381362c8fa06dfb31
ACR-f79c4c26e6184967a117d249c2bc2b73
ACR-7d74e5d719c0485a8e10d3ad00e54d99
ACR-d8a5b11f342a478e9d07e6d360b34e2f
ACR-18c5883ca929452aa2b6adc67a327af0
ACR-42201ec449f44bc1982699b4363ba3f2
ACR-574be701f04c4a9b9a80b7cd6b49dd71
ACR-3aa652f166314191a4ae0974e431c3f5
ACR-b03717cee0e24cef8a9584bc314254ad
ACR-339fca15901945a9afbda712901ab287
ACR-a14773813a864208bbe9d45c0c232785
ACR-d9d3369c91a340ebb73904720bbc93e4
ACR-e6e428dea8cb4b4f9c177911629e031a
ACR-50ef817ddb04473ea4d5c9af17028451
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fs;

import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetBaseDirResponse {
  private final Path baseDir;

  public GetBaseDirResponse(@Nullable Path baseDir) {
    this.baseDir = baseDir;
  }

  @CheckForNull
  public Path getBaseDir() {
    return baseDir;
  }
}
