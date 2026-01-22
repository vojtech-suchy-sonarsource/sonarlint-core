/*
ACR-9ec0dc98c1e5465f98b6d3cece78b44c
ACR-6c7009f9d7524a18adb876c7edc321f5
ACR-686b6c5a8e194c5d92cf5733f57ee013
ACR-a87bb1eaa84f4a8f9dc5a5310ca9d87e
ACR-fb34ba15c8694881a580654bcafe6188
ACR-09aa0c8444924c5b9aec44c080184547
ACR-f82b580b07ef42e2960cd8aad588b93f
ACR-37b53a4cb9c34a1393667157347f62cb
ACR-31c3362f31d04701966bbe038fa14fac
ACR-211217b6d1ae4d30a09f1e11b060b485
ACR-3cf42f7e31304a51aab62707eb343cb1
ACR-4f39675251e5472cbd06a81f0ca8dddf
ACR-894df9642a2543cca898ed084f081ac8
ACR-61be6e154c5a43a1acdc74467f7bb2f3
ACR-059180ed2d394a8a9ed178330943abd5
ACR-c978467628f040db964c2fd85f7b9d35
ACR-2887a7bf9767454dab3c1f440d9da63d
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
