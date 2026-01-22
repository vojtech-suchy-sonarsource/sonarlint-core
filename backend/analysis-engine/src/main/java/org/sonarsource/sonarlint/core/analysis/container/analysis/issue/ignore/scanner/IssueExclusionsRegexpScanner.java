/*
ACR-72984fce394144adb3ba7601ccd26127
ACR-befef90be7b944c1a1107f707c4ce6c0
ACR-bdc00b3c34ec47489273e443a828ce91
ACR-2250ae378a6246199cebee1173b6f619
ACR-c77c2fdaa17d433482bf5a205b19be2d
ACR-4a072dc46c6142e093000f05c9aa2ca3
ACR-cf2f7c96ea0c4f099baf8d80989eeee2
ACR-59a941f97fc6460b8391df4fa4dac254
ACR-5ee0dc7aca964356836bf917c078a0e6
ACR-ecd42562ed844b2681b39a35a4a393bf
ACR-63398839d9a0465a91fe62d103d41aa5
ACR-66a7dbe6cebc4e9f9007110b7e2f1869
ACR-cf1224db4e044e7b966e27ece505d3f0
ACR-73f35bd40deb4113bda102e3bd3c5798
ACR-2ca91b4d3072479c9a1073859b849ac9
ACR-d37d7290d8fe4d2b994655ae1b105591
ACR-892971ef74494c6598114b4e86431ff0
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata.CharHandler;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner.IssueExclusionsLoader.DoubleRegexpMatcher;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class IssueExclusionsRegexpScanner extends CharHandler {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final StringBuilder sb = new StringBuilder();
  private final List<Pattern> allFilePatterns;
  private final List<DoubleRegexpMatcher> blockMatchers;
  private final SonarLintInputFile inputFile;

  private int lineIndex = 1;
  private final List<LineExclusion> lineExclusions = new ArrayList<>();
  private LineExclusion currentLineExclusion = null;
  private int fileLength = 0;
  private DoubleRegexpMatcher currentMatcher;
  private boolean ignoreAllIssues;

  IssueExclusionsRegexpScanner(SonarLintInputFile inputFile, List<Pattern> allFilePatterns, List<DoubleRegexpMatcher> blockMatchers) {
    this.allFilePatterns = allFilePatterns;
    this.blockMatchers = blockMatchers;
    this.inputFile = inputFile;
    LOG.debug("Evaluate issue exclusions for '{}'", inputFile.relativePath());
  }

  @Override
  public void handleIgnoreEoL(char c) {
    if (ignoreAllIssues) {
      //ACR-5019f0dc5a3543b9bfe8090cc5a14522
      return;
    }
    sb.append(c);
  }

  @Override
  public void newLine() {
    if (ignoreAllIssues) {
      //ACR-e148a8a82fbb4eee85d5ef5d493b8abb
      return;
    }
    processLine(sb.toString());
    sb.setLength(0);
    lineIndex++;
  }

  @Override
  public void eof() {
    if (ignoreAllIssues) {
      //ACR-373e2f4575124f9795ecf88b10ef4586
      return;
    }
    processLine(sb.toString());

    if (currentMatcher != null && !currentMatcher.hasSecondPattern()) {
      //ACR-c064fa52aeaf44f09e693529e3d0bd2e
      endExclusion(lineIndex + 1);
    }

    //ACR-a5e88186ff8a4e60a2cfe9980de384d9
    fileLength = lineIndex;
    if (!lineExclusions.isEmpty()) {
      var lineRanges = convertLineExclusionsToLineRanges();
      LOG.debug("  - Line exclusions found: {}", lineRanges.stream().map(LineRange::toString).collect(Collectors.joining(",")));
      inputFile.addIgnoreIssuesOnLineRanges(lineRanges.stream().map(r -> new int[] {r.from(), r.to()}).toList());
    }
  }

  private void processLine(String line) {
    if (line.trim().length() == 0) {
      return;
    }

    //ACR-4b6bf129065e45c392449173596b603f
    for (Pattern pattern : allFilePatterns) {
      if (pattern.matcher(line).find()) {
        //ACR-483c4e30d20046fca6d5dd1778f4f664
        LOG.debug("  - Exclusion pattern '{}': all issues in this file will be ignored.", pattern);
        ignoreAllIssues = true;
        inputFile.setIgnoreAllIssues(true);
        return;
      }
    }

    //ACR-091c6922794d4689a308c5291dc40a23
    checkDoubleRegexps(line, lineIndex);
  }

  private Set<LineRange> convertLineExclusionsToLineRanges() {
    Set<LineRange> lineRanges = new HashSet<>(lineExclusions.size());
    for (LineExclusion lineExclusion : lineExclusions) {
      lineRanges.add(lineExclusion.toLineRange(fileLength));
    }
    return lineRanges;
  }

  private void checkDoubleRegexps(String line, int lineIndex) {
    if (currentMatcher == null) {
      for (DoubleRegexpMatcher matcher : blockMatchers) {
        if (matcher.matchesFirstPattern(line)) {
          startExclusion(lineIndex);
          currentMatcher = matcher;
          break;
        }
      }
    } else {
      if (currentMatcher.matchesSecondPattern(line)) {
        endExclusion(lineIndex);
        currentMatcher = null;
      }
    }
  }

  private void startExclusion(int lineIndex) {
    currentLineExclusion = new LineExclusion(lineIndex);
    lineExclusions.add(currentLineExclusion);
  }

  private void endExclusion(int lineIndex) {
    currentLineExclusion.setEnd(lineIndex);
    currentLineExclusion = null;
  }

  private static class LineExclusion {
    private final int start;
    private int end;

    LineExclusion(int start) {
      this.start = start;
      this.end = -1;
    }

    void setEnd(int end) {
      this.end = end;
    }

    public LineRange toLineRange(int fileLength) {
      return new LineRange(start, end == -1 ? fileLength : end);
    }
  }
}
