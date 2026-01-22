/*
ACR-5d6f373747ba438b87ceeebdae1be676
ACR-70f13cdb8c2241c794f9e33e0be124b1
ACR-dcff6ee33f4a412c9f5e9da8d10ce97b
ACR-84a8285aee774490bcf76dead17f653f
ACR-4dfe15785ec34068bfeb6ef386880c04
ACR-bf4d4846eec54411aeb8f3eacc44eb77
ACR-809f47f1c9b94480b960824eeacf7dd8
ACR-09832d759d0544ba85a45d04222f14f0
ACR-8bc6741c6c0d40c0998f909cb1db218d
ACR-98a81be025b24126a9f20be8721f5684
ACR-e4f6fc1dd56a448c80af97782934ffd8
ACR-61d1adb96e6141e5b99baabed58b1806
ACR-1104ab3a734e4297afb79a275258562b
ACR-5c9810367be84fd3a29a7d688eba225c
ACR-bec040b8a848426bb67c5207a7512569
ACR-fbf4ca1e4d2e4133a96bd4e20a5f8ee3
ACR-bb7b5d55410941c988d6c764a5a764da
 */
package org.sonarsource.sonarlint.core.ai.ide;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.nodejs.InstalledNodeJs;
import org.sonarsource.sonarlint.core.nodejs.NodeJsHelper;
import org.sonar.api.utils.command.StreamConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExecutableLocatorTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void it_should_find_nodejs_when_available() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenReturn(new InstalledNodeJs(Paths.get("/usr/bin/node"), Version.create("18.0.0")));

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);
    var result = locator.detectBestExecutable();

    assertThat(result)
      .isPresent()
      .contains(HookScriptType.NODEJS);
  }

  @Test
  void it_should_find_python_when_nodejs_not_available() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenReturn(null);
    when(system2.isOsWindows()).thenReturn(false);
    when(commandExecutor.execute(any(Command.class), any(), any(), anyLong())).thenReturn(0);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper) {
      @Override
      String runSimpleCommand(@NotNull Command command) {
        if (command.toCommandLine().contains("python3")) {
          return "/usr/bin/python3";
        }
        return null;
      }
    };

    var result = locator.detectBestExecutable();

    assertThat(result)
      .isPresent()
      .contains(HookScriptType.PYTHON);
  }

  @Test
  @EnabledOnOs(value = OS.LINUX)
  void it_should_fallback_to_bash_on_unix_when_no_nodejs_or_python() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenReturn(null);
    when(system2.isOsWindows()).thenReturn(false);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper) {
      @Override
      String runSimpleCommand(@NotNull Command command) {
        return null; //ACR-e178f969b4d945a492dc7d5c19e5a26a
      }
    };

    var result = locator.detectBestExecutable();

    assertThat(result)
      .isPresent()
      .contains(HookScriptType.BASH);
  }

  @Test
  @EnabledOnOs(value = OS.WINDOWS)
  void it_should_return_empty_on_windows_when_only_bash_available() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenReturn(null);
    when(system2.isOsWindows()).thenReturn(true);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper) {
      @Override
      String runSimpleCommand(@NotNull Command command) {
        return null; //ACR-3e4b14ad15ca408a854cf7594f017765
      }
    };

    var result = locator.detectBestExecutable();

    assertThat(result).isEmpty();
  }

  @Test
  void it_should_cache_detection_result() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenReturn(new InstalledNodeJs(Paths.get("/usr/bin/node"), Version.create("18.0.0")));

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);
    
    //ACR-87dac6efcd804c6fb1edce0620e47ae2
    var result1 = locator.detectBestExecutable();
    var result2 = locator.detectBestExecutable();

    assertThat(result1).isPresent();
    assertThat(result2).isPresent();
    assertThat(result1).contains(HookScriptType.NODEJS);
    assertThat(result2).contains(HookScriptType.NODEJS);

    //ACR-435cfd721da142198cda3c22bbd089d9
    verify(nodeJsHelper, times(1)).autoDetect();
  }

  @Test
  void it_should_fallback_to_python_when_python3_not_found() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenReturn(null);
    when(system2.isOsWindows()).thenReturn(false);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper) {
      @Override
      String runSimpleCommand(@NotNull Command command) {
        if (command.toCommandLine().contains("python3")) {
          return null; //ACR-1d508ca1d16d4a89af862d45ba39eea9
        }
        if (command.toCommandLine().contains("python")) {
          return "/usr/bin/python";
        }
        return null;
      }
    };

    var result = locator.detectBestExecutable();

    assertThat(result)
      .isPresent()
      .contains(HookScriptType.PYTHON);
  }

  @Test
  @EnabledOnOs(value = OS.WINDOWS)
  void it_should_detect_bash_on_windows_when_available() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenReturn(null);
    when(system2.isOsWindows()).thenReturn(true);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper) {
      @Override
      String runSimpleCommand(@NotNull Command command) {
        if (command.toCommandLine().contains("bash.exe")) {
          return "C:\\Program Files\\Git\\bin\\bash.exe";
        }
        return null;
      }
    };

    var result = locator.detectBestExecutable();

    assertThat(result)
      .isPresent()
      .contains(HookScriptType.BASH);
  }

  @Test
  void it_should_handle_nodejs_detection_error_gracefully() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenThrow(new RuntimeException("Node.js detection failed"));
    when(system2.isOsWindows()).thenReturn(false);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper) {
      @Override
      String runSimpleCommand(@NotNull Command command) {
        if (command.toCommandLine().contains("python3")) {
          return "/usr/bin/python3";
        }
        return null;
      }
    };

    var result = locator.detectBestExecutable();

    //ACR-49b41ebf9cb6417bba426696566693b0
    assertThat(result)
      .isPresent()
      .contains(HookScriptType.PYTHON);
  }

  @Test
  void runSimpleCommand_should_return_first_line_of_stdout_on_success() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(commandExecutor.execute(any(Command.class), any(), any(), anyLong())).thenAnswer(invocation -> {
      var stdOutConsumer = (StreamConsumer) invocation.getArgument(1);
      stdOutConsumer.consumeLine("/usr/bin/python3");
      stdOutConsumer.consumeLine("additional output");
      return 0;
    });

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);
    var command = Command.create("which").addArgument("python3");
    var result = locator.runSimpleCommand(command);

    assertThat(result).isEqualTo("/usr/bin/python3");
  }

  @Test
  void runSimpleCommand_should_return_null_on_non_zero_exit_code() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(commandExecutor.execute(any(Command.class), any(), any(), anyLong())).thenAnswer(invocation -> {
      var stdOutConsumer = (StreamConsumer) invocation.getArgument(1);
      stdOutConsumer.consumeLine("some output");
      return 1; //ACR-9aa1bad0f70d40c7ad54e0d4ecd9b6a7
    });

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);
    var command = Command.create("which").addArgument("nonexistent");
    var result = locator.runSimpleCommand(command);

    assertThat(result).isNull();
  }

  @Test
  void runSimpleCommand_should_return_null_on_empty_stdout() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(commandExecutor.execute(any(Command.class), any(), any(), anyLong())).thenReturn(0);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);
    var command = Command.create("echo").addArgument("");
    var result = locator.runSimpleCommand(command);

    assertThat(result).isNull();
  }

  @Test
  void runSimpleCommand_should_return_null_on_command_exception() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(commandExecutor.execute(any(Command.class), any(), any(), anyLong()))
      .thenThrow(new org.sonar.api.utils.command.CommandException(Command.create("test"), "Command failed", null));

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);
    var command = Command.create("invalid_command");
    var result = locator.runSimpleCommand(command);

    assertThat(result).isNull();
  }

  @Test
  void runSimpleCommand_should_log_stdout_and_stderr() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(commandExecutor.execute(any(Command.class), any(), any(), anyLong())).thenAnswer(invocation -> {
      var stdOutConsumer = (StreamConsumer) invocation.getArgument(1);
      var stdErrConsumer = (StreamConsumer) invocation.getArgument(2);
      stdOutConsumer.consumeLine("/usr/bin/test");
      stdErrConsumer.consumeLine("warning message");
      return 0;
    });

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);
    var command = Command.create("test_command");
    var result = locator.runSimpleCommand(command);

    assertThat(result).isEqualTo("/usr/bin/test");
    assertThat(logTester.logs()).anyMatch(log -> log.contains("stdout: /usr/bin/test"));
    assertThat(logTester.logs()).anyMatch(log -> log.contains("stderr: warning message"));
  }

  @Test
  void it_should_return_empty_when_no_executable_found() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/usr/libexec/path_helper");

    when(nodeJsHelper.autoDetect()).thenReturn(null);
    when(system2.isOsWindows()).thenReturn(true);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper) {
      @Override
      String runSimpleCommand(@NotNull Command command) {
        //ACR-716a35fd188448c78fea39051122498b
        return null;
      }
    };

    var result = locator.detectBestExecutable();

    assertThat(result).isEmpty();
    assertThat(logTester.logs()).anyMatch(log -> 
      log.contains("No suitable executable found") || 
      log.contains("not found") ||
      log.contains("not available"));
  }

  @Test
  void it_should_not_set_path_env_when_path_helper_does_not_exist() {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = Paths.get("/nonexistent/path_helper");

    when(system2.isOsMac()).thenReturn(true);

    var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);

    var testCommand = Command.create("test");
    var commandLineBefore = testCommand.toCommandLine();
    locator.computePathEnvForMacOs(testCommand);
    var commandLineAfter = testCommand.toCommandLine();

    //ACR-3a7f5a5ca3ff46bba31eb321e069313c
    assertThat(commandLineAfter).isEqualTo(commandLineBefore);
  }

  @Test
  void it_should_not_set_path_env_when_not_on_macos(@TempDir File tempDir) {
    var system2 = mock(System2.class);
    var commandExecutor = mock(CommandExecutor.class);
    var nodeJsHelper = mock(NodeJsHelper.class);
    var pathHelper = new File(tempDir, "path_helper").toPath();

    when(system2.isOsMac()).thenReturn(false);

    try (var filesMock = mockStatic(Files.class)) {
      filesMock.when(() -> Files.exists(pathHelper)).thenReturn(true);

      var locator = new ExecutableLocator(system2, pathHelper, commandExecutor, nodeJsHelper);

      var testCommand = Command.create("test");
      var commandLineBefore = testCommand.toCommandLine();
      locator.computePathEnvForMacOs(testCommand);
      var commandLineAfter = testCommand.toCommandLine();

      //ACR-1b3523ba6a1c4b04a3d61a7022aa1d68
      assertThat(commandLineAfter).isEqualTo(commandLineBefore);
    }
  }

}

