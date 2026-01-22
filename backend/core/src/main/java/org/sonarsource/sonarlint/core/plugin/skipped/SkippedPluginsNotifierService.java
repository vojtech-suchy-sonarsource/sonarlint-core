/*
ACR-21cab998105044f9a58b25532d0d2406
ACR-1f5317be61f54894b909053f39f94d0b
ACR-599719da4b6542979df9db55f49d12b6
ACR-58cf11a0fa2b41b0a40b3a25d0107d95
ACR-52143df2b6a947eaa2c7021785c6a268
ACR-169e77b867104298a7f9a2bfbb12eebb
ACR-160fe21f921e42a0a208abd1ebcb7a6c
ACR-1412202c0ac742ecae3c51c274537491
ACR-c74ab6604a214bbcad16e944b8b67de5
ACR-1f2870061e344b7ab88eb41d44a87b34
ACR-72f5c86fea364b8b8fb3f7da23f49997
ACR-b84f4ff3804747659631c1d9b40978bf
ACR-b136691d8535406a8740ff982acb7815
ACR-0f0caffe46eb4e43ae13376f39529f4a
ACR-a8bd4393f8fb4a7db35ded69a50173be
ACR-8b9053b8c45948a4af786f54bd707cf2
ACR-01ef92068fc24a889e5eed4f8fb48d34
 */
package org.sonarsource.sonarlint.core.plugin.skipped;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.analysis.AnalysisFinishedEvent;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.plugin.DidSkipLoadingPluginParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.springframework.context.event.EventListener;

public class SkippedPluginsNotifierService {
  private final SkippedPluginsRepository skippedPluginsRepository;
  private final ConfigurationRepository configurationRepository;
  private final SonarLintRpcClient client;
  private final Set<String> alreadyNotifiedPluginKeys = new HashSet<>();

  public SkippedPluginsNotifierService(SkippedPluginsRepository skippedPluginsRepository, ConfigurationRepository configurationRepository, SonarLintRpcClient client) {
    this.skippedPluginsRepository = skippedPluginsRepository;
    this.configurationRepository = configurationRepository;
    this.client = client;
  }

  @EventListener
  public void onAnalysisFinished(AnalysisFinishedEvent event) {
    var detectedLanguages = event.getDetectedLanguages();
    var configurationScopeId = event.getConfigurationScopeId();
    var skippedPlugins = getSkippedPluginsToNotify(configurationScopeId);
    if (skippedPlugins.isEmpty()) {
      return;
    }
    notifyClientOfSkippedPlugins(configurationScopeId, detectedLanguages, skippedPlugins);
  }

  private void notifyClientOfSkippedPlugins(String configurationScopeId, Set<SonarLanguage> detectedLanguages, List<SkippedPlugin> skippedPlugins) {
    detectedLanguages.stream().filter(Objects::nonNull)
      .forEach(sonarLanguage -> skippedPlugins.stream().filter(p -> p.getKey().equals(sonarLanguage.getPluginKey()))
        .findFirst()
        .ifPresent(skippedPlugin -> {
          var skipReason = skippedPlugin.getReason();
          if (skipReason instanceof SkipReason.UnsatisfiedRuntimeRequirement runtimeRequirement) {
            var rpcLanguage = Language.valueOf(sonarLanguage.name());
            var rpcSkipReason = runtimeRequirement.getRuntime() == SkipReason.UnsatisfiedRuntimeRequirement.RuntimeRequirement.JRE
              ? DidSkipLoadingPluginParams.SkipReason.UNSATISFIED_JRE
              : DidSkipLoadingPluginParams.SkipReason.UNSATISFIED_NODE_JS;
            alreadyNotifiedPluginKeys.add(skippedPlugin.getKey());
            client.didSkipLoadingPlugin(
              new DidSkipLoadingPluginParams(configurationScopeId, rpcLanguage, rpcSkipReason, runtimeRequirement.getMinVersion(), runtimeRequirement.getCurrentVersion()));
          }
        }));
  }

  private List<SkippedPlugin> getSkippedPluginsToNotify(String configurationScopeId) {
    var skippedPlugins = getSkippedPlugins(configurationScopeId);
    if (skippedPlugins != null) {
      return skippedPlugins.stream().filter(skippedPlugin -> !alreadyNotifiedPluginKeys.contains(skippedPlugin.getKey())).toList();
    }
    return List.of();
  }

  @CheckForNull
  private List<SkippedPlugin> getSkippedPlugins(String configurationScopeId) {
    return configurationRepository.getEffectiveBinding(configurationScopeId)
      .map(binding -> skippedPluginsRepository.getSkippedPlugins(binding.connectionId()))
      .orElseGet(skippedPluginsRepository::getSkippedEmbeddedPlugins);
  }
}
