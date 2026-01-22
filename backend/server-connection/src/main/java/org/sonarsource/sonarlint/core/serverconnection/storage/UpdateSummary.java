/*
ACR-f3d4d1c5f1804f58beae48556e0d56f0
ACR-3ca5c77a05094634b52f0b55c361b9ca
ACR-5cefd442ac974cd2b7ff5b4f6160165a
ACR-85407aad68124ae38f370871316c4aeb
ACR-91d050fe84324cfb93d348a379a06bff
ACR-46310c1a091248879c0cc4d509373103
ACR-4d1c4d42c6804b5384d8f102ef8fc369
ACR-ad9e2a3e397e41968ce4c434b955a5b1
ACR-ca426406ee964c75af9cb7fd06aa566a
ACR-8448a0aafb6a4273ac2850f32bed007c
ACR-a3535ea879124194907542e738f6f579
ACR-b2d0a5a40e5446c58412ece445cd9326
ACR-2a40f07769da4ef2a356fcba4057e728
ACR-a2a4da1be9654e90bdac077766ec2c6c
ACR-4b06bb0b9c224ebc963ee35f462f961a
ACR-f2cc1f61ebb44cd1956c56f42da9d518
ACR-e05da7e6a022482c9535e44c48142612
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
