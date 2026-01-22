/*
ACR-c2274469b8d94c819e21da08e87f3d19
ACR-e9aca541be764468bd04cf8dff670dfa
ACR-9e427d4c04fb4527aa3080732ea72ae8
ACR-52c1066bf5ed47188b9420eefe88b574
ACR-1647cb49147a490cab6f83131b435dd0
ACR-d60a9761bdec4ffb8eee828e334accd0
ACR-7f683bb7834d447f84c079ce4fe090f0
ACR-c97d7e4ff9f44490a561490ec5e60409
ACR-60975748cbe14fe0bc2b26a10a10bf68
ACR-2aaca01ee50d407b9e8985672b18d777
ACR-48819328249b44be9439b622205e5885
ACR-ab69ed255cc74315b1850c3bec6cbe42
ACR-fc3aa8e3d6ee4f379361c746d828c86d
ACR-2472a76ffd934314aa861d9153776be4
ACR-492489760e9348aea4e99ec01c237f72
ACR-5392c751fabf499d8c6447e0b89a62f4
ACR-adaad11c4fa047ceaec214c012a24cce
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class DidChangePathToCompileCommandsParams {

  private final String configurationScopeId;
  private final String pathToCompileCommands;

  public DidChangePathToCompileCommandsParams(String configScopeId, @Nullable String pathToCompileCommands) {
    this.configurationScopeId = configScopeId;
    this.pathToCompileCommands = pathToCompileCommands;
  }

  @CheckForNull
  public String getPathToCompileCommands() {
    return pathToCompileCommands;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
