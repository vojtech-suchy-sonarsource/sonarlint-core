/*
ACR-10b41cccd2d740fe8edf1041f54beb08
ACR-9a2163caf767463db1976359097d3c91
ACR-be768d68a4ec4a49a729a88c7301b55b
ACR-92e01fc4917a4492ba7718f7ca976ad7
ACR-3f35af20e3a847d08d996b2bccd3e591
ACR-3a38e01ecc014b64810801862ac95358
ACR-d12830667f31429a862a3026d3ad6cf5
ACR-186d40781cf242c3b205d4501feea920
ACR-54279858adb043f19415bf777044700d
ACR-d1ca3284d51347caaf82f76fa01ee716
ACR-19e8d42fc0de4135ae68512b01d7a33b
ACR-9be04d0777f54feb8f3f8874072d3293
ACR-012cff4f0dd445cead22a03e141742c3
ACR-92229d71243d4b57ba36dca93bb00cbc
ACR-8ede57bc7bc54339a16aa342229d2c52
ACR-f7cf55ba5ab84a848a130ba15a591dff
ACR-5ddf86ea1e84494386ecea588272c254
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.branch;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetMatchedSonarProjectBranchResponse {
  private final String matchedSonarProjectBranch;

  public GetMatchedSonarProjectBranchResponse(@Nullable String matchedSonarProjectBranch) {
    this.matchedSonarProjectBranch = matchedSonarProjectBranch;
  }

  @CheckForNull
  public String getMatchedSonarProjectBranch() {
    return matchedSonarProjectBranch;
  }
}
