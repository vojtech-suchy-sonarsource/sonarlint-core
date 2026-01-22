/*
ACR-3130f575731f4d08ac35d8bfab686e76
ACR-b3d38674e936440780ca403622ba5852
ACR-187a9922712a46f486cdec319cd58717
ACR-e1f9036109104bc0a88e188d80f828d1
ACR-c1d3baa731104ce2afd3e8e0dcee547a
ACR-877f51703cd34452b64aceecba99cf16
ACR-29313885b485406089320946af3999f2
ACR-7580ab0bcd5b40f2adec2ea437ba85f9
ACR-62685932e4a2429cb337973172d50b39
ACR-c111dbd3997048b3a36fb712bf220c86
ACR-5349c2ef0b0140d18c9ab7a2f0cee8b3
ACR-7b53cec34d6f4afba0a09366b82fd2aa
ACR-63c50e75bc8d4ccc8b4d2cb68a3d16a9
ACR-3d8e78ec3b2249ca88e30b452c24dcb3
ACR-5bed6101dc3b4449ade00e4894ed7485
ACR-a1378845a525471da3b068382a9a4ddd
ACR-282ae3245e4246ca867647730dd50a88
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.serverconnection.issues.FileLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.LineLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.RangeLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;

public class ServerIssueFixtures {
  public static LineLevelServerIssue aBatchServerIssue() {
    return new LineLevelServerIssue(
      "key",
      true,
      IssueStatus.WONT_FIX,
      "repo:key",
      "message",
      "hash",
      Path.of("file/path"),
      Instant.now(),
      IssueSeverity.MINOR,
      RuleType.BUG,
      1,
      Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH));
  }

  public static FileLevelServerIssue aFileLevelServerIssue() {
    return new FileLevelServerIssue(
      "key",
      true,
      IssueStatus.WONT_FIX,
      "repo:key",
      "message",
      Path.of("file/path"),
      Instant.now(),
      IssueSeverity.MINOR,
      RuleType.BUG,
      Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH));
  }

  public static RangeLevelServerIssue aServerIssue() {
    return new RangeLevelServerIssue(
      "key",
      true,
      IssueStatus.WONT_FIX,
      "repo:key",
      "message",
      Path.of("file/path"),
      Instant.now(),
      IssueSeverity.MINOR,
      RuleType.BUG,
      new TextRangeWithHash(1, 2, 3, 4, "ab12"),
      Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH));
  }

  public static ServerTaintIssue aServerTaintIssue() {
    return new ServerTaintIssue(
      UUID.randomUUID(),
      "key",
      false,
      null,
      "repo:key",
      "message",
      Path.of("file/path"),
      Instant.now(),
      IssueSeverity.MINOR,
      RuleType.VULNERABILITY,
      new TextRangeWithHash(1, 2, 3, 4, "ab12"), "context",
      CleanCodeAttribute.TRUSTWORTHY, Map.of(SoftwareQuality.SECURITY, ImpactSeverity.HIGH),
      List.of(aServerTaintIssueFlow()));
  }

  public static ServerDependencyRisk aServerDependencyRisk() {
    return new ServerDependencyRisk(
      UUID.randomUUID(),
      ServerDependencyRisk.Type.VULNERABILITY,
      ServerDependencyRisk.Severity.HIGH,
      ServerDependencyRisk.SoftwareQuality.SECURITY,
      ServerDependencyRisk.Status.OPEN,
      "com.example.vulnerable",
      "1.0.0",
      "CVE-1234",
      "7.5",
      List.of(
        ServerDependencyRisk.Transition.CONFIRM,
        ServerDependencyRisk.Transition.REOPEN));
  }

  private static ServerTaintIssue.Flow aServerTaintIssueFlow() {
    return new ServerTaintIssue.Flow(List.of(aServerTaintIssueFlowLocation()));
  }

  private static ServerTaintIssue.ServerIssueLocation aServerTaintIssueFlowLocation() {
    return new ServerTaintIssue.ServerIssueLocation(
      Path.of("file/path"),
      new TextRangeWithHash(5, 6, 7, 8, "rangeHash"),
      "message");
  }
}
