/*
ACR-657ee60fd7ac44e19b3aa24992f9785e
ACR-1df9325498404996866d3c5d7af8f202
ACR-9d6331758e9b41c1b209271a834fe219
ACR-a3dc9ce747fb4bfcbc44e4e535605324
ACR-a1f099ecbf654a489e03443dadb4394b
ACR-942d16a82f15418dab8f38ed87929020
ACR-5f39c26536d34e7ca2202c3e707281c2
ACR-4988b311a5ff4bff8882fab9aa03f1e2
ACR-08a26c5817b4466db2a9fbb621f70551
ACR-09a6481755cc4ac4a82cf8fb110ed466
ACR-21a50edafce1458cba02f329f010dc41
ACR-41d8c89b03bb4d038e22d17ea1163e50
ACR-4276d7918a3940f8bb0e939af7db6eed
ACR-63d6cace9cfa44d5b9189fde7fed9362
ACR-7ec745a9be60446f9cb9df63b5f00385
ACR-ea87cc1c2166480aa35836e8abeda348
ACR-bd998935fb3e49dda8cb7d9e2c175aa4
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
