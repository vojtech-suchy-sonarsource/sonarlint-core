/*
ACR-04d7016572254353931682045d4db5cc
ACR-fb299040ba104a5e897c109aa6215bf3
ACR-1a3ddb1e37e5408d82115e3f5bc44176
ACR-9cfa0e98971245bfbcf31b0c94be6369
ACR-23a79ca4f7f64b59a30d746f94f56283
ACR-709deafafc734d13b6a077f680f0b007
ACR-80e83fe0190940aab7c528d99e593a5a
ACR-b3f3da84875f482d9c449773ada355fb
ACR-473d2292648e4e8fac24f29aa06d25b2
ACR-b17efc64103a445280e6f775303888d2
ACR-e9e4c33853d24905a408cafd7a377d4d
ACR-9f1ac304f24941238004cc27b72bb008
ACR-87a01f59e2a4449aba3276c456cb02e9
ACR-062a5ddaf2494d81ab84280d631726d1
ACR-234f2a679f8343619d3186db69e29eac
ACR-036b7ab5b2f8449997557cd516339970
ACR-020357c489c94add8e22fdd2026c9546
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.binding;

public class GetBindingSuggestionParams {
  private final String configScopeId;
  private final String connectionId;

  public GetBindingSuggestionParams(String configScopeId, String connectionId) {
    this.configScopeId = configScopeId;
    this.connectionId = connectionId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
