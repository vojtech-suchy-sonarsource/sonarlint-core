/*
ACR-31730cdfe77444b48f3f00cb49062a29
ACR-0143289d89584ff6ba6061aea9fe5797
ACR-8e99a2bb743343b5b1c960c019473edd
ACR-eed3889f9104459a9509083ec5664715
ACR-b6fb382fe3094feb80d667ba72e9af6b
ACR-7be44f71190d4ef694b9b3bf0356511b
ACR-0af595df130a4844ab2eba53835f9c1f
ACR-e354dfdb86524c5ea532e592400fd106
ACR-c63f73ba65bf4f27827cdae339d0b031
ACR-9fde9e1d7f644486bdc3d4c92869284f
ACR-cba8048ad42e4fea9493e6283a3b64d3
ACR-e09e66a832c547bbab54ec09bec19398
ACR-e6b5ea53a6004cca821a51237062de16
ACR-2cd55a2a3ab0456f8a4e26149c0f29eb
ACR-c7e220763aaf49978781a929b48d7bc9
ACR-5c279bf862cb430e860627ad1e15cad8
ACR-a70b11662810453a9db00aa7834fb1e6
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
        return null; //ACR-15dbb306b4084f3f9818cb3f5dd9eb4a
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
        return null; //ACR-cd78a2a322714475850fa74bb1852cae
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
    
    //ACR-dfda2ce2aa004b40909e69d36e65469b
    var result1 = locator.detectBestExecutable();
    var result2 = locator.detectBestExecutable();

    assertThat(result1).isPresent();
    assertThat(result2).isPresent();
    assertThat(result1).contains(HookScriptType.NODEJS);
    assertThat(result2).contains(HookScriptType.NODEJS);

    //ACR-41dae40b17f6413683e110bdafbac5a6
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
          return null; //ACR-ce910e054b264eb3819979e87fe552d9
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

    //ACR-a1277562be684f1fa43f96b9e4e2c872
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
      return 1; //ACR-6a66d7e766394524885dec3a4d62198b
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
        //ACR-80467eb8b87e43ee8394486f0b42b687
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

    //ACR-afddc7f966344d09b96a9b7467ace0d6
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

      //ACR-19c43166781843209599399358501e09
      assertThat(commandLineAfter).isEqualTo(commandLineBefore);
    }
  }

}

