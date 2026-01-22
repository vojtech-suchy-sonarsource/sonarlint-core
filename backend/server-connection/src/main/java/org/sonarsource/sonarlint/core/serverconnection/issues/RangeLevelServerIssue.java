/*
ACR-a65ef833ee194c79bc831a6a8ab03ca8
ACR-0c8086f98681415d85965fbae0482bcc
ACR-a85101ea67c9477aa349edc0eb24df5d
ACR-1b86db0eb15c4e80896fdf68e39da15c
ACR-384b7d4219a4439f97b0b8ff0e85f5bf
ACR-da5b9b8a7cfd41c7af463e21bec9411b
ACR-0c9299b8ce23491888ca17a7fe6469d1
ACR-0887c461afd14f7d8593a47ab1602bae
ACR-46304a5533214947bfd13c80e7044284
ACR-444155753c4643848d3dee3b459d290b
ACR-a282d0b9460048ebb0f9afe8a3a34c9a
ACR-bc6d0a66e8d04ed8a08e1b22365618a6
ACR-28f9f28a08934cbaabca09a041e2c072
ACR-5230a8833e37474ab362a22394ce2801
ACR-1c27251dbee344b4b090af8bbd2ce2de
ACR-b16c74d25bda47b8b8f4a4064f3f6cc8
ACR-68a374ad845e4702833611e783d08758
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

/*ACR-1580081437a0443cbd020c7f4e5bb117
ACR-6298659b4e4f4518b8994d16a3ae07b9
 */
public class RangeLevelServerIssue extends ServerIssue<RangeLevelServerIssue> {
  private TextRangeWithHash textRange;

  public RangeLevelServerIssue(@Nullable UUID id, String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate,
    @Nullable IssueSeverity userSeverity, RuleType type, TextRangeWithHash textRange, Map<SoftwareQuality, ImpactSeverity> impacts) {
    super(id, key, resolved, resolutionStatus, ruleKey, message, filePath, creationDate, userSeverity, type, impacts);
    this.textRange = textRange;
  }

  /*ACR-5441888598fa446394a278c11abebe2d
ACR-d4c15cc769a644fe988a37550b5405e9
ACR-ee8285d6149b4910bbb2b4637d490d8b
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
