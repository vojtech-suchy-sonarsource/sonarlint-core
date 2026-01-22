/*
ACR-c9a4352f132f482682a5642470b1ff28
ACR-00e9d660462a45f0b1041860dc7919ec
ACR-fe2c419d6e794c729f7d85364da3b1b4
ACR-44aed1bd27b34e5c8b4aef69356bf881
ACR-d290e137d1ec48c3aacec22dee1b4d58
ACR-35824256a9654735a27f11c31c97107c
ACR-0611b6497e3c48ffb2ebf9385f0ef03a
ACR-6d85b884e19241b6ace67a080b384558
ACR-2b223cb73b0d434d9ba1034304e9cae6
ACR-caee295f16244172893f986089409cc9
ACR-d18702640b4f464ca95c217aff9f4411
ACR-427d4c77c7a74ea7b83ff675264d00e9
ACR-63ecee20d8be40fc88244b57af9e149e
ACR-161e453d383f4b5ca6eeaba8b6cc5f4c
ACR-3d6ab282ddd445bd8050272f9bcd5f96
ACR-c5101b4cb994402d9150eb1bfcca3f82
ACR-67f4592cb00c410c9bd63b0023fb019d
 */
package org.sonarsource.sonarlint.core.serverapi.branches;

public class ServerBranch {

  private final String name;
  private final boolean isMain;

  public ServerBranch(String name, boolean isMain) {
    this.name = name;
    this.isMain = isMain;
  }

  public String getName() {
    return name;
  }

  public boolean isMain() {
    return isMain;
  }

}
