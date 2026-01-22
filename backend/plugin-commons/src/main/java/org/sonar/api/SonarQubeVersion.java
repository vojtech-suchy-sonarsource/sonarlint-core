/*
ACR-1054638a65ab4e5bbee63d150ff76428
ACR-1f03cbca14204bbd89bc4079ac4eba8f
ACR-cea6089e4815421da823342e0e7ef433
ACR-5713043a13f54d1a973d285d2999da4d
ACR-e233d41793d8493c93aa0e6473e9cfb0
ACR-23bba52adb3f4d12a3a590e298a30530
ACR-ed3867ff8fd44a1890e2a3356e2bb485
ACR-0d95bb60b6cb47d3a7174bd39c7eca04
ACR-b562f101c50a4d028e22a41119fbff07
ACR-92a8a41211d04f908fd09b7a3dfb09d2
ACR-ed2c799a986c4877a8cd9153f89ac077
ACR-f8319dc4c8a242b4b874e6956fd558cb
ACR-5f4d13c33a8d49178c39ea13654bfb78
ACR-5b6ed00b86b24507a51215423050d8bf
ACR-a71dd31915ba47e0beeae4a85788c9fc
ACR-c26b827f703d426b90d8c526f3ee787a
ACR-eeafc4416e2f4646b360d0814073f019
 */
package org.sonar.api;

import javax.annotation.concurrent.Immutable;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.scanner.ScannerSide;
import org.sonar.api.server.ServerSide;
import org.sonar.api.utils.Version;

import static java.util.Objects.requireNonNull;

/*ACR-7ef8bd503bda4c1fa092aec0e1ecc97e
ACR-d47ebc72f6b74e2b98708b57a5a957e1
 */
@ScannerSide
@ServerSide
@ComputeEngineSide
@Immutable
@Deprecated
public class SonarQubeVersion {

  private final Version version;

  public SonarQubeVersion(Version version) {
    requireNonNull(version);
    this.version = version;
  }

  public Version get() {
    return this.version;
  }

  public boolean isGreaterThanOrEqual(Version than) {
    return this.version.isGreaterThanOrEqual(than);
  }
}
