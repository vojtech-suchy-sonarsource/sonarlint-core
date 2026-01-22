/*
ACR-05a42ede71c14c8eb68bbb5118310d5c
ACR-9b668dd765d34ce2b346c87daed7c855
ACR-edc25e12edba4f8fac1e1bb7ddfaab36
ACR-31e763725e3d484abe46f2e5df5d7d58
ACR-4df72be6ebbb4665a859fb5454f57bc4
ACR-3a38989dad3b48a48f586c3a07ec06c8
ACR-cf25a63133a64e0388c64f8d51eeb5e3
ACR-7b0541b704744da6902440560c90cad1
ACR-0cc07b680dbe4bae89569ba36c37edc4
ACR-fe2eae422939499d8aa36b69db4b1cc4
ACR-c310397e680c4e45b2b09df50ae38392
ACR-af9dcea91baa4f2f9341609a509fc037
ACR-541046c838d142048bdbd5ff0e4c886b
ACR-007809e29c5d49d9ba18517c0a30dc95
ACR-9fa46594a7d04aaa94a6be524c88bcab
ACR-3ff63c04f8d149c4aabe34da78f1b85d
ACR-448a68a7c5574fb7a12c97610169dd7f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

public class CheckLocalDetectionSupportedParams {
  private final String configScopeId;

  public CheckLocalDetectionSupportedParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
