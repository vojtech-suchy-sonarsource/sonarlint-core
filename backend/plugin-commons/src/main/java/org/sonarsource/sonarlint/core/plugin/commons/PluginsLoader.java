/*
ACR-f618ecc38c7c4bedb9cd4d97c514e0d3
ACR-ccbc6b4f236b4d45a4924ea08ee83535
ACR-1e8d66c365f1451fbe0978c9db073f19
ACR-bd3ab1a87fe7468ca68fb189c5e70179
ACR-67b2591110754042a768ce72561ef7f7
ACR-0c1b1708f38f4d8ca0b84b944d3e6d7f
ACR-4c4a4dac300b4bde9d763e0037d61d15
ACR-7c2c9d2e3d6943b2ac92498f0d289a3b
ACR-1810778a61fa450c990f9313d67e2ec7
ACR-c0873104edf34e71b8d5acb187f80739
ACR-92918bdec4f64405bcc7e3725b6e789e
ACR-1c16a99507934e3aa070a73cb4236f6b
ACR-98eb481fcfa8474693a84fb65ae504e5
ACR-90df92e6157d450ebac24c3f1a3cdf56
ACR-621a87e46f4b4e4a8b441784368fd742
ACR-3ab079f7e72a4511b767f6f630c1c93b
ACR-b34a5bc3794b4d74b5f2826d6fe98954
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

/*ACR-637f75fa1d574c3d8f4fac793d89c975
ACR-674a97c455d8433ea890dfc22f340854
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
