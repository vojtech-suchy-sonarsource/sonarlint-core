/*
ACR-44b3a67f210c41818e92eab9747439b4
ACR-51e7f0aef5dd4adb847b44db4d21f4d3
ACR-b7cff8c45b40434f8106294e350aa8a0
ACR-9c6dd4e2f78a4d6c987f139c925f7d68
ACR-3119bcf6876f408688985d1820927dac
ACR-820ab9de4a8c43af8f0509404a8e2a56
ACR-36b810e8db804a6da2f51446ad4db03f
ACR-1d11ce20c71f4f66af33509b2e6ba102
ACR-52abd68bf9f44487912f4f91920b3677
ACR-fc17f3f9a15748b8929520ff7114e2f1
ACR-cc92277ea2514f39806019a6927c4c47
ACR-7fafbb6aa76f49869709caf836721873
ACR-42ff45908f7542a0b5bbe9eccffc5bb2
ACR-9ab17430ea0046579364ac44570457a0
ACR-1b63552fc27149f9aa2988333a4ad199
ACR-518ed5f5b7584980a31defdf0efb90e9
ACR-5916720e082d4f6f82983ed63a126719
 */
package org.sonarsource.sonarlint.core.embedded.server;

import com.google.common.util.concurrent.MoreExecutors;
import jakarta.annotation.PreDestroy;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.BindingCandidatesFinder;
import org.sonarsource.sonarlint.core.BindingSuggestionProvider;
import org.sonarsource.sonarlint.core.SonarCloudActiveEnvironment;
import org.sonarsource.sonarlint.core.SonarCloudRegion;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.NoBindingSuggestionFoundParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageParams;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

public class RequestHandlerBindingAssistant {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final BindingSuggestionProvider bindingSuggestionProvider;
  private final BindingCandidatesFinder bindingCandidatesFinder;
  private final SonarLintRpcClient client;
  private final ConnectionConfigurationRepository connectionConfigurationRepository;
  private final ConfigurationRepository configurationRepository;
  private final ExecutorServiceShutdownWatchable<?> executorService;
  private final SonarCloudActiveEnvironment sonarCloudActiveEnvironment;
  private final ConnectionConfigurationRepository repository;

  public RequestHandlerBindingAssistant(BindingSuggestionProvider bindingSuggestionProvider, BindingCandidatesFinder bindingCandidatesFinder,
    SonarLintRpcClient client, ConnectionConfigurationRepository connectionConfigurationRepository, ConfigurationRepository configurationRepository,
    SonarCloudActiveEnvironment sonarCloudActiveEnvironment, ConnectionConfigurationRepository repository) {
    this.bindingSuggestionProvider = bindingSuggestionProvider;
    this.bindingCandidatesFinder = bindingCandidatesFinder;
    this.client = client;
    this.connectionConfigurationRepository = connectionConfigurationRepository;
    this.configurationRepository = configurationRepository;
    this.executorService = new ExecutorServiceShutdownWatchable<>(FailSafeExecutors.newSingleThreadExecutor("Show Issue or Hotspot Request Handler"));
    this.sonarCloudActiveEnvironment = sonarCloudActiveEnvironment;
    this.repository = repository;
  }

  public interface Callback {
    void andThen(String connectionId, Collection<String> boundScopes, @Nullable String configurationScopeId, SonarLintCancelMonitor cancelMonitor);
  }

  public void assistConnectionAndBindingIfNeededAsync(AssistCreatingConnectionParams connectionParams, String projectKey, String origin, Callback callback) {
    var cancelMonitor = new SonarLintCancelMonitor();
    cancelMonitor.watchForShutdown(executorService);
    executorService.execute(() -> assistConnectionAndBindingIfNeeded(connectionParams, projectKey, origin, callback, cancelMonitor));
  }

