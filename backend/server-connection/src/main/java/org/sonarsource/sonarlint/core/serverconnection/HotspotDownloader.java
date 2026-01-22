/*
ACR-ccd0ac54aa134f51af1f029e889205c4
ACR-86264159053e411296d7ddc4fbe55247
ACR-d12ec88f707c43c4997f1b3f54c2d9a7
ACR-b3f32f361fd9449a880166b5602d52f1
ACR-756f72e1930d424e8ac42c5b2e1fb573
ACR-d0c8cb8a9d014cd5929aade086c04b60
ACR-77ff66484f874ddb95a50015f67569c1
ACR-7ab051c1ddae45f59baf35120abf9000
ACR-de42c3ec6c4f46a2b091dd0175ca4bb6
ACR-d212a2a2ba7b4b0e8b3a2360fed7c7f6
ACR-6ff1b6f4e2804a709982e0356450101c
ACR-0702236a42d2443cb1bf36fb8ca7c74c
ACR-0c217f0994a9441684400afc69b71d7b
ACR-1d56f2c72bfb4d21ac1e593d2c2aaaa5
ACR-947c0be931614b4294119d619f8aa7b8
ACR-ab9c2557c7ee4725bf3947fab28bec9f
ACR-bdce64ddb0af42bdb11768253415c869
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

  /*ACR-b473f31188c14212a2bc840c4bd8d543
ACR-f057508404734eb69756b537f12f7c95
ACR-1a5b2e3de9b8429784940db4df81c303
ACR-0d3a28dae79844909299531b66c752f9
ACR-b88d581c3c074436ae0b5900bbced8dc
ACR-c2f3a53e641846479a4a03a675e4a311
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
