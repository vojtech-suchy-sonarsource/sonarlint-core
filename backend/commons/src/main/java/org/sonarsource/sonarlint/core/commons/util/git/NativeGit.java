/*
ACR-a3f45c4e901242f3adc966dc7bf78cb5
ACR-83d06b91d8f94671a8e5d9fd0fb9f411
ACR-678570da44d94435884ae76432ed827e
ACR-3dedf2d9eeec4a7b838368d1ca01d607
ACR-2bc365fafcff447bad1517b63c1b5b3c
ACR-26efa6654ee141099451adead550299a
ACR-257949908737426fbfb48f1f67b07862
ACR-685d90fde0d4412fb2e95aad9561c5d5
ACR-cbf285d08f794453b989c8f151f3f0d6
ACR-a8a58cd5b5494ef7be638d873c79395e
ACR-074646fcbe534ae2b21cf02c374a1df6
ACR-92fa112e7987486581a5ce269cab5e10
ACR-7e09af2e4f1e413bbf3e1950e7dee402
ACR-862f6f245d3d4e599f749e415f412024
ACR-6a41d1f6c1b9499cb28b2ff54d6e307e
ACR-b8d3bab1938648cbadac4d4b9901c14d
ACR-812bdcaaf5af4d53873ac2426aaed275
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.MultiFileBlameResult;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.util.FileUtils;

import static java.lang.String.format;

public class NativeGit {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final Version MINIMUM_REQUIRED_GIT_VERSION = Version.create("2.24");
  private static final String GIT_VERSION_OUTPUT_PREFIX = "git version";
  private static final Period BLAME_HISTORY_WINDOW = Period.ofDays(365);
  private final String executable;

  public NativeGit(String executable) {
    this.executable = executable;
  }

  public boolean isSupportedVersion() {
    return version()
      .filter(version -> version.satisfiesMinRequirement(MINIMUM_REQUIRED_GIT_VERSION))
      .isPresent();
  }

  private Optional<Version> version() {
    var lines = new ArrayList<String>();
    var success = executeGitCommand(null, lines::add, executable, "--version");
    return success ? parseGitVersionOutput(lines) : Optional.empty();
  }

  static Optional<Version> parseGitVersionOutput(List<String> lines) {
    var version = lines.stream().findFirst()
      .map(String::trim)
      .filter(line -> line.startsWith(GIT_VERSION_OUTPUT_PREFIX))
      .map(line -> line.substring(GIT_VERSION_OUTPUT_PREFIX.length()))
      .map(String::trim)
      .map(actualVersion -> actualVersion.split("\\.", 3))
      .filter(versionParts -> versionParts.length > 1)
      .flatMap(NativeGit::tryCreateVersion);
    if (version.isEmpty()) {
      LOG.debug("Cannot parse git --version output: {}", String.join("\n", lines));
    }
    return version;
  }

  private static Optional<Version> tryCreateVersion(String[] versionParts) {
    try {
      //ACR-d350d073ace84903af753c6cfb861056
      return Optional.of(Version.create(versionParts[0] + "." + versionParts[1]));
    } catch (Exception e) {
      //ACR-0b4ed26ff79840debe43a55fb860cacb
    }
    return Optional.empty();
  }

  public MultiFileBlameResult blame(Path projectBaseDir, Set<URI> fileUris, Instant thresholdDateFromNewCodeDefinition) {
    LOG.debug("Using native git blame");
    var startTime = System.currentTimeMillis();
    var blamePerFile = new HashMap<String, BlameResult>();
    for (var fileUri : fileUris) {
      var filePath = FileUtils.getFilePathFromUri(fileUri).toAbsolutePath().toString();
      var filePathUnix = filePath.replace("\\", "/");
      var yearAgo = Instant.now().minus(BLAME_HISTORY_WINDOW);
      var thresholdDate = thresholdDateFromNewCodeDefinition.isAfter(yearAgo) ? yearAgo : thresholdDateFromNewCodeDefinition;
      var blameHistoryThresholdCondition = "--since='" + thresholdDate + "'";
      var command = new String[] {executable, "blame", blameHistoryThresholdCondition, filePath, "--line-porcelain", "--encoding=UTF-8"};
      var blameReader = new GitBlameReader();
      var success = executeGitCommand(projectBaseDir, blameReader::readLine, command);
      if (success) {
        blamePerFile.put(filePathUnix, blameReader.getResult());
      }
    }
    LOG.debug("Blamed {} files in {}ms", fileUris.size(), System.currentTimeMillis() - startTime);
    return new MultiFileBlameResult(blamePerFile, projectBaseDir);
  }

  private static boolean executeGitCommand(@Nullable Path workingDir, Consumer<String> lineConsumer, String... command) {
    var output = new ProcessWrapperFactory()
      .create(workingDir, lineConsumer, command)
      .execute();
    if (output.exitCode() == 0) {
      return true;
    }
    LOG.debug(format("Command failed with code: %d", output.exitCode()));
    return false;
  }
}
