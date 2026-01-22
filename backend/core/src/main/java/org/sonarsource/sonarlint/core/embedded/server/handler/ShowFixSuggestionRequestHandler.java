/*
ACR-55a456c94cf74d869f5d9687f4efd951
ACR-825037f708104e52b9dc71242f718ca6
ACR-ec45dd4028a84f6a88d699f991ba960f
ACR-ff58b4fc54d849a6b4e7795bfbbb8aac
ACR-97c0a9cce6384c109f163a6c988a1c0d
ACR-ee9eeb554a9b43c285480fc661c2e868
ACR-aaf7ab4a4f1a4e8aa99e0dea214d5e68
ACR-1c9dc67396614066958db3eec1dc0349
ACR-eb4ea1bff00f436899b1f95fae21787a
ACR-56df4636598f46c2be8e749a506512b9
ACR-a10be6244e2b4bfe8041e2bb6db0efc3
ACR-f7d50e6a64dc4528bfb1fa89a94177f1
ACR-b66ddb6f8ba54885a1d5f83752fca79d
ACR-1c8e8fd2b01c49549e8b273cf98e99f0
ACR-bd157513aa4b4139bd0e4bae0bd5d73c
ACR-4d025125afae4ed28b9d1383eabceda2
ACR-69d88c2e1f454ba58596d5aa480a8e2e
 */
package org.sonarsource.sonarlint.core.embedded.server.handler;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Strings;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.SonarCloudActiveEnvironment;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;
import org.sonarsource.sonarlint.core.embedded.server.RequestHandlerBindingAssistant;
import org.sonarsource.sonarlint.core.event.FixSuggestionReceivedEvent;
import org.sonarsource.sonarlint.core.file.PathTranslationService;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarCloudConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarQubeConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.ChangesDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.FileEditDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.FixSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.LineRangeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.ShowFixSuggestionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.springframework.context.ApplicationEventPublisher;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.sonarsource.sonarlint.core.commons.util.StringUtils.sanitizeAgainstRTLO;

public class ShowFixSuggestionRequestHandler implements HttpRequestHandler {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final SonarLintRpcClient client;
  private final ApplicationEventPublisher eventPublisher;
  private final RequestHandlerBindingAssistant requestHandlerBindingAssistant;
  private final PathTranslationService pathTranslationService;
  private final SonarCloudActiveEnvironment sonarCloudActiveEnvironment;
  private final ClientFileSystemService clientFs;

  public ShowFixSuggestionRequestHandler(SonarLintRpcClient client, ApplicationEventPublisher eventPublisher,
    RequestHandlerBindingAssistant requestHandlerBindingAssistant,
    PathTranslationService pathTranslationService, SonarCloudActiveEnvironment sonarCloudActiveEnvironment, ClientFileSystemService clientFs) {
    this.client = client;
    this.eventPublisher = eventPublisher;
    this.requestHandlerBindingAssistant = requestHandlerBindingAssistant;
    this.pathTranslationService = pathTranslationService;
    this.sonarCloudActiveEnvironment = sonarCloudActiveEnvironment;
    this.clientFs = clientFs;
  }

