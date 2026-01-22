/*
ACR-bb046bf8823b4db1afd23eba46f56d18
ACR-aee684d7b4a947079dd1ded7f276aa04
ACR-60b1dd72ab764b81973f369291fac3ee
ACR-b188f6351e1246f8b993ec2a4ba9c84a
ACR-2feb92e8d5bd426da0dd27de8be272bc
ACR-f3ad17f0b52945bd94916777031694c8
ACR-bcd1452de54b404fa9c3a8e352bc52d7
ACR-9a679d7a52dc4b5495b344a843817b57
ACR-0786cdc809664c5caa7ab9982a67ffe6
ACR-fe60b32706ac457b913c31cbe0b2c9bd
ACR-2d8c4d5a3f044d25bc0a9bc81087f849
ACR-4385d7be17724495a8234ac291a5fb49
ACR-7e83da7ced714f098134367d9eb8dcc3
ACR-695b0c2d150e4c08a1ae49e279a581c7
ACR-20a3ed300e2f4ba99ceb95bffb712420
ACR-a46a5dace0e340c49b649dd4665305c5
ACR-a3ae1936429d4a37b9e6886b712671b9
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

import static org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue.*;

public class ServerTaintIssueFixtures {

  public static ServerTaintIssueBuilder aServerTaintIssue(String key) {
    return new ServerTaintIssueBuilder(key);
  }

  public static class ServerTaintIssueBuilder extends AbstractServerTaintIssueBuilder<ServerTaintIssueBuilder> {
    private TextRangeWithHash textRangeWithHash = new TextRangeWithHash(1, 2, 3, 4, "rangeHash");
    private String ruleKey = "ruleKey";
    private String filePath = "file/path";

    public ServerTaintIssueBuilder(String key) {
      super(key);
    }

    public ServerTaintIssueBuilder withRuleKey(String ruleKey) {
      this.ruleKey = ruleKey;
      return this;
    }

    public ServerTaintIssueBuilder withFilePath(String filePath) {
      this.filePath = filePath;
      return this;
    }

    public ServerTaintIssueBuilder withTextRange(TextRangeWithHash textRange) {
      this.textRangeWithHash = textRange;
      return this;
    }

    public ServerTaintIssue build() {
      return new ServerTaintIssue(UUID.randomUUID(), key, resolved, resolutionStatus, ruleKey, "message", Path.of(filePath).toString(), introductionDate,
        issueSeverity, ruleType, new ArrayList<>(), textRangeWithHash, "contextKey", CleanCodeAttribute.CONVENTIONAL,
        Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.MEDIUM));
    }
  }

  public abstract static class AbstractServerTaintIssueBuilder<T extends AbstractServerTaintIssueBuilder<T>> {
    protected final String key;
    protected boolean resolved = false;
    protected IssueStatus resolutionStatus;
    protected Instant introductionDate = Instant.now();
    protected RuleType ruleType = RuleType.BUG;
    protected IssueSeverity issueSeverity = IssueSeverity.MINOR;

    protected AbstractServerTaintIssueBuilder(String key) {
      this.key = key;
    }

    public T withIntroductionDate(Instant introductionDate) {
      this.introductionDate = introductionDate;
      return (T) this;
    }

    public T resolvedWithStatus(IssueStatus resolutionStatus) {
      this.resolved = true;
      this.resolutionStatus = resolutionStatus;
      return (T) this;
    }

    public T open() {
      this.resolved = false;
      resolutionStatus = null;
      return (T) this;
    }

    public T withType(RuleType ruleType) {
      this.ruleType = ruleType;
      return (T) this;
    }

    public T withSeverity(IssueSeverity issueSeverity) {
      this.issueSeverity = issueSeverity;
      return (T) this;
    }

  }

  public record ServerTaintIssue(UUID id, String key, boolean resolved, IssueStatus resolutionStatus, String ruleKey, String message,
                                 String filePath, Instant creationDate, IssueSeverity severity, RuleType type, List<Flow> flows,
                                 @Nullable TextRangeWithHash textRange, @Nullable String ruleDescriptionContextKey, @Nullable CleanCodeAttribute cleanCodeAttribute,
                                 Map<SoftwareQuality, ImpactSeverity> impacts) { }
}
