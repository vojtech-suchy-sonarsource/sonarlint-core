/*
ACR-99ca1c0e13a342bb876597e5a22486f4
ACR-1ede23572c1c4f59a0ff93a8b92d2774
ACR-60fb582d679044a6a680f68d44ff83cd
ACR-e00d4f355aaf46e2be5c44c3134a5376
ACR-ea4e6b986cdb4245b0f082a17790fc16
ACR-7015d6f4e62f48f1ae314ff054afd8d7
ACR-b8f7522e583542da9bf27265618bbbfc
ACR-496e8c594b9149a49f79312c9200609a
ACR-d3e262b357f74821a18948276e465a42
ACR-4dac0efd3ae346698ada5837d7f7088f
ACR-456bf43114d34d108d21b8d37e4645cf
ACR-cc8fcd7e931b42ed9a983297d58d3ab4
ACR-a7c3234dee87470c93c258143e32c3f5
ACR-07aa4838621a424e8cd699ed1a63c0d3
ACR-b024e85cef71407182f68e332b374070
ACR-352c082fcb9043978f9a9396a2e78981
ACR-81cb25d44a824469b846a56a82c81b87
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
