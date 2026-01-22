/*
ACR-c36808fd15674ae5bd648d51fe3cf8af
ACR-78723a230c6347e3a2f680fc1337117a
ACR-7e60af3ab0e4431f810b43e893b50b21
ACR-66fcf7b9feb24fbc9c71e1f33b2ef919
ACR-0747b071b4fc4d579a71dadc797e2e05
ACR-a1bbe9cd3e534c9e90ea6e7b041e66b9
ACR-c5a64e3dd3d942ef9e8b430dfd493ff9
ACR-f98701d15127491186491b6b5f71b821
ACR-75ea8cfdc04049e4968639e896a3f367
ACR-a6d40ecd038849abbbe871cea2814801
ACR-04cb082458784b30b0f2c858b5b3bc30
ACR-63c7980c03e2469f920c1ae789170d07
ACR-87a6b36ea4554732be078bf489016483
ACR-a2e0abe98be54a19aac06a5f427c0c98
ACR-214e3355fcd04c5abf4f112387c653fc
ACR-5f86652a89424b43ac437206ce2f8352
ACR-4cfeee6d8df246ada7b577dc6eb773d3
 */
package org.sonarsource.sonarlint.core.analysis.mediumtests;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.sonarlint.core.analysis.AnalysisScheduler;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.api.ClientModuleFileSystem;
import org.sonarsource.sonarlint.core.analysis.api.Issue;
import org.sonarsource.sonarlint.core.analysis.api.TriggerType;
import org.sonarsource.sonarlint.core.analysis.command.AnalyzeCommand;
import org.sonarsource.sonarlint.core.commons.LogTestStartAndEnd;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.progress.TaskManager;
import org.sonarsource.sonarlint.core.plugin.commons.PluginsLoader;
import testutils.OnDiskTestClientInputFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