  @Override
  public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext context) throws HttpException, IOException {
    var origin = AttributeUtils.getOrigin(context);
    var showFixSuggestionQuery = extractQuery(request, origin, AttributeUtils.getParams(context));

    if (!Method.POST.isSame(request.getMethod()) || !showFixSuggestionQuery.isValid()) {
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      return;
    }

    eventPublisher.publishEvent(new FixSuggestionReceivedEvent(showFixSuggestionQuery.getFixSuggestion().suggestionId,
      showFixSuggestionQuery.isSonarCloud ? AiSuggestionSource.SONARCLOUD : AiSuggestionSource.SONARQUBE,
      showFixSuggestionQuery.fixSuggestion.fileEdit.changes.size(), false));

    AssistCreatingConnectionParams serverConnectionParams = createAssistServerConnectionParams(showFixSuggestionQuery, sonarCloudActiveEnvironment);

    requestHandlerBindingAssistant.assistConnectionAndBindingIfNeededAsync(
      serverConnectionParams,
      showFixSuggestionQuery.projectKey, origin,
      (connectionId, boundScopes, configScopeId, cancelMonitor) -> {
        if (configScopeId != null) {
          if (doesClientFileExists(configScopeId, showFixSuggestionQuery.fixSuggestion.fileEdit.path, boundScopes)) {
            showFixSuggestionForScope(configScopeId, showFixSuggestionQuery.issueKey, showFixSuggestionQuery.fixSuggestion);
          } else {
            client.showMessage(new ShowMessageParams(MessageType.ERROR, "Attempted to show a fix suggestion for a file that is " +
              "not known by SonarQube for IDE"));
          }
        }
      });

    response.setCode(HttpStatus.SC_OK);
    response.setEntity(new StringEntity("OK"));
  }

  private boolean doesClientFileExists(String configScopeId, String filePath, Collection<String> boundScopes) {
    var optTranslation = pathTranslationService.getOrComputePathTranslation(configScopeId);
    if (optTranslation.isPresent()) {
      var translation = optTranslation.get();
      var idePath = translation.serverToIdePath(Paths.get(filePath));
      for (var scope : boundScopes) {
        for (var file : clientFs.getFiles(scope)) {
          if (Path.of(file.getUri()).endsWith(idePath)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private static AssistCreatingConnectionParams createAssistServerConnectionParams(ShowFixSuggestionQuery query, SonarCloudActiveEnvironment sonarCloudActiveEnvironment) {
    String tokenName = query.getTokenName();
    String tokenValue = query.getTokenValue();
    if (query.isSonarCloud) {
      //ACR-8afc9b0407514abdafabcb424daa56d6
      var region = sonarCloudActiveEnvironment.getRegionOrThrow(query.getServerUrl());
      return new AssistCreatingConnectionParams(new SonarCloudConnectionParams(query.getOrganizationKey(), tokenName, tokenValue, SonarCloudRegion.valueOf(region.name())));
    } else {
      return new AssistCreatingConnectionParams(new SonarQubeConnectionParams(query.getServerUrl(), tokenName, tokenValue));
    }
  }

  private void showFixSuggestionForScope(String configScopeId, String issueKey, FixSuggestionPayload fixSuggestion) {
    pathTranslationService.getOrComputePathTranslation(configScopeId).ifPresent(translation -> {
      var fixSuggestionDto = new FixSuggestionDto(
        fixSuggestion.suggestionId,
        fixSuggestion.explanation(),
        new FileEditDto(
          translation.serverToIdePath(Paths.get(fixSuggestion.fileEdit.path)),
          fixSuggestion.fileEdit.changes.stream().map(c ->
            new ChangesDto(
              new LineRangeDto(c.beforeLineRange.startLine, c.beforeLineRange.endLine),
              c.before,
              c.after)
          ).toList()
        )
      );
      client.showFixSuggestion(new ShowFixSuggestionParams(configScopeId, issueKey, fixSuggestionDto));
    });
  }

  @VisibleForTesting
  ShowFixSuggestionQuery extractQuery(ClassicHttpRequest request, String origin, Map<String, String> params) throws HttpException, IOException {
    var payload = extractAndValidatePayload(request);
    boolean isSonarCloud = sonarCloudActiveEnvironment.isSonarQubeCloud(origin);
    String serverUrl;
    if (isSonarCloud) {
      serverUrl = Strings.CS.removeEnd(origin, "/");
    } else {
      serverUrl = params.get("server");
    }
    return new ShowFixSuggestionQuery(serverUrl, params.get("project"), params.get("issue"), params.get("branch"),
      params.get("tokenName"), params.get("tokenValue"), params.get("organizationKey"), isSonarCloud, payload);
  }

  private static FixSuggestionPayload extractAndValidatePayload(ClassicHttpRequest request) throws IOException, ParseException {
    var requestEntityString = EntityUtils.toString(request.getEntity(), "UTF-8");
    FixSuggestionPayload payload = null;
    try {
      payload = new Gson().fromJson(requestEntityString, FixSuggestionPayload.class);
    } catch (Exception e) {
      //ACR-6ec8561a887a474783f41f0eb204db81
      LOG.error("Could not deserialize fix suggestion payload", e);
    }
    return payload;
  }

  @VisibleForTesting
  public static class ShowFixSuggestionQuery {
    private final String serverUrl;
    private final String projectKey;
    private final String issueKey;
    @Nullable
    private final String branch;
    @Nullable
    private final String tokenName;
    @Nullable
    private final String tokenValue;
    @Nullable
    private final String organizationKey;
    private final boolean isSonarCloud;
    private final FixSuggestionPayload fixSuggestion;

    public ShowFixSuggestionQuery(@Nullable String serverUrl, String projectKey, String issueKey, @Nullable String branch,
      @Nullable String tokenName, @Nullable String tokenValue, @Nullable String organizationKey, boolean isSonarCloud,
      FixSuggestionPayload fixSuggestion) {
      this.serverUrl = serverUrl;
      this.projectKey = projectKey;
      this.issueKey = issueKey;
      this.branch = branch;
      this.tokenName = tokenName;
      this.tokenValue = tokenValue;
      this.organizationKey = organizationKey;
      this.isSonarCloud = isSonarCloud;
      this.fixSuggestion = fixSuggestion;
    }

    public boolean isValid() {
      return isNotBlank(projectKey) && isNotBlank(issueKey)
        && (isSonarCloud || isNotBlank(serverUrl))
        && (!isSonarCloud || isNotBlank(organizationKey))
        && fixSuggestion.isValid() && isTokenValid();
    }

    /*ACR-5da6afd73b3c44fab42ff727dc708fab
ACR-698d47e964e94d03aeef17025e592c09
     */
    public boolean isTokenValid() {
      if (tokenName != null && tokenValue != null) {
        return isNotEmpty(tokenName) && isNotEmpty(tokenValue);
      }

      return tokenName == null && tokenValue == null;
    }

    public String getServerUrl() {
      return serverUrl;
    }

    public String getProjectKey() {
      return projectKey;
    }

    @Nullable
    public String getOrganizationKey() {
      return organizationKey;
    }

    public String getIssueKey() {
      return issueKey;
    }

    @Nullable
    public String getBranch() {
      return branch;
    }

    @Nullable
    public String getTokenName() {
      return tokenName;
    }

    @Nullable
    public String getTokenValue() {
      return tokenValue;
    }

    public FixSuggestionPayload getFixSuggestion() {
      return fixSuggestion;
    }
  }

  @VisibleForTesting
  public record FixSuggestionPayload(FileEditPayload fileEdit, String suggestionId, String explanation) {

    public FixSuggestionPayload(FileEditPayload fileEdit, String suggestionId, String explanation) {
      this.fileEdit = fileEdit;
      this.suggestionId = suggestionId;
      this.explanation = escapeHtml4(explanation);
    }

    public boolean isValid() {
      return fileEdit.isValid() && !suggestionId.isBlank();
    }

  }

  @VisibleForTesting
  public record FileEditPayload(List<ChangesPayload> changes, String path) {

    public boolean isValid() {
      return !path.isBlank() && changes.stream().allMatch(ChangesPayload::isValid);
    }

  }

  @VisibleForTesting
  public record ChangesPayload(TextRangePayload beforeLineRange, String before, String after) {

    public ChangesPayload(TextRangePayload beforeLineRange, String before, String after) {
      this.beforeLineRange = beforeLineRange;
      this.before = sanitizeAgainstRTLO(before);
      this.after = sanitizeAgainstRTLO(after);
    }

    public boolean isValid() {
      return beforeLineRange.isValid();
    }

  }

  @VisibleForTesting
  public record TextRangePayload(int startLine, int endLine) {

    public boolean isValid() {
      return startLine >= 0 && endLine >= 0 && startLine <= endLine;
    }

  }

}
