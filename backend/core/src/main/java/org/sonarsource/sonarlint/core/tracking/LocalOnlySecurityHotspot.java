/*
ACR-0ff7c865cd8543a6a415778bfbe293db
ACR-d4691c993986473fa38d4e2414b81f19
ACR-426effe9e3dd48b0a21cf42176d1f69b
ACR-8c3c031eb33d4c8d91e4d053fe4901c4
ACR-1f7a9d5c9ca04a3182fd5afc052fa0c4
ACR-daa6171f431c46fd99cf3b5b64bb5a59
ACR-5ec334c4d2054e2cb9f20b423c533059
ACR-bb1b3e5b78644ba19395abaaff891e18
ACR-a5d4d6f91d9e4c1d91e34688433bbf2e
ACR-11873acb976d49a3a8eb990466d618ef
ACR-112c359a2d444e6c84dc3deba39a467f
ACR-b6818df5920f4b41af034756edb76f5e
ACR-92010aa08ba344dcb46f3c5cd9a6795f
ACR-c6a6a4a4e3d149cfa638393cce5537b2
ACR-9c7223a925a04f67aaa4b0148ed9c2c3
ACR-3d4e55247cf242ba9a2b571b59d42928
ACR-8bf4cc4e29b84af7b66177e171c6be15
 */
package org.sonarsource.sonarlint.core.tracking;

import java.util.UUID;

public class LocalOnlySecurityHotspot {
  private final UUID id;

  public LocalOnlySecurityHotspot(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }
}
