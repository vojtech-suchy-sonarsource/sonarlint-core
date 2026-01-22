/*
ACR-e6f08ca2f8bc483295ca11f99b6942ae
ACR-b73da9ece7f54c5d9bd7dbfb36dc70f9
ACR-0507317d60c74ef1b71aac919ad98c29
ACR-c3bdaf8d79004ecb8aa1f41eaf4891bf
ACR-f946289423c344478712fd60b3932e47
ACR-73b487d7d15d42e083a635365a9b30c0
ACR-ccb5bb928c28497b8b920d3cf11403ce
ACR-39109bf3fdce4e39921a844c1a110eaf
ACR-421e5c54c7d749d28769fdeb83dca118
ACR-ddcc1bce15ee4fada77b87ba9ba51324
ACR-5fbaa579f3d644b5a5e30e69e7e97788
ACR-cc61858190624532b1b39314531a2475
ACR-fa67f17f149749ed95c2ff375b3de5dd
ACR-49a692ca23d44cd8bf4811423a958066
ACR-a4cae1ef50854f519b0bb851f4a5b654
ACR-99d5cda02fcd4961ba9a38b49e1831a8
ACR-72b66f571e5d47f6a8a055ee525025a8
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
