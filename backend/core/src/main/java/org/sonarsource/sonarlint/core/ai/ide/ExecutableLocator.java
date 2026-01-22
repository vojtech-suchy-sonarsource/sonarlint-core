/*
ACR-01406bd57c1e4439adb1f5119fa80a0a
ACR-d5142b41567747e3a8642ff2262ffaff
ACR-ebc51226d0c34a2c96ecced168c48832
ACR-b6ff84252b104d6da8006389399e7431
ACR-d3ab6269025747a3a0f112d2bf17d8d9
ACR-77a7aaa4d84e49108aa887e676edf701
ACR-09ecbeb89c1a4462b49194d698df620a
ACR-d5c06ad0bd29400586dd0b56fc71af48
ACR-3d2b5aafdc084cfab94d032233386c56
ACR-d7119dca8c3e43c78307fe34a177dbf4
ACR-e02a31504554472ea2dd4d6c4ea72063
ACR-bb5e86e56f1d413d8318bff1719ae654
ACR-6ec783ee5fb2444381e39e4c9d02c20e
ACR-e36ba8972b7c45eb8c92cbcd9230a501
ACR-240aef91692349af90f3ca0fd0fb44a3
ACR-28432ef8afa24e6bbafa978698c21bad
ACR-83671d545ab44776a06b6ff2631109d2
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

  //ACR-3d80a4044c424b21a56d4ee9fe6c5daa
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

    //ACR-1be72f13266b4baebd27552921d9e838
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
    //ACR-130d1f38f7f74ba68a6e4241657dbea9
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
      //ACR-62dd94dc8a8c4c2b8d87bccd3a1f64a1
      var bashPath = runSimpleCommand(Command.create("C:\\Windows\\System32\\where.exe").addArgument("$PATH:bash.exe"));
      return bashPath != null;
    } else {
      //ACR-cd2e0a9946c24d608399cee019d851b3
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

