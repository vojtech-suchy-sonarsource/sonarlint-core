/*
ACR-f9f3c406835e405a9b23f7a8ed852311
ACR-cd5d26635f1b495bbd21473e2397335c
ACR-24ecb288223944749bb02e8391863c1f
ACR-4e62d774b9c04b098b2782bde880f0c2
ACR-98aeb4ac218d488dbfb069416d939d8d
ACR-1378e8361fd248f08da9f364cf902794
ACR-82b20e390091458898f90dc4ebd7c0d3
ACR-bdb073268d4d4837bdfb9dd3fda7770a
ACR-72ebc86056af474488ab0209acd040e4
ACR-cf83a2ba2ffa4ac9878a5fb5e63707e5
ACR-b81e2893a5e54f7bb3a080f5dbbc98c5
ACR-4ea84b656aa54e9e8f376074cca76378
ACR-d24e12163eab4e979988202dcd35a5fe
ACR-6c221b6deb5f463095f75bc7cb614b23
ACR-8c1baf8969e64757beeb4914897772c0
ACR-1e640d5079e94a20aa30bf8b8e6f66b4
ACR-69d5f7600d2640c2980cfa34f195e7f2
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
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

/*ACR-26c3966797c245c5a4014d5fcac1ec64
ACR-31de8015d009430eb04ee998591b0c49
 */
public class RangeLevelServerIssue extends ServerIssue<RangeLevelServerIssue> {
  private TextRangeWithHash textRange;

  public RangeLevelServerIssue(@Nullable UUID id, String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate,
    @Nullable IssueSeverity userSeverity, RuleType type, TextRangeWithHash textRange, Map<SoftwareQuality, ImpactSeverity> impacts) {
    super(id, key, resolved, resolutionStatus, ruleKey, message, filePath, creationDate, userSeverity, type, impacts);
    this.textRange = textRange;
  }

  /*ACR-f863a36235084899bab5267cdd20f918
ACR-a6f5d5486000459facdbd2ba0992dc13
ACR-67bfa08babc24696a3d7d1ba193da8fe
   */
  public RangeLevelServerIssue(String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate,
    @Nullable IssueSeverity userSeverity, RuleType type, TextRangeWithHash textRange, Map<SoftwareQuality, ImpactSeverity> impacts) {
    this(null, key, resolved, resolutionStatus, ruleKey, message, filePath, creationDate, userSeverity, type, textRange, impacts);
  }

  public TextRangeWithHash getTextRange() {
    return textRange;
  }

  public RangeLevelServerIssue setTextRange(TextRangeWithHash textRange) {
    this.textRange = textRange;
    return this;
  }

}
