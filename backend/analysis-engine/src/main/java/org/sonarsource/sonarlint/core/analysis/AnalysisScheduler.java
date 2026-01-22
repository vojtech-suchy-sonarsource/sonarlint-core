/*
ACR-e60aa9becac147809dfdc8cab223249c
ACR-400e960d87a24fc384a91eb2b1364ccd
ACR-faee7028525f4347941032efdae595ad
ACR-f1f621341c264acfb8f13c5f1dbd8edb
ACR-bc3d53b91e6c497298eb942a22f04507
ACR-a82b92b0c579459292b27cd5a6adca17
ACR-5b6db018fe594a8780ad87bbea4ee21e
ACR-ba2b861299d14186b266c29e19a953c3
ACR-f434b2f029e3496096b7b61ab049834c
ACR-25292b316bb747c5a528d8c36005da1d
ACR-7efb5e82d4f4413a92f185ed77d8c63a
ACR-65781e99bad54187b16ecf640e772a73
ACR-2b11265240494a1c96a0e20d3f0996b8
ACR-5469027968374bd3aa41c8be8e786f1f
ACR-3544f29276b84190bb3c17852a369d60
ACR-8d0e17572ae143d498f9510bb85c931a
ACR-853542d39fed43c8997fbbea67046e5b
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
    //ACR-47add125ff76414da4ba5e6e242dcca3
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
      //ACR-35e1de977b404a32906579c12bc5f208
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
