/*
ACR-03c8221180c444edb7f27a9964066566
ACR-1f7d755fdaf64c77816574d22070e977
ACR-db0ec45b02364b06a2b517b670cffa6f
ACR-3a6d2d25224b4bb9ae533a1529d86887
ACR-d6da7e7449a9474db4ad86276368c8f9
ACR-e0eaf73cee044cbcb5468744e268bf60
ACR-830978c8dd974fa08f31c8adf92285a3
ACR-7677eb2f932143ad9556d34d574fde2b
ACR-6318a03597934f619104132b188378ef
ACR-410de0b9586a40ce9bdb86d89972da9e
ACR-f957113df4754049a3bfd822c3addc40
ACR-23e061c53789459088af4b71278e95be
ACR-1e70d494828b4fd5aa147c8a5649fefb
ACR-ee3858474e9c471c8882f60ed99cd61f
ACR-35204e657fb047f0a5aef825574f2198
ACR-5738c1cbf5024374893f851eef36c5bd
ACR-84c3aa651afc4f918f540716862067a4
 */
package org.sonarsource.sonarlint.core.tracking;

import java.util.UUID;

public class LocalOnlySecurityHotspot {
  private final UUID id;

  public LocalOnlySecurityHotspot(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }
}
