/*
ACR-64d9c823e4ed470097c40ce439ffab9c
ACR-5bb6636919d34946b0fd7f00f00cd8ef
ACR-bb3692f848c04ef3a8940243db656626
ACR-8c66609429674d78a340715fad47676f
ACR-212133c046ee400ba0a3334f76b2fcc3
ACR-1ff5dd38c3ee4c4d820e9f2158e4a396
ACR-5b2d17c3e2a2444189a51909f119d844
ACR-df7b1de3ba83448ca3b1eb974f2ace83
ACR-14b2b095aa9c4a0ea8b74a6398520b0e
ACR-f420279c67d3471987ae689083a6fa3e
ACR-3b029cc3178142c1b08af3b59aeee76e
ACR-8986d84cab1842ea87abe558d53791c9
ACR-6bbaf9c9e7b8463088a6d06e206d2755
ACR-b53d7ed0d4d747f59d2e32d747c55a6f
ACR-684841d4f94b4ad7bd23316d72ca62f4
ACR-6b7af1c52c5a47028790ae2c7284a9d4
ACR-b979a8cad4c748ae94ccc105cc486b0b
 */
package org.sonarsource.sonarlint.core.analysis.command;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.analysis.api.TriggerType;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.progress.TaskManager;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyzeCommandTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void it_should_cancel_posting_command() throws Exception {
    var files = Set.of(new URI("file:///test1"));
    var props = Map.of("a", "b");
    var cmd1 = newAnalyzeCommand(files, props);
    var cmd2 = newAnalyzeCommand(files, props);

    assertThat(cmd1.shouldCancelPost(cmd2)).isTrue();
  }

  @Test
  void it_should_cancel_posting_command_if_canceled() throws Exception {
    var cmd1 = newAnalyzeCommand(Set.of(new URI("file:///test1")), Map.of());
    var cmd2 = newAnalyzeCommand(Set.of(new URI("file:///test2")), Map.of());
    cmd1.cancel();

    assertThat(cmd1.shouldCancelPost(cmd2)).isTrue();
  }

  @Test
  void it_should_not_cancel_when_files_are_different() throws Exception {
    var cmd1 = newAnalyzeCommand(Set.of(new URI("file:///test1")), Map.of());
    var cmd2 = newAnalyzeCommand(Set.of(new URI("file:///test2")), Map.of());

    assertThat(cmd1.shouldCancelPost(cmd2)).isFalse();
  }


  @Test
  void if_should_cancel_task_in_queue_when_canceled() {
    var cmd = newAnalyzeCommand(Set.of(), Map.of());
    cmd.cancel();

    assertThat(cmd.shouldCancelQueue()).isTrue();
  }

  @Test
  void it_should_not_cancel_task_in_queue_if_not_canceled() {
    var cmd = newAnalyzeCommand(Set.of(), Map.of());

    assertThat(cmd.shouldCancelQueue()).isFalse();
  }

  private static AnalyzeCommand newAnalyzeCommand(Set<URI> files, Map<String, String> extraProps) {
    return new AnalyzeCommand(
      "moduleKey",
      UUID.randomUUID(),
      TriggerType.FORCED,
      () -> AnalysisConfiguration.builder().addInputFiles().build(),
      issue -> {},
      null,
      new SonarLintCancelMonitor(),
      new TaskManager(),
      inputFiles -> {},
      () -> true,
      files,
      extraProps
    );
  }

}