  private void assistConnectionAndBindingIfNeeded(AssistCreatingConnectionParams connectionParams, String projectKey, String origin,
    Callback callback, SonarLintCancelMonitor cancelMonitor) {
    var serverUrl = getServerUrl(connectionParams);
    LOG.debug("Assist connection and binding if needed for project {} and server {}", projectKey, serverUrl);
    try {
      var isSonarCloud = connectionParams.getConnectionParams().isRight();
      var connectionsMatchingOrigin = isSonarCloud ? connectionConfigurationRepository.findByOrganization(connectionParams.getConnectionParams().getRight().getOrganizationKey())
        : connectionConfigurationRepository.findByUrl(serverUrl);
      if (connectionsMatchingOrigin.isEmpty()) {
        startFullBindingProcess();
        try {
          var assistNewConnectionResult = assistCreatingConnectionAndWaitForRepositoryUpdate(connectionParams, cancelMonitor);
          var assistNewBindingResult = assistBindingAndWaitForRepositoryUpdate(assistNewConnectionResult.getNewConnectionId(), isSonarCloud,
            projectKey, cancelMonitor);
          var boundScopes = new HashSet<String>();
          if (assistNewBindingResult.getConfigurationScopeId() != null) {
            boundScopes.add(assistNewBindingResult.getConfigurationScopeId());
          }
          callback.andThen(assistNewConnectionResult.getNewConnectionId(), boundScopes, assistNewBindingResult.getConfigurationScopeId(), cancelMonitor);
        } finally {
          endFullBindingProcess();
        }
      } else {
        var isOriginTrusted = repository.hasConnectionWithOrigin(origin);
        if (isOriginTrusted) {
          //ACR-1f91e32a8d11486aa7ab153e3d972cb3
          //ACR-e72271c757b84593b1881ccce7c270f1
          assistBindingIfNeeded(connectionsMatchingOrigin.get(0).getConnectionId(), isSonarCloud, projectKey, callback, cancelMonitor);
        } else {
          LOG.warn("The origin '" + origin + "' is not trusted, this could be a malicious request");
          client.showMessage(new ShowMessageParams(MessageType.ERROR, "SonarQube for IDE received a non-trusted request and could not proceed with it. " +
            "See logs for more details."));
        }
      }
    } catch (Exception e) {
      LOG.error("Unable to show issue", e);
    }
  }

  private String getServerUrl(AssistCreatingConnectionParams connectionParams) {
    return connectionParams.getConnectionParams().isLeft() ? connectionParams.getConnectionParams().getLeft().getServerUrl()
      : sonarCloudActiveEnvironment.getUri(SonarCloudRegion.valueOf(connectionParams.getConnectionParams().getRight().getRegion().name())).toString();
  }

  private AssistCreatingConnectionResponse assistCreatingConnectionAndWaitForRepositoryUpdate(
    AssistCreatingConnectionParams connectionParams, SonarLintCancelMonitor cancelMonitor) {
    var assistNewConnectionResult = assistCreatingConnection(connectionParams, cancelMonitor);

    //ACR-ac8edf5f2cb84a52a0e3e2b8ddcdca7a
    //ACR-7bf126726e914f32b47433d7fc7e136a
    LOG.debug("Waiting for connection creation notification...");
    for (var i = 50; i >= 0; i--) {
      if (connectionConfigurationRepository.getConnectionsById().containsKey(assistNewConnectionResult.getNewConnectionId())) {
        break;
      }
      sleep();
    }
    if (!connectionConfigurationRepository.getConnectionsById().containsKey(assistNewConnectionResult.getNewConnectionId())) {
      LOG.warn("Did not receive connection creation notification on a timely manner");
      throw new CancellationException();
    }

    return assistNewConnectionResult;
  }

