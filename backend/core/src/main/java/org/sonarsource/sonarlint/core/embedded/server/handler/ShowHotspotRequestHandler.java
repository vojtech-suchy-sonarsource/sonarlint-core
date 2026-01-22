/*
ACR-eb7f5095f2734d399bf691f35e3a1e1b
ACR-638eedad2dda4d77aaa09833537c3e18
ACR-8c1455c3c5e44ef4a4fd78b0b80b5d32
ACR-f219fb3fc5e04ae5a70495b3b63e7090
ACR-d3f88ff90e6d48c68d05e75250c68bd5
ACR-5ce0042da7254585afcc509abb9e4dbb
ACR-59469d3648c847a4a5553fc6613dbfdc
ACR-229ae3d3aee34d918bad3dd5b25cc270
ACR-79d819060fee472381fb9f23262f5365
ACR-2c95c42067c34c968c6b078202871cf6
ACR-9a112a600b524a759f68be5eeb6fb8ca
ACR-702e2b7511654aa695eca11f83cc5a0a
ACR-fe43e7fa2726428e94583a5d9ea2cba7
ACR-0663549ebe334f4ab00f4f1a57bf9ef9
ACR-efb2a52c7a754b00ae4a4b1b71415c5d
ACR-09a088db13ca42e6845beedebf7a7db9
ACR-6ed7a42288b74f80995c09ce9b03ee94
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
