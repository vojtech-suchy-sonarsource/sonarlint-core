/*
ACR-e545f5cd034c4ffd9df3759efe216e99
ACR-37f5ef06e1f14592a517b3aaef6cf432
ACR-31968ed293f14796ba95419d65d4fc84
ACR-09f5d40d65cf457fae13c133f0ae2067
ACR-06500a91e5144bfd85e5ba6f28482d88
ACR-36a61139f8d44e379e8287b8bcf3f613
ACR-9d0330654416422d8c8f5990119f8803
ACR-875d0ebe91c344dcb739269a07cf0dff
ACR-e8bbef0ea0104acd80b955a42f22e436
ACR-c1cac5865d514f71afb4626e1097ad5d
ACR-4195d1364f9d4649a79877569405727f
ACR-ebe4a4e77c2f457e887a01130765bc85
ACR-e936a1a16e5345efbbb0176a89b5f863
ACR-078adffc307c4342a44e78bd1909ad58
ACR-4df1d10ced9c4bd7903b156471835686
ACR-e7250b51b87f41ee9cf8bbd607b0b4a3
ACR-503093afb6624fafaa9168b0e2ade89a
 */
package org.sonarsource.sonarlint.core.serverapi.hotspot;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.exception.UnexpectedBodyException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Hotspots;
import org.sonarsource.sonarlint.core.serverapi.source.SourceApi;
import org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils;

import static org.sonarsource.sonarlint.core.http.HttpClient.FORM_URL_ENCODED_CONTENT_TYPE;
import static org.sonarsource.sonarlint.core.serverapi.UrlUtils.urlEncode;
import static org.sonarsource.sonarlint.core.serverapi.util.ProtobufUtil.readMessages;
import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.toSonarQubePath;

