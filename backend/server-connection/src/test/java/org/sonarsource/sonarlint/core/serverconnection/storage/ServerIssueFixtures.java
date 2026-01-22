/*
ACR-0ea00ec2826c4b9b993363a81f520bfd
ACR-edc7ca4dfbb0440694279a63f3416bba
ACR-89cea746f64b4045bad235d4afcb4572
ACR-43be7bec07c8404eb183ed3e566b185d
ACR-ec76a5fa63b54b94901aa5c276acc8f0
ACR-5681180ade4f4323b65eff3932da1085
ACR-2d97392bb6ad4ccc9f282780e7139611
ACR-acb7198e324d488698ea849f73223edc
ACR-5a0eb09a376c48278c5d2a3d9d0c3007
ACR-01d68a2bc04a44a58d28c77ebe12a4f6
ACR-f975bc15f2134262a133bdfcca3aea0e
ACR-09f081c4b2044cdbafe0703d26e74ec7
ACR-8a5a3c82f95e44d99d3aa41611c67f77
ACR-747daef3b7a342018b2361b78b0f1f6e
ACR-b04354e3c4ab483e8b9efa40d6bdb5d7
ACR-0a792356030e481883abdb786dbc44f6
ACR-3ef91a4259364a75afd436747693e385
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
