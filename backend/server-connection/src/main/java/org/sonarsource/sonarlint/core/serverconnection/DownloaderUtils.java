/*
ACR-503e1ceb180b4495b76cf6aca7a90a5c
ACR-28d5d568601a4480b920f9c48989582f
ACR-c3496a4b1ae643e0857b6953ae7b6b0d
ACR-36fceff2c3e9424bb83dc1b80e94c83c
ACR-de8b5df2052e4ed2b7e5a5c5bfbe5a8c
ACR-929810aa8d0841e7add1559d0e988786
ACR-770069e6e66f47ba9eb1ba12f6da65ce
ACR-7c3f362b97b948e6ada4aa54c1200046
ACR-977fb03865ab4fb0a0a506a3861f3b34
ACR-0ee93ef892a9492e8c3d48acb10d9756
ACR-b69bf27b2a6342a7b1ffe74aa6e189e0
ACR-d5651ca04ec347e78529605c805108ac
ACR-0409a22afb43459bac12bc7880aad740
ACR-3f6dfabccbcc469f88c6099fc3d210f6
ACR-b82e4d0a63fe49059a9a3d4dc50cbe7d
ACR-7a97dec0e55f47db85636b16c5756b51
ACR-6db257a9ac0a42469acb6376e8f308b0
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;

public class DownloaderUtils {

  private DownloaderUtils() {
    //ACR-39f59cdfdadc4213b8f825b2d888cdfb
  }

  public static SoftwareQuality parseProtoSoftwareQuality(Common.Impact protoImpact) {
    if (!protoImpact.hasSoftwareQuality() || protoImpact.getSoftwareQuality() == Common.SoftwareQuality.UNKNOWN_IMPACT_QUALITY) {
      throw new IllegalArgumentException("Unknown or missing software quality");
    }
    return SoftwareQuality.valueOf(protoImpact.getSoftwareQuality().name());
  }

  public static ImpactSeverity parseProtoImpactSeverity(Common.Impact protoImpact) {
    if (!protoImpact.hasSeverity() || protoImpact.getSeverity() == Common.ImpactSeverity.UNKNOWN_IMPACT_SEVERITY) {
      throw new IllegalArgumentException("Unknown or missing impact severity");
    }
    return ImpactSeverity.mapSeverity(protoImpact.getSeverity().name());
  }

}
