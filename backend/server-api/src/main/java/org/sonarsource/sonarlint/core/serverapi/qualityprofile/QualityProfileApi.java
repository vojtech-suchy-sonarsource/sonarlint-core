/*
ACR-444aab87057a43c48030f6221d3a025a
ACR-812489bc823c434dbc31785b6e9a42cb
ACR-ac8396b1159243a68248edd265cf4498
ACR-57d3f27970914d22b6a5c845700d98ff
ACR-6557737ab24a4b1da2ac6dacfb75d5c8
ACR-419a9f411cef40cf9ea76b1408e7c745
ACR-790c8d0cd8fb4691be23da3bb84bf22c
ACR-9498203539e24a82aa7c27e6bef281fa
ACR-35c83321fd1a4487a5366436e7451e05
ACR-50a641c4db744e7891f2c8a833bf9bac
ACR-1c10dd9544514c808d4b40409be0e891
ACR-a9126f2de78b48c283a8a057deab87d6
ACR-85015c1dd9be4dc3a27bac58c0ab6135
ACR-c1f42d13d6aa4ef8a4a39a9bb8fd948d
ACR-8c439d22b57f42e285c019121b1d89ec
ACR-33a1515005ca442e932d380515874aa0
ACR-fd7c48a6368f46b29b9bf5d9fce739ee
 */
package org.sonarsource.sonarlint.core.serverapi.qualityprofile;

import java.util.List;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.exception.NotFoundException;
import org.sonarsource.sonarlint.core.serverapi.exception.ProjectNotFoundException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Qualityprofiles;

public class QualityProfileApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String DEFAULT_QP_SEARCH_URL = "/api/qualityprofiles/search.protobuf";

  private final ServerApiHelper helper;

  public QualityProfileApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public List<QualityProfile> getQualityProfiles(String projectKey, SonarLintCancelMonitor cancelMonitor) {
    Qualityprofiles.SearchWsResponse qpResponse;
    var url = new StringBuilder();
    url.append(DEFAULT_QP_SEARCH_URL + "?project=");
    url.append(UrlUtils.urlEncode(projectKey));
    helper.getOrganizationKey()
      .ifPresent(org -> url.append("&organization=").append(UrlUtils.urlEncode(org)));
    try {
      qpResponse = ServerApiHelper.processTimed(
        () -> helper.get(url.toString(), cancelMonitor),
        response -> Qualityprofiles.SearchWsResponse.parseFrom(response.bodyAsStream()),
        duration -> LOG.debug("Downloaded project quality profiles in {}ms", duration));
      return qpResponse.getProfilesList().stream().map(QualityProfileApi::adapt).toList();
    } catch (NotFoundException e) {
      throw new ProjectNotFoundException(projectKey, helper.getOrganizationKey().orElse(null));
    }
  }

  private static QualityProfile adapt(Qualityprofiles.SearchWsResponse.QualityProfile wsQualityProfile) {
    return new QualityProfile(
      wsQualityProfile.getIsDefault(),
      wsQualityProfile.getKey(),
      wsQualityProfile.getName(),
      wsQualityProfile.getLanguage(),
      wsQualityProfile.getLanguageName(),
      wsQualityProfile.getActiveRuleCount(),
      wsQualityProfile.getRulesUpdatedAt(),
      wsQualityProfile.getUserUpdatedAt());
  }
}
