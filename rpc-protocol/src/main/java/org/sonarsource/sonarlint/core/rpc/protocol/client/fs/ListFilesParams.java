/*
ACR-627ad693c3374238a4f22819352535ad
ACR-34aafb46f8bc4441858691f0e1e2caf0
ACR-a64060b2d365410990ea1d6af50389b5
ACR-572af2b5baf449fdaf442474e886a78d
ACR-3d6ece7a767b4edb91227ad349050911
ACR-5d3f80c69e1241fa87f018adf5d20860
ACR-1a01ea9bdcf74f7ba22fb834da978878
ACR-53cee427f1814e30af49c0dec8ac57d4
ACR-3fef618ed0344d3c899ea55d26197baf
ACR-e60cdb9219b141e5824f7ec9481e01d4
ACR-89612a4882714c7f9034c6a28429243a
ACR-e76cff27edfe4a8586180fa7747546ee
ACR-16720360ee5d4c938dfe2680f8fae669
ACR-e3da97aa33c544afaf91384e242302a2
ACR-19bc028a8b394fa8a8accad9c0678552
ACR-ce31229f8e0049e7ac08f8d8afc66ba6
ACR-797e98f3215a4b6d8fc04bc1c101df4c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fs;

public class ListFilesParams {
  
  private final String configScopeId;

  public ListFilesParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
