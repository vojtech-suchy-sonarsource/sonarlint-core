/*
ACR-376d96cb378b48f0b3af10e08b8dd75a
ACR-177cb1c5ceda4cbb9743cda0b531286f
ACR-f495119cb7414b3995610f74b9088d55
ACR-eb03e0eec80d4299ad58e13089cf8448
ACR-38d92670ecba448487b48579ca8c7253
ACR-b5b9896953334fba88af8274a68271f6
ACR-147626028ab24d45bf3dd41afcd2b5dc
ACR-c7f7f3770b6543e681951857d27d1e3c
ACR-d6cf3b1079da4794b43cc780f146d746
ACR-53cff16762c1435c8df314d6c260974e
ACR-b1b705e6b0794579a9c03cb1e10454e2
ACR-e4cf8e2a60ac4d2b9591e702059d7f08
ACR-711e54a906e74c9f9b702d9e3d9c2fad
ACR-a49d2e01a92b48f9b78b57aad5333f83
ACR-9eeed610de084c7fbfaf76cf6be4a2fb
ACR-1f7d760ac239468f90c9c5f7fc392bf8
ACR-e606a1ca2f134bcf8f3aca3e1fdf67fb
 */
package org.sonarsource.sonarlint.core.analysis;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;
import org.sonarsource.sonarlint.core.analysis.api.ClientModuleFileSystem;
import org.sonarsource.sonarlint.core.analysis.command.UnregisterModuleCommand;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.tracing.Trace;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.plugin.DotnetSupport;
import org.sonarsource.sonarlint.core.plugin.PluginsService;
import org.sonarsource.sonarlint.core.plugin.commons.LoadedPlugins;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.sync.PluginsSynchronizedEvent;
import org.springframework.context.event.EventListener;

import static org.sonarsource.sonarlint.core.commons.tracing.Trace.startChild;

public class AnalysisSchedulerCache {
  private final Path workDir;
  private final ClientFileSystemService clientFileSystemService;
  private final ConfigurationRepository configurationRepository;
  private final PluginsService pluginsService;
  private final NodeJsService nodeJsService;
  private final Map<String, String> extraProperties = new HashMap<>();
  private final AtomicReference<AnalysisScheduler> standaloneScheduler = new AtomicReference<>();
  private final Map<String, AnalysisScheduler> connectedSchedulerByConnectionId = new ConcurrentHashMap<>();

  public AnalysisSchedulerCache(InitializeParams initializeParams, UserPaths userPaths, ConfigurationRepository configurationRepository, NodeJsService nodeJsService,
    PluginsService pluginsService, ClientFileSystemService clientFileSystemService) {
    this.configurationRepository = configurationRepository;
    this.pluginsService = pluginsService;
    this.nodeJsService = nodeJsService;
    this.workDir = userPaths.getWorkDir();
    this.clientFileSystemService = clientFileSystemService;
    var shouldSupportCsharp = initializeParams.getEnabledLanguagesInStandaloneMode().contains(Language.CS);
    var languageSpecificRequirements = initializeParams.getLanguageSpecificRequirements();
    if (shouldSupportCsharp && languageSpecificRequirements != null) {
      var omnisharpRequirements = languageSpecificRequirements.getOmnisharpRequirements();
      if (omnisharpRequirements != null) {
        extraProperties.put("sonar.cs.internal.omnisharpMonoLocation", omnisharpRequirements.getMonoDistributionPath().toString());
        extraProperties.put("sonar.cs.internal.omnisharpWinLocation", omnisharpRequirements.getDotNet472DistributionPath().toString());
        extraProperties.put("sonar.cs.internal.omnisharpNet6Location", omnisharpRequirements.getDotNet6DistributionPath().toString());
      }
    }
  }

  @CheckForNull
  public AnalysisScheduler getAnalysisSchedulerIfStarted(String configurationScopeId) {
    return configurationRepository.getEffectiveBinding(configurationScopeId)
      .map(binding -> getConnectedSchedulerIfStarted(binding.connectionId()))
      .orElseGet(this::getStandaloneSchedulerIfStarted);
  }

  public AnalysisScheduler getOrCreateAnalysisScheduler(String configurationScopeId) {
    return getOrCreateAnalysisScheduler(configurationScopeId, null);
  }

  public AnalysisScheduler getOrCreateAnalysisScheduler(String configurationScopeId, @Nullable Trace trace) {
    return configurationRepository.getEffectiveBinding(configurationScopeId)
      .map(binding -> getOrCreateConnectedScheduler(binding.connectionId(), trace))
      .orElseGet(() -> getOrCreateStandaloneScheduler(trace));
  }

  private synchronized AnalysisScheduler getOrCreateConnectedScheduler(String connectionId, @Nullable Trace trace) {
    return connectedSchedulerByConnectionId.computeIfAbsent(connectionId,
      k -> createScheduler(pluginsService.getPlugins(connectionId), pluginsService.getDotnetSupport(connectionId), trace));
  }

  @CheckForNull
  private synchronized AnalysisScheduler getConnectedSchedulerIfStarted(String connectionId) {
    return connectedSchedulerByConnectionId.get(connectionId);
  }

  private synchronized AnalysisScheduler getOrCreateStandaloneScheduler(@Nullable Trace trace) {
    var scheduler = standaloneScheduler.get();
    if (scheduler == null) {
      scheduler = createScheduler(pluginsService.getEmbeddedPlugins(), pluginsService.getDotnetSupport(null), trace);
      standaloneScheduler.set(scheduler);
    }
    return scheduler;
  }

  @CheckForNull
  private synchronized AnalysisScheduler getStandaloneSchedulerIfStarted() {
    return standaloneScheduler.get();
  }

