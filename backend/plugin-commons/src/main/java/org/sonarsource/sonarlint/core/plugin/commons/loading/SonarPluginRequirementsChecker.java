/*
ACR-0d5c6341f0254cac9fc9098cdaf29a60
ACR-bc096f75bce542438a85b2c8b0cccda1
ACR-d251720634c24bfb981b976952e23c73
ACR-540e2744b42b4b2a98f7dbfb65b3ef64
ACR-1d833dd1cec7499895e6fd436094f38a
ACR-b99804f3911540a08cce8ce232f8cc7e
ACR-04fc9f794f7649488261b345ecfa31a9
ACR-efd7b22de0414d8ca6f7fa8aea2d60e1
ACR-108e305444344707b7b92da4fd8258db
ACR-cc521a6ee1ff4d5f94614a9c82755f80
ACR-544e3dff033a4029aa18c92d523ee952
ACR-b264b2d61e9441198df710246f3228ff
ACR-a336a86958d14ea6a03cdd8534186d48
ACR-de83f975a13e4abdba57696471bb35b3
ACR-cfdd9b52a6114a8788ae09d0360751b7
ACR-6e5e949619c6434d868d2326d074c666
ACR-8de6b233c2ed4502a83a58e091b9f1ba
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.plugin.commons.ApiVersions;
import org.sonarsource.sonarlint.core.plugin.commons.DataflowBugDetection;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason.UnsatisfiedRuntimeRequirement.RuntimeRequirement;
import org.sonarsource.sonarlint.core.plugin.commons.loading.SonarPluginManifest.RequiredPlugin;

public class SonarPluginRequirementsChecker {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String OLD_SONARTS_PLUGIN_KEY = "typescript";

  private final Version implementedPluginApiVersion;

  public SonarPluginRequirementsChecker() {
    this(ApiVersions.loadSonarPluginApiVersion());
  }

  SonarPluginRequirementsChecker(org.sonar.api.utils.Version pluginApiVersion) {
    this.implementedPluginApiVersion = Version.create(pluginApiVersion.toString());
  }

  /*ACR-fef0e0becdec4c91b02ad91862e5fe29
ACR-8238f50bcb88483293b75e4716e1a6a4
   */
  public Map<String, PluginRequirementsCheckResult> checkRequirements(Set<Path> pluginJarLocations, Set<SonarLanguage> enabledLanguages, Version jreCurrentVersion,
    Optional<Version> nodeCurrentVersion, boolean enableDataflowBugDetection) {
    Map<String, PluginRequirementsCheckResult> resultsByKey = new HashMap<>();

    for (Path jarLocation : pluginJarLocations) {
      PluginInfo plugin;

      try {
        plugin = PluginInfo.create(jarLocation);
      } catch (Exception e) {
        LOG.error("Unable to load plugin " + jarLocation, e);
        continue;
      }
      if (resultsByKey.containsKey(plugin.getKey())) {
        throw new IllegalStateException(
          "Duplicate plugin key '" + plugin.getKey() + "' from '" + plugin.getJarFile() + "' and '" + resultsByKey.get(plugin.getKey()).getPlugin().getJarFile() + "'");
      }
      resultsByKey.put(plugin.getKey(), checkIfSkippedAndPopulateReason(plugin, enabledLanguages, jreCurrentVersion, nodeCurrentVersion));
    }
    //ACR-be8d8acc70e64b349f0c04c20fd87ce5
    for (PluginRequirementsCheckResult result : resultsByKey.values()) {
      if (!result.isSkipped()) {
        resultsByKey.put(result.getPlugin().getKey(), checkUnsatisfiedPluginDependency(result, resultsByKey, enableDataflowBugDetection));
      }
    }
    return resultsByKey;
  }

  private PluginRequirementsCheckResult checkIfSkippedAndPopulateReason(PluginInfo plugin, Set<SonarLanguage> enabledLanguages, Version jreCurrentVersion,
    Optional<Version> nodeCurrentVersion) {
    var pluginKey = plugin.getKey();
    var languages = SonarLanguage.getLanguagesByPluginKey(pluginKey);
    if (!languages.isEmpty() && enabledLanguages.stream().noneMatch(languages::contains)) {
      if (languages.size() > 1) {
        LOG.debug("Plugin '{}' is excluded because none of languages '{}' are enabled. Skip loading it.", plugin.getName(),
          languages.stream().map(SonarLanguage::toString).collect(Collectors.joining(",")));
      } else {
        LOG.debug("Plugin '{}' is excluded because language '{}' is not enabled. Skip loading it.", plugin.getName(),
          languages.iterator().next());
      }
      return new PluginRequirementsCheckResult(plugin, new SkipReason.LanguagesNotEnabled(languages));
    }

    if (!isCompatibleWith(plugin, implementedPluginApiVersion)) {
      LOG.debug("Plugin '{}' requires plugin API {} while SonarLint supports only up to {}. Skip loading it.", plugin.getName(),
        plugin.getMinimalSqVersion(), implementedPluginApiVersion.removeQualifier().toString());
      return new PluginRequirementsCheckResult(plugin, SkipReason.IncompatiblePluginApi.INSTANCE);
    }
    var jreMinVersion = plugin.getJreMinVersion();
    if (jreMinVersion != null && !jreCurrentVersion.satisfiesMinRequirement(jreMinVersion)) {
      LOG.debug("Plugin '{}' requires JRE {} while current is {}. Skip loading it.", plugin.getName(), jreMinVersion, jreCurrentVersion);
      return new PluginRequirementsCheckResult(plugin,
        new SkipReason.UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, jreCurrentVersion.toString(), jreMinVersion.toString()));
    }
    var nodeMinVersion = plugin.getNodeJsMinVersion();
    if (nodeMinVersion != null) {
      if (nodeCurrentVersion.isEmpty()) {
        LOG.debug("Plugin '{}' requires Node.js {}. Skip loading it.", plugin.getName(), nodeMinVersion);
        return new PluginRequirementsCheckResult(plugin, new SkipReason.UnsatisfiedRuntimeRequirement(RuntimeRequirement.NODEJS, null, nodeMinVersion.toString()));
      } else if (!nodeCurrentVersion.get().satisfiesMinRequirement(nodeMinVersion)) {
        LOG.debug("Plugin '{}' requires Node.js {} while current is {}. Skip loading it.", plugin.getName(), nodeMinVersion, nodeCurrentVersion.get());
        return new PluginRequirementsCheckResult(plugin,
          new SkipReason.UnsatisfiedRuntimeRequirement(RuntimeRequirement.NODEJS, nodeCurrentVersion.get().toString(), nodeMinVersion.toString()));
      }
    }

    return new PluginRequirementsCheckResult(plugin, null);
  }

  /*ACR-4d4b187233284ead8d0bb579f7821e7a
ACR-1f1d20ad68734c249a6dc3dad771e625
ACR-0ea39d8cfab04e159036a32f65c2a99d
ACR-6fb1f3d16d8d42da9426b1776be54041
   */
  static boolean isCompatibleWith(PluginInfo plugin, Version implementedApiVersion) {
    var sonarMinVersion = plugin.getMinimalSqVersion();
    if (sonarMinVersion == null) {
      //ACR-b7fd14311c7648568a9012ad41f99d86
      return true;
    }

    //ACR-c228c4bf925e4927aadf5483ff82047f
    var requestedApi = Version.create(sonarMinVersion.getMajor() + "." + sonarMinVersion.getMinor());
    return implementedApiVersion.satisfiesMinRequirement(requestedApi);
  }

  private static PluginRequirementsCheckResult checkUnsatisfiedPluginDependency(PluginRequirementsCheckResult currentResult,
    Map<String, PluginRequirementsCheckResult> currentResultsByKey, boolean enableDataflowBugDetection) {
    var plugin = currentResult.getPlugin();
    for (RequiredPlugin required : plugin.getRequiredPlugins()) {
      if ("license".equals(required.getKey()) || (SonarLanguage.JS.getPluginKey().equals(plugin.getKey()) && OLD_SONARTS_PLUGIN_KEY.equals(required.getKey()))) {
        //ACR-338bbd7365e34aa481247a0150acd435
        //ACR-98a7d6fb88854a6398651fa29642df44
        //ACR-4d61be9ec73c418c8615f67b206161c2
        //ACR-3eb9bd1b8608412fbc93b026f689c583
        continue;
      }
      var depInfo = currentResultsByKey.get(required.getKey());
      //ACR-ab3bb5a0596b42b7b942d5e54db27996
      //ACR-24f09c668c3642d1bcfc996f87936ebb
      //ACR-4f0d9e748ef1467d9c7dda8b8de60702
      //ACR-3d3fb2ebdc134d39923754cf9de089be
      //ACR-d862e3320b1e4c10862507a1d9696d65
      if (depInfo == null || depInfo.isSkipped()) {
        return processUnsatisfiedDependency(currentResult.getPlugin(), required.getKey());
      }
    }
    var basePluginKey = plugin.getBasePlugin();
    if (basePluginKey != null && checkForPluginSkipped(currentResultsByKey.get(basePluginKey))) {
      return processUnsatisfiedDependency(currentResult.getPlugin(), basePluginKey);
    }
    if (DataflowBugDetection.PLUGIN_ALLOW_LIST.contains(plugin.getKey())) {
      //ACR-6765ad86f3d54416a0d30ff37f9ad583
      //ACR-00b10ad3ab5d4192a328add169182ca2
      if (!enableDataflowBugDetection) {
        LOG.debug("DBD feature disabled. Skip loading plugin '{}'.", plugin.getName());
        return new PluginRequirementsCheckResult(plugin, SkipReason.UnsupportedFeature.INSTANCE);
      }
      var pythonPluginResult = currentResultsByKey.get(SonarLanguage.PYTHON.getPluginKey());
      if (checkForPluginSkipped(pythonPluginResult)) {
        return processUnsatisfiedDependency(currentResult.getPlugin(), SonarLanguage.PYTHON.getPluginKey());
      }
    }
    return currentResult;
  }

  private static boolean checkForPluginSkipped(@Nullable PluginRequirementsCheckResult plugin) {
    return plugin == null || plugin.isSkipped();
  }

  private static PluginRequirementsCheckResult processUnsatisfiedDependency(PluginInfo plugin, String pluginKeyDependency) {
    LOG.debug("Plugin '{}' dependency on '{}' is unsatisfied. Skip loading it.", plugin.getName(), pluginKeyDependency);
    return new PluginRequirementsCheckResult(plugin, new SkipReason.UnsatisfiedDependency(pluginKeyDependency));
  }

}
