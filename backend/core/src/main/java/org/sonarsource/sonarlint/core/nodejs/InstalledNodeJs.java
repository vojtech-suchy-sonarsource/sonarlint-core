/*
ACR-1fbbc0d475864eab91df6f902787633f
ACR-373208b8b33a409bbac41fccdb2be538
ACR-e35a0a60e530482195c7d6892296d03a
ACR-a54f2c2690374f88aed9c938dde8c5a6
ACR-cb1c89219af94102bda7201165f57a10
ACR-4c697fc9d11040a88e7668d36fa3d9a6
ACR-82f4646c1480472c9b72d9164a8c523d
ACR-78ea84cf55b249589b2d1bbc66cb2df1
ACR-17e663f9bfcd4b1c8b9372fbb014ae5b
ACR-8fa96ffdecfd4fb09792c0ade2219022
ACR-a1b8793d2f5441d9a8ab3477a2bf12b4
ACR-7e3709962d6749ccb25ba7dcdb032cdc
ACR-4449e9e0e90946669d6128bdc00756ab
ACR-d4b8fa61393945ed9f72cddd91a5a226
ACR-e89eaacb33e04062b2c4919123c8f9dd
ACR-3a612c7c082343a0a9d9c2bac56489ea
ACR-9568fae988874f54a3f5f0eef0ef9e42
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
