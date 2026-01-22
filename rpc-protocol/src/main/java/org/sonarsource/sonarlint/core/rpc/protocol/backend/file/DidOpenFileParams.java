/*
ACR-5d80b846d2dd4987812f857fbe47fd59
ACR-1c99dd5e89c141e1a225a0e9c5703d03
ACR-01c6cc42d7f4419c89b38035995985e6
ACR-f77d1afd981b4811a6d403c1f60e0d38
ACR-a013c3db54cc43cd839dc394b59dbdb0
ACR-e40fae7b2e894ee3869541189c959f06
ACR-030305c5ae224a8e8f294cb34c9231e2
ACR-cf786686ea104886b5565dd1f4d36e82
ACR-e101074df6684982b0dc47a6de344392
ACR-bbee4e11e54140318151e46fb55809d1
ACR-8e30dbbaaa9e498da22d11e454e9405d
ACR-281737075a064748b55258c70ddb9746
ACR-ba2d20007c9f4d778578662161239122
ACR-a0a4cacf737c4e3b8f4ad2dc3fa7fe2c
ACR-fc7c6b35dd52418aa6824035d9657b89
ACR-fe6f954a8793479eb72903b7bfe69125
ACR-61e88eeb0bad4c8e85491d051f4269a9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.net.URI;

public class DidOpenFileParams {
  private final String configurationScopeId;
  private final URI fileUri;

  public DidOpenFileParams(String configurationScopeId, URI fileUri) {
    this.configurationScopeId = configurationScopeId;
    this.fileUri = fileUri;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public URI getFileUri() {
    return fileUri;
  }
}
