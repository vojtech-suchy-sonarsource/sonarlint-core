/*
ACR-98a502d2b8e042bd8ec014240be4fecf
ACR-74e982223f30457389b09bc2ff298c03
ACR-d92ca811e55f432095a9050a127e9def
ACR-a33f115654834de28142dfc6dafb46b9
ACR-91d2ed8ab28f46c5babc243b9ad47ab5
ACR-04b49cbe59984bd5ac325c4c4ca9801c
ACR-449d460d02f8416cbdc9153872fe9a5f
ACR-ad826a616f7f4ac8b9d4c373e4712c2f
ACR-22d2ca5f3309478aa7a139328e6a736b
ACR-a1d6ccd9b0f84bb39365d878bd698c11
ACR-54d7be221c4f4ee1b1a191a8bfdd1026
ACR-d6a59b808593420e9e704829cc35c1ff
ACR-0a8d5e3299f9466cbbb624bab58519e6
ACR-7be2878f842942b78f7b863ac0cbe799
ACR-2e0110e368674f2db17c73dffae6e33d
ACR-efdda7d1e76a4184bf0b9cbddcb45f41
ACR-eb0e19dfb24f40b4bdf74488b3fb505c
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.util.List;

public class ClientInputFileEdit {
  private final ClientInputFile target;
  private final List<TextEdit> textEdits;

  public ClientInputFileEdit(ClientInputFile target, List<TextEdit> textEdits) {
    this.target = target;
    this.textEdits = textEdits;
  }

  public ClientInputFile target() {
    return target;
  }

  public List<TextEdit> textEdits() {
    return textEdits;
  }
}
