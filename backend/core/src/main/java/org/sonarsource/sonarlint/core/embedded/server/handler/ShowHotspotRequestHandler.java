/*
ACR-2eecb3f7940445b1917cab148bdb6ffa
ACR-cdcfacdedfde4ff8887c30804b902f66
ACR-a487b85132114f2f805d209b7323b74e
ACR-cc975bfdfe8b471a8b9cb0cff126f295
ACR-fcefe7da2fc84f5ea1e417809f100fbb
ACR-51bc8b7eaa7649d1a5001bbb9416bf45
ACR-aa68653dc9aa47d18b70b3a8071a263c
ACR-c76457d0e8ab4cf6b73b137a4913fd6e
ACR-98bc24355364486cb96f4d2092ddad65
ACR-4442bc6439ca48898c2840025a54e26f
ACR-998751546885417d8d67fa1fd4fbd32f
ACR-2ccd5fe9f179416087342a7505afbeec
ACR-71da21eb61c5448cb0f3e4d9392300fe
ACR-81b4665e6ebf43288d39431253284f88
ACR-65311677119745ca87749d1163321ed8
ACR-e2491471544846ff8e26c4042a9d61e7
ACR-2ea7134cbbd54e639d4a118713e73698
 */
package org.sonarsource.sonarlint.core.embedded.server.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;
import org.sonarsource.sonarlint.core.embedded.server.RequestHandlerBindingAssistant;
import org.sonarsource.sonarlint.core.file.FilePathTranslation;
import org.sonarsource.sonarlint.core.file.PathTranslationService;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarQubeConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.HotspotDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.ShowHotspotParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspotDetails;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ShowHotspotRequestHandler implements HttpRequestHandler {
  private final SonarLintRpcClient client;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final TelemetryService telemetryService;
  private final RequestHandlerBindingAssistant requestHandlerBindingAssistant;
  private final PathTranslationService pathTranslationService;

  public ShowHotspotRequestHandler(SonarLintRpcClient client, SonarQubeClientManager sonarQubeClientManager, TelemetryService telemetryService,
    RequestHandlerBindingAssistant requestHandlerBindingAssistant, PathTranslationService pathTranslationService) {
    this.client = client;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.telemetryService = telemetryService;
    this.requestHandlerBindingAssistant = requestHandlerBindingAssistant;
    this.pathTranslationService = pathTranslationService;
  }

  @Override
  public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext context) throws HttpException, IOException {
    var origin = AttributeUtils.getOrigin(context);
    var showHotspotQuery = extractQuery(AttributeUtils.getParams(context));
    if (!Method.GET.isSame(request.getMethod()) || !showHotspotQuery.isValid()) {
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      return;
    }
    telemetryService.showHotspotRequestReceived();
    var sonarQubeConnectionParams = new SonarQubeConnectionParams(showHotspotQuery.serverUrl, null, null);
    var connectionParams = new AssistCreatingConnectionParams(sonarQubeConnectionParams);
    requestHandlerBindingAssistant.assistConnectionAndBindingIfNeededAsync(connectionParams, showHotspotQuery.projectKey, origin,
      (connectionId, boundScopes, configScopeId, cancelMonitor) -> {
        if (configScopeId != null) {
          showHotspotForScope(connectionId, configScopeId, showHotspotQuery.hotspotKey, cancelMonitor);
        }
      });

    response.setCode(HttpStatus.SC_OK);
    response.setEntity(new StringEntity("OK"));
  }

  private void showHotspotForScope(String connectionId, String configurationScopeId, String hotspotKey,
    SonarLintCancelMonitor cancelMonitor) {
    var hotspotOpt = tryFetchHotspot(connectionId, hotspotKey, cancelMonitor);
    if (hotspotOpt.isPresent()) {
      pathTranslationService.getOrComputePathTranslation(configurationScopeId)
        .ifPresent(translation -> client.showHotspot(new ShowHotspotParams(configurationScopeId, adapt(hotspotKey, hotspotOpt.get(),
          translation))));
    } else {
      client.showMessage(new ShowMessageParams(MessageType.ERROR, "Could not show the hotspot. See logs for more details"));
    }
  }

  private Optional<ServerHotspotDetails> tryFetchHotspot(String connectionId, String hotspotKey, SonarLintCancelMonitor cancelMonitor) {
    return sonarQubeClientManager.withActiveClientFlatMapOptionalAndReturn(connectionId, api -> api.hotspot().fetch(hotspotKey,
      cancelMonitor));
  }

  private static HotspotDetailsDto adapt(String hotspotKey, ServerHotspotDetails hotspot, FilePathTranslation translation) {
    return new HotspotDetailsDto(
      hotspotKey,
      hotspot.message,
      translation.serverToIdePath(hotspot.filePath),
      adapt(hotspot.textRange),
      hotspot.author,
      hotspot.status.toString(),
      hotspot.resolution != null ? hotspot.resolution.toString() : null,
      adapt(hotspot.rule),
      hotspot.codeSnippet);
  }

  private static HotspotDetailsDto.HotspotRule adapt(ServerHotspotDetails.Rule rule) {
    return new HotspotDetailsDto.HotspotRule(
      rule.key,
      rule.name,
      rule.securityCategory,
      rule.vulnerabilityProbability.toString(),
      rule.riskDescription,
      rule.vulnerabilityDescription,
      rule.fixRecommendations);
  }

  private static TextRangeDto adapt(TextRange textRange) {
    return new TextRangeDto(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(), textRange.getEndLineOffset());
  }

  private static ShowHotspotQuery extractQuery(Map<String, String> params) {
    return new ShowHotspotQuery(params.get("server"), params.get("project"), params.get("hotspot"));
  }

  private record ShowHotspotQuery(String serverUrl, String projectKey, String hotspotKey) {

    public boolean isValid() {
      return isNotBlank(serverUrl) && isNotBlank(projectKey) && isNotBlank(hotspotKey);
    }
  }
}
