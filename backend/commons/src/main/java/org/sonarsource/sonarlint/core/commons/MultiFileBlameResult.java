/*
ACR-3c4abff957734fdc9fe0c00f8fa2beea
ACR-25e89022120b4c1992a8722d42782d30
ACR-150f5e21ac5e434a85328ce5ea42b5fd
ACR-9c94beab11f5459a860017fce0e34bd6
ACR-4149bdfd7e1d4e9aa09026eb96fa855e
ACR-647437ff5d964100b944b8a4f330549a
ACR-3ca187a4406649d1809af72add81a660
ACR-7a4cbe9182fd40a3a87e077b80fba1af
ACR-1d560527186e4e76863c2e5bfa6b3835
ACR-da39ab76db0d4cf5b722a2d992da04c6
ACR-264a6c96e3a6490cb1a3f7fda143040f
ACR-0d2acae9e8fd4d2781076b00cb13e61c
ACR-678f5b4741a04c43ac2376fcc7a088fc
ACR-09bd8934c0174ce7961c7ecabc830d63
ACR-405852f5fcd74ea390d0dc0cde959085
ACR-d007a3dc0c0f456c99ac77b861fe539e
ACR-01f27e66ddd847de9b7dc05410dbcf9f
 */
package org.sonarsource.sonarlint.core.commons;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.sonarsource.sonarlint.core.commons.util.git.BlameResult;

import static java.util.Objects.isNull;

public class MultiFileBlameResult {

  private final Map<String, BlameResult> blameResultPerFile;
  private final Path gitRepoRelativeProjectBaseDir;

  public MultiFileBlameResult(Map<String, BlameResult> blameResultPerFile, Path gitRepoRelativeProjectBaseDir) {
    this.blameResultPerFile = blameResultPerFile;
    this.gitRepoRelativeProjectBaseDir = gitRepoRelativeProjectBaseDir;
  }

  public static MultiFileBlameResult empty(Path gitRepoRelativeProjectBaseDir) {
    return new MultiFileBlameResult(Map.of(), gitRepoRelativeProjectBaseDir);
  }

  /*ACR-83fdfcd9b11e42279df4c9cd33bdd15a
ACR-480b34943b90468c951e2dc64eff9bc6
ACR-de76cb80cb41436f8cd63320f86427ac
ACR-5cae26f51eae4b9da0cfb5dd5fc5d031
   */
  public Optional<Instant> getLatestChangeDateForLinesInFile(Path projectDirRelativeFilePath, Collection<Integer> lineNumbers) {
    validateLineNumbersArgument(lineNumbers);

    return Optional.of(projectDirRelativeFilePath.toString())
      .map(gitRepoRelativeProjectBaseDir::resolve)
      .map(Path::toString)
      .map(FilenameUtils::separatorsToUnix)
      .map(blameResultPerFile::get)
      .map(fileBlame -> getTheLatestChange(fileBlame, lineNumbers));
  }

  private static Instant getTheLatestChange(BlameResult blameForFile, Collection<Integer> lineNumbers) {
    Instant latestDate = null;
    for (var lineNumber : lineNumbers) {
      if (lineNumber > blameForFile.lineCommitDates().size()) {
        continue;
      }
      var dateForLine = blameForFile.lineCommitDates().get(lineNumber - 1);
      if (isLineModified(dateForLine)) {
        return null;
      }
      latestDate = isNull(latestDate) || latestDate.isBefore(dateForLine) ? dateForLine : latestDate;
    }
    return latestDate;
  }

  private static void validateLineNumbersArgument(Collection<Integer> lineNumbers) {
    if (lineNumbers.stream().anyMatch(i -> i < 1)) {
      throw new IllegalArgumentException("Line numbers must be greater than 0. The numbering starts from 1 (i.e. the " +
        "first line of a file should be `1`)");
    }
  }

  private static boolean isLineModified(@Nullable Instant dateForLine) {
    return dateForLine == null;
  }
}