public class HotspotApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public static final Version MIN_SQ_VERSION_SUPPORTING_PULL = Version.create("10.1");

  private static final String HOTSPOTS_SEARCH_API_URL = "/api/hotspots/search.protobuf";
  private static final String HOTSPOTS_SHOW_API_URL = "/api/hotspots/show.protobuf";
  private static final String HOTSPOTS_PULL_API_URL = "/api/hotspots/pull";
  private static final String PROJECT_KEY_QUERY_PARAM = "?projectKey=";

  private final ServerApiHelper helper;

  public HotspotApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public void changeStatus(String hotspotKey, HotspotReviewStatus status, SonarLintCancelMonitor cancelMonitor) {
    var isReviewed = status.isReviewed();
    var webApiStatus = isReviewed ? "REVIEWED" : "TO_REVIEW";
    var body = "hotspot=" + urlEncode(hotspotKey) + "&status=" + urlEncode(webApiStatus);
    if (isReviewed) {
      body += "&resolution=" + urlEncode(status.name());
    }
    helper.post("api/hotspots/change_status", FORM_URL_ENCODED_CONTENT_TYPE, body, cancelMonitor);
  }

  public Collection<ServerHotspot> getAll(String projectKey, String branchName, SonarLintCancelMonitor cancelMonitor) {
    return searchHotspots(getSearchUrl(projectKey, null, branchName), cancelMonitor);
  }

  public Collection<ServerHotspot> getFromFile(String projectKey, Path filePath, String branchName, SonarLintCancelMonitor cancelMonitor) {
    return searchHotspots(getSearchUrl(projectKey, filePath, branchName), cancelMonitor);
  }

  public HotspotApi.HotspotsPullResult pullHotspots(String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, @Nullable Long changedSince
    , SonarLintCancelMonitor cancelMonitor) {
    return ServerApiHelper.processTimed(
      () -> helper.get(getPullHotspotsUrl(projectKey, branchName, enabledLanguages, changedSince), cancelMonitor),
      response -> {
        var input = response.bodyAsStream();
        var timestamp = Hotspots.HotspotPullQueryTimestamp.parseDelimitedFrom(input);
        return new HotspotApi.HotspotsPullResult(timestamp, readMessages(input, Hotspots.HotspotLite.parser()));
      },
      duration -> LOG.debug("Pulled issues in {}ms", duration));
  }

  public static class HotspotsPullResult {
    private final Hotspots.HotspotPullQueryTimestamp timestamp;
    private final List<Hotspots.HotspotLite> hotspots;

    public HotspotsPullResult(Hotspots.HotspotPullQueryTimestamp timestamp, List<Hotspots.HotspotLite> hotspots) {
      this.timestamp = timestamp;
      this.hotspots = hotspots;
    }

    public Hotspots.HotspotPullQueryTimestamp getTimestamp() {
      return timestamp;
    }

    public List<Hotspots.HotspotLite> getHotspots() {
      return hotspots;
    }
  }

  private static String getPullHotspotsUrl(String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, @Nullable Long changedSince) {
    var enabledLanguageKeys = enabledLanguages.stream().map(SonarLanguage::getSonarLanguageKey).collect(Collectors.joining(","));
    var url = new StringBuilder()
      .append(HOTSPOTS_PULL_API_URL)
      .append(PROJECT_KEY_QUERY_PARAM)
      .append(UrlUtils.urlEncode(projectKey))
      .append("&branchName=")
      .append(UrlUtils.urlEncode(branchName));
    if (!enabledLanguageKeys.isEmpty()) {
      url.append("&languages=").append(enabledLanguageKeys);
    }
    if (changedSince != null) {
      url.append("&changedSince=").append(changedSince);
    }
    return url.toString();
  }

  public boolean supportHotspotsPull(Supplier<Version> serverVersion) {
    return supportHotspotsPull(helper.isSonarCloud(), serverVersion.get());
  }

  public static boolean supportHotspotsPull(boolean isSonarCloud, Version serverVersion) {
    return !isSonarCloud && serverVersion.compareToIgnoreQualifier(HotspotApi.MIN_SQ_VERSION_SUPPORTING_PULL) >= 0;
  }

  private Collection<ServerHotspot> searchHotspots(String searchUrl, SonarLintCancelMonitor cancelMonitor) {
    Collection<ServerHotspot> hotspots = new ArrayList<>();
    Map<String, Path> componentPathsByKey = new HashMap<>();
    helper.getPaginated(
      searchUrl,
      Hotspots.SearchWsResponse::parseFrom,
      r -> r.getPaging().getTotal(),
      r -> {
        componentPathsByKey.clear();
        componentPathsByKey.putAll(r.getComponentsList().stream().collect(Collectors.toMap(Hotspots.Component::getKey, component -> Path.of(component.getPath()))));
        return r.getHotspotsList();
      },
      hotspot -> {
        var filePath = componentPathsByKey.get(hotspot.getComponent());
        if (filePath != null) {
          hotspots.add(adapt(hotspot, filePath));
        } else {
          LOG.error("Error while fetching security hotspots, the component '" + hotspot.getComponent() + "' is missing");
        }
      },
      false,
      cancelMonitor);
    return hotspots;
  }

  private static String getSearchUrl(String projectKey, @Nullable Path filePath, String branchName) {
    return HOTSPOTS_SEARCH_API_URL
      + PROJECT_KEY_QUERY_PARAM + urlEncode(projectKey)
      + (filePath != null ? ("&files=" + urlEncode(toSonarQubePath(filePath))) : "")
      + "&branch=" + urlEncode(branchName);
  }

  public ServerHotspotDetails show(String hotspotKey, SonarLintCancelMonitor cancelMonitor) {
    try (var wsResponse = helper.get(getShowUrl(hotspotKey), cancelMonitor); var is = wsResponse.bodyAsStream()) {
      return adapt(Hotspots.ShowWsResponse.parseFrom(is), null);
    } catch (IOException e) {
      throw new UnexpectedBodyException(e);
    }
  }

  public Optional<ServerHotspotDetails> fetch(String hotspotKey, SonarLintCancelMonitor cancelMonitor) {
    Hotspots.ShowWsResponse response;
    try (var wsResponse = helper.get(getShowUrl(hotspotKey), cancelMonitor); var is = wsResponse.bodyAsStream()) {
      response = Hotspots.ShowWsResponse.parseFrom(is);
    } catch (Exception e) {
      LOG.error("Error while fetching security hotspot", e);
      return Optional.empty();
    }
    var fileKey = response.getComponent().getKey();
    var source = new SourceApi(helper).getRawSourceCode(fileKey, cancelMonitor);
    String codeSnippet;
    if (source.isPresent()) {
      try {
        codeSnippet = ServerApiUtils.extractCodeSnippet(source.get(), response.getTextRange());
      } catch (Exception e) {
        LOG.debug("Unable to compute code snippet of '" + fileKey + "' for text range: " + response.getTextRange(), e);
        codeSnippet = null;
      }
    } else {
      codeSnippet = null;
    }
    return Optional.of(adapt(response, codeSnippet));
  }

  private static ServerHotspotDetails adapt(Hotspots.ShowWsResponse hotspot, @Nullable String codeSnippet) {
    return new ServerHotspotDetails(
      hotspot.getMessage(),
      Path.of(hotspot.getComponent().getPath()),
      convertTextRange(hotspot.getTextRange()),
      hotspot.getAuthor(),
      ServerHotspotDetails.Status.valueOf(hotspot.getStatus()),
      hotspot.hasResolution() ? ServerHotspotDetails.Resolution.valueOf(hotspot.getResolution()) : null,
      adapt(hotspot.getRule()),
      codeSnippet, hotspot.getCanChangeStatus());
  }

  private static ServerHotspotDetails.Rule adapt(Hotspots.Rule rule) {
    return new ServerHotspotDetails.Rule(rule.getKey(), rule.getName(), rule.getSecurityCategory(),
      VulnerabilityProbability.valueOf(rule.getVulnerabilityProbability()),
      rule.getRiskDescription(), rule.getVulnerabilityDescription(), rule.getFixRecommendations());
  }

  private static ServerHotspot adapt(Hotspots.SearchWsResponse.Hotspot hotspot, Path filePath) {
    return new ServerHotspot(
      hotspot.getKey(),
      hotspot.getRuleKey(),
      hotspot.getMessage(),
      filePath,
      convertTextRange(hotspot.getTextRange()),
      ServerApiUtils.parseOffsetDateTime(hotspot.getCreationDate()).toInstant(),
      getStatus(hotspot),
      VulnerabilityProbability.valueOf(hotspot.getVulnerabilityProbability()),
      hotspot.getAssignee());
  }

  private static HotspotReviewStatus getStatus(Hotspots.SearchWsResponse.Hotspot hotspot) {
    var status = hotspot.getStatus();
    var resolution = hotspot.hasResolution() ? hotspot.getResolution() : null;
    return HotspotReviewStatus.fromStatusAndResolution(status, resolution);
  }

  private static String getShowUrl(String hotspotKey) {
    return HOTSPOTS_SHOW_API_URL
      + "?hotspot=" + urlEncode(hotspotKey);
  }

  private static TextRangeWithHash convertTextRange(Common.TextRange textRange) {
    return new TextRangeWithHash(textRange.getStartLine(), textRange.getStartOffset(), textRange.getEndLine(), textRange.getEndOffset(), "");
  }
}
