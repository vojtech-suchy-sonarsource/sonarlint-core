/*
ACR-17285233fdb845f59b7270ad7f0b72fe
ACR-c4d30740c4ce4bc29edc531eb5db091d
ACR-4f97f199b41844c68ea56506be3378c3
ACR-ddb8cd3f89a44ed19ea2c7fcdcc1224a
ACR-1ae5f1147da0466c8bbf076729e2a3b4
ACR-18347a0b1ab841a1bca5dfd0455349ea
ACR-ad3a28030f9644ad99a9b8546feb44d4
ACR-0e3c92f6526e43ae902240475fc4a19a
ACR-5fc6efafa26f4a35988bdf6b71e9f060
ACR-2f2d5d4f811e44b1b9d9b1b0be05302a
ACR-ed284f269d3e4a609005331f31084203
ACR-23dbe150acee432497d50804d633a958
ACR-e3206d2134124718ba0f6587441b3de7
ACR-2237f31830da4101b0f9c523b6e33a48
ACR-af39b33a20a346078a16e4d510115fd0
ACR-7189ef94459f48aeae61b56506c84ce2
ACR-47b482f021434224b41e2a1bee415e54
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.nio.file.Path;
import java.time.Instant;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

public class ServerSecurityHotspotFixture {
  public static ServerSecurityHotspotBuilder aServerHotspot(String key) {
    return new ServerSecurityHotspotBuilder(key);
  }

  public static class ServerSecurityHotspotBuilder {
    private final String key;
    private Instant introductionDate = Instant.now();
    private HotspotReviewStatus status = HotspotReviewStatus.TO_REVIEW;
    private VulnerabilityProbability vulnerabilityProbability = VulnerabilityProbability.MEDIUM;
    private String assignee;
    private TextRangeWithHash textRangeWithHash = new TextRangeWithHash(1, 2, 3, 4, "rangeHash");
    private String ruleKey = "ruleKey";
    private String filePath = Path.of("file/path").toString();
    private String message = "message";

    public ServerSecurityHotspotBuilder(String key) {
      this.key = key;
    }

    public ServerSecurityHotspotBuilder withRuleKey(String ruleKey) {
      this.ruleKey = ruleKey;
      return this;
    }

    public ServerSecurityHotspotBuilder withTextRange(TextRangeWithHash textRange) {
      this.textRangeWithHash = textRange;
      return this;
    }

    public ServerSecurityHotspotBuilder withIntroductionDate(Instant introductionDate) {
      this.introductionDate = introductionDate;
      return this;
    }

    public ServerSecurityHotspotBuilder withStatus(HotspotReviewStatus status) {
      this.status = status;
      return this;
    }

    public ServerSecurityHotspotBuilder withVulnerabilityProbability(VulnerabilityProbability vulnerabilityProbability) {
      this.vulnerabilityProbability = vulnerabilityProbability;
      return this;
    }

    public ServerSecurityHotspotBuilder withAssignee(String assignee) {
      this.assignee = assignee;
      return this;
    }

    public ServerSecurityHotspotBuilder withFilePath(String filePath) {
      this.filePath = filePath;
      return this;
    }

    public ServerSecurityHotspotBuilder withMessage(String message) {
      this.message = message;
      return this;
    }

    public ServerHotspot build() {
      return new ServerHotspot(key, ruleKey, message, filePath, introductionDate, null, textRangeWithHash, status, vulnerabilityProbability, assignee);
    }
  }

  public record ServerHotspot(String key, String ruleKey, String message, String filePath, Instant introductionDate, @Nullable IssueSeverity userSeverity,
    TextRangeWithHash textRangeWithHash, HotspotReviewStatus status, VulnerabilityProbability vulnerabilityProbability, String assignee) {
  }
}
