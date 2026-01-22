/*
ACR-ef604b0aa77b47d59641ea57c93f5208
ACR-4e9b31bf26dd437584132835ddd10699
ACR-086383861af64a15b54e255cbbaa2322
ACR-0f7f83e3c56242adaf123fba7d6cd9fb
ACR-ed3a83cf900e4050895c1f6467c23da9
ACR-3e4e0610f2a94f4e9006c3aacc6cb732
ACR-6f0e9c4a6f3d4f7aa281cb194c923d26
ACR-470034a46a844cff97208cd8f8e0030e
ACR-daaee973c898478b8a5659158785c719
ACR-c177af2437684936868a16a79cd654c1
ACR-4eb1567174204862bb25ed14514df6fa
ACR-b57ce038ad0a4780acd71410c7cbc0a7
ACR-4ef4e61a65044e50aab2610eab7b2771
ACR-246736eb04f84141886689284262fb8d
ACR-dd167afe757a4940942be3f6bc97c69b
ACR-cd001d2e4721490784ecdfcb120411f6
ACR-337ceeebafe04a619a343812dfcf769c
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class ServerUpdaterUtils {

  /*ACR-eadfc34c6c4b4d50ac25f287bf17812a
ACR-b92e0c7b5dcd483faee9870faf014567
   */
  public static Optional<Instant> computeLastSync(Set<SonarLanguage> enabledLanguages, Optional<Instant> lastSync,
    Set<SonarLanguage> lastEnabledLanguages) {
    if (lastEnabledLanguages.isEmpty() || (!lastEnabledLanguages.equals(enabledLanguages))) {
      lastSync = Optional.empty();
    }
    return lastSync;
  }

}
