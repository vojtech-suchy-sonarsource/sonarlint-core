/*
ACR-3334ac6255364bf2a74538ca3645d58d
ACR-a99c866b196b4406b662d6a66d6658c9
ACR-93b67254886a4eb2b893e6a41c75d7e1
ACR-f2c3de55b94c4acfbe04878a05514517
ACR-59b9c83462f34dd797ccd8bfd7f9a702
ACR-d5aafffedb9140e98db9fbcd78c1def2
ACR-8955674394e34495a4cefdc4a56ee3cb
ACR-0d70dade33c14a538171561f13f25f52
ACR-025aa708cea642398d6ccfb513e90851
ACR-d3456a17defd40749c03fa8d1e3e6e39
ACR-d665d882c1354f67ac2693164111da09
ACR-f9ea5f624a444e4da107e949bcba6024
ACR-ed2913ce34d94c67ae0967a3dc2ce729
ACR-ce8c315dab064dd0b2fe8965af20625b
ACR-551581dd59244f16908f9cbaca5cd950
ACR-ec2d15ec698640c793a77e982063c911
ACR-6629ec6f238b4adc9ab72ac5ebe3a59d
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.sonarsource.sonarlint.core.commons.Version;

public class VersionUtils {

  private static final Version CURRENT_LTS = Version.create("9.9");
  private static final Version MINIMAL_SUPPORTED_VERSION = Version.create("9.9");

  private VersionUtils() {
  }

  /*ACR-daf6d8309b504225a5e0ba7303ad2851
ACR-06528a2a3ed64a9198b211c76e62cc9a
ACR-8ee594b86ce44e8cb04fd25714382328
   */
  public static boolean isVersionSupportedDuringGracePeriod(Version currentVersion) {
    return currentVersion.compareTo(CURRENT_LTS) < 0 &&
      currentVersion.compareToIgnoreQualifier(MINIMAL_SUPPORTED_VERSION) >= 0;
  }

  public static Version getCurrentLts() {
    return CURRENT_LTS;
  }

  public static Version getMinimalSupportedVersion() {
    return MINIMAL_SUPPORTED_VERSION;
  }
}
