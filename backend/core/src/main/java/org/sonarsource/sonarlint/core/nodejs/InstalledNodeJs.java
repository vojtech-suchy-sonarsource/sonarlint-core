/*
ACR-82729f0c51dd4ca1a8b6adda78a72269
ACR-70aafdb3cdea4a989aa4a1a98212bb4a
ACR-95d8d5b2e09f48e98b70699bcb09e1ce
ACR-5725edd3a5374b0f819e753f4f0c7dde
ACR-fd45fed840464e29a2ba3f8cc7dbfc00
ACR-9c6764c617b1442b90cf657cc30eef0f
ACR-a61be05c274749fa90a70c8209e5c947
ACR-77793ea5c33b45598d36e28000c41e0d
ACR-5eeff580863b41c08957636977fa284d
ACR-0eecd09ffdb2492dbcf8fc86b42a01d3
ACR-25f85e29fae6470ab23632a5dde8ce29
ACR-104d9930de6c4a678f1342127c67f7f9
ACR-e71a46d83e2f4ccb92f03b3ec5a75c4b
ACR-1ed02f5417f3479f80f9cf17f45c352c
ACR-6990578fbaa24fdfbaf40800da43ccb2
ACR-b16864e5add1433e8bfd9bf82d7c6917
ACR-85d5c2c94d65490687a74a30cafa9291
 */
package org.sonarsource.sonarlint.core.nodejs;

import java.nio.file.Path;
import org.sonarsource.sonarlint.core.commons.Version;

public class InstalledNodeJs {
  private final Path path;
  private final Version version;

  public InstalledNodeJs(Path path, Version version) {
    this.path = path;
    this.version = version;
  }

  public Path getPath() {
    return path;
  }

  public Version getVersion() {
    return version;
  }
}
