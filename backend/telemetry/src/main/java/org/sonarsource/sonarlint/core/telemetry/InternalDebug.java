/*
ACR-8cf1147e912a4e59a650e927ef9db6e1
ACR-fd748dc47e6145b3b577c0685b985767
ACR-d80b481d45a34565ac014d13091f6ddd
ACR-fbebc52b27ae4f96b5116c1181fc6483
ACR-94615781146f4ed08a44659fbcab5021
ACR-f16e292f239743cfa0e525f6d327479d
ACR-eb221cfb831c4b9b884d03d569912402
ACR-939802e414444df4870968fc6f0cbb55
ACR-9e0c3b87fc4c48889dbac7d39c16dd79
ACR-7663023fa07c49bbb87c801286a9bfdc
ACR-0f052e59819b4a438c8bae2758b10828
ACR-d72b3915a2b84e5fa467029e8c5b5523
ACR-b13d4a411c3846678d22ccdb20812390
ACR-b293485ae664486fa58bcebce309506d
ACR-1509422141ee41f0b0a4c84ffb0b05f3
ACR-fe26714d816b48c890b8297d103d95a8
ACR-3f3d9c2e9d02430a825dca2759056a16
 */
package org.sonarsource.sonarlint.core.telemetry;

/*ACR-5bd25d2477834707b382c6fc65a401a9
ACR-95c3cc2523fb48418e5c1bd05b46bfd1
ACR-fb2003b1b3ff4c72b0f92f25ee5747fd
ACR-0a254136c8d24b8b9d9cea69e5800179
 */
public class InternalDebug {

  static final String INTERNAL_DEBUG_ENV = "SONARLINT_INTERNAL_DEBUG";

  private static boolean isEnabled = "true".equals(System.getenv(INTERNAL_DEBUG_ENV));

  private InternalDebug() {
    //ACR-c6ba2c03042c4f45990356b4777d7e62
  }

  public static boolean isEnabled() {
    return isEnabled;
  }

  //ACR-3e5aed1ad5f84bbebb4185bdb97b3ea4
  public static void setEnabled(boolean isEnabled) {
    InternalDebug.isEnabled = isEnabled;
  }
}
