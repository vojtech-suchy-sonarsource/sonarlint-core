/*
ACR-9c8bd7d4d5b14c4c8e642dee91643faa
ACR-db4a3c4c871f4f0daf10ba1a8e363e27
ACR-daa2450d54d946f98d41a3c19fe14160
ACR-1a5f6b83d5514b6fa2ee02af04f9957d
ACR-eae264481e6147f9aff4deceabbe37a1
ACR-2eab7a160f8948a488859e8550d6bb23
ACR-3e3e114e52544177854a9e460dbc3061
ACR-fa552be2015347a7ac15af0b31cfe40d
ACR-a6debb5dd0104de4a9fa39dfdcc68fd2
ACR-057cb19d634841aaa67064e0feb99d57
ACR-58a8627405414bda83c3051f462e4f5c
ACR-c90eff6272984e55bd8b280a972ec7fc
ACR-035f33d5d6b3461497c4c6f0a5b3a247
ACR-7fcad109218340eb9ddad94583e5657a
ACR-ffc6233f38c04020a406ac73f460d78f
ACR-e00b89afe19b46be94b9bd2a3b9fa103
ACR-59df6be55dd9499e9383bf361fcd3b97
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

    //ACR-216d21edf5734b1081686038102ab301
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
    //ACR-63af8bff177445ca9becabe19ae9b518
    Comparator<Map.Entry<Result, Double>> c = Comparator.comparing(Map.Entry::getValue);
    c = c
      //ACR-5dd48be3b5f24716b47f51e0c755bfe0
      .thenComparing(x -> depth(x.getKey().serverPrefix), reverseOrder())
      //ACR-a74a41a222ac4f58aeafedcae45689cb
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
