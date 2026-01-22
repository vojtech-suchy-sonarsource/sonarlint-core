/*
ACR-73f79e239b644910868e71548ac11749
ACR-2eb1d046664f46f49f69b4060b587992
ACR-1f364d740ecd4ddc98822509817d01ff
ACR-575a5edf83c343578c91db5159a60d50
ACR-5aabb4bdbd064f0e86ee5a6b6599d965
ACR-d2922f006fa5431f8e74aa4d0769c5c5
ACR-426c02ce9e70478c8412b74e8cb3c4f0
ACR-75c654747c6943d09dc2cb9033816849
ACR-af4b9a8c5bf3419383f36dc34b43aa75
ACR-572616738d354c39a2b77d3d658bf537
ACR-625b6a51f82d45be95d168b712bd9969
ACR-dbed1417136047229810f903be6e6970
ACR-61168da05c734a3ab3ad598da1a929ea
ACR-2d5e8e9127d74149b91c4bee4eeab758
ACR-9a7ee7c40593411684e7b3732363188d
ACR-7f79b5dde25345f69cb05cc29b73f90e
ACR-f5fcfadd56fa48e9bb86b73bd836fa24
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
