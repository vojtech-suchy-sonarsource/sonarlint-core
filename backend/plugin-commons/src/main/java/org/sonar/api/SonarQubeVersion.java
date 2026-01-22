/*
ACR-108775e2e37c4788a695c1f982059b22
ACR-49417487eab44b4692472b1d72e8e350
ACR-f3e2e913d4624a70b9e27413d1559094
ACR-e71b97a133b34e40bfe51cdf8f269812
ACR-4c1f07fd248347c9813c75fcf6e028d9
ACR-698ea87a5d034f199d64b312d0f1d836
ACR-be602ad2b9ac43f2a5e191da041f4e35
ACR-515e1fa87e014455adc52459dcc63cdc
ACR-2171cd43ba654a0a9d1410e3249d7c27
ACR-28da0937c32a4bce918cfb72797997ea
ACR-912e2af918a846718a5e4a83df45a698
ACR-e0f9403becce458f85e1fa821377c60f
ACR-f0d065b5252f4f6ba5734c5b1e5624e9
ACR-5f9247709a334b7d83e415d790caee16
ACR-fa162ede72534123813833bfd10a2b8e
ACR-a80868317fce4ca0923406b3686dacfa
ACR-eab2d46c7b03402f99a2b79348d12086
 */
package org.sonar.api;

import javax.annotation.concurrent.Immutable;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.scanner.ScannerSide;
import org.sonar.api.server.ServerSide;
import org.sonar.api.utils.Version;

import static java.util.Objects.requireNonNull;

/*ACR-5c823f43d8b140b48fbfacc580cf52e9
ACR-926b0b690d74453fad7a30bff54bc71c
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
