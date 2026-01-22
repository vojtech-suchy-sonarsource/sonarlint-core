/*
ACR-d2a9ef72d9064447a32f7b0ff3252674
ACR-4b9084e2e8484e7db2502553006a0f9d
ACR-83dbf35676984e709dcfc43ea9798fd6
ACR-8f6cfb5f3b514352b6e1fc6522bc874e
ACR-2a3a62f740ce4a159aa9278fba7d3363
ACR-7455b4effbf94d50bd667a0f28f007ee
ACR-b161bf142f094b9fa008d6163351254b
ACR-731b20ffd6374e7bb2db99a6585f96f5
ACR-1981fa30658e425aa053f022b64d3763
ACR-dbd4d9f26a2847df83632f521cb6331c
ACR-9b5d41c19ee341329ec1b01fd6c031b0
ACR-4ebfa9692d44448cb96194cf8ca86315
ACR-8d009f01a0fb4d74832494ac772cb980
ACR-f61df9802f124a0cbbf644b714ea7a3f
ACR-103c8b0074aa4672bfa9c6cecd48e955
ACR-498f9ffa34c042bdae890f0faf18baa1
ACR-779430de9f6b421e8d041c48eb37103d
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
