/*
ACR-d0705c66af524116a7dc7ce60ad9ede6
ACR-bde7c9164a8845d9966e8e25ee0f2528
ACR-41874b757efc4a85b8e0a63eae92c48d
ACR-975cb574cfce4e82a6ba5086b8c44670
ACR-97f81d98d429493198eef26ce6702aee
ACR-4f91a29d07e5431a9a4a91ee8c2bc14d
ACR-70be395962a346f494e4768d7b8ca93e
ACR-6d5c8ac099654c22a25c04be2927cdb8
ACR-204428d7f293443db52987e2b1a6789d
ACR-23b31457a3b24944b0a4c516b08b627c
ACR-e0d40627a02744789efe55baf26d6b97
ACR-33a707bcc3284073938db46aba8834ab
ACR-bda943eaf93d49bc869cd6f6132397f3
ACR-247bc2233e324572bf4932502958dd41
ACR-bf022894a01d4b9da0387f6728f9c1b8
ACR-77f23309e47944f1935b279fce48f9cf
ACR-ef4f68f51cbf409898b08faba87938ff
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
