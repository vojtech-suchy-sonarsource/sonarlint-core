/*
ACR-fcce41886e7c40bcb72193b37bee46ad
ACR-8fb9f29ee28e4b51a3c3103f4803b6eb
ACR-ab567083612045728d4b4e8bcf4df5cc
ACR-ef0dfe86b06142a9982f85175c1b26d3
ACR-68e278329c2e43b38cd7b625d1165682
ACR-0d166480cb574e36b083876adc2fcc19
ACR-8c21ab0f39bc4de689dc0944a8890188
ACR-ccd696486760494a872d84b63749dacc
ACR-af8db479f5b64d03ab95cd6fda4462df
ACR-e24d22d6d98f43a6b94cee76d13c443e
ACR-b8b32218e3e54574956d54c13e4e66d0
ACR-2b1d4b3d76004049aa80566f71b51f89
ACR-3e94bca6382f4810b926aa456d8f03fa
ACR-ebe40d6b754d4801a4d2183944a19316
ACR-e75e5c790b1147458fb4921cdf6b6cfe
ACR-63c28bb739154fe98bb561197f287f63
ACR-531baac4166f4c9b8cace3de5120bc41
 */
package org.sonarsource.sonarlint.core.remediation.aicodefix;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.FixSuggestionReceivedEvent;
import org.sonarsource.sonarlint.core.fs.ClientFile;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.reporting.PreviouslyRaisedFindingsRepository;
import org.sonarsource.sonarlint.core.repository.reporting.RaisedIssue;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.SuggestFixChangeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.SuggestFixResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.exception.TooManyRequestsException;
import org.sonarsource.sonarlint.core.serverapi.fixsuggestions.AiSuggestionRequestBodyDto;
import org.sonarsource.sonarlint.core.serverapi.fixsuggestions.AiSuggestionResponseBodyDto;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFix;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixFeatureEnablement;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixRepository;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixSettings;
import org.sonarsource.sonarlint.core.tracking.TaintVulnerabilityTrackingService;
import org.springframework.context.ApplicationEventPublisher;

import static java.util.Objects.requireNonNull;
import static org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode.CONFIG_SCOPE_NOT_BOUND;
import static org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode.CONNECTION_NOT_FOUND;
import static org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode.FILE_NOT_FOUND;
import static org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode.ISSUE_NOT_FOUND;
import static org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode.TOO_MANY_REQUESTS;

public class AiCodeFixService {
  private final ConnectionConfigurationRepository connectionRepository;
  private final ConfigurationRepository configurationRepository;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final PreviouslyRaisedFindingsRepository previouslyRaisedFindingsRepository;
  private final ClientFileSystemService clientFileSystemService;
  private final ApplicationEventPublisher eventPublisher;
  private final TaintVulnerabilityTrackingService taintVulnerabilityTrackingService;
  private final AiCodeFixRepository aiCodeFixRepository;

  public AiCodeFixService(ConnectionConfigurationRepository connectionRepository, ConfigurationRepository configurationRepository, SonarQubeClientManager sonarQubeClientManager,
    PreviouslyRaisedFindingsRepository previouslyRaisedFindingsRepository, ClientFileSystemService clientFileSystemService,
    ApplicationEventPublisher eventPublisher, TaintVulnerabilityTrackingService taintVulnerabilityTrackingService, AiCodeFixRepository aiCodeFixRepository) {
    this.connectionRepository = connectionRepository;
    this.configurationRepository = configurationRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.previouslyRaisedFindingsRepository = previouslyRaisedFindingsRepository;
    this.clientFileSystemService = clientFileSystemService;
    this.eventPublisher = eventPublisher;
    this.taintVulnerabilityTrackingService = taintVulnerabilityTrackingService;
    this.aiCodeFixRepository = aiCodeFixRepository;
  }

  public static AiCodeFixSettings aiCodeFixMapping(AiCodeFix entity) {
    return new AiCodeFixSettings(
      Sets.newHashSet(entity.supportedRules()),
      entity.organizationEligible(),
      AiCodeFixFeatureEnablement.valueOf(entity.enablement().name()),
      Sets.newHashSet(entity.enabledProjectKeys()));
  }

