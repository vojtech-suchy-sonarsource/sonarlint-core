/*
ACR-f3729fbec1274ea6820dffe3200759d7
ACR-cbfa6cec70a949e99c32b6dc52df10f5
ACR-53fe1ec1338442ddb93d59cd6971bc9a
ACR-e2ea7a2797f94f689ea214bae0682138
ACR-3c9dda7da2c941e9910392937bccf003
ACR-17eb21ba660f4bc0a032f740023d4bf7
ACR-fa194e7c48c8475d9c9d0c79ab68a099
ACR-efbbc74dc17c4b948d857a0792fc6b2c
ACR-9e10771fe47a48568dcfbc572090c167
ACR-ea4b657dc1c8453f85964ff7e6d3e1c9
ACR-4676c3f7700a4bf99a6c10fb5d78fc3f
ACR-0fb0eadd9476470e8599634525414299
ACR-8166e43038d340ff89e0ff703ce84beb
ACR-6f05b587ac2b46c4951f66b7c2ff7e73
ACR-52a8155c64334c31925c3ac9568c8cb7
ACR-e3de829e17144f1e942fb218951456df
ACR-35d8f0b5cd7f46458526969cf594d166
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

/*ACR-90c44aa30a9144419436b4ad799089f0
ACR-fc6cd969541d4d999975d2ae9fea8f01
 */
public class FileLevelServerIssue extends ServerIssue<FileLevelServerIssue> {

  public FileLevelServerIssue(@Nullable UUID id, String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate, @Nullable IssueSeverity userSeverity,
    RuleType type, Map<SoftwareQuality, ImpactSeverity> impacts) {
    super(id, key, resolved, resolutionStatus, ruleKey, message, filePath, creationDate, userSeverity, type, impacts);
  }

  /*ACR-6c847268aef040cd9d5a1fce536efbe4
ACR-0659350e9da14f459fe7af7ca47d0445
ACR-c9e031c97eae4008814957506a567843
   */
  public FileLevelServerIssue(String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate, @Nullable IssueSeverity userSeverity,
    RuleType type, Map<SoftwareQuality, ImpactSeverity> impacts) {
    this(null, key, resolved, resolutionStatus, ruleKey, message, filePath, creationDate, userSeverity, type, impacts);
  }
}
