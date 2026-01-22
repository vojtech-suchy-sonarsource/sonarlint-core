/*
ACR-e18a42336e66474f83031f452697c063
ACR-f52651934cd94683ac6f29b3ae29260f
ACR-dcd31faa938140748701f95aa88cc471
ACR-e070281703314a078e3c6d828bf17962
ACR-be85e59b0d1a4f66be2f8438a866f7e9
ACR-1a10981bca254483b3a620619b5f7f1e
ACR-c466fb384a3a4708816131e01dd81977
ACR-313c6265b5ec49a3b00705addf5b4810
ACR-23756005598c4364969735b5ad7fcd10
ACR-85fa45e13e4e48e68259fb4c0d2792bb
ACR-72c764e635a54fffabbe3ca787180e36
ACR-80cd8e2e0c2f4ca5807fd89b1b771334
ACR-c4173f21220e4bbfb437004923901186
ACR-33c75e6137b343648251c7a44dcd5025
ACR-13f4003e4535407e8144a20893182ee9
ACR-3ae362de603046a88430b39674124369
ACR-f3d9cc46b02f4182ab36c19d606e5e0e
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
