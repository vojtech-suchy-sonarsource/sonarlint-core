/*
ACR-2cebb6cfbf12407a8c3133e5501325c9
ACR-80ff70975aa2426c8b3ee4f657e893e9
ACR-ab0c66c8fc8b4c85b43b313ea4bcc829
ACR-2bf872a34bf74c1da7510c4cf2153392
ACR-f64d9d51b9c645b2ac1fb372fba3cadc
ACR-f7c8f83c005043d5968cb971d851a0ce
ACR-7f9b1c73c03a42cb9ba18a92d760d625
ACR-64b06c8e918e4db4ac4a2ce6b25a62d3
ACR-ed9cc3f16dfc4b1d98824f6dafc5990d
ACR-01ad85acfaae48c4a88475b47d8539c8
ACR-53a7fd813d7e47d086603c44c0e82c00
ACR-b15c2591cf5046d89d77ba2cf335315e
ACR-8b16905d39eb48a8bbf1794744871549
ACR-e78fcf5a0bf44787b4c451b6204a96fa
ACR-fd5f14ad3dde440e82f3b7ad13e8ad01
ACR-a5fd0443c9c2482f99760ff0686af1b7
ACR-902e87b6be094aafb385e41e83f4fde2
 */
package org.sonarsource.sonarlint.core.serverapi.newcode;

import java.util.Optional;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Measures;

import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.parseOffsetDateTime;

public class NewCodeApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final String GET_NEW_CODE_DEFINITION_URL = "/api/measures/component.protobuf";
  private static final String OLD_SQ_OR_SC_PERIOD = "periods";
  private static final String NEW_SQ_PERIOD = "period";
  private static final Version NEW_SQ_VERSION = Version.create("8.1");
  private final ServerApiHelper helper;

  public NewCodeApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public Optional<NewCodeDefinition> getNewCodeDefinition(String projectKey, @Nullable String branch, Version serverVersion, SonarLintCancelMonitor cancelMonitor) {
    Measures.ComponentWsResponse response;
    var period = getPeriodForServer(helper, serverVersion);
    var requestPath = new StringBuilder().append(GET_NEW_CODE_DEFINITION_URL)
      .append("?additionalFields=")
      .append(period)
      .append("&metricKeys=projects&component=")
      .append(UrlUtils.urlEncode(projectKey));
    if (branch != null) {
      requestPath.append("&branch=").append(UrlUtils.urlEncode(branch));
    }
    try (
      var wsResponse = helper.get(requestPath.toString(), cancelMonitor);
      var is = wsResponse.bodyAsStream()) {
      response = Measures.ComponentWsResponse.parseFrom(is);
    } catch (Exception e) {
      LOG.error("Error while fetching new code definition", e);
      return Optional.empty();
    }
    var periodFromWs = getPeriodFromWs(response);
    var modeString = periodFromWs.getMode();
    var parameter = periodFromWs.hasParameter() ? periodFromWs.getParameter() : null;
    if (modeString.equals("REFERENCE_BRANCH") && parameter != null) {
      return Optional.of(NewCodeDefinition.withReferenceBranch(parameter));
    }
    var date = periodFromWs.hasDate() ? parseOffsetDateTime(periodFromWs.getDate()).toInstant().toEpochMilli() : 0;
    if ((modeString.equals("NUMBER_OF_DAYS") || modeString.equals("days")) && parameter != null) {
      var days = Integer.parseInt(parameter);
      return Optional.of(NewCodeDefinition.withNumberOfDaysWithDate(days, date));
    }
    if (modeString.equalsIgnoreCase("PREVIOUS_VERSION")) {
      return Optional.of(NewCodeDefinition.withPreviousVersion(date, parameter));
    }
    if (modeString.equals("SPECIFIC_ANALYSIS") || modeString.equals("version") || modeString.equals("date")) {
      return Optional.of(NewCodeDefinition.withSpecificAnalysis(date));
    }
    LOG.warn("Unsupported mode of new code definition: " + modeString);
    return Optional.empty();
  }

  static Measures.Period getPeriodFromWs(Measures.ComponentWsResponse response) {
    if (response.hasPeriods()) {
      return response.getPeriods().getPeriods(0);
    }
    return response.getPeriod();
  }

  static String getPeriodForServer(ServerApiHelper helper, Version serverVersion) {
    if (helper.isSonarCloud()) {
      return OLD_SQ_OR_SC_PERIOD;
    }
    if (serverVersion.compareToIgnoreQualifier(NEW_SQ_VERSION) < 0) {
      return OLD_SQ_OR_SC_PERIOD;
    }
    return NEW_SQ_PERIOD;
  }

}