@ExtendWith(LogTestStartAndEnd.class)
class AnalysisSchedulerMediumTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester(true);
  private static final Consumer<List<ClientInputFile>> NO_OP_ANALYSIS_STARTED_CONSUMER = inputFiles -> {
  };
  private static final Supplier<Boolean> ANALYSIS_READY_SUPPLIER = () -> true;
  private static final Consumer<Issue> NO_OP_ISSUE_LISTENER = issue -> {
  };
  public static final TaskManager TASK_MANAGER = new TaskManager();

  private AnalysisScheduler analysisScheduler;
  private volatile boolean engineStopped = true;
  private final SonarLintCancelMonitor progressMonitor = new SonarLintCancelMonitor();

  @BeforeEach
  void prepare(@TempDir Path workDir) throws IOException {
    var enabledLanguages = Set.of(SonarLanguage.PYTHON);
    var analysisGlobalConfig = AnalysisSchedulerConfiguration.builder()
      .setClientPid(1234L)
      .setWorkDir(workDir)
      .setFileSystemProvider(this::provideFileSystem)
      .build();
    var result = new PluginsLoader().load(new PluginsLoader.Configuration(Set.of(findPythonJarPath()), enabledLanguages, false, Optional.empty()), Set.of());
    this.analysisScheduler = new AnalysisScheduler(analysisGlobalConfig, result.getLoadedPlugins(), logTester.getLogOutput());
    engineStopped = false;
  }

  private ClientModuleFileSystem provideFileSystem(String moduleKey) {
    return aModuleFileSystem();
  }

  @AfterEach
  void cleanUp() {
    if (!engineStopped) {
      this.analysisScheduler.stop();
    }
  }

  @Test
  void should_analyze_a_file_inside_a_module(@TempDir Path baseDir) throws Exception {
    var content = """
      def foo():
        x = 9; # trailing comment
      """;
    ClientInputFile inputFile = preparePythonInputFile(baseDir, content);

    AnalysisConfiguration analysisConfig = AnalysisConfiguration.builder()
      .addInputFiles(inputFile)
      .addActiveRules(trailingCommentRule())
      .setBaseDir(baseDir)
      .build();
    List<Issue> issues = new ArrayList<>();
    var analyzeCommand = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED, () -> analysisConfig, issues::add, null, progressMonitor, TASK_MANAGER,
      NO_OP_ANALYSIS_STARTED_CONSUMER, ANALYSIS_READY_SUPPLIER, Set.of(), Map.of());
    analysisScheduler.post(analyzeCommand);
    analyzeCommand.getFutureResult().get();
    assertThat(issues).hasSize(1);
    assertThat(issues)
      .extracting("ruleKey", "message", "inputFile", "flows", "textRange.startLine", "textRange.startLineOffset", "textRange.endLine", "textRange.endLineOffset")
      .containsOnly(tuple(RuleKey.parse("python:S139"), "Move this trailing comment on the previous empty line.", inputFile, List.of(), 2, 9, 2, 27));
    assertThat(issues.get(0).quickFixes()).hasSize(1);
  }

  @Test
  void should_fail_the_future_if_the_analyze_command_execution_fails() {
    var command = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED, () -> {
      throw new RuntimeException("Kaboom");
    }, issue -> {
    }, null, progressMonitor, TASK_MANAGER, NO_OP_ANALYSIS_STARTED_CONSUMER, ANALYSIS_READY_SUPPLIER, Set.of(), Map.of());
    analysisScheduler.post(command);

    assertThat(command.getFutureResult()).failsWithin(300, TimeUnit.MILLISECONDS)
      .withThrowableOfType(ExecutionException.class)
      .havingCause()
      .isInstanceOf(RuntimeException.class)
      .withMessage("Kaboom");
  }

  @Test
  void should_cancel_progress_monitor_of_executing_analyze_command_when_stopping(@TempDir Path baseDir) throws IOException, InterruptedException {
    var content = """
      def foo():
        x = 9; # trailing comment
      """;
    ClientInputFile inputFile = preparePythonInputFile(baseDir, content);

    AnalysisConfiguration analysisConfig = AnalysisConfiguration.builder()
      .addInputFiles(inputFile)
      .addActiveRules(trailingCommentRule())
      .setBaseDir(baseDir)
      .build();
    var analyzeCommand = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED, () -> analysisConfig, NO_OP_ISSUE_LISTENER, null, progressMonitor, TASK_MANAGER,
      inputFiles -> pause(300), ANALYSIS_READY_SUPPLIER, Set.of(), Map.of());
    analysisScheduler.post(analyzeCommand);
    //ACR-e9334f4a30954403a4bd837ca054d50c
    Thread.sleep(100);
    analysisScheduler.stop();
    engineStopped = true;

    await().until(analyzeCommand.getFutureResult()::isDone);
    assertThat(analyzeCommand.getFutureResult())
      .isCancelled();
    assertThat(progressMonitor.isCanceled()).isTrue();
  }

  @Test
  void should_cancel_pending_commands_when_stopping(@TempDir Path baseDir) throws IOException, InterruptedException {
    var content = """
      def foo():
        x = 9; # trailing comment
      """;
    ClientInputFile inputFile = preparePythonInputFile(baseDir, content);

    AnalysisConfiguration analysisConfig = AnalysisConfiguration.builder()
      .addInputFiles(inputFile)
      .addActiveRules(trailingCommentRule())
      .setBaseDir(baseDir)
      .build();
    var analyzeCommand = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED, () -> analysisConfig, NO_OP_ISSUE_LISTENER, null, progressMonitor, TASK_MANAGER,
      inputFiles -> pause(300), ANALYSIS_READY_SUPPLIER, Set.of(), Map.of());
    var secondAnalyzeCommand = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED, () -> analysisConfig, NO_OP_ISSUE_LISTENER, null, progressMonitor,
      TASK_MANAGER, NO_OP_ANALYSIS_STARTED_CONSUMER, ANALYSIS_READY_SUPPLIER, Set.of(), Map.of());
    analysisScheduler.post(analyzeCommand);
    analysisScheduler.post(secondAnalyzeCommand);
    //ACR-09326d2ec1e94255a9b8f727e2663a1b
    Thread.sleep(100);

    analysisScheduler.stop();
    engineStopped = true;

    await().until(analyzeCommand.getFutureResult()::isDone);
    assertThat(analyzeCommand.getFutureResult())
      .isCancelled();
    assertThat(secondAnalyzeCommand.getFutureResult())
      .isCancelled();
    assertThat(progressMonitor.isCanceled()).isTrue();
  }

  @Test
  void should_not_fail_next_analysis_on_exception_from_command(@TempDir Path baseDir) throws IOException {
    Supplier<Boolean> throwingSupplier = () -> {
      throw new RuntimeException("Kaboom");
    };
    var content = """
      def foo():
        x = 9; # trailing comment
      """;
    var inputFile = preparePythonInputFile(baseDir, content);

    var analysisConfig = AnalysisConfiguration.builder()
      .addInputFiles(inputFile)
      .addActiveRules(trailingCommentRule())
      .setBaseDir(baseDir)
      .build();
    var issues1 = new ArrayList<>();
    var issues2 = new ArrayList<>();
    var analyzeCommand1 = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED,
      () -> analysisConfig, issues1::add, null, progressMonitor, TASK_MANAGER,
      NO_OP_ANALYSIS_STARTED_CONSUMER, ANALYSIS_READY_SUPPLIER, Set.of(), Map.of("a", "1"));
    var throwingCommand = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED,
      () -> analysisConfig, NO_OP_ISSUE_LISTENER, null, progressMonitor, TASK_MANAGER,
      NO_OP_ANALYSIS_STARTED_CONSUMER, throwingSupplier, Set.of(), Map.of("b", "2"));
    var analyzeCommand2 = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED,
      () -> analysisConfig, issues2::add, null, progressMonitor, TASK_MANAGER,
      NO_OP_ANALYSIS_STARTED_CONSUMER, ANALYSIS_READY_SUPPLIER, Set.of(), Map.of("c", "3"));

    analysisScheduler.post(analyzeCommand1);
    analysisScheduler.post(throwingCommand);
    analysisScheduler.post(analyzeCommand2);

    await().untilAsserted(() -> assertThat(logTester.logs()).contains("Analysis command failed"));
    await().atMost(3, TimeUnit.SECONDS)
      .until(() -> analyzeCommand2.getFutureResult().isDone());
    assertThat(issues2).hasSize(1);
  }

  @Test
  void should_not_queue_command_if_already_canceled(@TempDir Path baseDir) {
    var analysisConfig = AnalysisConfiguration.builder()
      .addActiveRules(trailingCommentRule())
      .setBaseDir(baseDir)
      .build();
    var analyzeCommand = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED,
      () -> analysisConfig, i -> {
      }, null, progressMonitor, TASK_MANAGER,
      NO_OP_ANALYSIS_STARTED_CONSUMER, ANALYSIS_READY_SUPPLIER, Set.of(), Map.of("a", "1"));
    progressMonitor.cancel();

    analysisScheduler.post(analyzeCommand);

    await().untilAsserted(() -> assertThat(logTester.logs()).contains("Not picking next command " + analyzeCommand + ", is canceled"));
  }

  @Test
  void should_interrupt_executing_thread_when_stopping(@TempDir Path baseDir) throws IOException {
    var content = """
      def foo():
        x = 9; # trailing comment
      """;
    ClientInputFile inputFile = preparePythonInputFile(baseDir, content);

    AnalysisConfiguration analysisConfig = AnalysisConfiguration.builder()
      .addInputFiles(inputFile)
      .addActiveRules(trailingCommentRule())
      .setBaseDir(baseDir)
      .build();
    var threadTermination = new AtomicReference<String>();
    var analyzeCommand = new AnalyzeCommand("moduleKey", UUID.randomUUID(), TriggerType.FORCED, () -> analysisConfig, NO_OP_ISSUE_LISTENER, null, progressMonitor, TASK_MANAGER,
      inputFiles -> {
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          threadTermination.set("INTERRUPTED");
          return;
        }
        threadTermination.set("FINISHED");
      }, ANALYSIS_READY_SUPPLIER, Set.of(), Map.of());
    analysisScheduler.post(analyzeCommand);
    //ACR-e1941cf7c6b1408dbc2b7ff20c38909c
    pause(200);

    analysisScheduler.stop();
    engineStopped = true;

    await().until(analyzeCommand.getFutureResult()::isDone);
    assertThat(threadTermination).hasValue("INTERRUPTED");
  }

  @Test
  void should_not_log_any_error_when_stopping() {
    //ACR-5edbfe550b5c475a8aad94b2ad99dac2
    pause(500);

    analysisScheduler.stop();

    //ACR-6ef9d7d474d747ddb9046ac35c6965fb
    pause(1000);
    assertThat(logTester.logs(LogOutput.Level.ERROR)).isEmpty();
  }

  private ClientInputFile preparePythonInputFile(Path baseDir, String content) throws IOException {
    final var file = new File(baseDir.toFile(), "file.py");
    FileUtils.write(file, content, StandardCharsets.UTF_8);
    return new OnDiskTestClientInputFile(file.toPath(), "file.py", false, StandardCharsets.UTF_8, SonarLanguage.PYTHON);
  }

  private static Path findPythonJarPath() throws IOException {
    var pluginsFolderPath = Paths.get("target/plugins/");
    try (var files = Files.list(pluginsFolderPath)) {
      return files.filter(x -> x.getFileName().toString().endsWith(".jar"))
        .filter(x -> x.getFileName().toString().contains("python"))
        .findFirst().orElseThrow(() -> new RuntimeException("Unable to locate the python plugin"));
    }
  }

  private static ActiveRule trailingCommentRule() {
    return new ActiveRule() {
      @Override
      public RuleKey ruleKey() {
        return RuleKey.parse("python:S139");
      }

      @Override
      public String severity() {
        return "";
      }

      @Override
      public String language() {
        return "py";
      }

      @CheckForNull
      @Override
      public String param(String key) {
        return params().get(key);
      }

      @Override
      public Map<String, String> params() {
        return Map.of("legalTrailingCommentPattern", "^#\\s*+[^\\s]++$");
      }

      @Override
      public String internalKey() {
        return "";
      }

      @CheckForNull
      @Override
      public String templateRuleKey() {
        return null;
      }

      @Override
      public String qpKey() {
        return "";
      }
    };
  }

  private static ClientModuleFileSystem aModuleFileSystem() {
    return new ClientModuleFileSystem() {
      @Override
      public Stream<ClientInputFile> files(String suffix, InputFile.Type type) {
        return Stream.of();
      }

      @Override
      public Stream<ClientInputFile> files() {
        return Stream.of();
      }
    };
  }

  private static void pause(long period) {
    try {
      Thread.sleep(period);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
