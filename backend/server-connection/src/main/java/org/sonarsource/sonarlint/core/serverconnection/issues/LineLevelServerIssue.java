/*
ACR-c19f3273827642eebea62873478b9f8d
ACR-4639b626b8634aaf9919a2d48929336b
ACR-ee424c4382794b078257f0a5f4c41309
ACR-0d45072c22ba41cbaf6c23afcca2dfa1
ACR-a9fc63b24fb44baea4f804d6e5650343
ACR-0ba7c211983e4e05aab6804b1810467a
ACR-af5aa3d7387543e8a9a5675951f90509
ACR-e5993547688143d083c1ef9001916922
ACR-102a76aa6f824b98ae24c4d4a3bd10cf
ACR-cb295e87cfca4a89b79a3c78b4d46eb4
ACR-a7ca31778f5c490493eb0b510b1253ce
ACR-96e606dcc5a44a62b3d46b8581560486
ACR-bac0271305b549118a2dc04b637e236f
ACR-377012f2e51b4e7f9a58913c1a240f72
ACR-ed21eeabba8b400cadbcbf1f3201da6b
ACR-88262b64a60d4cb78f7411dc2603c2ca
ACR-9f3538435d864c04beff6e206f1b355a
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

/*ACR-20910e3ae7ea4cda93480feb2891cb35
ACR-b3787cf529b846699af0460bb050b526
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

  /*ACR-ba2dd2e725b7498197d0fc1cabfd5fc1
ACR-6444e373e4df43188fc155be52382c3d
ACR-021f5c8c2de449989a0507461a18706b
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
