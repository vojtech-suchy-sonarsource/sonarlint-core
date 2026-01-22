/*
ACR-c45e0a35fe3245098e264facad53c4fd
ACR-3c6ac9f802684ccc93776f068605a85a
ACR-0fea1d5b3750429baa3cb8e9baeaf578
ACR-9c126efb95b34deaaabb48d38e889f99
ACR-7cb4c49f6c8d49ea9580397ff2c9539d
ACR-7611eae4153747a4b000f47f8f25ed6f
ACR-da9b44f7af2a42cfb0c847da239097d3
ACR-e9a752b7f78b49a98cf7bdbca69689e2
ACR-3f6fe20b958b4395862951d247500de8
ACR-685e175a160a452aa915cc87539e019f
ACR-27456b1f4e134259bf3e942b948d6ab3
ACR-86d1bb81f42842828d3ea26011b55c2c
ACR-3c72c42dabf04810b49782f77ea3a13c
ACR-9b319db75419476c82d97e7649e32c64
ACR-af7082cafcd443f08d1c500636e4ddf4
ACR-dee977b943704fb9a7cd2efb08c6bd22
ACR-d89848a9eca5480c97917f16c5371c85
 */
package org.sonarsource.sonarlint.core.analysis.command;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.sonarsource.sonarlint.core.analysis.AnalysisQueue;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;
import org.sonarsource.sonarlint.core.analysis.container.global.GlobalAnalysisContainer;
import org.sonarsource.sonarlint.core.analysis.container.global.ModuleRegistry;
import org.sonarsource.sonarlint.core.plugin.commons.LoadedPlugins;

public class ResetPluginsCommand extends Command {

  private final AnalysisSchedulerConfiguration analysisGlobalConfig;
  private final Supplier<LoadedPlugins> pluginsSupplier;
  private final AtomicReference<GlobalAnalysisContainer> globalAnalysisContainer;
  private final AnalysisQueue analysisQueue;

  public ResetPluginsCommand(AnalysisSchedulerConfiguration analysisGlobalConfig, AtomicReference<GlobalAnalysisContainer> globalAnalysisContainer, AnalysisQueue analysisQueue,
    Supplier<LoadedPlugins> pluginsSupplier) {
    this.analysisGlobalConfig = analysisGlobalConfig;
    this.pluginsSupplier = pluginsSupplier;
    this.globalAnalysisContainer = globalAnalysisContainer;
    this.analysisQueue = analysisQueue;
  }

  @Override
  public void execute(ModuleRegistry moduleRegistry) {
    globalAnalysisContainer.get().stopComponents();
    var newPlugins = pluginsSupplier.get();
    globalAnalysisContainer.set(new GlobalAnalysisContainer(analysisGlobalConfig, newPlugins));
    globalAnalysisContainer.get().startComponents();
    analysisQueue.clearAllButAnalyses();
  }
}
