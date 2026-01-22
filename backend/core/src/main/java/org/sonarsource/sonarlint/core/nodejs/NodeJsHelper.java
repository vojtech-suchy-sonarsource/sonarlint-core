/*
ACR-d96997a693ff4c28ae30827154c1298d
ACR-24cfe36560454d0889e070ea08cbe562
ACR-05f03b4d11f9485a8de004a4f1b318c9
ACR-85aa100075e446f9be645145e8f6fba2
ACR-432b9cb51ce042db9f3b22954cf1ff53
ACR-2beb4a29308847579afe02a65fbf2040
ACR-d2fe3547a11944be8d34125689263f24
ACR-29f78055b16744d696b78c96e1b837b7
ACR-c05d51287d2b4abda98c365b3d90de91
ACR-1bd8848e4ef24bb7b5f2ed96460ca36b
ACR-81bf9d25e2524d148b1e65d32e7a8051
ACR-bac28022484e4a5abec49abb83592af7
ACR-c4c08d04cc8740b5ac2136bdfee76dd2
ACR-ad056b5ce0814be6a936bb31684c8e6f
ACR-da13858ffd4048b09837d3d53f646478
ACR-b86fe2377c684a829a4a4c7a9ad6a060
ACR-5f06f96316a1496491a80547becac59f
 */
package org.sonarsource.sonarlint.core.nodejs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandException;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class NodeJsHelper {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final Pattern NODEJS_VERSION_PATTERN = Pattern.compile("v?(\\d+\\.\\d+\\.\\d+(?:-\\S+)?)");
  private final System2 system2;
  private final Path pathHelperLocationOnMac;
  private final CommandExecutor commandExecutor;

  public NodeJsHelper() {
    this(System2.INSTANCE, Paths.get("/usr/libexec/path_helper"), CommandExecutor.create());
  }

  //ACR-db5d35775bef4f5cb80f92b19eed6408
  NodeJsHelper(System2 system2, Path pathHelperLocationOnMac, CommandExecutor commandExecutor) {
    this.system2 = system2;
    this.pathHelperLocationOnMac = pathHelperLocationOnMac;
    this.commandExecutor = commandExecutor;
  }

  @CheckForNull
  public InstalledNodeJs autoDetect() {
    return detect(null);
  }

  @CheckForNull
  public InstalledNodeJs detect(@Nullable Path configuredNodejsPath) {
    var detectedNodePath = locateNode(configuredNodejsPath);
    if (detectedNodePath != null) {
      var nodeJsVersion = readNodeVersion(detectedNodePath);
      if (nodeJsVersion == null) {
        LOG.warn("Unable to query node version");
      } else {
        return new InstalledNodeJs(detectedNodePath, nodeJsVersion);
      }
    }
    return null;
  }

  @CheckForNull
  private Version readNodeVersion(Path detectedNodePath) {
    LOG.debug("Checking node version...");
    String nodeVersionStr;
    var forcedNodeVersion = System.getProperty("sonarlint.internal.nodejs.forcedVersion");
    if (forcedNodeVersion != null) {
      nodeVersionStr = forcedNodeVersion;
    } else {
      var command = Command.create(detectedNodePath.toString()).addArgument("-v");
      nodeVersionStr = runSimpleCommand(command);
    }
    Version nodeJsVersion = null;
    if (nodeVersionStr != null) {
      var matcher = NODEJS_VERSION_PATTERN.matcher(nodeVersionStr);
      if (matcher.matches()) {
        var version = matcher.group(1);
        nodeJsVersion = Version.create(version);
        LOG.debug("Detected node version: {}", nodeJsVersion);
      } else {
        LOG.debug("Unable to parse node version: {}", nodeVersionStr);
      }
    }
    return nodeJsVersion;
  }

  @CheckForNull
  private Path locateNode(@Nullable Path configuredNodejsPath) {
    if (configuredNodejsPath != null) {
      LOG.debug("Node.js path provided by configuration: {}", configuredNodejsPath);
      return configuredNodejsPath;
    }
    LOG.debug("Looking for node in the PATH");

    var forcedPath = System.getProperty("sonarlint.internal.nodejs.forcedPath");
    String result;
    if (forcedPath != null) {
      result = forcedPath;
    } else if (system2.isOsWindows()) {
      result = runSimpleCommand(Command.create("C:\\Windows\\System32\\where.exe").addArgument("$PATH:node.exe"));
    } else {
      //ACR-54798185340344a8a0aba6ca3ba9f01d
      var which = Command.create("/usr/bin/which").addArgument("node");
      computePathEnvForMacOs(which);
      result = runSimpleCommand(which);
    }
    if (result != null) {
      LOG.debug("Found node at {}", result);
      return Paths.get(result);
    } else {
      LOG.debug("Unable to locate node");
      return null;
    }
  }

  private void computePathEnvForMacOs(Command which) {
    if (system2.isOsMac() && Files.exists(pathHelperLocationOnMac)) {
      var command = Command.create(pathHelperLocationOnMac.toString()).addArgument("-s");
      var pathHelperOutput = runSimpleCommand(command);
      if (pathHelperOutput != null) {
        var regex = Pattern.compile("^\\s*PATH=\"([^\"]+)\"; export PATH;?\\s*$");
        var matchResult = regex.matcher(pathHelperOutput);
        if (matchResult.matches()) {
          which.setEnvironmentVariable("PATH", matchResult.group(1));
        }
      }
    }
  }

  /*ACR-386f97ccd8a04ea787ad53ea2a4edd0d
ACR-83ed37b6e49c42da996bf0d56b993938
   */
  @CheckForNull
  private String runSimpleCommand(Command command) {
    List<String> stdOut = new ArrayList<>();
    List<String> stdErr = new ArrayList<>();
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
