/*
ACR-39748f107e5e4b5ba314151a34c52374
ACR-7838ff191f1b46d0bbc888191e3e477b
ACR-1270ce337e3441c29894cac880c68612
ACR-e312f8f357f64941a3d8c34016d4f394
ACR-99b7ce32b7dd46868aebe1df1209c6c2
ACR-2fd7a3e711e94bda897d393a8a3c0d1d
ACR-ab2176cde27b40149043526606449f2f
ACR-951c473c5f364019b80912c693e9ba9a
ACR-4fa15bee055e43a2be450dfb01f73bf4
ACR-a0a2f0739b7e48e4bcf3f728d999d3ec
ACR-02e7b5cccb1b416cbe530fc4e476c25a
ACR-e5dfa68f42cf4899954b8e81eae316c2
ACR-6aad9b15afcb40ddae4e11b9f8438ea6
ACR-873f235e9f2240bab609cc4e2fbcaa67
ACR-c9cd48d6d5ac439e89632e4089fb3e29
ACR-51db9bc049db4b59b2c67525967cd791
ACR-48dd8c7ca52e444ea9bfc537d2bee170
 */
package org.sonarsource.sonarlint.core.analysis;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.analysis.api.TriggerType;
import org.sonarsource.sonarlint.core.analysis.command.AnalyzeCommand;
import org.sonarsource.sonarlint.core.analysis.command.UnregisterModuleCommand;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.progress.TaskManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AnalysisQueueTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void it_should_prioritize_unregister_module_commands_over_analyses() throws InterruptedException {
    var analysisQueue = new AnalysisQueue();
    var taskManager = mock(TaskManager.class);
    analysisQueue.post(new AnalyzeCommand("key", UUID.randomUUID(), null, null, null, null, new SonarLintCancelMonitor(), taskManager, null, () -> true, Set.of(), Map.of()));
    var unregisterModuleCommand = new UnregisterModuleCommand("key");
    analysisQueue.post(unregisterModuleCommand);

    var command = analysisQueue.takeNextCommand();

    assertThat(command).isEqualTo(unregisterModuleCommand);
  }

  @Test
  void it_should_not_queue_a_canceled_command() throws InterruptedException {
    var canceledProgressMonitor = new SonarLintCancelMonitor();
    var progressMonitor = new SonarLintCancelMonitor();
    var analysisQueue = new AnalysisQueue();
    var taskManager = mock(TaskManager.class);
    var canceledCommand = new AnalyzeCommand("1", UUID.randomUUID(), TriggerType.FORCED, null, null, null, canceledProgressMonitor, taskManager, null, () -> true, Set.of(), Map.of());
    var command = new AnalyzeCommand("2", UUID.randomUUID(), TriggerType.FORCED, null, null, null, progressMonitor, taskManager, null, () -> true, Set.of(), Map.of());
    canceledProgressMonitor.cancel();
    analysisQueue.post(canceledCommand);
    analysisQueue.post(command);

    var nextCommand = analysisQueue.takeNextCommand();

    assertThat(nextCommand).isEqualTo(command);
  }

}
