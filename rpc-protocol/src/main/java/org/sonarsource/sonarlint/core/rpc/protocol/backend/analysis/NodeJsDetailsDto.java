/*
ACR-b2f7fae71d7949a9b4eaf152967bdbab
ACR-a8a60406a6cd43c594626aca242708f3
ACR-3b68974476834495b73b5c9cf724230b
ACR-bb99a98a4bf940b9b7be6850ed64053b
ACR-9e57123e7c564c778f04d3c2fce4aaf0
ACR-5ba035e5e1a44a9e966bc46e8f20b5d5
ACR-e00aa51492354af38dc5f6455ed0ad6b
ACR-42b74071f2994e528432f75dc30a5bc8
ACR-7b30d690d1794727b6bd91f343738968
ACR-326366087986421ab5c94fddbb1e02f8
ACR-25be9cb4184a40809ff167bfb0fa2314
ACR-79271426f52c49cb84b58310c9598a10
ACR-8208bfdcb8804afc8ae1b4b8c85783bc
ACR-25cf1f85835a4f3eabf5cfbd3d91cc24
ACR-cbe8eef84d3744d2ac9851c5087a162c
ACR-c420b8d92c9d40de9618b4e0e1ae68aa
ACR-a9a24e57d3e1414ea61105aefc629fca
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.nio.file.Path;

public class NodeJsDetailsDto {
  private final Path path;
  private final String version;

  public NodeJsDetailsDto(Path path, String version) {
    this.path = path;
    this.version = version;
  }

  public Path getPath() {
    return path;
  }

  public String getVersion() {
    return version;
  }
}
