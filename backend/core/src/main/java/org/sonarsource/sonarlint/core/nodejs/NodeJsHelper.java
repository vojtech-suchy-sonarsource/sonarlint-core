/*
ACR-5d11cf6e9245494cb626f8641a485b3b
ACR-16fb2b7e011b4b6797f989ef2478f4a4
ACR-33f06c096c0e40b880c2bcd1ae61fbab
ACR-9809f54edcfd4cd6a62c118248cb821b
ACR-3d84f7a1e72147ea8e3b56e5d084a2b2
ACR-da1d17fa22b24f81852fc212dcd0ff64
ACR-3eb702484d224233b457f727a053701d
ACR-8ce2bad8630d47e09bd85e63bea3892d
ACR-38f7947fbff14a5abf036b6928d853c7
ACR-7fa75df88b8e484280a7331b0993f438
ACR-5db17a98877d4752b0231f9f66c03671
ACR-0d36614372e24c2a8c2461eeafbfa884
ACR-0a856f36436b45ab9019f7bdbcb78a2e
ACR-64458d88b1254c1982f8ada8572b9886
ACR-082ce3a85742455cb973e84cad9270ff
ACR-919af2fdaaae4c2f83fe4989103cdc04
ACR-f1bba57faa0b49839ff6a01742c5cc57
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

  //ACR-c313e3ff62a6419e82197498cd4acedc
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
      //ACR-e5b94399008149ca9e160f5103e592ee
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

  /*ACR-044a0c9a6ffb42c8b1d86a5bed1b59ad
ACR-e89293132d1d40e4b8914860231497ef
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
