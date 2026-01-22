/*
ACR-9b860d34ffd34ed1a24327ba0be1eb1f
ACR-18730b695aa94e418239aeba07c6cf56
ACR-2ffd0e317a6b4477848f71c91f343bbb
ACR-93e8e2e4fce84243ad05ef8b6592c2a2
ACR-b1965e44a9cc4a5babb393f9a86fe814
ACR-245ecd47cc744ac5ab47518eef4516ab
ACR-ca12004a273748f09f8319ec3cc497f8
ACR-14b06098cafd4199931cbf010129dd50
ACR-0867d7a342a14186902850216a2e16e5
ACR-620bc3da932343cf935a1563ea0b6d82
ACR-dc6885787278447db640fc992e0035f2
ACR-ca13aa732537439fbe21cd77b14e092c
ACR-7900c57c3bf2432dbb3a01f4b4ebe0a9
ACR-b6ed45a477c14702a226e777cbd318be
ACR-3da365fc77814251b7bc8cd2dbf6bd64
ACR-2e581180ee37485c987e15a4a6f6d5b4
ACR-7631532f64ad4092b82eabafd04fbb2d
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.sonarsource.sonarlint.core.commons.Version;

public class VersionUtils {

  private static final Version CURRENT_LTS = Version.create("9.9");
  private static final Version MINIMAL_SUPPORTED_VERSION = Version.create("9.9");

  private VersionUtils() {
  }

  /*ACR-615d4e5aaab0496099ff654c2b39fac5
ACR-846ed5d7f00642c59748fb86e89c87d6
ACR-096ff463bb164980aa62bc6bc01c9789
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
