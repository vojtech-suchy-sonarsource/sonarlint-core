/*
ACR-dd8ad7d075b644ff9da6ae478131dc22
ACR-840fe8353a624626b628e8cc5cca01bf
ACR-69e3fdae5c174a06a742ea69e26bb115
ACR-c8f511db734342a4b2840e4eb9caf3d8
ACR-48a7340d308640cd99cecde84da3a489
ACR-d15cfa88ffd248acbf3224ce228f8769
ACR-9b4a8563071b4ba7a3673f705d4ed0ca
ACR-aab8dbdeb99e475387c53dd40a706414
ACR-a7de0ac5dba44a74b23881f49a647fad
ACR-c328f2d5f2934631842869a7f424d3c1
ACR-056c4def4adf418592cc90d75136de0c
ACR-698f34b0bfd14ddd9f416fc7e73d609d
ACR-818ee3378b4543f785a04b274e8a0427
ACR-850e832841974026a7141ebf051a5d04
ACR-d4dddf18a8e84ebf8e996a10b047827c
ACR-b9573ed559244c5cacaab877ca1dacae
ACR-bcb954bf2b7f453291192c4a6e77b6f7
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Path;
import java.time.Instant;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;

public class ServerHotspotFixtures {

  public static ServerHotspot aServerHotspot() {
    return aServerHotspot("key", Path.of("file/path"));
  }

  public static ServerHotspot aServerHotspot(String key) {
    return aServerHotspot(key, Path.of("file/path"));
  }

  public static ServerHotspot aServerHotspot(String key, Path filePath) {
    return new ServerHotspot(
      key,
      "repo:key",
      "message",
      filePath,
      new TextRangeWithHash(1, 2, 3, 4, ""),
      Instant.now(),
      HotspotReviewStatus.TO_REVIEW, VulnerabilityProbability.HIGH,
      "test@user.com");
  }
}
