/*
ACR-66f79bc31ddd4907b9ef91b7a2dc2719
ACR-166f012aeac04dc4bc3d6c122a9385c2
ACR-9f570048afa3435fbceb2a059e7af060
ACR-744f75c681194f76b98a68d4c4f84343
ACR-caed16cc5db041d3bab45097c476e0e8
ACR-5dd9ffd2686743bba643a2bace5f62b0
ACR-cfa03c79899d4a61b1c852536feaa891
ACR-394f5b5afdae45dcb535a82b6987ae3c
ACR-65a54e0657fc435c8d227d2b406dba36
ACR-8af3c2b2bef94a4882b094fe9f28f955
ACR-a0e1d9f60f1e4b3faea12c4f7ee63768
ACR-80a85678b7a54a3fa43bed5d2a806728
ACR-1aab2b329c49474cb3cb1cbeeca715f3
ACR-ccb5665515fd49849e47d8d75aef8d2a
ACR-20d6f243e2f94266b3a0bf2cad2c2aa2
ACR-1e952786eb694c74ac9c4edfad5a96e1
ACR-f2a31cf78e5747b2aa2bfc30907aaa05
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
