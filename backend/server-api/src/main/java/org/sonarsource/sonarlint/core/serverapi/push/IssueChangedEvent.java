/*
ACR-76dabcd626f6486c9728d93a91b78ff2
ACR-50a31611b3224cf7a97c0b05a53ce5ff
ACR-a0bae9dde0de491b8abb5d7945160d18
ACR-5f20f7f639804751a72469e21c15ce75
ACR-80e3c078e5e04e5cb54976d974a09b51
ACR-c355c2c0f1df419d893834aefc325eb8
ACR-f045e1071c9149bb87bc2c374712c427
ACR-8efd9b49b3a24b03b9ee6bf5ce02937b
ACR-e98e30dfc4ca48f1bd184bb537de6eb6
ACR-965b653d1e4048a9a27e2426926c6dd9
ACR-fe7dd3792ff34404884c37b9fd8d931f
ACR-ef09d8663c354161846dfef31f757724
ACR-ca7010527e5e4cf0b9ebebf3dde15d9f
ACR-d34b83c6c5364cd18336cc3c1816c3e5
ACR-0353b78780b74a36bb7a81fb65e3f75d
ACR-390580fb7c434c97b1856ab1d73276fb
ACR-b3be23e6a27847349f7615053444bb5c
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

  /*ACR-fadcd14b337648519a3727f3627e8529
ACR-bbf02e70c5944fa88aba13746069ada5
   */
  @CheckForNull
  public IssueSeverity getUserSeverity() {
    return userSeverity;
  }

  /*ACR-408d016efb524ada9a4203907958e5b2
ACR-c99f55ea19d644dfbbc25d3cd83464be
   */
  @CheckForNull
  public RuleType getUserType() {
    return userType;
  }

  /*ACR-52112c1b7a7a44c0b93a135b53a28644
ACR-7f63f622eca04dc684229ff7cd1e666f
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
