/*
ACR-0b9339ea21ab484696f4831cc8c74912
ACR-4b4fdfbbb1bf49c78fcb6625b3d4ed0e
ACR-5ec11c6acb484c5ba8dcadd5dede2c96
ACR-fab22d965a9f4f9ba8b3da99b46ba0b5
ACR-a89a47fdd35742f4bdb5dba3aff30119
ACR-e504845e8348494b9cda3501267d2b0a
ACR-2c157f31efff4221b1292f8a0ad5a65e
ACR-804f2f63b5bc46ea98d8b6c6f164f22e
ACR-c59067598cfe475b82c6f464b97d50f4
ACR-3b9bf2b29c5f408daf9041439cf618e8
ACR-dcc0e1c50f54466eb8e20ee76790a77d
ACR-e73a525ae66e4018afe6f9ffa664b51a
ACR-d6839ab80a6849518bc3e8f3d33f0a9d
ACR-aceaaac749f147f18d365f5fc6c29329
ACR-8083dd5ffd5247bfbee6bb99429c25c0
ACR-7c2084a1e7bb43a18bc70a026075ef88
ACR-836798043bf5437488828875a7fca5a0
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org;

public class OrganizationDto {

  private final String key;
  private final String name;
  private final String description;


  public OrganizationDto(String key, String name, String description) {
    this.key = key;
    this.name = name;
    this.description = description;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