  private AnalysisScheduler createScheduler(LoadedPlugins plugins, DotnetSupport dotnetSupport, @Nullable Trace trace) {
    return new AnalysisScheduler(createSchedulerConfiguration(dotnetSupport, trace), plugins, SonarLintLogger.get().getTargetForCopy());
  }

  private AnalysisSchedulerConfiguration createSchedulerConfiguration(DotnetSupport dotnetSupport) {
    return createSchedulerConfiguration(dotnetSupport, null);
  }

  private AnalysisSchedulerConfiguration createSchedulerConfiguration(DotnetSupport dotnetSupport, @Nullable Trace trace) {
    var activeNodeJs = startChild(trace, "getActiveNodeJs", "createSchedulerConfiguration", nodeJsService::getActiveNodeJs);
    var nodeJsPath = activeNodeJs == null ? null : activeNodeJs.getPath();
    var fullExtraProperties = new HashMap<>(extraProperties);
    enhanceDotnetExtraProperties(fullExtraProperties, dotnetSupport);

    return AnalysisSchedulerConfiguration.builder()
      .setWorkDir(workDir)
      .setClientPid(ProcessHandle.current().pid())
      .setExtraProperties(fullExtraProperties)
      .setNodeJs(nodeJsPath)
      .setFileSystemProvider(this::getFileSystem)
      .build();
  }

  private static void enhanceDotnetExtraProperties(HashMap<String, String> fullExtraProperties, DotnetSupport dotnetSupport) {
    if (dotnetSupport.getActualCsharpAnalyzerPath() != null) {
      fullExtraProperties.put("sonar.cs.internal.analyzerPath", dotnetSupport.getActualCsharpAnalyzerPath().toString());
    }
    if (dotnetSupport.isSupportsCsharp()) {
      fullExtraProperties.put("sonar.cs.internal.shouldUseCsharpEnterprise", String.valueOf(dotnetSupport.isShouldUseCsharpEnterprise()));
    }
    if (dotnetSupport.isSupportsVbNet()) {
      fullExtraProperties.put("sonar.cs.internal.shouldUseVbEnterprise", String.valueOf(dotnetSupport.isShouldUseVbNetEnterprise()));
    }
  }

  private ClientModuleFileSystem getFileSystem(String configurationScopeId) {
    return new BackendModuleFileSystem(clientFileSystemService, configurationScopeId);
  }

  @EventListener
  public void onConnectionRemoved(ConnectionConfigurationRemovedEvent event) {
    stop(event.getRemovedConnectionId());
  }

  @EventListener
  public void onPluginsSynchronized(PluginsSynchronizedEvent event) {
    var connectionId = event.connectionId();
    var scheduler = connectedSchedulerByConnectionId.get(connectionId);
    if (scheduler != null) {
      scheduler.reset(createSchedulerConfiguration(pluginsService.getDotnetSupport(connectionId)), () -> pluginsService.reloadPluginsFromStorage(connectionId));
    }
  }

  @EventListener
  public void onClientNodeJsPathChanged(ClientNodeJsPathChanged event) {
    resetStartedSchedulers();
  }

  @EventListener
  public void onBindingConfigurationChanged(BindingConfigChangedEvent event) {
    var schedulerBeforeBindingChange = event.previousConfig().isBound() ? getConnectedSchedulerIfStarted(Objects.requireNonNull(event.previousConfig().connectionId()))
      : getStandaloneSchedulerIfStarted();
    var schedulerAfterBindingChange = getAnalysisSchedulerIfStarted(event.configScopeId());
    if (schedulerBeforeBindingChange != null && schedulerAfterBindingChange != schedulerBeforeBindingChange) {
      schedulerBeforeBindingChange.post(new UnregisterModuleCommand(event.configScopeId()));
      configurationRepository.getChildrenWithInheritedBinding(event.configScopeId())
        .forEach(childId -> schedulerBeforeBindingChange.post(new UnregisterModuleCommand(childId)));
    }
  }

  @PreDestroy
  public void shutdown() {
    try {
      stopAll();
    } catch (Exception e) {
      SonarLintLogger.get().error("Error shutting down analysis scheduler cache", e);
    }
  }

  private synchronized void resetStartedSchedulers() {
    var standaloneAnalysisScheduler = this.standaloneScheduler.get();
    if (standaloneAnalysisScheduler != null) {
      standaloneAnalysisScheduler.reset(createSchedulerConfiguration(pluginsService.getDotnetSupport(null)), pluginsService::getEmbeddedPlugins);
    }
    connectedSchedulerByConnectionId.forEach(
      (connectionId, scheduler) -> scheduler.reset(createSchedulerConfiguration(pluginsService.getDotnetSupport(connectionId)), () -> pluginsService.getPlugins(connectionId)));
  }

  private synchronized void stopAll() {
    var standaloneAnalysisScheduler = this.standaloneScheduler.get();
    if (standaloneAnalysisScheduler != null) {
      standaloneAnalysisScheduler.stop();
      this.standaloneScheduler.set(null);
    }
    connectedSchedulerByConnectionId.forEach((connectionId, scheduler) -> scheduler.stop());
    connectedSchedulerByConnectionId.clear();
  }

  private synchronized void stop(String connectionId) {
    var scheduler = connectedSchedulerByConnectionId.remove(connectionId);
    if (scheduler != null) {
      scheduler.stop();
    }
  }

  public void unregisterModule(String scopeId, @Nullable String connectionId) {
    var analysisScheduler = connectionId == null ? getStandaloneSchedulerIfStarted() : getConnectedSchedulerIfStarted(connectionId);
    if (analysisScheduler != null) {
      analysisScheduler.post(new UnregisterModuleCommand(scopeId));
    }
  }
}
