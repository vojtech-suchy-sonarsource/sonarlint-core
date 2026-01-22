/*
ACR-b746f8f63d5a457594038e563900abd6
ACR-5bde0ec376d14d8bb42ea891800b20bb
ACR-6bc0afd2f3b24f8aaa1b907e7f873c44
ACR-76f8e9f198df466993697920ccf27bcf
ACR-6955c063320842e093349567258693dc
ACR-55c05b395c414d87b3bf9817d47911cc
ACR-e114b21f271a41c695511b8285c0aa3d
ACR-e042b11240294facbb047c313096f496
ACR-ebe9073e6a8942d088f5b029e5989531
ACR-b4813acfcdff4e9693302b322ca57157
ACR-8f6d42fa007e433088b2f180cd2c830a
ACR-dd21d40b01fa4d3999606609828c498c
ACR-52f45b18c8934e4fa6d85f19c48051ab
ACR-2b35e1d497c040669ca8ebc23462d54f
ACR-8c6105dfa1c04d80ae6d3190c4ecc850
ACR-40f95369b85740eaa2ec4b07803b1cf4
ACR-b521c77eaa9640be9169ef784be03e28
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
