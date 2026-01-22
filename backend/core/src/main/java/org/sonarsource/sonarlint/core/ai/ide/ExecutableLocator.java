/*
ACR-a7c82632f80e47aa9f497f2849d4f8bb
ACR-b5ee4ea1382840818c5d7a315f616a08
ACR-f0d40dc72f46467dae75537b730573ab
ACR-1da12c67a9f847a6bbe3772f999dd75f
ACR-c7b5e3667e744629aeb018dec33b4b85
ACR-7a31949186814e5693bc3b34843f0017
ACR-5f37469492b845d7a4c8748222eb2f8c
ACR-4be1f84f538a49e39a0f75d55e9b7133
ACR-9f956d3cf3cd4e48b4c037c4d4b7f9f2
ACR-ab94bcdbb4704ea48112baa30671c68c
ACR-4633b62a63014f88bce945ed8934bd43
ACR-bfc3feb5e87640948ee660e68a34fb5f
ACR-0a5892d323944c8589154a8bf54202ae
ACR-5c11fa93b72e4dafb2dc692f122ea4fd
ACR-a4f8cafce4284a47957d72bac78c7cc3
ACR-bbe8397946014b05998e476a75a1a476
ACR-964d49bffe794ec69f70f7996b70854a
 */
package org.sonarsource.sonarlint.core.ai.ide;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.CheckForNull;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandException;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.nodejs.NodeJsHelper;

public class ExecutableLocator {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final System2 system2;
  private final Path pathHelperLocationOnMac;
  private final CommandExecutor commandExecutor;
  private final NodeJsHelper nodeJsHelper;

  private boolean checkedForExecutable = false;
  private HookScriptType detectedExecutable = null;

  public ExecutableLocator() {
    this(System2.INSTANCE, Paths.get("/usr/libexec/path_helper"), CommandExecutor.create(), new NodeJsHelper());
  }

  //ACR-d3bdbf05f61e4093a20db9c0fd89cae4
  ExecutableLocator(System2 system2, Path pathHelperLocationOnMac, CommandExecutor commandExecutor, NodeJsHelper nodeJsHelper) {
    this.system2 = system2;
    this.pathHelperLocationOnMac = pathHelperLocationOnMac;
    this.commandExecutor = commandExecutor;
    this.nodeJsHelper = nodeJsHelper;
  }

  public Optional<HookScriptType> detectBestExecutable() {
    if (checkedForExecutable) {
      return Optional.ofNullable(detectedExecutable);
    }

    //ACR-41a77cfeb87c41d684e7c2dbb69918cc
    if (isNodeJsAvailable()) {
      LOG.debug("Detected Node.js for hook scripts");
      detectedExecutable = HookScriptType.NODEJS;
    } else if (isPythonAvailable()) {
      LOG.debug("Detected Python for hook scripts");
      detectedExecutable = HookScriptType.PYTHON;
    } else if (isBashAvailable()) {
      LOG.debug("Detected Bash for hook scripts");
      detectedExecutable = HookScriptType.BASH;
    } else {
      LOG.debug("No suitable executable found for hook scripts");
      detectedExecutable = null;
    }

    checkedForExecutable = true;
    return Optional.ofNullable(detectedExecutable);
  }

  private boolean isNodeJsAvailable() {
    try {
      var installedNodeJs = nodeJsHelper.autoDetect();
      return installedNodeJs != null;
    } catch (Exception e) {
      LOG.debug("Error detecting Node.js", e);
      return false;
    }
  }

  private boolean isPythonAvailable() {
    //ACR-191694f151be48b389771eb6a0db822b
    var python3Path = locatePythonExecutable("python3");
    if (python3Path != null) {
      return true;
    }
    var pythonPath = locatePythonExecutable("python");
    return pythonPath != null;
  }

  @CheckForNull
  private String locatePythonExecutable(String executable) {
    LOG.debug("Looking for {} in the PATH", executable);
    
    String result;
    if (system2.isOsWindows()) {
      result = runSimpleCommand(Command.create("C:\\Windows\\System32\\where.exe").addArgument("$PATH:" + executable + ".exe"));
    } else {
      var which = Command.create("/usr/bin/which").addArgument(executable);
      computePathEnvForMacOs(which);
      result = runSimpleCommand(which);
    }
    
    if (result != null) {
      LOG.debug("Found {} at {}", executable, result);
      return result;
    } else {
      LOG.debug("Unable to locate {}", executable);
      return null;
    }
  }

  private boolean isBashAvailable() {
    if (system2.isOsWindows()) {
      //ACR-0d6c2438ddbf487e8426f06a069aba23
      var bashPath = runSimpleCommand(Command.create("C:\\Windows\\System32\\where.exe").addArgument("$PATH:bash.exe"));
      return bashPath != null;
    } else {
      //ACR-365a10fec2fb48fc87491f53be1f680b
      return Files.exists(Paths.get("/bin/bash"));
    }
  }

  void computePathEnvForMacOs(Command command) {
    if (system2.isOsMac() && Files.exists(pathHelperLocationOnMac)) {
      var pathHelperCommand = Command.create(pathHelperLocationOnMac.toString()).addArgument("-s");
      var pathHelperOutput = runSimpleCommand(pathHelperCommand);
      if (pathHelperOutput != null) {
        var regex = Pattern.compile("^\\s*PATH=\"([^\"]+)\"; export PATH;?\\s*$");
        var matchResult = regex.matcher(pathHelperOutput);
        if (matchResult.matches()) {
          command.setEnvironmentVariable("PATH", matchResult.group(1));
        }
      }
    }
  }

  @CheckForNull
  String runSimpleCommand(Command command) {
    var stdOut = new ArrayList<String>();
    var stdErr = new ArrayList<String>();
    LOG.debug("Execute command '{}'...", command);
    int exitCode;
    try {
      exitCode = commandExecutor.execute(command, stdOut::add, stdErr::add, 10_000);
    } catch (CommandException e) {
      LOG.debug("Unable to execute the command", e);
      return null;
    }
    var msg = new StringBuilder(String.format("Command '%s' exited with %s", command, exitCode));
    if (!stdOut.isEmpty()) {
      msg.append("\nstdout: ").append(String.join("\n", stdOut));
    }
    if (!stdErr.isEmpty()) {
      msg.append("\nstderr: ").append(String.join("\n", stdErr));
    }
    LOG.debug("{}", msg);
    if (exitCode != 0 || stdOut.isEmpty()) {
      return null;
    }
    return stdOut.get(0);
  }

}

