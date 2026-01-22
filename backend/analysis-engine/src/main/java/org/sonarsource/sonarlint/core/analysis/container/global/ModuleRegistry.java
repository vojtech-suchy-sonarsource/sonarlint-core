/*
ACR-efc611e4fcd343c2a0f32510be0975b7
ACR-53c517d80f4049e985ec8a23c8e7370d
ACR-a6e597b82e5d46178cdfb9d0bbcdf91c
ACR-4ff273fe1af142df975f137b5fa1d035
ACR-ddb0738cfb9149e8b488d3e5b0c4fd04
ACR-8908901b267f4eeb8644a673e67b2889
ACR-c9a6542bb44d40e79b093af0a0f277be
ACR-4cbc65b0bb7347f484a83494a62bcc2b
ACR-b7bf29e0c64a4fd7b21e05b3f06e58e4
ACR-9afb4305563e4e5ba53894d3b39cb10e
ACR-e07b7280c5cb4ec5baa3ea74eb1780dd
ACR-6d44e9c6394441aca48ffe7097cda159
ACR-7100fe44bf9b4d0880e9318886b04e23
ACR-fc3bdbeae2e84f8989d32a66dbd81e35
ACR-c38c382bc5ba4262abebc9cb667951bc
ACR-22b7e10462e7441d8931c9b5bda27a5f
ACR-26ced2dcf7da42268192dd5488ee4821
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.analysis.api.ClientModuleFileSystem;
import org.sonarsource.sonarlint.core.analysis.container.module.ModuleContainer;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.plugin.commons.container.SpringComponentContainer;

public class ModuleRegistry {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ConcurrentHashMap<String, ModuleContainer> moduleContainersByKey = new ConcurrentHashMap<>();
  private final SpringComponentContainer parent;
  private final Function<String, ClientModuleFileSystem> fileSystemProvider;

  public ModuleRegistry(SpringComponentContainer parent, Function<String, ClientModuleFileSystem> fileSystemProvider) {
    this.parent = parent;
    this.fileSystemProvider = fileSystemProvider;
  }

  public ModuleContainer getContainerFor(String moduleKey) {
    return moduleContainersByKey.computeIfAbsent(moduleKey, key -> createContainer(key, fileSystemProvider.apply(key)));
  }

  @Nullable
  public ModuleContainer getContainerIfStarted(String moduleKey) {
    return moduleContainersByKey.get(moduleKey);
  }

  private ModuleContainer createContainer(Object moduleKey, @Nullable ClientModuleFileSystem clientFileSystem) {
    LOG.debug("Creating container for module '" + moduleKey + "'");
    var moduleContainer = new ModuleContainer(parent, false);
    if (clientFileSystem != null) {
      moduleContainer.add(clientFileSystem);
    }
    moduleContainer.startComponents();
    return moduleContainer;
  }

  public void unregisterModule(String moduleKey) {
    var container = moduleContainersByKey.remove(moduleKey);
    if (container != null) {
      container.stopComponents();
    }
  }

  public void stopAll() {
    moduleContainersByKey.values().forEach(SpringComponentContainer::stopComponents);
    moduleContainersByKey.clear();
  }
}
