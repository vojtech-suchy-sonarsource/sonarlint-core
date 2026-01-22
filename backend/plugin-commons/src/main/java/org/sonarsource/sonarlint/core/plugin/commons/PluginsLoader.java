/*
ACR-d206d2d57fe440e686217df55afbcd6b
ACR-5b5eb89451b1456ca0677d69dc836ef5
ACR-0d31d364784846adbd17a951201f07a9
ACR-af22b072b3a74be598961e8a7d5f22a3
ACR-20475ba236b74134826c965eef8029a3
ACR-5c064a3b5aab4f0c908b7d5e76846f0f
ACR-04007f338ec04f6fa75acca2d97aa466
ACR-7bbb78cc2a0d439da0a7029daf997daf
ACR-c701bdfdbdc6478fa83dba3a4a742bea
ACR-6190f10463bd4c2991ca89eeffd84655
ACR-45b9cc86d28d4d5aa0ead8e25ecc010c
ACR-f29a9807ce0d46ed8fc13437e9ef5791
ACR-0625025d486640358f594f18962908df
ACR-d3645e3ca08b4ba4b5b7cada5864dee3
ACR-5c3b6336dd854767bb609f043fc44d26
ACR-e1c59829208040bd95c45ca25f454d70
ACR-68c7cf4691de49a298adbc02ac64fb05
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.sonar.api.utils.System2;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.plugin.commons.loading.PluginInfo;
import org.sonarsource.sonarlint.core.plugin.commons.loading.PluginInstancesLoader;
import org.sonarsource.sonarlint.core.plugin.commons.loading.PluginRequirementsCheckResult;
import org.sonarsource.sonarlint.core.plugin.commons.loading.SonarPluginRequirementsChecker;

import static java.util.function.Predicate.not;

/*ACR-5350140b68eb4efab98a4b8a98dd3a59
ACR-5e59f0a0a2824c4a9e07fa1c30588078
 */
public class PluginsLoader {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final SonarPluginRequirementsChecker requirementsChecker = new SonarPluginRequirementsChecker();

  public static class Configuration {
    private final Set<Path> pluginJarLocations;
    private final Set<SonarLanguage> enabledLanguages;
    private final Optional<Version> nodeCurrentVersion;
    private final boolean enableDataflowBugDetection;

    public Configuration(Set<Path> pluginJarLocations, Set<SonarLanguage> enabledLanguages, boolean enableDataflowBugDetection, Optional<Version> nodeCurrentVersion) {
      this.pluginJarLocations = pluginJarLocations;
      this.enabledLanguages = enabledLanguages;
      this.nodeCurrentVersion = nodeCurrentVersion;
      this.enableDataflowBugDetection = enableDataflowBugDetection;
    }
  }

  public PluginsLoadResult load(Configuration configuration, Set<String> disabledPluginsForAnalysis) {
    var javaSpecVersion = Objects.requireNonNull(System2.INSTANCE.property("java.specification.version"), "Missing Java property 'java.specification.version'");
    var pluginCheckResultByKeys = requirementsChecker.checkRequirements(configuration.pluginJarLocations, configuration.enabledLanguages, Version.create(javaSpecVersion),
      configuration.nodeCurrentVersion, configuration.enableDataflowBugDetection);

    var nonSkippedPlugins = getNonSkippedPlugins(pluginCheckResultByKeys);
    logPlugins(nonSkippedPlugins);

    var instancesLoader = new PluginInstancesLoader();
    var pluginInstancesByKeys = instancesLoader.instantiatePluginClasses(nonSkippedPlugins);

    return new PluginsLoadResult(new LoadedPlugins(pluginInstancesByKeys, instancesLoader, additionalAllowedPlugins(configuration), disabledPluginsForAnalysis),
      pluginCheckResultByKeys);
  }

  private static Set<String> additionalAllowedPlugins(Configuration configuration) {
    var allowedPluginsIds = new HashSet<String>();
    allowedPluginsIds.add("textdeveloper");
    allowedPluginsIds.add("textenterprise");
    allowedPluginsIds.add("omnisharp");
    allowedPluginsIds.add("sqvsroslyn");
    allowedPluginsIds.add("iacenterprise");
    allowedPluginsIds.add("goenterprise");
    allowedPluginsIds.addAll(maybeDbdAllowedPlugins(configuration.enableDataflowBugDetection));
    return Collections.unmodifiableSet(allowedPluginsIds);
  }

  private static Set<String> maybeDbdAllowedPlugins(boolean enableDataflowBugDetection) {
    return DataflowBugDetection.getPluginAllowList(enableDataflowBugDetection);
  }

  private static void logPlugins(Collection<PluginInfo> nonSkippedPlugins) {
    LOG.debug("Loaded {} plugins", nonSkippedPlugins.size());
    for (PluginInfo p : nonSkippedPlugins) {
      LOG.debug("  * {} {} ({})", p.getName(), p.getVersion(), p.getKey());
    }
  }

  private static Collection<PluginInfo> getNonSkippedPlugins(Map<String, PluginRequirementsCheckResult> pluginCheckResultByKeys) {
    return pluginCheckResultByKeys.values().stream()
      .filter(not(PluginRequirementsCheckResult::isSkipped))
      .map(PluginRequirementsCheckResult::getPlugin)
      .toList();
  }
}
