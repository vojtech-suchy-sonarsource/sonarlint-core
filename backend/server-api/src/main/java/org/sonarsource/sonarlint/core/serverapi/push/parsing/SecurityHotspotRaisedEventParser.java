/*
ACR-8a864fb620fe4feb80c022d8afe0ea27
ACR-00344c2d8a3c43088f2f60a46f4d0765
ACR-fc9ae6d6d5e4414f996515a1ea51d3ad
ACR-c61f405b891b44d7aa6858d18648ccfb
ACR-c4b8cf497eeb47759b12ac86447c9287
ACR-8a601e66b281487e86cbb9605df1ffad
ACR-a6502550ccdb4279b6479956581114a6
ACR-26ae32ccd63e4b9f993b8b28dc638ded
ACR-a5a5f1e0cbf04b3bb276ea97265275a9
ACR-40af34a92aaf4a888101b647e1d70170
ACR-4ed6062f1b894624833258535ca31d4e
ACR-8600b05cc5b24187be3db741c78d3970
ACR-f623aef8e78e422e80abbf7e1d367601
ACR-10b17f8664224824ad4f49f9722bc8d3
ACR-744b0ea74fc5431fbaf16abc0cd60461
ACR-7139c07bb2c245daa0eb0a664e7181c5
ACR-2475e54b133243a6a6696b9cbaffc5b4
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing;

import com.google.gson.Gson;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.push.SecurityHotspotRaisedEvent;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.LocationPayload;

import static org.sonarsource.sonarlint.core.serverapi.push.parsing.TaintVulnerabilityRaisedEventParser.adapt;
import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.isBlank;

public class SecurityHotspotRaisedEventParser implements EventParser<SecurityHotspotRaisedEvent> {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Gson gson = new Gson();

  @Override
  public Optional<SecurityHotspotRaisedEvent> parse(String jsonData) {
    var payload = gson.fromJson(jsonData, HotspotRaisedEventPayload.class);
    if (payload.isInvalid()) {
      LOG.error("Invalid payload for 'SecurityHotspotRaised' event: {}", jsonData);
      return Optional.empty();
    }
    return Optional.of(new SecurityHotspotRaisedEvent(
      payload.key,
      payload.projectKey,
      VulnerabilityProbability.valueOf(payload.vulnerabilityProbability),
      HotspotReviewStatus.fromStatusAndResolution(payload.status, payload.resolution),
      Instant.ofEpochMilli(payload.creationDate),
      payload.branch,
      adapt(payload.mainLocation),
      payload.ruleKey,
      payload.ruleDescriptionContextKey, payload.assignee));
  }

  private static class HotspotRaisedEventPayload {
    private String key;
    private String projectKey;
    private String status;
    @Nullable
    private String resolution;
    private String branch;
    private String vulnerabilityProbability;
    private long creationDate;
    private String ruleKey;
    private LocationPayload mainLocation;
    @Nullable
    private String ruleDescriptionContextKey;
    @Nullable
    private String assignee;

    private boolean isInvalid() {
      return isBlank(key) || isBlank(projectKey) || isBlank(vulnerabilityProbability) || creationDate == 0L || isBlank(branch) || isBlank(ruleKey)
        || mainLocation.isInvalid();
    }
  }

}
