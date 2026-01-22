/*
ACR-36fa55468b644e3e84d1dcf7ebd9b5bd
ACR-2990aa12cf3743b79b39ff57148857dc
ACR-f0043030be664e6f93001c211dce72a6
ACR-b656b24b861b40f7a13494d8de2dee2c
ACR-05e09d6009a74cc2985d79e1272e23c6
ACR-77acc8c2c7204eb0a35041cb60a0a83f
ACR-08934da62e5649cfadd2cf119a8ee839
ACR-c81eb0bb403c4d1ba62c7e84b710ce23
ACR-9cf3d322bee94960a357cf5bb2ff3618
ACR-084dc82504bd499bb26fe102b0e2883f
ACR-1544e426f3ec4ed2887d8db0e71cfc5c
ACR-91ad0fc5502a4a9d8c28849f1fe2b819
ACR-7373902ea89345daa609f12239778c1c
ACR-730828d168144280b68da0183e36c540
ACR-4f0ff659949143008b473c1081dde21b
ACR-b9e8d4ae322f4260b273b65927160f8a
ACR-ff848e557bf24b7690c5aac070d22a54
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
