/*
ACR-c9898490afcb4fa79d9e8e4623b5f266
ACR-a77bf16920914adba6425ff0dc3cf534
ACR-8579b7bcc987433da3d53dd08a3822d3
ACR-caa50193b84b4a909b68ae214a066442
ACR-9a044a89ea83404bae24fc2724566ecd
ACR-861273c36f8b40a0ab3784a39b8c4931
ACR-a7e420ede28c4191bb55de7c35307933
ACR-575798740fb24b1f9060c9ba76d689d7
ACR-3d62b201a47943c69685630666f3a271
ACR-23bc553c07b844f4be2bad9cd14279bb
ACR-5b746fd7181144089bd798362b3ac8dc
ACR-53b453180ba444eaaced8e815e6da621
ACR-d928f8d2f44446168f8e25dd817763ca
ACR-46c16e2d13004f34a5cdbd21ed6e43d7
ACR-adc23c3fe6ff4690b7e972eeb742a5ab
ACR-09a4fa12b58141ed87d617ab7bdaf341
ACR-c0959acac8294413bcc07abe5271934b
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

  /*ACR-538f7a8cd4c74ce8a4ef68f4b9c965b4
ACR-e00de1635f9e41458ee1cc0ad478d440
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
    //ACR-8e0f186f223b4da79f13840ff3c33cc3
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

  /*ACR-056852eb84194fc6be56ee8cce2b2e11
ACR-cefaf2a236c04e5a9caffd40eb21998a
ACR-7b720ab0f60645c6b5e02135a9f27174
ACR-8126ad6c4496446dbadef260f7310dbf
   */
  static boolean isCompatibleWith(PluginInfo plugin, Version implementedApiVersion) {
    var sonarMinVersion = plugin.getMinimalSqVersion();
    if (sonarMinVersion == null) {
      //ACR-17a7e2fc086c42f995095ec94b2f579c
      return true;
    }

    //ACR-320c1c7906194bf983f29d1dac4a276b
    var requestedApi = Version.create(sonarMinVersion.getMajor() + "." + sonarMinVersion.getMinor());
    return implementedApiVersion.satisfiesMinRequirement(requestedApi);
  }

  private static PluginRequirementsCheckResult checkUnsatisfiedPluginDependency(PluginRequirementsCheckResult currentResult,
    Map<String, PluginRequirementsCheckResult> currentResultsByKey, boolean enableDataflowBugDetection) {
    var plugin = currentResult.getPlugin();
    for (RequiredPlugin required : plugin.getRequiredPlugins()) {
      if ("license".equals(required.getKey()) || (SonarLanguage.JS.getPluginKey().equals(plugin.getKey()) && OLD_SONARTS_PLUGIN_KEY.equals(required.getKey()))) {
        //ACR-d252019478a04561909b8ede3bc4b8fe
        //ACR-eb3569729e46409ab65a2ae10310b0f1
        //ACR-b81c7b6d948a41839875028e6789082e
        //ACR-280b10fb0d0340b98ed512cb0646d576
        continue;
      }
      var depInfo = currentResultsByKey.get(required.getKey());
      //ACR-70b326a825054374b2ff43aa2db4d6aa
      //ACR-5cd9a38936984e85b4eec5e38845708b
      //ACR-49337b6f920e4a6fb4da0ce9bca859ac
      //ACR-3dcc66beb9914d6385b2a2c299d93d1c
      //ACR-d028187abfcf49e689bfd9b199dc4856
      if (depInfo == null || depInfo.isSkipped()) {
        return processUnsatisfiedDependency(currentResult.getPlugin(), required.getKey());
      }
    }
    var basePluginKey = plugin.getBasePlugin();
    if (basePluginKey != null && checkForPluginSkipped(currentResultsByKey.get(basePluginKey))) {
      return processUnsatisfiedDependency(currentResult.getPlugin(), basePluginKey);
    }
    if (DataflowBugDetection.PLUGIN_ALLOW_LIST.contains(plugin.getKey())) {
      //ACR-b75c8b2022984a548e97bb6068560e04
      //ACR-733e01b9f05b438aa7be87530dbf7790
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
