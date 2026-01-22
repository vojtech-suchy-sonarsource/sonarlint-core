/*
ACR-3e71c7026c2a43cb9ebd6939f331ed2e
ACR-0d534861e2034c9b9a88366132b6abc3
ACR-49b1300cbd724277a029ac28d71c9a5c
ACR-34f19826d739447d830576914196031b
ACR-9cd403d61e2542f3986465a69e7a35e3
ACR-4824402aab6448bcaf66e04ea1937ea2
ACR-e1a0caa501d744798e90155b056ac645
ACR-2b61b07cad8f4caf86d6bac7ee5ced0a
ACR-ecac81adf5524eacbff70f543eea315a
ACR-20f0612e05db4817a49d5feeae251e42
ACR-bfc5c49a8b2c4c82989cd00212f8405d
ACR-54ecd3621ab543d7baa240f0df8132a8
ACR-27c9610eb8ea4eabb92573146b08740e
ACR-ca3cf67af0b94e6bb10372fbc89a157c
ACR-862c8c6d6381480ca82b5dec91dcfa13
ACR-63b40f7d11bd48ea82fce47f9e477830
ACR-b0358de4a0bd41b4a6365569fb7c66da
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
