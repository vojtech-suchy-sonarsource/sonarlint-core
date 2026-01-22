/*
ACR-5bde6902bdc0412eadc88438fedc0558
ACR-45e20a5d18c74902ae03f6d7952aa724
ACR-cc1434d1e80f416eb45af8e010bee7a9
ACR-e204c594ff24410d8ae6a6cfe0b828ed
ACR-403620aade7a4ff88226422e344d4633
ACR-5e755ec7340c4c4fa83a99e38b5fc6a3
ACR-a08e7ca748574b44969ef71bafc16c2e
ACR-e285143ce32545d3bea826b951b62add
ACR-022764add12f4abe971b9d2f3bf90d77
ACR-87454fb58c964e9085bc65e4133478f9
ACR-bf434871255946c0b5c6d73cdf9a8639
ACR-607e7fd5478443c2a447faa5d9ca6dd6
ACR-cb5a0c0b1b4e4b03af9d09a63e3d090c
ACR-0e4bed5c56ba41d3b986c1592bde29ed
ACR-3efafd6bf46e45fcbd2866f44f7b7489
ACR-f35386f4181e4bc2a4c1b9bc52399d93
ACR-cb290e123db74438ad46dfbcb3ffbc2f
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
