/*
ACR-86f0b33c711b488589196223ad49d51b
ACR-2bb78566afc54630af21bf726979b83e
ACR-fa294c32c3124084aefcfd499a9ea371
ACR-d0fc55a95ce04907b2b13a03b2677ce9
ACR-e434b0a23ad34296af496ab5c9a8f053
ACR-35e5accb202d4d309c7be33eceaad4b2
ACR-7d95ca03385d43a6968969cb95bcc434
ACR-03b686c4618d44f6956f3e66667dcb89
ACR-a97b7cf8d151421d8125406425f010e9
ACR-d90e641c66df450d895434b41de03210
ACR-299a3ed9d3c049cfaac96318259e62fc
ACR-c042498a3d5a45db90de88e202874054
ACR-b43d6d738c5a4294b886369d32466fda
ACR-a151e15b40224b9a809c1cff2d9c4869
ACR-73b13adbcc1a4e59821570ef0c6458d1
ACR-97a73c08c9054ab888ff7764f4a52c2d
ACR-39d35cc0fdbe49e18f838c5c53aa0261
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

public class ServerIssueFixtures {
  public static ServerIssueBuilder aServerIssue(String key) {
    return new ServerIssueBuilder(key);
  }

  public static LegacyServerIssueBuilder aLegacyServerIssue(String key) {
    return new LegacyServerIssueBuilder(key);
  }

  public static class ServerIssueBuilder extends AbstractServerIssueBuilder<ServerIssueBuilder> {
    private TextRangeWithHash textRangeWithHash = new TextRangeWithHash(1, 2, 3, 4, "rangeHash");
    private String ruleKey = "ruleKey";

    private String filePath = "file/path";
    private String message = "message";

    public ServerIssueBuilder(String key) {
      super(key);
    }

    public ServerIssueBuilder withRuleKey(String ruleKey) {
      this.ruleKey = ruleKey;
      return this;
    }

    public ServerIssueBuilder withTextRange(TextRangeWithHash textRange) {
      this.textRangeWithHash = textRange;
      return this;
    }

    public ServerIssueBuilder withFilePath(String filePath) {
      this.filePath = filePath;
      return this;
    }

    public ServerIssueBuilder withMessage(String message) {
      this.message = message;
      return this;
    }

    public ServerIssue build() {
      return new ServerIssue(key, resolved, resolutionStatus, ruleKey,
        message, Path.of(filePath).toString(), introductionDate, issueSeverity, ruleType,
        textRangeWithHash, null, null, impacts);
    }
  }

  public static class LegacyServerIssueBuilder extends AbstractServerIssueBuilder<LegacyServerIssueBuilder> {
    private int lineNumber = 1;
    private String lineHash = "lineHash";

    public LegacyServerIssueBuilder(String key) {
      super(key);
    }

    public LegacyServerIssueBuilder withLine(int number, String hash) {
      this.lineNumber = number;
      this.lineHash = hash;
      return this;
    }

    public ServerIssue build() {
      return new ServerIssue(key, resolved, resolutionStatus, "ruleKey", "message", Path.of("file/path").toString(), introductionDate, issueSeverity, ruleType,
        null, lineNumber, lineHash, impacts);
    }
  }

  public abstract static class AbstractServerIssueBuilder<T extends AbstractServerIssueBuilder<T>> {
    protected final String key;
    protected boolean resolved = false;
    protected IssueStatus resolutionStatus;
    protected Instant introductionDate = Instant.now();
    protected RuleType ruleType = RuleType.BUG;
    protected IssueSeverity issueSeverity;
    protected Map<SoftwareQuality, ImpactSeverity> impacts = Collections.emptyMap();

    protected AbstractServerIssueBuilder(String key) {
      this.key = key;
    }

    public T withIntroductionDate(Instant introductionDate) {
      this.introductionDate = introductionDate;
      return (T) this;
    }

    public T resolved(IssueStatus resolutionStatus) {
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

    public T withImpacts(Map<SoftwareQuality, ImpactSeverity> impacts) {
      this.impacts = impacts;
      return (T) this;
    }
  }

  public record ServerIssue(String key, boolean resolved, IssueStatus resolutionStatus, String ruleKey, String message, String filePath, Instant introductionDate,
                            IssueSeverity userSeverity, RuleType ruleType, @Nullable TextRangeWithHash textRangeWithHash, @Nullable Integer lineNumber, @Nullable String lineHash,
                            Map<SoftwareQuality, ImpactSeverity> impacts) { }
}
