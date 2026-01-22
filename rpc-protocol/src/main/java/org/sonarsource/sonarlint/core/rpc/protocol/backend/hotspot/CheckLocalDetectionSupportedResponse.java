/*
ACR-b760e609f3d144fe87cb3534ee9e01f6
ACR-22f98f71e0e64cc1a6bf378b08d13693
ACR-6b5a58ea8497444babff977bfea51530
ACR-69388a9e4bc8456197aea5bfe871ecb6
ACR-9be41127715443fdb9d9aa6390dba8f9
ACR-f6aa483e101840c2b47b25e02154e22a
ACR-7b7ddf162ede48719cab93ed0d62889d
ACR-c608db2b783548ab9841142eea2563dc
ACR-ab0a17fda71f449b974d727ef75916dd
ACR-9d4d12bab7804092a8e9fce2f56d3074
ACR-8824856961ae461aa4bedca313f35105
ACR-78da2992893e4042b674932657f386cf
ACR-df96876384834b2996b396eb22f714d2
ACR-2da9bd9cace3413586337d69d55a1d7d
ACR-e4609f3d56d84d17baa6096c9673f2ef
ACR-7cf113d04cba477b9024e002db64c530
ACR-c744cd4511714ce58445073f088b7e17
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class CheckLocalDetectionSupportedResponse {
  private final boolean supported;
  private final String reason;

  public CheckLocalDetectionSupportedResponse(boolean supported, @Nullable String reason) {
    this.supported = supported;
    this.reason = reason;
  }

  public boolean isSupported() {
    return supported;
  }

  @CheckForNull
  public String getReason() {
    return reason;
  }
}
