/*
ACR-5f93719ae149414e9ad4dc8022dbfbe6
ACR-ad04494827e3401db1a4a11ef586de81
ACR-9cbcdf94ab744ccabbddfea12fc393e5
ACR-5ce45e7d8196409d8e65deeccf3859b8
ACR-0568104e263e4c40a3227f2ccb8ba1cc
ACR-828da2a63de9484280bafe7808291b45
ACR-1caa3af35756410abdbde24b38c4892f
ACR-3f0ac552e2944ba48cbf278cdcec8c6b
ACR-e7319806cb844357b515c6c8e2d3e64c
ACR-2465606afbd6473889e9d31404162321
ACR-709b0128a0e44f8c962975971e41ad29
ACR-99ee764041da40619da1b5875b2eb1fa
ACR-e909b8d1e2e74f3fa96b93b9572fc4ea
ACR-a7c6fe061f684008986bacff6a1fab80
ACR-e9d4a94b78c84df58e5d4691d95a2848
ACR-b70c6f28f4724c51a81bb9821c85cf4b
ACR-f500e9fef6694ae88e34be070bc3528e
 */
package org.sonarsource.sonarlint.core.remediation.aicodefix;

import org.sonarsource.sonarlint.core.repository.reporting.RaisedIssue;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityDto;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixSettings;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;
import org.sonarsource.sonarlint.core.tracking.TrackedIssue;

public record AiCodeFixFeature(AiCodeFixSettings settings) {
  public boolean isFixable(TrackedIssue issue) {
    return settings.supportedRules().contains(issue.getRuleKey()) && issue.getTextRangeWithHash() != null;
  }

  public boolean isFixable(RaisedIssue issue) {
    return settings.supportedRules().contains(issue.issueDto().getRuleKey()) && issue.issueDto().getTextRange() != null;
  }

  public boolean isFixable(ServerTaintIssue serverTaintIssue) {
    return settings.supportedRules().contains(serverTaintIssue.getRuleKey()) && serverTaintIssue.getTextRange() != null;
  }

  public boolean isFixable(TaintVulnerabilityDto taintDto) {
    return settings.supportedRules().contains(taintDto.getRuleKey()) && taintDto.getTextRange() != null;
  }
}
