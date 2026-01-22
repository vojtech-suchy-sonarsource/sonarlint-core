/*
ACR-b0cc468e99f84e7b8522711fd18cd892
ACR-74db43b9466d4d958b0e5c948963d19b
ACR-056456d1559345e497800632ff740734
ACR-ba9a2c3b411047bd8f9cb18f827b4cdd
ACR-82d585cd5207446eb5376c29c35219d4
ACR-525ae575d44f4a14be8026a432650ab6
ACR-5f749d6445294b6898f4169d1e3ebed3
ACR-001527e8a04f4ca9b9a742b6684d322d
ACR-522e26346cae436f830f4ed869193a7f
ACR-2a64a8aa387e4b41ae43a5f824c71b9a
ACR-3410816b354e47679d380b27ba03e2ce
ACR-6045947955444933bd406fa26dfb768b
ACR-917fd4dd5ce44be288f3955fee9a856b
ACR-f9400ce5ffa24921ab813ffe8961ac1b
ACR-8ecfc75884134a2792f5d60c4ebc3bc7
ACR-a192352ce4974bafaa0f2448199c72bb
ACR-e32fe61aa4ec4acf91b8e16611f69414
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
