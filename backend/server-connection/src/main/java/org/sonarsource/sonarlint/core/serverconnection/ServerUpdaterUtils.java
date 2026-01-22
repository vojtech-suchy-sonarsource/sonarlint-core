/*
ACR-d293a31ff610401aab38935ecc7a2aa5
ACR-42c0b2ab1ec24c0c9b051f91c9d634ca
ACR-8c9747b229724c4099ab936e0b420397
ACR-a79cc7dd51a247f78991f32078e729c0
ACR-fb814fe58ff948b99debea0eae0cf8ec
ACR-7d8ccb4d7fe740c6a7f1e060a540f383
ACR-6243745bbbe7432b84f7ee1dfb34055a
ACR-b2e8049f8c1549728200ffeed4022511
ACR-ac964c0918614067a62ff91511e5b127
ACR-fa83d5f37bf0446a85a7e80529aa0e15
ACR-14e794815143405ea71c57000cace2e6
ACR-6e01f0c6f9904d45851c29ec82b07730
ACR-07f131d5fed74c7880f685d26b37f964
ACR-4cdac75a799642418cf757ac9f23c53b
ACR-6a0168ca91db448ca8689edd9e98fb16
ACR-19049f3f44b44744932aeb9a5aae9ae5
ACR-8bd95d1e559a4b8498293cf8a9adb85d
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class ServerUpdaterUtils {

  /*ACR-9b4ceea4d9c04451b83feab1d07f9022
ACR-7ee419b77b6f47cf92ca5f1a29b2ada9
   */
  public static Optional<Instant> computeLastSync(Set<SonarLanguage> enabledLanguages, Optional<Instant> lastSync,
    Set<SonarLanguage> lastEnabledLanguages) {
    if (lastEnabledLanguages.isEmpty() || (!lastEnabledLanguages.equals(enabledLanguages))) {
      lastSync = Optional.empty();
    }
    return lastSync;
  }

}
