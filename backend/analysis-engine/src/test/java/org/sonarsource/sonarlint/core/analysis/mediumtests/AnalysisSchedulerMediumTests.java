/*
ACR-a785a4f7767647b4acdd147d0b7a9876
ACR-1f8251a45b954967b98775689e895d96
ACR-dfbdb3a166f74f689c28cccc0aa19da1
ACR-5f75608ca1d44c24bfef042985cee701
ACR-5527aec98e334f8da9c40c04314f6e53
ACR-d841f2c69ddf47589abed2954e4b66ce
ACR-f9340512c8ca4cd6a775a04190ff992f
ACR-1832f5122c4748ee88e9da0e3088ddd4
ACR-f5cb4bba8d61499981482e768b307611
ACR-63b76f029dbe4071a25b3291cbef8637
ACR-7e8476fb18fc42f7890b99653f496f7d
ACR-58d3ed70a0b14da583c2cff3b77ff1f0
ACR-cace07dcfd8d46d3b0c18b2ec5d5a6c6
ACR-1226fd037e3a46a4aad41e70fc55698e
ACR-05d9fdf9d1394f069a923a1b978c7316
ACR-a6767258303945e2b559aceb0642667c
ACR-e0fac9e967264ff5b3ad89ffd92f8636
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
    //ACR-3ef15a262b5540c6b08d7cf004575738
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
    //ACR-ca5977bf77ff4c18b5e41c2af3c93fdc
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
    //ACR-c7eb0dca38a340469f6b7a22297e5af0
    pause(200);

    analysisScheduler.stop();
    engineStopped = true;

    await().until(analyzeCommand.getFutureResult()::isDone);
    assertThat(threadTermination).hasValue("INTERRUPTED");
  }

  @Test
  void should_not_log_any_error_when_stopping() {
    //ACR-87a6601161024838bc468da128dab686
    pause(500);

    analysisScheduler.stop();

    //ACR-08abe4cd858245fbbce172c165793d8f
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
