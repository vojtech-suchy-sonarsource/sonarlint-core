/*
ACR-257e5eae00204075ae3e62d545c4d602
ACR-8f7fa509f28849cb836de662007a625f
ACR-052021afee4d4235979b40c44b492719
ACR-faedc04c76004abea5d2c81d78512751
ACR-539828d261664e78984f04d29bd2edba
ACR-2753ed704d424f96b046f1553aa0f016
ACR-8c8ddde935244ce2a7d9584d9f482e6d
ACR-00f8038e51b44a0c9fd486b4879f0d85
ACR-e74963f74a0d47b58cef312c393d25fd
ACR-9bfc13a4b7794acb8314ad9b133000e5
ACR-ae4239a7df694b2aaf7a5820774c7b0b
ACR-b1e6650c72034fa3a2bf58e97b67f682
ACR-debe73f924a04abca5b7afd568ba4bd5
ACR-6c106a6d7722445c9765115a3e99e4f8
ACR-60b3d42f90d14db780f70fa8895f6875
ACR-7a0954923f4b444092ff7b60e5962e16
ACR-267a6f8c489d4fd5bb8610c1f7892472
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
