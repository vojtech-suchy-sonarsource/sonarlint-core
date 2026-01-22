/*
ACR-c0de4f48fd1c461687fa39b4523f6696
ACR-5016dc1c2c3d4058b419df5b7076c18d
ACR-5955f91851744940b117ff79708ff121
ACR-6be87944e13249dea9bccbe619926bfd
ACR-fec7715bd45c4366b79cf163c6ee88ac
ACR-96d2e7dae45c44faae6650025a1cfefb
ACR-b767af0bbec24c3c9e0e56c950cd2aa3
ACR-73666153d61c4799970c9e0b4aaae40a
ACR-ae9056218bfa4ca692c8aa1fad42ebfe
ACR-d3c6fb6a16b44a2599fed358c46e285c
ACR-7026b7ec09c04ceab8f5ad495f045bb0
ACR-dbb3d5d84b394eb59e372b87b9676d80
ACR-d9affb9838ed492d9a1db84c04e4fd5d
ACR-9f3145fe65954170a2e3e74e8959f26b
ACR-0cbe442825724bd8853bf030c4463459
ACR-4d44311290324f478aecc8f4febfc6f8
ACR-b8b305719dce468cabc91afe86fc1a30
 */
package org.sonarsource.sonarlint.core.analysis.api;

import org.sonarsource.sonarlint.plugin.api.module.file.ModuleFileEvent;

public class ClientModuleFileEvent {
  private final ClientInputFile target;

  private final ModuleFileEvent.Type type;

  private ClientModuleFileEvent(ClientInputFile target, ModuleFileEvent.Type type) {
    this.target = target;
    this.type = type;
  }

  public static ClientModuleFileEvent of(ClientInputFile target, ModuleFileEvent.Type type) {
    return new ClientModuleFileEvent(target, type);
  }

  public ClientInputFile target() {
    return target;
  }

  public ModuleFileEvent.Type type() {
    return type;
  }
}
