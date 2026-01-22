/*
ACR-45d305d6d0144688bac2932b576417e2
ACR-70e73038ff4446f7ad0793d0e403d3d5
ACR-0fe6c6ae78584db9b2fe26e9122aa300
ACR-a87e79cf9996457d9d78bf0d1b8a273c
ACR-41e1c3135075451eab5512946c7e57a6
ACR-9096e8a46b5c4c28a0dcdc4fcf2b7440
ACR-6f8bcefce169447d9147fe40b4396fe7
ACR-6709cdb5674a43b8975bf7013d581fcd
ACR-bda9a08f323f48679f65e0fb4c34007b
ACR-1d39fe545183442783a18a93f5a82fcc
ACR-9862b77bd5aa43b883b87a77dd358b7d
ACR-083a91fbf26e4ce09b21991f81058c5b
ACR-803fbda5abb6468bb9fcc3250422fa46
ACR-1d09e0d09a2342a18b2f3bd5d676be9f
ACR-2c24c78886ae42cf91d8ac3af0aba17b
ACR-618efa9f87144f76bfa97958172babf5
ACR-5957efe2c5094da788b5c41b4f6a8dcd
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
      //ACR-dce804250dd34f00944a22ef719338eb
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
      //ACR-6cd69e309f954ccabd33af796c50fde2
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
    //ACR-7b7a5c0ecdc94620ab585027bc066ffa
    var clientFile = clientFileSystemService.getClientFile(raisedIssue.fileUri());
    if (clientFile == null) {
      throw new ResponseErrorException(new ResponseError(FILE_NOT_FOUND, "The provided issue ID corresponds to an unknown file", null));
    }
    var issue = raisedIssue.issueDto();
    //ACR-a8c2097e4653477388712ff9c91f8323
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
    //ACR-bd3b1cd408844f55a4ff33806bfb0485
    var textRange = requireNonNull(taint.getTextRange());
    return new AiSuggestionRequestBodyDto(organizationKey, projectKey,
      new AiSuggestionRequestBodyDto.Issue(taint.getMessage(), textRange.getStartLine(), textRange.getEndLine(), taint.getRuleKey(), clientFile.getContent()));
  }

  private record BindingWithOrg(@Nullable String organizationKey, Binding binding) {
  }
}
