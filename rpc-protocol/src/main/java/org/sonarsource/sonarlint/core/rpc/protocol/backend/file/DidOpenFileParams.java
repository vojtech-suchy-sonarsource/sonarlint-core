/*
ACR-97c927c98f2643808ec87a5e8c259ab5
ACR-7f2cdbd3436e431bb3330df557f76b0d
ACR-854bd5b8d6134a62af21b3145751a301
ACR-e7c86d7bfb494d33b10417daf4f0626a
ACR-2862b8b47f5d4ba498f88e7dc29051a2
ACR-844759e835a544f88baccbf6e5c96c8d
ACR-e99a12b3d3e240cb8cabb9f2256e17b7
ACR-58bcd5b8a28349fc8ed76827b56d0653
ACR-ee4b4e1e665b49448e39a51c288339e6
ACR-99bfd89957164bc4a6e61141bee44409
ACR-b1dd67f5937a454c8fb907d3b60620db
ACR-1c7323187abb4af7be08e679e9adf443
ACR-4f52ccbf30a24a4094a951f7ea0ede48
ACR-107c15997aa647e392cc6b0ddf8f7f3d
ACR-60bacfbdaa344d979c84bfdd5d883702
ACR-7cf78f0fbbb849baa3aa46fff5fa2e41
ACR-52327cb8de6c4d4aba50f69502775738
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
