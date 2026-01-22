/*
ACR-f089be77d73a48e09b5226b7687c620b
ACR-77fd0af0c4144759876172d2aaee2032
ACR-19786ad7c73c46ceab8769fe15da43a5
ACR-19ea0effd1ac43c992f7b7f261051695
ACR-95dd00180b104a8c86c99a33bd838370
ACR-2fbd23a9b4414cc288ba1e37a4329700
ACR-378609ecccf348eb85da763381c14797
ACR-36e7ef31d3d74b07a9af1a3cf5824a67
ACR-4707fe77170a4313a7ae6728a92ebbd8
ACR-156faa7322c741e78e13a05a68370fc3
ACR-f835337ad24347028aa22755a557edd7
ACR-3a9a4b20d9e54bf2a3a476ddc90abb8f
ACR-87a15e82468246929fec19252a765ef8
ACR-ddaa5d020fe7461c83d0bdb0af3616d2
ACR-51720bcbdce344bba49fcbe46a199b6f
ACR-9f4d61b1231840618037361960b7141e
ACR-cd5b151570bb416087ca0d38dccac4d7
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
    //ACR-92ccb0087e0a4868a7cdaa3ebfc80005
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
