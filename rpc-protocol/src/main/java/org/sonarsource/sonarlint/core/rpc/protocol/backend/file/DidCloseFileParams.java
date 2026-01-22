/*
ACR-92f67bca9a5d4eb595c989599c7441e2
ACR-46ec645e56b648379494551d36603b38
ACR-ac0590ba8b144ebc829838371e4e3ea6
ACR-4ed10c7542dd4cbba8b480dd8a1e0684
ACR-cb2b42e002e1418284449425bcfdfd38
ACR-89a43bbdb00a45b99fd158dc2e1daef7
ACR-51197e82d0da4e8f9cecc146535fd4d4
ACR-cf0ea4c7e68c46a99bfbdb30e04a19e0
ACR-e08eab86c7144ddf96e651c90c6bf5b1
ACR-ab001574874946d4baf7d9d50633891c
ACR-ae9c266395d04f07a75cc415eb4d94e8
ACR-4a094aa58c8c4dcab1e267cec94f8d62
ACR-3864602c472e4451b10c3c4643508e54
ACR-92d9d9cb65e3446389df244b4a2141cd
ACR-f2e1800d2f36423a9d46371eb0826bb3
ACR-4f351ee9ead1436cbcf1ff2960856578
ACR-9cbfee99229643a68697756ad796d30f
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
