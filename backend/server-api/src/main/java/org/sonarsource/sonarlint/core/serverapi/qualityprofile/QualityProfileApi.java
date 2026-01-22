/*
ACR-d80db904bb1a46bb82e2d2b3cb3a6bf6
ACR-8d32437f83d94237b8f2937859759a35
ACR-05fe34ad0ec14faea2910477b5f80fdc
ACR-5e6afe57820b429a9c6243aa1f3928c3
ACR-03b109baa8b54207973842fadac92161
ACR-a2f1b6943d714c0e94b4d731abb6ee5f
ACR-1e1093952c6641958830adaa2dfd131d
ACR-c1f94df6ad0742a1b9d6882ab03e47b1
ACR-159acd3cba234696a63151a919006e8c
ACR-12600142b57a47d386dc435ce9efcac0
ACR-11ded064edc445efa22c88428872c77b
ACR-324d34e487d6455e854603c7432f813a
ACR-eae47f2145e64e8bbdb518c1d30b910b
ACR-59dd2a5bce3c4e629b90f962429a2643
ACR-f3fbd72d72d04eb7ac91801afc85cb6a
ACR-9da83f71eb344062a7b0457e00d03e31
ACR-c566e593dda54cf881663a7a18fe5773
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
