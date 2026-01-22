/*
ACR-078afda490924ee684f6d65b509ff5d9
ACR-e39e707093f14222b39cfc7697f36646
ACR-dd3684742a7a4e8897913d8b2e8211a3
ACR-436e4d2416984a6db329ae1b49d54199
ACR-bf8a6bc32faf4cc4803270d5dc03bb71
ACR-22a2330f08314c6c8d0929e136135885
ACR-aa39befb90714987a44afb63b6526648
ACR-1db9e4604b984f8e9f2d10b9c6e756cb
ACR-8dc400b820574d8bac9634e6fb1ec5fb
ACR-9e703a57f9074c4eade557c8c9812d9c
ACR-e7c2ce0d43b945ecb24f7ebd9cf36a05
ACR-0963ff0f21bc45efbd06673a967aa782
ACR-cd293a7b6c1e44e9bd7dc3b28b449dad
ACR-8d920666ecf248139d649c67eda2a986
ACR-3155b078da0040eeafd6abc48c496142
ACR-8aeea2fddfa04fb09515e7e232ca1230
ACR-08f59c77f3cb4f21b41131b48c503123
 */
package org.sonarsource.sonarlint.core.serverapi.fixsuggestions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.exception.TooManyRequestsException;
import org.sonarsource.sonarlint.core.serverapi.exception.UnexpectedBodyException;

import static org.sonarsource.sonarlint.core.http.HttpClient.JSON_CONTENT_TYPE;

public class FixSuggestionsApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ServerApiHelper helper;

  public FixSuggestionsApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public AiSuggestionResponseBodyDto getAiSuggestion(AiSuggestionRequestBodyDto dto, SonarLintCancelMonitor cancelMonitor) {
    //ACR-6f45bb10a4db4f24b89f9cb23127c047
    var gson = new GsonBuilder().disableHtmlEscaping().create();
    try (var response = helper.isSonarCloud() ? helper.apiPost("/fix-suggestions/ai-suggestions", JSON_CONTENT_TYPE, gson.toJson(dto), cancelMonitor)
      : helper.post("/api/v2/fix-suggestions/ai-suggestions", JSON_CONTENT_TYPE, gson.toJson(dto), cancelMonitor)) {
      return gson.fromJson(response.bodyAsString(), AiSuggestionResponseBodyDto.class);
    } catch (TooManyRequestsException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("Error while generating an AI CodeFix", e);
      throw new UnexpectedBodyException(e);
    }
  }

  public SupportedRulesResponseDto getSupportedRules(SonarLintCancelMonitor cancelMonitor) {
    try (
      var response = helper.isSonarCloud() ? helper.apiGet("/fix-suggestions/supported-rules", cancelMonitor)
        : helper.get("/api/v2/fix-suggestions/supported-rules", cancelMonitor)) {
      return new Gson().fromJson(response.bodyAsString(), SupportedRulesResponseDto.class);
    } catch (Exception e) {
      LOG.error("Error while fetching the list of AI CodeFix supported rules", e);
      throw new UnexpectedBodyException(e);
    }
  }

  public OrganizationConfigsResponseDto getOrganizationConfigs(String organizationId, SonarLintCancelMonitor cancelMonitor) {
    try (var response = helper.apiGet("/fix-suggestions/organization-configs/" + UrlUtils.urlEncode(organizationId), cancelMonitor)) {
      return new Gson().fromJson(response.bodyAsString(), OrganizationConfigsResponseDto.class);
    } catch (Exception e) {
      LOG.error("Error while fetching the AI CodeFix organization config", e);
      throw new UnexpectedBodyException(e);
    }
  }
}
