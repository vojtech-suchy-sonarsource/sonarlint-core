/*
ACR-8ae65ccbfae146bdaffbf234d44ac67e
ACR-3465b69e73e547ce9c21115bc2ec2cc3
ACR-aecb7acc3da34d7da40d47f50dbba84f
ACR-19a5afe028114ec1af01c9398a076e8b
ACR-0789d4f029dd460985d0cb8703f07cac
ACR-ee9d968efd9246cfb29ae7af50e15ca9
ACR-a4d43f6bfa8c4238bb1c411bcf4737d3
ACR-75556ae31a1645608405458a89d588c6
ACR-21a3684182ff4abaa925a09febaf5643
ACR-ba2046f62c534ccb9441c22859228886
ACR-3095044770a64698972a76dfca956d93
ACR-550a7717c23a4151890da48672c4a09c
ACR-2a2401913ed0454a8cc2dfec9f615db6
ACR-b2b3a7991b99437a849116723d81e376
ACR-c8e84024c22d488cbbd440ad637a3946
ACR-8831058dee56482ca790311753c1331d
ACR-52bf06a0454a4abb8e3a7a7c165a63e5
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.hotspot.HotspotApi;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Hotspots;

import static java.util.function.Predicate.not;

public class HotspotDownloader {

  private final Set<SonarLanguage> enabledLanguages;

  public HotspotDownloader(Set<SonarLanguage> enabledLanguages) {
    this.enabledLanguages = enabledLanguages;
  }

  /*ACR-a2fc0640267c48d6bf55ab31feb4a450
ACR-bf86bb7732dc4b788de43131419c6e45
ACR-55a4962350e14acda3424205f1c4a0c1
ACR-df06954ebbe6448eb84774a2dc3191d0
ACR-86044f1b09d94110a6c1878b93c5cfe2
ACR-f1dd067464ba4e9aa16a4ee89334bd9a
   */
  public PullResult downloadFromPull(HotspotApi hotspotApi, String projectKey, String branchName, Optional<Instant> lastSync, SonarLintCancelMonitor cancelMonitor) {
    var apiResult = hotspotApi.pullHotspots(projectKey, branchName, enabledLanguages, lastSync.map(Instant::toEpochMilli).orElse(null), cancelMonitor);
    var changedHotspots = apiResult.getHotspots()
      .stream()
      .filter(not(Hotspots.HotspotLite::getClosed))
      .map(HotspotDownloader::convertLiteHotspot)
      .toList();
    var closedIssueKeys = apiResult.getHotspots()
      .stream()
      .filter(Hotspots.HotspotLite::getClosed)
      .map(Hotspots.HotspotLite::getKey)
      .collect(Collectors.toSet());

    return new PullResult(Instant.ofEpochMilli(apiResult.getTimestamp().getQueryTimestamp()), changedHotspots, closedIssueKeys);
  }

  private static ServerHotspot convertLiteHotspot(Hotspots.HotspotLite liteHotspotFromWs) {
    var creationDate = Instant.ofEpochMilli(liteHotspotFromWs.getCreationDate());
    return new ServerHotspot(
      liteHotspotFromWs.getKey(),
      liteHotspotFromWs.getRuleKey(),
      liteHotspotFromWs.getMessage(),
      Path.of(liteHotspotFromWs.getFilePath()),
      toServerHotspotTextRange(liteHotspotFromWs.getTextRange()),
      creationDate,
      fromHotspotLite(liteHotspotFromWs),
      VulnerabilityProbability.valueOf(liteHotspotFromWs.getVulnerabilityProbability()),
      liteHotspotFromWs.getAssignee()
    );
  }

  private static HotspotReviewStatus fromHotspotLite(Hotspots.HotspotLite hotspot) {
    var status = hotspot.getStatus();
    var resolution = hotspot.hasResolution() ? hotspot.getResolution() : null;
    return HotspotReviewStatus.fromStatusAndResolution(status, resolution);
  }

  private static TextRangeWithHash toServerHotspotTextRange(Hotspots.TextRange textRange) {
    return new TextRangeWithHash(
      textRange.getStartLine(),
      textRange.getStartLineOffset(),
      textRange.getEndLine(),
      textRange.getEndLineOffset(),
      textRange.getHash()
    );
  }

  public static class PullResult {
    private final Instant queryTimestamp;
    private final List<ServerHotspot> changedHotspots;
    private final Set<String> closedHotspotKeys;

    public PullResult(Instant queryTimestamp, List<ServerHotspot> changedHotspots, Set<String> closedHotspotKeys) {
      this.queryTimestamp = queryTimestamp;
      this.changedHotspots = changedHotspots;
      this.closedHotspotKeys = closedHotspotKeys;
    }

    public Instant getQueryTimestamp() {
      return queryTimestamp;
    }

    public List<ServerHotspot> getChangedHotspots() {
      return changedHotspots;
    }

    public Set<String> getClosedHotspotKeys() {
      return closedHotspotKeys;
    }
  }

}
