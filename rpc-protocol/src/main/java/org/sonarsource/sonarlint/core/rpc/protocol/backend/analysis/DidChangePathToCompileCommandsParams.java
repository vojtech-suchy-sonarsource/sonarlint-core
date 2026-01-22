/*
ACR-2cab1a49af6349268d3fb9a478050c96
ACR-c5550b02aaa44e76ae0c4901a3aee161
ACR-c53e8db776854b72be6848f86c285816
ACR-019d530e2a7e4084887933aa440f9a7d
ACR-5b18f8bedac34e5f884f235e6cd68c22
ACR-ad205b7ae0c54450bcdb1c57bb3bbfaf
ACR-c46de5fa2ec141e98507e37fa06048ca
ACR-668a3df6b6c44141a4f8bab35ed33bfb
ACR-3f8d1bb71adc488fa11e4d32fb200bd5
ACR-c8d3a97b31ad42698d77238890e21c34
ACR-a5014b66621b426cb4a10332644b7c21
ACR-868a4f0400e24cae96f80fffc05054ce
ACR-1407e46cc06e46878b1d0f04446f0fee
ACR-8cf745d8dd8b426baea8f54edf699e8b
ACR-a2fd79894b004916a84644b47226f443
ACR-ed108ab75efd4c1098ed5a686841a08c
ACR-8a69e5f243af41b8880661bd6c3438c9
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
