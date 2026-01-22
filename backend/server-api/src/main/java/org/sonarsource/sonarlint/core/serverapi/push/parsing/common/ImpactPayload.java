/*
ACR-d2482f8e2d7e4bd699520aab98bc12e0
ACR-92a3ee9874704b0fab765b186581be97
ACR-104ac9cdc56048419bf9d4aea54b8db2
ACR-a911f569c23b4c4c87527ef7213b42e4
ACR-d0bc6adb7a86410287e670fed00fb444
ACR-92694d4004164f559d9b6dd4fabfcecc
ACR-aa6cb734044d4fc7b5a6caa336026568
ACR-88a546ec60f64f55b6d18c60e52c6865
ACR-c902b94280ad42f489111f505cbd9a45
ACR-7f00d4bfc27b43fb93e1aef5f41554d7
ACR-94d9f2d532f24b818d9b9997f3d9cb9a
ACR-dc5a8b6ba69748eeaf20e52cb3c8fcf2
ACR-bd306eb88cad457ebbf7746b58b54249
ACR-21b2e809e4fa4efcbd6db5a50db76b92
ACR-4a5dead6547e4df89271c886f58703ae
ACR-845a9ac7e7f04277b3876668aed3b277
ACR-bba54119e2db4a02a880552010683a9e
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing.common;

public class ImpactPayload {

  private String softwareQuality;
  private String severity;

  public ImpactPayload(String softwareQuality, String severity) {
    this.softwareQuality = softwareQuality;
    this.severity = severity;
  }

  public String getSoftwareQuality() {
    return softwareQuality;
  }

  public void setSoftwareQuality(String softwareQuality) {
    this.softwareQuality = softwareQuality;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

}
