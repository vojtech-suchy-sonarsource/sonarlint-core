/*
ACR-1748129710e845ec814cc3ee7b1012d2
ACR-c75ff64d53764d5fa7e6f43e2898ddc1
ACR-311ebb2f8c8249f5870d266dbb651eb8
ACR-b495ba92a9a4485cb4489293075a8b0a
ACR-14fbabe335954e3a85e0b729fa5343dd
ACR-c1012fab828c4c0cb0916ae045c44812
ACR-eb3cef4fa3bb45a89f41d61564dcecf1
ACR-d05b9aabe71b49f69f0f9cb53730842d
ACR-373a455bc128433bafec267893e0bac3
ACR-5a4ec70c62d54217991fbc5d9c856660
ACR-a3da6e2debd04ab4a49450cb7f83e5be
ACR-da6f4e244dc9418a8aa48fccae576d46
ACR-caebbe6425c040b4b9f6843251b5dbb5
ACR-2018b4ac9cac407f9fb207b18368d687
ACR-6dbbfa9baf8648b2bf7a52200f9ff89f
ACR-ddb995dab53840da9234deb4de1dede1
ACR-9a78c02e3a4a47a79bbed2abcffcd5af
 */
package org.sonarsource.sonarlint.core.serverapi.push;

import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;

public class IssueChangedEvent implements SonarProjectEvent {
  private final String projectKey;
  private final List<Issue> impactedIssues;
  private final IssueSeverity userSeverity;
  private final RuleType userType;
  private final Boolean resolved;

  public IssueChangedEvent(String projectKey, List<Issue> impactedIssues, @Nullable IssueSeverity userSeverity, @Nullable RuleType userType, @Nullable Boolean resolved) {
    this.projectKey = projectKey;
    this.impactedIssues = impactedIssues;
    this.userSeverity = userSeverity;
    this.userType = userType;
    this.resolved = resolved;
  }

  @Override
  public String getProjectKey() {
    return projectKey;
  }

  public List<Issue> getImpactedIssues() {
    return impactedIssues;
  }

  /*ACR-11a5b5e4997b48019dfcc55008fd6829
ACR-9aa1dcdc05094fc28ab85d0c81e72e20
   */
  @CheckForNull
  public IssueSeverity getUserSeverity() {
    return userSeverity;
  }

  /*ACR-5d6b4feea0b74f90bedbdaf80dcaed51
ACR-67c63ce224e941149023d32a0310bff1
   */
  @CheckForNull
  public RuleType getUserType() {
    return userType;
  }

  /*ACR-7b958c4e3d1d48099d1b1529d773fe39
ACR-e18ccffc049a44a29fdf376935c0b67a
   */
  @CheckForNull
  public Boolean getResolved() {
    return resolved;
  }

  public static class Issue {
    private final String issueKey;
    private final String branchName;
    private final Map<SoftwareQuality, ImpactSeverity> impacts;

    public Issue(String issueKey, String branchName, Map<SoftwareQuality, ImpactSeverity> impacts) {
      this.issueKey = issueKey;
      this.branchName = branchName;
      this.impacts = impacts;
    }

    public String getIssueKey() {
      return issueKey;
    }

    public String getBranchName() {
      return branchName;
    }

    public Map<SoftwareQuality, ImpactSeverity> getImpacts() {
      return impacts;
    }
  }
}
