/*
ACR-7aa8174aeec14159b5a03e9446180a2c
ACR-cff338245850400193271c89f69d1ebb
ACR-2682ff0a3b8640c9a022e7ae9738dda0
ACR-94d7972e63234aa8b6c738ff511c4977
ACR-b70133b3afe34ca29e2076309201a79f
ACR-59b3ff5b7a9449aca8afd70ae97c91a3
ACR-f162a3f6481c440aba709f35c78038fc
ACR-35e092c817494ae2a5ad1bf50a8bd107
ACR-c3a1015d18e04184af152c48357efa03
ACR-d24ff173232f49eebde5cd48c331c0de
ACR-94ffe8fcdd28403f8dc04fc7601ae0a7
ACR-4759b9b40fa540c098d218b290fd9ba4
ACR-ffe9bbf8bffb4a8aad9b0ad51259b2e4
ACR-641998d78cdd4cf69aac4bba474cce26
ACR-3cd80719066149399cbab6613c7c1f0f
ACR-fa0fc9a14aca4e67ac9fdedd73402e76
ACR-ddbb5cf912e344379da45defac7e3cc6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class GetFilesStatusParams {
  
  private final Map<String, List<URI>> fileUrisByConfigScopeId;

  public GetFilesStatusParams(Map<String, List<URI>> fileUrisByConfigScopeId) {
    this.fileUrisByConfigScopeId = fileUrisByConfigScopeId;
  }

  public Map<String, List<URI>> getFileUrisByConfigScopeId() {
    return fileUrisByConfigScopeId;
  }
}
