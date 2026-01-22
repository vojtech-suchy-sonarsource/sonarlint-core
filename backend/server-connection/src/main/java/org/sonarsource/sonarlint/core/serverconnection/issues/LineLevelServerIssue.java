/*
ACR-4c55cf9a3a5a42c7ad4f1ec8240a7105
ACR-db30c588e0ae41f7a29e56cbf7671d74
ACR-7cf74204b395445086347174e12e8eac
ACR-0f909d8d6c46452c8fd436fe374d6a98
ACR-44eea791043642bbb5719f1cd5bec5c0
ACR-8ca54834c16f4a7296d51374b98b31ca
ACR-4048000c62f54ed6b2a8166137f09aa1
ACR-a23bdf455342478c8e7e369ace12ce5c
ACR-36cf9bd7e7bf42f296a835bbd96f28c9
ACR-77da1abd022a4763a907c057ac2105a1
ACR-3acd9d150ae644388a09eb0f740aa91c
ACR-83fbc08fbc2b422c8ada250201c6fb2e
ACR-671329d5ac5f4bc4a52d186455014d35
ACR-78c4a75c8dbf46f2be6305c76d654873
ACR-8e9b4d09d0a34f54845929aa5c2eb268
ACR-61aa6fd89d084adb934f2ba797dda577
ACR-b843f80ff1b54e7aa5be20d234383d8b
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;

/*ACR-c808781508b145de93ff61d8fbb60f52
ACR-413dc1b9ade4481eba45516c0985ecc8
 */
public class LineLevelServerIssue extends ServerIssue<LineLevelServerIssue> {
  private int line;
  private String lineHash;

  public LineLevelServerIssue(@Nullable UUID id, String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, String lineHash, Path filePath, Instant creationDate,
    @Nullable IssueSeverity userSeverity, RuleType type, int line, Map<SoftwareQuality, ImpactSeverity> impacts) {
    super(id, key, resolved, resolutionStatus, ruleKey, message, filePath, creationDate, userSeverity, type, impacts);
    this.lineHash = lineHash;
    this.line = line;
  }

  /*ACR-57e407a7be754f50a0193d04877476fd
ACR-73125a876e284cc4bb406c4451073aaa
ACR-fa266c42a1564232b6bcead852ce83af
   */
  public LineLevelServerIssue(String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, String lineHash, Path filePath, Instant creationDate,
    @Nullable IssueSeverity userSeverity, RuleType type, int line, Map<SoftwareQuality, ImpactSeverity> impacts) {
    this(null, key, resolved, resolutionStatus, ruleKey, message, lineHash, filePath, creationDate, userSeverity, type, line, impacts);
  }

  public String getLineHash() {
    return lineHash;
  }

  public Integer getLine() {
    return line;
  }

  public LineLevelServerIssue setLineHash(String lineHash) {
    this.lineHash = lineHash;
    return this;
  }

  public LineLevelServerIssue setLine(int line) {
    this.line = line;
    return this;
  }

}
