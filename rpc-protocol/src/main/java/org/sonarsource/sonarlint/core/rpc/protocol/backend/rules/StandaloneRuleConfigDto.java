/*
ACR-174da851bbb047c68b2483fb6f0722d2
ACR-b6d054ad543242d1baa1d6ad7f3b8938
ACR-2f78e28f7c484b9faa09db84134f3c71
ACR-a69adedb6a1e4a1caa98de901f972e2c
ACR-f225de9845644b29a5aea1ef01341cc8
ACR-2e252f07612640aa84ef5d9ec03fbe08
ACR-a5f65522aef3422b8ef00961593ca407
ACR-af334673a5fa492487908177e4fe08db
ACR-fe8d8815387a42fbbfe30d40194a007b
ACR-9aca7d547b874f8987a88c277a4ac93d
ACR-1c906a9939304b07972c366bb96bc297
ACR-7d0f717b33fa4ebeb4bc0e9aefb4c63e
ACR-bc91fe6eb641466481eedeb1e72f94bc
ACR-814bc6c39c11445191e77dda57f761ed
ACR-73961058c9344d5e8e967b5a6a691332
ACR-e20e395b18ec4976ba9e19c71eb8c0d3
ACR-c5df176928dd4f5d9a03b858413c6495
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.Map;

public class StandaloneRuleConfigDto {

  private final boolean isActive;

  private final Map<String, String> paramValueByKey;

  public StandaloneRuleConfigDto(boolean isActive, Map<String, String> paramValueByKey) {
    this.isActive = isActive;
    this.paramValueByKey = paramValueByKey;
  }

  public boolean isActive() {
    return isActive;
  }

  public Map<String, String> getParamValueByKey() {
    return paramValueByKey;
  }
}
