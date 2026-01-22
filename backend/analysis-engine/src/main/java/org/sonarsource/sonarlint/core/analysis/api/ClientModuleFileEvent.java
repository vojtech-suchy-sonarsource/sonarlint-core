/*
ACR-9d6c9898069f48c1a80b8644f7694dfd
ACR-c118ccecc23440f4afad8476542e3c26
ACR-bf29182a8c534c3882c4680d68165384
ACR-97b996516c1a4c3fa315e647ecea406e
ACR-5a4ab86a50d647dc89f38cfce9be8485
ACR-a508123beafd48dabdd8647a12250475
ACR-1f989161df26474dafa30e8dbcdc8eef
ACR-f31d9d326e1b4763826fee07149bea7e
ACR-621f808a979f474285a2be1587f3cc91
ACR-6e5451362296456e8bd8f20f7239ceb8
ACR-4befd48050f24c2ba565882669a70d57
ACR-7875a45098aa4bda8bad97429e7357bd
ACR-50887a95768e4002a8620ff04d174ff2
ACR-07eccb34f0ca45269edf62f151c927e2
ACR-3acdaf445bf54bdb801b79ad4af3e4f5
ACR-ccadb7e0069e4b87a2949996f536038c
ACR-442316913834447d87e2f28ccc0d0cf0
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
