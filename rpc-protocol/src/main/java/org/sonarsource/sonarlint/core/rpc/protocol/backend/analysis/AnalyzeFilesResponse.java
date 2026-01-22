/*
ACR-dd9ab950dcf048e19ca379a96db9a92e
ACR-a2c55a0a92b54383a74d5b750d53f3fe
ACR-95ed3dd1fae641b4b9402f5dc9570330
ACR-4531faf826064aae87ed08eb850a67df
ACR-c5f0d975e86b4f229b9305f587d89dd6
ACR-092139a0661e4593a770dc17f68733f8
ACR-5fc4651bfd994957a1ef43d74e9cda60
ACR-965a03d050174153ac812e66323524a7
ACR-eb87711cfbdf4fac9b1acf0b2f992a88
ACR-30a8fa0aabde4e6fb9ba28b3de137dbe
ACR-1a28991042eb42b8a22b3ff88d8816a5
ACR-a701967a95fd4b5a981018316a8d3a07
ACR-03a0450b0850489ba676a778ac3d6909
ACR-6150e7f3f01547ad895fef722ee6a20a
ACR-6d1d97b6d3a4466dae92d44ad8b5a243
ACR-5aa65bbfe4084985b835f9281e2797e3
ACR-2a21df7ecde14b31a737625935ca5ce1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.net.URI;
import java.util.List;
import java.util.Set;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.RawIssueDto;

public class AnalyzeFilesResponse {
  private final Set<URI> failedAnalysisFiles;
  private final List<RawIssueDto> rawIssues;

  public AnalyzeFilesResponse(Set<URI> failedAnalysisFiles, List<RawIssueDto> rawIssues) {
    this.failedAnalysisFiles = failedAnalysisFiles;
    this.rawIssues = rawIssues;
  }

  public Set<URI> getFailedAnalysisFiles() {
    return failedAnalysisFiles;
  }

  public List<RawIssueDto> getRawIssues() {
    return rawIssues;
  }
}
