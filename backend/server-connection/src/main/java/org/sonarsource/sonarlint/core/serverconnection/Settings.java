/*
ACR-05aec1533e0f4ae4aafbf68ef7883c8f
ACR-020dbaf6c3d440b19b065fb5fd56c519
ACR-f9193d3cd82c4c6a91a5721bd6d4909a
ACR-528b50d2818d45b7847f30bce97b1242
ACR-b3c20eed931b44f78ddcedffdafb7b92
ACR-7c3b8c3ce3324d9b8ad7d6cafbc1a295
ACR-58ac0f5d3cb64e3ea1d88be8290dc4eb
ACR-34003898567645d8be1f75c8b01ba873
ACR-8dee96b60366448c9de00a1d4b09456e
ACR-34430dde907447388234771821437cc9
ACR-37d0f7760ddd4dc0bb047722f9694a91
ACR-cc29e1a4d73645a9862ea117faae6891
ACR-e09529e8107b4e049a67e7af4cb06498
ACR-da085f4f886d44f19d0e7f3474dde88f
ACR-0f1d3abc110440cf9f048ccceec93509
ACR-b09b326bc61a42c2b88402763df08470
ACR-5d88bfd07b3444bab6895f59aff47a43
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Map;

public class Settings {
  private final Map<String, String> settings;

  public Settings(Map<String, String> settings) {
    this.settings = settings;
  }

  public Map<String, String> getAll() {
    return settings;
  }
}
