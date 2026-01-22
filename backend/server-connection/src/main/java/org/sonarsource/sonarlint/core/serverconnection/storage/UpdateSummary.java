/*
ACR-94d07b47e6ce41f9a01279a2342ffb01
ACR-ceca79c9c7f845baab84162a0bf7d86a
ACR-d34e30f910af4e3d85a7da68b6662537
ACR-ec2ec4ff99454e49a90da05d1117a25a
ACR-f3b29989f8704f9f96214080cfd320d3
ACR-97b6a414b13a48acb6ddfd8d34877f27
ACR-ae514420e14541db82d74a068ec2065e
ACR-e4a1774c08d04ed39d4221866b99c278
ACR-fbaeeb29eb8e47619633c8e42568d95b
ACR-ed2bc816087c4986bc887352b4de8c9e
ACR-d290e8c8f7a644b3a836914ed6c22ef1
ACR-7a40341b19ef4b4daf6594f79032c7ea
ACR-3733a856fbfa4b58ad38417062d8b5ae
ACR-94b5d0bf76c94abe86d4b7dd00ddfe33
ACR-2c82bde3c81e4e55b67e1c63a2aaf5ac
ACR-0d8c65133a1c400ebd53aa541a9a8f3a
ACR-0a47686a2b2b4d339e9772df695a61e7
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UpdateSummary<T>(Set<UUID> deletedItemIds, List<T> addedItems, List<T> updatedItems) {
  public boolean hasAnythingChanged() {
    return !deletedItemIds.isEmpty() || !addedItems.isEmpty() || !updatedItems.isEmpty();
  }
}
