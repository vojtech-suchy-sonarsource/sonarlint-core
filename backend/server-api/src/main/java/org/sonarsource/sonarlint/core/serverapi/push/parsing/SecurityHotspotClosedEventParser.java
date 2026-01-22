/*
ACR-8a1a4aff89d5497faeae4c210676a696
ACR-6a7d2037ccfb4109acfe2562019c4cb8
ACR-a066d3e880bf4852adaccf994f7cd118
ACR-ad6931e759234dd99b3dd000aea5f898
ACR-e76e9fd3e48247e08cbab87a1d5d61af
ACR-a2fff80c280c484d90fbde033cd00ea0
ACR-ac10d9ded51145f8b5723451adb6d48a
ACR-3068a8c71375421a90ebc0c55f48159d
ACR-5c589c1354784205b397bb5e2a951e67
ACR-901c572975d44352b668e08ec2d68183
ACR-11b1727b90fd4631aa808af0ada92545
ACR-9e33ba89187144ffade6dfed9edde66b
ACR-5d6b26d53cec4e528a3de3da86b05c64
ACR-939a49e174774c7c9173f356e80cb9e5
ACR-0270840aabd246bab34c77e57e5ef804
ACR-8f3d16fda42849b89eb52b5583262d03
ACR-60441acdc883449b8f7fd7ac294b0b83
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
