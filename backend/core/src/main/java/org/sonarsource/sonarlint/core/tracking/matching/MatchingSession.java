/*
ACR-804f7627eab54b51969da17ed4e6c60c
ACR-e05839fb6b7b4bb3a097fa5db89f7c68
ACR-f5d5d79d5f9b4a7f8efbdcfa5796b0f9
ACR-39e839f59ee94941b0a9ba86d07e9403
ACR-3cbccbb23c2a4573ab0b0e58ad391e54
ACR-d62b2838b14f4a40b8c0498ab697ccf6
ACR-f81f8fc5e2674eb481429d04c3f8d554
ACR-615419e705b640a5abfd8c8490099ee6
ACR-686ae947bd2a445eb1f86a89fe1d2f73
ACR-7331ac7e66104c948e6d5a28deb7041f
ACR-2f82961f1c954f65bd6cafa36222a9f9
ACR-0b0e4d6239914a4c9a00dfb8af66e62b
ACR-0a71267105f84720a97bfece6e9a8e1d
ACR-54198bbae5144e84a566f78a151672a9
ACR-199b86190cc1494cba5dc171f6f6c665
ACR-b17d27713e354c43b6721afa67a062e2
ACR-130adcf55efb48e19ca5f3c86dc3e6ec
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.analysis.RawIssue;
import org.sonarsource.sonarlint.core.commons.KnownFinding;
import org.sonarsource.sonarlint.core.tracking.IntroductionDateProvider;
import org.sonarsource.sonarlint.core.tracking.IssueMapper;
import org.sonarsource.sonarlint.core.tracking.KnownFindings;
import org.sonarsource.sonarlint.core.tracking.TextRangeUtils;
import org.sonarsource.sonarlint.core.tracking.TrackedIssue;

public class MatchingSession {
  private final Map<Path, IssueMatcher<RawIssue, KnownFinding>> issueMatchersByFile = new HashMap<>();
  private final Map<Path, IssueMatcher<RawIssue, KnownFinding>> hotspotMatchersByFile = new HashMap<>();

  private final IntroductionDateProvider introductionDateProvider;
  private final ConcurrentHashMap<Path, List<TrackedIssue>> issuesPerFile = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Path, List<TrackedIssue>> securityHotspotsPerFile = new ConcurrentHashMap<>();
  private final Set<Path> relativePathsInvolved = new HashSet<>();
  private long newIssuesFound = 0;

  public MatchingSession(KnownFindings previousFindings, IntroductionDateProvider introductionDateProvider) {
    var knownIssuesPerFile = previousFindings.getIssuesPerFile().entrySet().stream().map(entry -> Map.entry(entry.getKey(), new ArrayList<>(entry.getValue())))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    var knownSecurityHotspotsPerFile = previousFindings.getSecurityHotspotsPerFile().entrySet().stream()
      .map(entry -> Map.entry(entry.getKey(), new ArrayList<>(entry.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    knownIssuesPerFile.forEach((path, issues) -> issueMatchersByFile.put(path, new IssueMatcher<>(new KnownIssueMatchingAttributesMapper(), issues)));
    knownSecurityHotspotsPerFile.forEach((path, hotspots) -> hotspotMatchersByFile.put(path, new IssueMatcher<>(new KnownIssueMatchingAttributesMapper(), hotspots)));
    this.introductionDateProvider = introductionDateProvider;
  }

  public TrackedIssue matchWithKnownFinding(Path relativePath, RawIssue rawIssue) {
    if (rawIssue.isSecurityHotspot()) {
      return matchWithKnownSecurityHotspot(relativePath, rawIssue);
    } else {
      return matchWithKnownIssue(relativePath, rawIssue);
    }
  }

  public TrackedIssue matchWithKnownSecurityHotspot(Path relativePath, RawIssue newSecurityHotspot) {
    var hotspotMatcher = hotspotMatchersByFile.get(relativePath);
    if (hotspotMatcher == null) {
      throw new IllegalStateException("No hotspot matcher found for " + relativePath);
    }
    var trackedSecurityHotspot = matchWithKnownFinding(relativePath, hotspotMatcher, newSecurityHotspot);
    securityHotspotsPerFile.computeIfAbsent(relativePath, f -> new ArrayList<>()).add(trackedSecurityHotspot);
    relativePathsInvolved.add(relativePath);
    return trackedSecurityHotspot;
  }

  private TrackedIssue matchWithKnownIssue(Path relativePath, RawIssue rawIssue) {
    var issueMatcher = issueMatchersByFile.get(relativePath);
    if (issueMatcher == null) {
      throw new IllegalStateException("No issue matcher found for " + relativePath);
    }

    var trackedIssue = matchWithKnownFinding(relativePath, issueMatcher, rawIssue);
    issuesPerFile.computeIfAbsent(relativePath, f -> new ArrayList<>()).add(trackedIssue);
    relativePathsInvolved.add(relativePath);
    return trackedIssue;
  }

  private TrackedIssue matchWithKnownFinding(Path relativePath, IssueMatcher<RawIssue, KnownFinding> issueMatcher, RawIssue newFinding) {
    var localMatchingResult = issueMatcher.matchWith(new RawIssueFindingMatchingAttributeMapper(), List.of(newFinding));
    return localMatchingResult.getMatchOpt(newFinding)
      .map(knownFinding -> updateKnownFindingWithRawIssueData(knownFinding, newFinding))
      .orElseGet(() -> newlyKnownIssue(relativePath, newFinding));
  }

  public static TrackedIssue updateKnownFindingWithRawIssueData(KnownFinding knownIssue, RawIssue rawIssue) {
    return new TrackedIssue(knownIssue.getId(), rawIssue.getMessage(), knownIssue.getIntroductionDate(),
      false, rawIssue.getSeverity(), rawIssue.getRuleType(), rawIssue.getRuleKey(),
      TextRangeUtils.getTextRangeWithHash(rawIssue.getTextRange(), rawIssue.getClientInputFile()),
      TextRangeUtils.getLineWithHash(rawIssue.getTextRange(), rawIssue.getClientInputFile()), knownIssue.getServerKey(), rawIssue.getImpacts(), rawIssue.getFlows(),
      rawIssue.getQuickFixes(), rawIssue.getVulnerabilityProbability(), null, null, rawIssue.getRuleDescriptionContextKey(),
      rawIssue.getCleanCodeAttribute(), rawIssue.getFileUri());
  }

  private TrackedIssue newlyKnownIssue(Path relativePath, RawIssue rawFinding) {
    newIssuesFound++;
    var introductionDate = introductionDateProvider.determineIntroductionDate(relativePath, rawFinding.getLineNumbers());
    return IssueMapper.toTrackedIssue(rawFinding, introductionDate);
  }

  public Map<Path, List<TrackedIssue>> getIssuesPerFile() {
    return issuesPerFile;
  }

  public Map<Path, List<TrackedIssue>> getSecurityHotspotsPerFile() {
    return securityHotspotsPerFile;
  }

  public Set<Path> getRelativePathsInvolved() {
    return relativePathsInvolved;
  }

  public long countNewIssues() {
    return newIssuesFound;
  }

  public long countRemainingUnmatchedIssues() {
    return issueMatchersByFile.values().stream().mapToLong(IssueMatcher::getUnmatchedIssuesCount).sum();
  }
}
