/*
ACR-c1cd414ce2394c91907ae6fb9544394e
ACR-688882e45190494989fbac5cec5f41f9
ACR-242ff7c2ee1c4ec191363b7335ba6089
ACR-4370c61dcdc441d3b1b7366e9d10f4e8
ACR-bbcb08e359824097a7349c2a1025e06c
ACR-faa0cd15855a42f994c1ae53b38b349f
ACR-3ceb67a5743744f4986424a9cafd188c
ACR-40cbf7b3c0da485a91a02b812bd5e6a6
ACR-004237321a0b44c9ae62fac9240dccc4
ACR-413c4864bc384f5888f3ad0c644856ed
ACR-0ba0d89fe03340b2b4c1a6d9c112afe3
ACR-cdaec6c3d6f446c6b9b58750d61c5116
ACR-cc1c5af45466489a98b1f3c9e7ada467
ACR-2ece18e0bbff403bb02fb950736d6bcc
ACR-3265482160c94912adb8034b51d8d178
ACR-6082f3a08f8f4970bf51b10171fe44b6
ACR-61e4aa4cd8a64818a19cb037fd8f2dcd
 */
package org.sonarsource.sonarlint.core.fs;

import java.util.List;
import java.util.stream.Stream;

public class FileSystemUpdatedEvent {

  private final List<ClientFile> removed;
  private final List<ClientFile> added;
  private final List<ClientFile> updated;

  public FileSystemUpdatedEvent(List<ClientFile> removed, List<ClientFile> added, List<ClientFile> updated) {
    this.removed = removed;
    this.added = added;
    this.updated = updated;
  }

  public List<ClientFile> getRemoved() {
    return removed;
  }

  public List<ClientFile> getAdded() {
    return added;
  }

  public List<ClientFile> getUpdated() {
    return updated;
  }

  public List<ClientFile> getAddedOrUpdated() {
    return Stream.concat(getAdded().stream(), getUpdated().stream())
      .toList();
  }

}
