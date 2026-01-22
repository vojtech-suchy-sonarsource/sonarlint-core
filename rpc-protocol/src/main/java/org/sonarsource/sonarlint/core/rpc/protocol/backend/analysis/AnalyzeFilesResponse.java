/*
ACR-02a2d0bc93464286b232a2a894b6a0d3
ACR-b6cde8117ca74bb18cb97cb572015245
ACR-03efdb05ba154b9182768af9d9395100
ACR-abbd28978e8241b8a5aef986e8d6c18c
ACR-8da698f0138a4841b1f841656ca62524
ACR-cc18213ff9b140179f76afd740a2a6dc
ACR-0397318ee84c46fd92849a5f5ff161e5
ACR-7186ae9cae4b4025a2bbfa8c03761998
ACR-f43702aad1f143d79200b0348dc18d67
ACR-5f7262cc83574c21a95214757eab7226
ACR-94352cdb9dd0412d88cbc5db492cb64a
ACR-8791006d6e83419aa79657b8d120050b
ACR-08cdbb52c99b4967bc97762b103eca54
ACR-7d21c3e4290a4771bef08bf38aa7501f
ACR-8786b89590e448cca12dcf6decb76df1
ACR-7dd0d965f98b48d0b2431d46624b087b
ACR-47508576ab5f4ad6be3df48e9c258fcb
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
