/*
ACR-0afd6e817b0840098555993c6c947a6f
ACR-3c10c33ebd53480696ca4357bd8bf257
ACR-7b2dddc5a09e45ffb784d16ea7ad423b
ACR-d0d61d9ca9844bce88ac4bf9d346d459
ACR-93b4b64190e94f78b8ac2200f5daa7f7
ACR-e23dbba9453e405d9057ee06591df2aa
ACR-71c3a9574ee24ab3a31cb0f974736e1a
ACR-47b40ff2fb1446e982893c6eba984533
ACR-edb2db13491f4589acf95a49136883df
ACR-7378baf4751947589e8386b9553b2206
ACR-ca7ab53788934208a67ffa998e101126
ACR-a3561c0392354cf9a0a4c17752c416a0
ACR-15411543458e4f098be16ada2f64db37
ACR-a8688d0fe3ee44f4a4870cc52baeb879
ACR-7bbe2f13871843608f7400f21b3ab177
ACR-7c124979336745088bdaea4488cca83f
ACR-7539d16d2ee946eca912e595bb67ca11
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.CheckForNull;
import org.apache.commons.lang3.StringUtils;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata.CharHandler;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.IgnoreIssuesFilter;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.BlockIssuePattern;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssueExclusionPatternInitializer;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern.IssuePattern;

public class IssueExclusionsLoader {

  private final List<java.util.regex.Pattern> allFilePatterns;
  private final List<DoubleRegexpMatcher> blockMatchers;
  private final IgnoreIssuesFilter ignoreIssuesFilter;
  private final IssueExclusionPatternInitializer patternsInitializer;
  private final boolean enableCharHandler;

  public IssueExclusionsLoader(IssueExclusionPatternInitializer patternsInitializer, IgnoreIssuesFilter ignoreIssuesFilter) {
    this.patternsInitializer = patternsInitializer;
    this.ignoreIssuesFilter = ignoreIssuesFilter;
    this.allFilePatterns = new ArrayList<>();
    this.blockMatchers = new ArrayList<>();

    for (String pattern : patternsInitializer.getAllFilePatterns()) {
      allFilePatterns.add(java.util.regex.Pattern.compile(pattern));
    }
    for (BlockIssuePattern pattern : patternsInitializer.getBlockPatterns()) {
      blockMatchers.add(new DoubleRegexpMatcher(
        java.util.regex.Pattern.compile(pattern.getBeginBlockRegexp()),
        java.util.regex.Pattern.compile(pattern.getEndBlockRegexp())));
    }
    enableCharHandler = !allFilePatterns.isEmpty() || !blockMatchers.isEmpty();
  }

  public void addMulticriteriaPatterns(SonarLintInputFile inputFile) {
    for (IssuePattern pattern : patternsInitializer.getMulticriteriaPatterns()) {
      if (pattern.matchFile(inputFile.relativePath())) {
        ignoreIssuesFilter.addRuleExclusionPatternForComponent(inputFile, pattern.getRulePattern());
      }
    }
  }

  @CheckForNull
  public CharHandler createCharHandlerFor(SonarLintInputFile inputFile) {
    if (enableCharHandler) {
      return new IssueExclusionsRegexpScanner(inputFile, allFilePatterns, blockMatchers);
    }
    return null;
  }

  public static class DoubleRegexpMatcher {

    private final java.util.regex.Pattern firstPattern;
    private final java.util.regex.Pattern secondPattern;

    DoubleRegexpMatcher(java.util.regex.Pattern firstPattern, java.util.regex.Pattern secondPattern) {
      this.firstPattern = firstPattern;
      this.secondPattern = secondPattern;
    }

    boolean matchesFirstPattern(String line) {
      return firstPattern.matcher(line).find();
    }

    boolean matchesSecondPattern(String line) {
      return hasSecondPattern() && secondPattern.matcher(line).find();
    }

    boolean hasSecondPattern() {
      return StringUtils.isNotEmpty(secondPattern.toString());
    }
  }

  @Override
  public String toString() {
    return "Issues Exclusions - Source Scanner";
  }
}