  public Optional<AiCodeFixFeature> getFeature(Binding binding) {
    return aiCodeFixRepository.get(binding.connectionId())
      .map(AiCodeFixService::aiCodeFixMapping)
      .filter(settings -> settings.isFeatureEnabled(binding.sonarProjectKey()))
      .map(AiCodeFixFeature::new);
  }

  public SuggestFixResponse suggestFix(String configurationScopeId, UUID issueId, SonarLintCancelMonitor cancelMonitor) {
    var bindingWithOrg = ensureBound(configurationScopeId);
    var connection = sonarQubeClientManager.getClientOrThrow(bindingWithOrg.binding().connectionId());
    var responseBodyDto = connection.withClientApiAndReturn(serverApi -> {
      var issueOptional = previouslyRaisedFindingsRepository.findRaisedIssueById(issueId);
      if (issueOptional.isPresent()) {
        return generateResponseBodyForIssue(serverApi, issueOptional.get(), issueId, bindingWithOrg, cancelMonitor);
      } else {
        var taintOptional = taintVulnerabilityTrackingService.getTaintVulnerability(configurationScopeId, issueId, cancelMonitor);
        if (taintOptional.isPresent()) {
          return generateResponseBodyForTaint(serverApi, taintOptional.get(), configurationScopeId, bindingWithOrg, cancelMonitor);
        } else {
          throw new ResponseErrorException(new ResponseError(ISSUE_NOT_FOUND, "The provided issue or taint does not exist", issueId));
        }
      }
    });
    return adapt(responseBodyDto);
  }

  private AiSuggestionResponseBodyDto generateResponseBodyForIssue(ServerApi serverApi, RaisedIssue raisedIssue, UUID issueId,
    BindingWithOrg bindingWithOrg, SonarLintCancelMonitor cancelMonitor) {
    var aiCodeFixFeature = getFeature(bindingWithOrg.binding());
    if (!aiCodeFixFeature.map(feature -> feature.isFixable(raisedIssue)).orElse(false)) {
      throw new ResponseErrorException(new ResponseError(ResponseErrorCode.InvalidParams, "The provided issue cannot be fixed", issueId));
    }

    AiSuggestionResponseBodyDto fixResponseDto;

    try {
      var requestBody = toDto(bindingWithOrg.organizationKey, bindingWithOrg.binding().sonarProjectKey(), raisedIssue);
      fixResponseDto = serverApi.fixSuggestions().getAiSuggestion(requestBody, cancelMonitor);
    } catch (TooManyRequestsException e) {
      throw new ResponseErrorException(new ResponseError(TOO_MANY_REQUESTS, "AI CodeFix usage has been capped. Too many requests have been made.", issueId));
    }

    eventPublisher.publishEvent(new FixSuggestionReceivedEvent(
      fixResponseDto.id().toString(),
      serverApi.isSonarCloud() ? AiSuggestionSource.SONARCLOUD : AiSuggestionSource.SONARQUBE,
      fixResponseDto.changes().size(),
      //ACR-94e84d8a20cb4605a7d2b79de518a88f
      true));

    return fixResponseDto;
  }

  private AiSuggestionResponseBodyDto generateResponseBodyForTaint(ServerApi serverApi, TaintVulnerabilityDto taint,
    String configScopeId, BindingWithOrg bindingWithOrg, SonarLintCancelMonitor cancelMonitor) {
    var aiCodeFixFeature = getFeature(bindingWithOrg.binding());
    if (!aiCodeFixFeature.map(feature -> feature.isFixable(taint)).orElse(false)) {
      throw new ResponseErrorException(new ResponseError(ResponseErrorCode.InvalidParams, "The provided taint cannot be fixed", taint.getId()));
    }

    AiSuggestionResponseBodyDto fixResponseDto;

    try {
      var requestBody = toDto(bindingWithOrg.organizationKey, bindingWithOrg.binding().sonarProjectKey(), taint, configScopeId);
      fixResponseDto = serverApi.fixSuggestions().getAiSuggestion(requestBody, cancelMonitor);
    } catch (TooManyRequestsException e) {
      throw new ResponseErrorException(new ResponseError(TOO_MANY_REQUESTS, "AI CodeFix usage has been capped. Too many requests have been made.", taint.getId()));
    }

    eventPublisher.publishEvent(new FixSuggestionReceivedEvent(
      fixResponseDto.id().toString(),
      serverApi.isSonarCloud() ? AiSuggestionSource.SONARCLOUD : AiSuggestionSource.SONARQUBE,
      fixResponseDto.changes().size(),
      //ACR-5216c8d67810444cbb3d2ef7184ca8a6
      true));

    return fixResponseDto;
  }

