/*
ACR-c32c0c7d0f334d43a42ca25ba49ada70
ACR-e8afeaacdbd84c4bb1b492c74f1d6b78
ACR-e3118b2ad0814678a948cda1a225239d
ACR-803a1d2d455845c79d89a24d1cff06d4
ACR-938b53ff72dd40b19caee705ee682b09
ACR-6b6be54187054beaa56e605bc221957a
ACR-1d3cf4b642594350978c5fdffb4dd50a
ACR-77ef2447fa2542b6b0a039e8ff21434f
ACR-45e0d60d65ea4f9bac0e86517220bda3
ACR-ad5108c3c54949508e1df91f1edf5647
ACR-ec2ae118e4a94cce95222c2c87c5f290
ACR-c7a8d40368394ce58a7359945c0807f7
ACR-26020a4c8de1477d92861c8c652d053e
ACR-eff942564e0a455395d787b01f6840e5
ACR-c91508b64746498ba00d0ac6b8c18651
ACR-300ece4f515f4cb98af22003d3551a6c
ACR-15baa1ebf84e43c39dc49ce6e9de96f0
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.net.URI;

public class DidCloseFileParams {
  private final String configurationScopeId;
  private final URI fileUri;

  public DidCloseFileParams(String configurationScopeId, URI fileUri) {
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
