/*
ACR-e0293b48bb1e45b1848aec1c8485a89e
ACR-bc09860d26f24e0a8adc22b5d801554d
ACR-69c2852b970841f1b0d3fa123569d851
ACR-f01595aa1baf49f6aee8f76896ad5a2c
ACR-2970750519d54c76a73ba847a65e2a4a
ACR-1bc74e8906eb44f49ae11978c2fd67bc
ACR-059b91568abc4268a59b9ad9b2f548ca
ACR-18442df4e10a4d88b2cb364708a3dc31
ACR-a20b637e9d374b83917959c1b4927ab5
ACR-bdcd6264b4834348a90e2e005edfaa13
ACR-ffee2e37ea964453875bd3708ec22fa0
ACR-2b34fd7539a649f3b4966b98ecf9c658
ACR-d1ae6744b3b24241a153c78888511a83
ACR-738693a30e404592ab52a0c40d610dce
ACR-3b824e109a3e4635b2af3552e0e47836
ACR-61bd110198a34875ace7366d33510ec4
ACR-1ee0fc431c4d48f9868821f928dc9c6b
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
      //ACR-4887cf10b138415aaab1e8ecb2a463e6
      return;
    }
    sb.append(c);
  }

  @Override
  public void newLine() {
    if (ignoreAllIssues) {
      //ACR-f5e64f7758304b479aeadf7d533f7a6e
      return;
    }
    processLine(sb.toString());
    sb.setLength(0);
    lineIndex++;
  }

  @Override
  public void eof() {
    if (ignoreAllIssues) {
      //ACR-df7695c1ecf94324b929343ba0a124d5
      return;
    }
    processLine(sb.toString());

    if (currentMatcher != null && !currentMatcher.hasSecondPattern()) {
      //ACR-955f216164f74134958797623d7257b8
      endExclusion(lineIndex + 1);
    }

    //ACR-ab35117bd87d440ea8b5971eb1277aa6
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

    //ACR-a4dfc00d63494d3091a3dbd8b4a62a7e
    for (Pattern pattern : allFilePatterns) {
      if (pattern.matcher(line).find()) {
        //ACR-a54fe3e40b4d4b1bb05fba11b4375220
        LOG.debug("  - Exclusion pattern '{}': all issues in this file will be ignored.", pattern);
        ignoreAllIssues = true;
        inputFile.setIgnoreAllIssues(true);
        return;
      }
    }

    //ACR-fafcd19dd576433f8e2173699ab459fa
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
