/*
ACR-d3a0fdb767114932b6775bcbb57bea33
ACR-ba3c981961504ff9bd98f8f44651063e
ACR-8032c40fbf74486a96d541f7bc733f78
ACR-034304cfb37b4f149a43ed43c2a06dc9
ACR-62f3edfa143c40318f95151b044d684a
ACR-180e2cba6df04fe692904ad10c07c3be
ACR-72bf131a82b14a34824d538642a91f24
ACR-af5e67cad15649768cf5856caebfc064
ACR-20470c7a7cb0465abdc0cefbe3bda876
ACR-ddb96e81894043928bb099d8b426c786
ACR-d48a1788e88c42f8af80ccf2a00e473b
ACR-60cbe1a5826b475c95ad72ee8231ed65
ACR-358cd318449e4faab67676057639aa53
ACR-746441e3d16d4399b05997cbae82cdf2
ACR-9cb550db1f2048dcbac08c7aaa8106a5
ACR-9522943f4ffe467a9498059530fcc0cd
ACR-eb100cf6483344e882757c14093f1aa4
 */
package org.sonarsource.sonarlint.core.tracking;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;

public class LocalOnlyIssueRepository {
  private final Map<Path, List<LocalOnlyIssue>> localOnlyIssuesByRelativePath = new ConcurrentHashMap<>();

  public void save(Path serverRelativePath, List<LocalOnlyIssue> localOnlyIssues) {
    localOnlyIssuesByRelativePath.put(serverRelativePath, localOnlyIssues);
  }

  public Optional<LocalOnlyIssue> findByKey(UUID localOnlyIssueKey) {
    return localOnlyIssuesByRelativePath.values().stream().flatMap(List::stream).filter(issue -> issue.getId().equals(localOnlyIssueKey)).findFirst();
  }
}
