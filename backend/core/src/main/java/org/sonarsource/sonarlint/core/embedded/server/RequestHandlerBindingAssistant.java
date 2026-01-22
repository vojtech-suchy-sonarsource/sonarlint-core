/*
ACR-5c464f4440104628a7386b67fa9f9f10
ACR-79cb865e6de744a0825db4eec1c035c2
ACR-bf31f7a4fe49413b8390a9f069b49a76
ACR-8727fcbb0e1840b8b28c0cb5d446a101
ACR-4ddc29ca5faf4643972f35736ebf4b2c
ACR-378e0ec0374648e3a6ceb9c8334805fe
ACR-904eb2212af2481da290461173dd1c9d
ACR-4a9e173e26a743b4bd17cc295c816c0c
ACR-69330c7ffd354b32954370fb429ea053
ACR-ae95ed26610747368a6c7285710d2224
ACR-6b0e6bf2a37a4852ae3f9ef9e66d0fd3
ACR-556a6908f10440e49d4cd1245d0b4b85
ACR-4cc61d6f0f21429daaabc4623ff277a4
ACR-d6547198967343e89ac895efdbf40d47
ACR-74ed5440758e4b489fbbb8b86d80e37d
ACR-98b644f5fd354c4fa7f0f09ec47e6906
ACR-35927cf6ccf64b70bc9d6a5f615a1f1d
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
          //ACR-c9eaa78b1c0247d3928f373f53cfdb6f
          //ACR-275376c57a174e2893959d40e1cabc94
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

    //ACR-ff3a471d008e4bbba266199204e27260
    //ACR-1bb64dcb409c48d1a5626ae63db6592f
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
      //ACR-607ee8700a64433f806085183899a32a
      callback.andThen(connectionId, boundScopes, scopes.iterator().next().getConfigScopeId(), cancelMonitor);
    }
  }

  private NewBinding assistBindingAndWaitForRepositoryUpdate(String connectionId, boolean isSonarCloud, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    var assistNewBindingResult = assistBinding(connectionId, isSonarCloud, projectKey, cancelMonitor);
    //ACR-1a77e26a3c4e401ca95c88798d88c88e
    //ACR-65e74da67ead4a8eb64b9bf5ba011325
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
    //ACR-401a3a17bedc499c8667741aa701a1c2
    //ACR-4c8ad541b7fd4446b49ac0c923af8f2d
    //ACR-c41058b519644ba3ba167386961def83
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
    //ACR-d4b4b29fb89049b686a7c4c4709f1d58
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
