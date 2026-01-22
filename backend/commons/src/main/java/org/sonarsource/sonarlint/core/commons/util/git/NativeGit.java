/*
ACR-4b126eba7051482c98d29cff1e4273ea
ACR-8632dadbede9472995a8935541c9f78e
ACR-042c7c0253264249a323c9c85147d268
ACR-7350d9ef5e03455d8c1e156390cdf1d7
ACR-527e7de731694454a3d5308875811f6c
ACR-d52203fceb994b9a8b63ee5ec7de2c02
ACR-19ba69f053c041cbb4eb7647eebaeefd
ACR-eb4cbd49994b4d90b0bcd83a2bcfbefe
ACR-2eadcafb85ce4ee89b18d68eaefefea5
ACR-615c11264fcb42cea152460f28abf835
ACR-259c8a6729d448abbf8ae6d76799a7a4
ACR-cd8dd49fff0c4dcab0d4e42e191e1f67
ACR-4a11890874ca437bad1b21f098176f47
ACR-618087d35a6a4ad69df563d3dffc9c3c
ACR-2ce589e6ca9d402b9c6c874baf373c95
ACR-6594ecb706ac4f14998266bb34571e16
ACR-af5f1f8837004eff95486885c3e0ace0
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
      //ACR-5e631c586a874605972f2a0572bb1ce1
      return Optional.of(Version.create(versionParts[0] + "." + versionParts[1]));
    } catch (Exception e) {
      //ACR-a0a49df357f042f6a2586452f3618755
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
