/*
ACR-1c7fa95a27de4ae1b3cceaebad159ac8
ACR-5f2acdb7eb1841399d6eefa948dd6523
ACR-e1e910e0465f4664a06178f11ba157fc
ACR-4e22e363102c4177a93dff63b4dc1dc7
ACR-d847357e686a4f9097b1d52d24181498
ACR-dd27119f933243cfa5ee5cf5e7ccb859
ACR-06463ed5875a4e75bf5c52af450f283d
ACR-a99b1e77d0ba4bee94357c39d2e89b24
ACR-017942bb50ac44d98452df45cf055429
ACR-031ac4696626457aa5f1f21ec035b8b1
ACR-965432a2505f45a0be620be1f9b23bef
ACR-0ffd5be41795492299afadb9a0b9713b
ACR-4d34ba1e5ea74f89b4d73ed5cd72b9cd
ACR-d3ecafcaf4e8483a8075c710755dd163
ACR-ae5805e37d584f5b8c87002c40b1b59c
ACR-e7d71432283149a9bafa90d14a6818ea
ACR-ff6d197d1bc346e09fc033240d0205a7
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

/*ACR-ed1cec0ca7f0422a8d50bfc0db734366
ACR-37f62e955b684c57b1ea95793032e2fc
 */
public class FileLevelServerIssue extends ServerIssue<FileLevelServerIssue> {

  public FileLevelServerIssue(@Nullable UUID id, String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate, @Nullable IssueSeverity userSeverity,
    RuleType type, Map<SoftwareQuality, ImpactSeverity> impacts) {
    super(id, key, resolved, resolutionStatus, ruleKey, message, filePath, creationDate, userSeverity, type, impacts);
  }

  /*ACR-1de176f519cf479ba9303198bd3d150c
ACR-b87b5c09a9d14e5aaf9686df0ac9db21
ACR-feaab86e90d240f080769e4212fb3cae
   */
  public FileLevelServerIssue(String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate, @Nullable IssueSeverity userSeverity,
    RuleType type, Map<SoftwareQuality, ImpactSeverity> impacts) {
    this(null, key, resolved, resolutionStatus, ruleKey, message, filePath, creationDate, userSeverity, type, impacts);
  }
}
