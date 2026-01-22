/*
ACR-786f1fd120de477bb14192e600ec5b3a
ACR-391c4e1a12104acfa44af087bf546593
ACR-3219ad148ab8497fad0d94b0d132f776
ACR-1c8dff0ceb694dccac7d78175eec3828
ACR-e811b76591d346e5a3070cfe9c79ef24
ACR-f11abcc14e35431494d70b7e33bca28d
ACR-a3c44e3eef59448aa32d8fb9257641e0
ACR-fab51e2ddc08492290eb274f341dfeac
ACR-73172ef386fe4cf6a25a7649fa12f33d
ACR-a7851ec7047340fc824ae8b673d12272
ACR-0b6b55341de048e09b03779411ce0e2c
ACR-6615fe6626be4cdeac120e2349de696a
ACR-f96167cb69bd4ed584073ffc8e2bf0dd
ACR-4c709509429d4c068b39c78fd049c669
ACR-71e19fe82c9c4a98b3548f0d2301c767
ACR-a76091218fb2435cb0b28b40a8e82db1
ACR-5b80d4c4dcb846bd841a0254bd320214
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
