/*
ACR-4a5c8a8e92f24e9a860cac17d38407fd
ACR-fc894d0c4a634dbab16d50322ef92b0b
ACR-cb1ad1598d234b8fa677368f093a5ad3
ACR-945ce879715a415c9096eb40663749a5
ACR-d7ea3c6080fa44829f116bd8b4a0c741
ACR-842c9fa0d4b24c6cb9cebe9b6597ecb5
ACR-9d191244217d423fa1c46ef35ab48784
ACR-83202329a8d74bb0964cfb12f622f03d
ACR-e6462906c31d4527aaf1045974ec40e5
ACR-0ad76b9346d748478a0b569c30d1aec6
ACR-f249bcb88d2744be9bc0d3727ac6edc3
ACR-69d9b968930f4552b5d5123dc013b452
ACR-7e4c6ed627fd4c9ab1bb4d05c232b557
ACR-59a4c013653745a884808cd8dbc89768
ACR-af89a44a8e8c49b2a92d34f856ff4ab4
ACR-345c39b8b55a46618ea93baaa96edb56
ACR-702cd23a3fac4ef8bd679f6905fb9943
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
