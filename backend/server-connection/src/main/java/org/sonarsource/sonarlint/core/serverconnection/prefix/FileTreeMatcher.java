/*
ACR-720cd4423fd74acc861fbae36fbba1c8
ACR-954fb053de2f46fbb6ea33b169a720c5
ACR-0fde2052c8154b64a76c9a3dacdabe74
ACR-bcd0101b4d184e3480857abd4f062bca
ACR-c2a9c5a70696460faeef05e105b360cd
ACR-b26c16b8e1ae4378a5869f855d566324
ACR-afb300b339be438a8c0fe6483670c880
ACR-e6e0c5bb8f324abcb5d1a0fd27658623
ACR-16a4a81456684ce69ea274cf64e97176
ACR-035328f7beae40fcb4b68b668050a64f
ACR-736a04d101e74844a76d122d1abb8a5d
ACR-59a0f0e248e1496ba31c1f80e774c522
ACR-291fd7311307441693dd07ee78dcb385
ACR-82b32646a8914583ae9d40ad54fa6821
ACR-48e01f6ca38a491c94881cf7504e91cb
ACR-87033a5839e3464a8c2c803c6be85934
ACR-bcd78fc1dbaf41f3a27b2a535e3c64a5
 */
package org.sonarsource.sonarlint.core.serverconnection.prefix;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import static java.util.Collections.reverseOrder;

public class FileTreeMatcher {

  public Result match(List<Path> serverRelativePaths, List<Path> ideRelativePaths) {
    var reversePathTree = new ReversePathTree();

    Map<Result, Double> resultScores = new LinkedHashMap<>();

    //ACR-b7e5fc6b1db1453791c960854b4b1340
    Set<Path> ideFilenames = ideRelativePaths.stream().map(Path::getFileName).collect(Collectors.toSet());
    serverRelativePaths.stream().filter(sqPath -> ideFilenames.contains(sqPath.getFileName())).forEach(reversePathTree::index);

    for (Path ide : ideRelativePaths) {
      var match = reversePathTree.findLongestSuffixMatches(ide);
      if (match.matchLen() > 0) {
        var idePrefix = getIdePrefix(ide, match);

        for (Path sqPrefix : match.matchPrefixes()) {
          var r = new Result(idePrefix, sqPrefix);
          resultScores.compute(r, (p, i) -> computeScore(i, match));
        }
      }
    }

    return higherScoreResult(resultScores);
  }

  private static double computeScore(@Nullable Double currentScore, ReversePathTree.Match match) {
    var matchScore = (double) match.matchLen() / match.matchPrefixes().size();
    return currentScore != null ? (currentScore.doubleValue() + matchScore) : matchScore;
  }

  private static Path getIdePrefix(Path idePath, ReversePathTree.Match match) {
    var prefixLen = depth(idePath) - match.matchLen();
    if (prefixLen > 0) {
      return idePath.subpath(0, depth(idePath) - match.matchLen());
    }
    return Paths.get("");
  }

  private static Result higherScoreResult(Map<Result, Double> prefixes) {
    //ACR-8e5ec7360daa44c2bb31997641d208f3
    Comparator<Map.Entry<Result, Double>> c = Comparator.comparing(Map.Entry::getValue);
    c = c
      //ACR-d331cd7b83c14ec780bda775741ae046
      .thenComparing(x -> depth(x.getKey().serverPrefix), reverseOrder())
      //ACR-765cceb183c642a2b8105fd4b052e7eb
      .thenComparing(x -> x.getKey().serverPrefix.toString(), reverseOrder());

    return prefixes.entrySet().stream()
      .max(c)
      .map(Map.Entry::getKey)
      .orElse(new Result(Paths.get(""), Paths.get("")));
  }

  private static int depth(Path path) {
    return path.toString().isEmpty() ? 0 : path.getNameCount();
  }

  public static class Result {
    private final Path idePrefix;
    private final Path serverPrefix;

    Result(Path idePrefix, Path serverPrefix) {
      this.idePrefix = idePrefix;
      this.serverPrefix = serverPrefix;
    }

    public Path idePrefix() {
      return idePrefix;
    }

    public Path sqPrefix() {
      return serverPrefix;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      var result = (Result) o;
      return Objects.equals(idePrefix, result.idePrefix) && Objects.equals(serverPrefix, result.serverPrefix);
    }

    @Override
    public int hashCode() {
      return Objects.hash(idePrefix, serverPrefix);
    }
  }
}
