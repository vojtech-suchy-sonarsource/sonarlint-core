/*
ACR-04d72b97096148f9826c37ea1376dafa
ACR-f8e20c8fbee84845901e622de703c3f3
ACR-006bf77eebd44c1e9f806c7560d5f89a
ACR-1f40e53ac5734fc898df11a7ea4799e2
ACR-3f99cf27c55c49d09fd9c55867d035ba
ACR-4cfb88fe513947a4b34b90b46f08a524
ACR-f3e5a0183dc5424fac2c84a478d77d35
ACR-14193c74c49e411d8d753ff72d0e63ee
ACR-c9a54311d4ad4a9285688e9abb15b1a1
ACR-a6f4189bbcef432394902b268d2d2626
ACR-d36483ebbc83469fa1c04803693a9417
ACR-4f697e7559d040bba44d1bd83e2b5faa
ACR-9713854b57c34ad39a6272e7b213a33c
ACR-23bf6f01846149d89b91698f000701da
ACR-65adc5e6017a48f9a1d620df12821f49
ACR-9366ae486f3a4a4bbea091aa40b98b12
ACR-b31da43bc86248db8312bb5de84b64d7
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
