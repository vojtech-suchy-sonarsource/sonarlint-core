/*
ACR-bf04819116134d71940e1630f26909ac
ACR-add9e78d40714833ac7348255355f6c4
ACR-3d80405254ba4b8e963e4731a9ea615e
ACR-34c3f6e1d62c4f2195b17e82fcf95639
ACR-fa0d49b80b594ed38ec6fa999bf3bb0f
ACR-b312eeb9f54c4837b7624d82607f2ff3
ACR-0e17d747a6e744b7889826b02c9f1614
ACR-0a6b12dbf0c44339ab074ef954effbd2
ACR-85667869ffc24d88820c1dbb9ab8bdd0
ACR-7e6a6a36228448c3bdedbaf6093e76b3
ACR-2995cfdee5064a2bb1ca4fb4e0f97480
ACR-5f811281ea3d42c594a2313ee1c46893
ACR-49fa51462b2a463790172289226ae70c
ACR-b320e3ab4a83480b8bbfa45c7cc0b02f
ACR-d25e0ab2e25e48478bcd978c50f6197f
ACR-f09e602032504af3ad189aed1455d3b2
ACR-c58c1244118a402c866cce17a7d695c5
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing;

import com.google.gson.Gson;
import java.nio.file.Path;
import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.push.SecurityHotspotClosedEvent;

import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.isBlank;

public class SecurityHotspotClosedEventParser implements EventParser<SecurityHotspotClosedEvent> {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Gson gson = new Gson();

  @Override
  public Optional<SecurityHotspotClosedEvent> parse(String jsonData) {
    var payload = gson.fromJson(jsonData, HotspotClosedEventPayload.class);
    if (payload.isInvalid()) {
      LOG.error("Invalid payload for 'SecurityHotspotClosed' event: {}", jsonData);
      return Optional.empty();
    }
    return Optional.of(new SecurityHotspotClosedEvent(payload.projectKey, payload.key, Path.of(payload.filePath)));
  }

  private static class HotspotClosedEventPayload {
    private String projectKey;
    private String key;
    private String filePath;

    private boolean isInvalid() {
      return isBlank(projectKey) || isBlank(key) || isBlank(filePath);
    }

  }
}
