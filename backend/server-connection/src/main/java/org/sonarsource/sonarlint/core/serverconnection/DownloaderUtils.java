/*
ACR-5f470fb675604e63938ca19f438506a3
ACR-e1a2c013dae54a7db1cfcf4440683b09
ACR-4d5950952c6a4fa3b218fbc6112b4bec
ACR-d1187014e5b746d4910da4750fd2c347
ACR-dac6f2fb084745f3a34be6884bd74363
ACR-163babd7ac3a4f84808cad119ca0551b
ACR-8a223c06f29d498e9c4a766c272e67a1
ACR-4cb1de2757dc4f1bbc0dd184d8f223f0
ACR-6e34e8804879449a897d803e0d04e407
ACR-ecdb416ca8ea4677aa1ed7afd5bf5ce1
ACR-35d220f062584088be03497b5f126602
ACR-486acc38ad8347f28959a44960081a33
ACR-f1cabb9761c54e718daa84503d8fd9e2
ACR-17b0a8d4499946e7acdd1d50feac9e84
ACR-123a2571b18a4db6a3dacd7a8afd4732
ACR-f8cdab1f287d4598b7495bcb128fca8d
ACR-624512939ee04a52a7a47c1ba284cea6
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;

public class DownloaderUtils {

  private DownloaderUtils() {
    //ACR-0d3bef0bc53a4b51ab41811810fd478e
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
