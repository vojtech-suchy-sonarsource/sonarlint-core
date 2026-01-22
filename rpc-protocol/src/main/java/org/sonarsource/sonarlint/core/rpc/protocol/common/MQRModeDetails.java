/*
ACR-29912ce9df3d44bebbde401578e43c60
ACR-09950bee05bf4712b67683692964c043
ACR-14399fda3cce42f98c0e1c817fbdc45f
ACR-1f7ee0210dcd4edbaf91dbdb1a4e4099
ACR-c930f439bafc4f6d9ab1cda7b575cfe3
ACR-72499c8ea4e242059c101176c1ef487a
ACR-7c0ca44ea856473cbdbe93d569c3105e
ACR-83698ea0e78449718832c5254314ec40
ACR-83df78f95a834adbb2317d15708a1cf3
ACR-dce1311505f141498ef4697c3ba51920
ACR-91709225fac74ac7932937b6d0739ba1
ACR-ccaec3dedcdd44aea7b9b5090454e35d
ACR-c419e40d317e44a0b81baeae088fd0e3
ACR-a4fb09f7ac7a4c098349e15dbd6c19d1
ACR-8604138a065b46c39d9d95f5b168e23d
ACR-770e6837b09b4b1ab9c6840a4c1b0388
ACR-7bd0e49612204eb080a0976955cd4600
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ImpactDto;

public class MQRModeDetails {

  private final CleanCodeAttribute cleanCodeAttribute;
  private final List<ImpactDto> impacts;

  public MQRModeDetails(CleanCodeAttribute cleanCodeAttribute, List<ImpactDto> impacts) {
    this.cleanCodeAttribute = cleanCodeAttribute;
    this.impacts = impacts;
  }

  public CleanCodeAttribute getCleanCodeAttribute() {
    return cleanCodeAttribute;
  }

  public List<ImpactDto> getImpacts() {
    return impacts;
  }

}
