/*
ACR-93d19b66b7fc486e9f872c20b08544eb
ACR-68ed2a5f33a3423db4d96c0b96e370f7
ACR-693044833e134b50a74e40ca8780aff7
ACR-3a47c3e23cd64aa091356f7b0311beb5
ACR-c342bec301ed48d895ebbd58e1db2f9e
ACR-b6cf393034594aab84e99ea7a37f2b4d
ACR-0e3852af335c4463b9a783d8034f54aa
ACR-b1740e48918c4ec9a3dffde410f694ae
ACR-8ad0fe45069549d3af4911ac08ba15da
ACR-f0f2d5237a844001aabcd3639d7168af
ACR-60fccc27ea654c1686fbdcf63e009924
ACR-d060e04631e94be4ba44492c278cad52
ACR-13662d8d43054f029706cbefa094723f
ACR-2a5d9b401cb74613a45fb6bc4df7e474
ACR-533596d9af4a4ebf97611ec67c43a1cf
ACR-77b2239c701f40dcb61ac992c79ff04e
ACR-f3fc3624cc7b4eed9700a67385bf82fa
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class ForceAnalyzeResponse {
  UUID analysisId;

  public ForceAnalyzeResponse(@Nullable UUID analysisId) {
    this.analysisId = analysisId;
  }

  @CheckForNull
  public UUID getAnalysisId() {
    return analysisId;
  }
}