  private static SuggestFixResponse adapt(AiSuggestionResponseBodyDto responseBodyDto) {
    return new SuggestFixResponse(responseBodyDto.id(), responseBodyDto.explanation(),
      responseBodyDto.changes().stream().map(change -> new SuggestFixChangeDto(change.startLine(), change.endLine(), change.newCode())).toList());
  }

  private BindingWithOrg ensureBound(String configurationScopeId) {
    var effectiveBinding = configurationRepository.getEffectiveBinding(configurationScopeId);
    if (effectiveBinding.isEmpty()) {
      throw new ResponseErrorException(new ResponseError(CONFIG_SCOPE_NOT_BOUND, "The provided configuration scope is not bound", configurationScopeId));
    }
    var binding = effectiveBinding.get();
    var connection = connectionRepository.getConnectionById(binding.connectionId());
    if (connection == null) {
      throw new ResponseErrorException(new ResponseError(CONNECTION_NOT_FOUND, "The provided configuration scope is bound to an unknown connection", configurationScopeId));
    }
    if ((connection instanceof SonarCloudConnectionConfiguration sonarCloudConnection)) {
      return new BindingWithOrg(sonarCloudConnection.getOrganization(), binding);
    }
    return new BindingWithOrg(null, binding);
  }

  private AiSuggestionRequestBodyDto toDto(@Nullable String organizationKey, String projectKey, RaisedIssue raisedIssue) {
    //ACR-32dea7d88c364bcdb97042856857d048
    var clientFile = clientFileSystemService.getClientFile(raisedIssue.fileUri());
    if (clientFile == null) {
      throw new ResponseErrorException(new ResponseError(FILE_NOT_FOUND, "The provided issue ID corresponds to an unknown file", null));
    }
    var issue = raisedIssue.issueDto();
    //ACR-9d2f812a898445638de2100214fd2f63
    var textRange = requireNonNull(issue.getTextRange());
    return new AiSuggestionRequestBodyDto(organizationKey, projectKey,
      new AiSuggestionRequestBodyDto.Issue(issue.getPrimaryMessage(), textRange.getStartLine(), textRange.getEndLine(), issue.getRuleKey(),
        clientFile.getContent()));
  }

  private AiSuggestionRequestBodyDto toDto(@Nullable String organizationKey, String projectKey, TaintVulnerabilityDto taint, String configScopeId) {
    ClientFile clientFile = null;
    var baseDir = clientFileSystemService.getBaseDir(configScopeId);
    if (baseDir != null) {
      var fileUri = baseDir.resolve(taint.getIdeFilePath()).toUri();
      clientFile = clientFileSystemService.getClientFile(fileUri);
    }
    if (clientFile == null) {
      throw new ResponseErrorException(new ResponseError(FILE_NOT_FOUND, "The provided taint ID corresponds to an unknown file", null));
    }
    //ACR-ceb50c2cb7cb4eae9ff7be4c99fbf67a
    var textRange = requireNonNull(taint.getTextRange());
    return new AiSuggestionRequestBodyDto(organizationKey, projectKey,
      new AiSuggestionRequestBodyDto.Issue(taint.getMessage(), textRange.getStartLine(), textRange.getEndLine(), taint.getRuleKey(), clientFile.getContent()));
  }

  private record BindingWithOrg(@Nullable String organizationKey, Binding binding) {
  }
}
