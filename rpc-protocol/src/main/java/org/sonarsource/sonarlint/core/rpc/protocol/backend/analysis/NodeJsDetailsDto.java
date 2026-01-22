/*
ACR-60cc00ea464243a286d9ad9625b7c982
ACR-1d09d8f194fa4ce6af36919029b5dd03
ACR-4ce4025716fc48b3ba3f8ed31854061f
ACR-14e8d155af5f46a6a3364aee7e639f2d
ACR-8498d415c90644bcb33784b2aa498d40
ACR-eecdaa9d95614c919e74a4c9d6c8a05c
ACR-842761dbd7264c7bac9629b403e05991
ACR-a45e564289474d9684fe45abfa893978
ACR-81a2bc394153434ba616988165fb166b
ACR-05d3f1b648df4be6affa2cef92221a09
ACR-95346c0760ed4326bb4a25f3cb52b8e6
ACR-e6acfb8bc2a54c449842dfff8a0f9d41
ACR-2dc6cad09d9d45899a78257388cbec9c
ACR-554d34e438374a7699feb6a45a8abd7c
ACR-3fa7aee508664c97973caa56377c2a2f
ACR-b5599c0aab6a41cbbe443123e35c2ae6
ACR-b61b413eba5d41b68634479aa7cbf439
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
