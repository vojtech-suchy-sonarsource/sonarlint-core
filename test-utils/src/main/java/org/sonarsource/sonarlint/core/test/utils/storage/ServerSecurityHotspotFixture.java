/*
ACR-1d5eea8372cf4258887cee63cc86c458
ACR-4de8a366f30e485aa895a61912bfa6ac
ACR-aab2a9e42ca141e79c3f4a1484b31e7b
ACR-397bb7b853bd4bb8afc079e852cd85f5
ACR-c859a8cac9dd4926914fba5f25808d81
ACR-ad147ab3296747689388073514986654
ACR-7429ea29d5894ab4a0624132c27cbdab
ACR-ea2b486637d34e84bf57c7f41d80ac95
ACR-06e2f948bc8a4cc38f38b8e6b62c7065
ACR-8ba945a7eda54e6da8fdbba56581ae77
ACR-4290e546fd524412aa8c21b98c174589
ACR-1de2cf0db4914296b283c6341c8011a6
ACR-f553be0ec5f24bccaf56bb5dfa2e4ddd
ACR-451e9ea4d9094c1ea78331f786b0c0ce
ACR-40c8588a3a6d4a0194deb18edfb1d9ba
ACR-7d4dd3356d8b4ca7a7447731e68dc0e3
ACR-6fc17144b37546929bcb86f05fd57a5b
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
