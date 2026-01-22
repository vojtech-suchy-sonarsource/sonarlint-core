/*
ACR-6ceabefff4504f0489b98bb4bbbedd18
ACR-7650faea003a4973adbd7a4e444f643f
ACR-85eed0c6cf3847f6b0218f43412ec61c
ACR-ff086c2af5b94bc483c49b987aa03d8f
ACR-184046b126bd46eb9b92f2e4ad874177
ACR-4b4005aa45c840258a168ef35c83c81a
ACR-e29b1addc3a343db844b2dfd05e87f1a
ACR-d03b1ba099f942f1811b3b51a0a040fb
ACR-941593994e454481aa0d94d3430397a9
ACR-6e78b325fe2b40beb34aafa1fe9030f4
ACR-0cccb25226204db0aaffaa4a33260478
ACR-ee260835f0f2438a83df00ff2f99f08c
ACR-0c85c10a803e4a2b86f3503f71f0bf94
ACR-92e5b9b800e44976b44bd72e3c6ce99a
ACR-db343b680bb1404caa0e668b57cf2e55
ACR-76e98cb0d2b84865b79706da026de3d1
ACR-95200ae9c1624c45a7d93f50fcd1ca11
 */
package org.sonarsource.sonarlint.core.analysis;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;
import org.sonarsource.sonarlint.core.analysis.command.Command;
import org.sonarsource.sonarlint.core.analysis.command.ResetPluginsCommand;
import org.sonarsource.sonarlint.core.analysis.container.global.GlobalAnalysisContainer;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.plugin.commons.LoadedPlugins;

public class AnalysisScheduler {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final Runnable CANCELING_TERMINATION = () -> {
  };

  private final AtomicReference<GlobalAnalysisContainer> globalAnalysisContainer = new AtomicReference<>();
  private final AnalysisQueue analysisQueue = new AnalysisQueue();
  private final Thread analysisThread = new Thread(this::executeQueuedCommands, "sonarlint-analysis-scheduler");
  private final LogOutput logOutput;
  private final AtomicReference<Runnable> termination = new AtomicReference<>();
  private final AtomicReference<Command> executingCommand = new AtomicReference<>();

  public AnalysisScheduler(AnalysisSchedulerConfiguration analysisGlobalConfig, LoadedPlugins loadedPlugins, @Nullable LogOutput logOutput) {
    this.logOutput = logOutput;
    //ACR-4dc3d7a65a8047f6944d782a7f2991ec
    var analysisContainer = new GlobalAnalysisContainer(analysisGlobalConfig, loadedPlugins);
    analysisContainer.startComponents();
    globalAnalysisContainer.set(analysisContainer);
    analysisThread.start();
  }

  public void reset(AnalysisSchedulerConfiguration analysisGlobalConfig, Supplier<LoadedPlugins> pluginsSupplier) {
    post(new ResetPluginsCommand(analysisGlobalConfig, globalAnalysisContainer, analysisQueue, pluginsSupplier));
  }

  public void wakeUp() {
    analysisQueue.wakeUp();
  }

  private void executeQueuedCommands() {
    while (termination.get() == null) {
      SonarLintLogger.get().setTarget(logOutput);
      try {
        var command = analysisQueue.takeNextCommand();
        executingCommand.set(command);
        if (termination.get() == CANCELING_TERMINATION) {
          break;
        }
        executingCommand.get().execute(globalAnalysisContainer.get().getModuleRegistry());
        executingCommand.set(null);
      } catch (InterruptedException e) {
        if (termination.get() != CANCELING_TERMINATION) {
          LOG.error("Analysis engine interrupted", e);
        }
      } catch (Exception e) {
        LOG.debug("Analysis command failed", e);
      }
    }
    termination.get().run();
  }

  public void post(Command command) {
    LOG.debug("Post: " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
    LOG.debug("Posting command from Scheduler: " + command);
    if (termination.get() != null) {
      LOG.error("Analysis engine stopping, ignoring command");
      command.cancel();
      return;
    }
    if (!analysisThread.isAlive()) {
      LOG.error("Analysis engine not started, ignoring command");
      command.cancel();
      return;
    }
    var currentCommand = executingCommand.get();
    if (currentCommand != null && command.shouldCancelPost(currentCommand)) {
      LOG.debug("Cancelling queuing of command");
      currentCommand.cancel();
    }
    LOG.debug("Posting command from Scheduler to queue: " + command);
    analysisQueue.post(command);
  }

  public void stop() {
    if (!analysisThread.isAlive()) {
      return;
    }
    if (!termination.compareAndSet(null, CANCELING_TERMINATION)) {
      //ACR-2d3cf254f00c450dbe28d179612ff7e0
      return;
    }
    var command = executingCommand.getAndSet(null);
    if (command != null) {
      command.cancel();
    }
    analysisThread.interrupt();
    analysisQueue.removeAll().forEach(Command::cancel);
    globalAnalysisContainer.get().stopComponents();
  }
}
