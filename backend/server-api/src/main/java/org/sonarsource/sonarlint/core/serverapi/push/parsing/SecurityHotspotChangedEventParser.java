/*
ACR-16d06555fa1d4f23a454710a6548928c
ACR-c229018744534cdd805278b1b9f7c007
ACR-2ba5bc990e84405797d49208de98d235
ACR-dd9118c922dd4ea9a72047d8dc499599
ACR-39b851e1a88f44c88485a1be80290af2
ACR-fa853f628a774d0dab1bb87db04de2f3
ACR-48d30671fafa4ed78ec2463daa8fce55
ACR-6d5412071f12403b9da5e25e048ecf1d
ACR-e9549a0def56423ab2ff0b5449acaa06
ACR-10715717f6044a6fa034c8acc09da733
ACR-3700ec7f6c6347ed9e2b3eca9d07d3d9
ACR-67cdf4da78b643d0a68f8139f778e585
ACR-d9ffb93094884075919bcaee57f37261
ACR-afe1b011142b4f67944cf9e09d5e0788
ACR-ad0b36e52b434ee385fd33980ae36cec
ACR-86b5ff602a4c4cf1ba0bb750cd844c94
ACR-5bbcbffb3d4f42eca81232ac8a01936c
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing;

import com.google.gson.Gson;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.push.SecurityHotspotChangedEvent;

import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.isBlank;

public class SecurityHotspotChangedEventParser implements EventParser<SecurityHotspotChangedEvent> {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Gson gson = new Gson();

  @Override
  public Optional<SecurityHotspotChangedEvent> parse(String jsonData) {
    var payload = gson.fromJson(jsonData, HotspotChangedEventPayload.class);
    if (payload.isInvalid()) {
      LOG.error("Invalid payload for 'SecurityHotspotChanged' event: {}", jsonData);
      return Optional.empty();
    }
    return Optional.of(new SecurityHotspotChangedEvent(
      payload.key,
      payload.projectKey,
      Instant.ofEpochMilli(payload.updateDate),
      HotspotReviewStatus.fromStatusAndResolution(payload.status, payload.resolution),
      payload.assignee,
      Path.of(payload.filePath)));
  }

  private static class HotspotChangedEventPayload {
    private String key;
    private String projectKey;
    private String status;
    private String resolution;
    private long updateDate;
    private String assignee;
    private String filePath;

    public String getKey() {
      return key;
    }

    public String getProjectKey() {
      return projectKey;
    }

    public String getStatus() {
      return status;
    }

    public String getResolution() {
      return resolution;
    }

    public long getUpdateDate() {
      return updateDate;
    }

    public String getAssignee() {
      return assignee;
    }

    private boolean isInvalid() {
      return isBlank(key) || isBlank(projectKey) || updateDate == 0L || isBlank(filePath);
    }
  }
}
