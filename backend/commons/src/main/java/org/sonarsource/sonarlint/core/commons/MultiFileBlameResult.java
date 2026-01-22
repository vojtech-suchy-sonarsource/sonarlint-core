/*
ACR-f8917381cee04900a8722af35ee3ef81
ACR-51e8d0346b1f4a708f47975549ba9f5a
ACR-ddd8b10101bd4c818d8b62189d346623
ACR-8fcd892e087945c38cc93c42dd55b2ef
ACR-b33695d573e4491cae881345e8befa8a
ACR-a77b0b5f75d941dd946add95361e585b
ACR-b85887bf6efa4009ac57fd3af123e7f7
ACR-e1bbca79f17c45509bc679a8a7c368de
ACR-255e982f1c1f4e1598adc853e389932d
ACR-288039bce8c54feabaeb9869a29512c8
ACR-bd4fd193da8f4092a1229593c31064ae
ACR-63c58bb439b24428895dd6a959e2530e
ACR-6a06907288d34213ba5cc3f667be2a86
ACR-823adeec75064b61abd87dcef84aabb2
ACR-e371357ac68c4422a80ebd0bc3b9384e
ACR-909b2ab5c96940e2808f6522958937d5
ACR-7b2afa1e9af84e11ba73946dc4efc06d
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

  /*ACR-baa8d713a19344ae92a4395e90465690
ACR-d2a54cd129204a8eae6276347e295da1
ACR-fd21495821e444fc8c2c996b744be42f
ACR-bae1d9de26244fabb1760948ae227cd2
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