  private void assistBindingIfNeeded(String connectionId, boolean isSonarCloud, String projectKey, Callback callback, SonarLintCancelMonitor cancelMonitor) {
    var scopes = configurationRepository.getBoundScopesToConnectionAndSonarProject(connectionId, projectKey);
    if (scopes.isEmpty()) {
      var assistNewBindingResult = assistBindingAndWaitForRepositoryUpdate(connectionId, isSonarCloud, projectKey, cancelMonitor);
      var boundScopes = new HashSet<String>();
      if (assistNewBindingResult.getConfigurationScopeId() != null) {
        boundScopes.add(assistNewBindingResult.getConfigurationScopeId());
      }
      callback.andThen(connectionId, boundScopes, assistNewBindingResult.getConfigurationScopeId(), cancelMonitor);
    } else {
      var boundScopes = scopes.stream().map(BoundScope::getConfigScopeId).filter(Objects::nonNull).collect(Collectors.toSet());
      //ACR-70143272f0e343e881a265245d7c97ed
      callback.andThen(connectionId, boundScopes, scopes.iterator().next().getConfigScopeId(), cancelMonitor);
    }
  }

  private NewBinding assistBindingAndWaitForRepositoryUpdate(String connectionId, boolean isSonarCloud, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    var assistNewBindingResult = assistBinding(connectionId, isSonarCloud, projectKey, cancelMonitor);
    //ACR-fab27c9daec547fcba13f909c2efa9ca
    //ACR-ac9d698c34cd4e49b836a99ca367cbd6
    var configurationScopeId = assistNewBindingResult.getConfigurationScopeId();
    if (configurationScopeId != null) {
      LOG.debug("Waiting for binding creation notification...");
      for (var i = 50; i >= 0; i--) {
        if (configurationRepository.getEffectiveBinding(configurationScopeId).isPresent()) {
          break;
        }
        sleep();
      }
      if (configurationRepository.getEffectiveBinding(configurationScopeId).isEmpty()) {
        LOG.warn("Did not receive binding creation notification on a timely manner");
        throw new CancellationException();
      }
    }

    return assistNewBindingResult;
  }

  private static void sleep() {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new CancellationException("Interrupted!");
    }
  }

  void startFullBindingProcess() {
    //ACR-92f5a062c8ce4a9da0dcdb20c75fe0cb
    //ACR-8bc7e824d4834452b93ae6697af41671
    //ACR-99ac64f9caad4f3da28f70f5fd5d7ee8
    bindingSuggestionProvider.disable();
  }

  void endFullBindingProcess() {
    bindingSuggestionProvider.enable();
  }

  AssistCreatingConnectionResponse assistCreatingConnection(AssistCreatingConnectionParams connectionParams, SonarLintCancelMonitor cancelMonitor) {
    var future = client.assistCreatingConnection(connectionParams);
    cancelMonitor.onCancel(() -> future.cancel(true));
    return future.join();
  }

  NewBinding assistBinding(String connectionId, boolean isSonarCloud, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    var configScopeCandidates = bindingCandidatesFinder.findConfigScopesToBind(connectionId, projectKey, cancelMonitor);
    //ACR-ff0944d2bcd14ff18c8e678468dd4c9f
    if (configScopeCandidates.size() != 1) {
      client.noBindingSuggestionFound(new NoBindingSuggestionFoundParams(escapeHtml4(projectKey), isSonarCloud));
      return new NewBinding(connectionId, null);
    }
    var bindableConfig = configScopeCandidates.iterator().next();
    var future = client.assistBinding(new AssistBindingParams(connectionId, projectKey, bindableConfig.getConfigurationScope().id(),
      bindableConfig.getOrigin()));
    cancelMonitor.onCancel(() -> future.cancel(true));
    var response = future.join();
    return new NewBinding(connectionId, response.getConfigurationScopeId());
  }

  static class NewBinding {
    private final String connectionId;
    private final String configurationScopeId;

    private NewBinding(String connectionId, @Nullable String configurationScopeId) {
      this.connectionId = connectionId;
      this.configurationScopeId = configurationScopeId;
    }

    public String getConnectionId() {
      return connectionId;
    }

    @CheckForNull
    public String getConfigurationScopeId() {
      return configurationScopeId;
    }
  }

  @PreDestroy
  public void shutdown() {
    if (!MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.SECONDS)) {
      LOG.warn("Unable to stop show issue request handler executor service in a timely manner");
    }
  }
}
