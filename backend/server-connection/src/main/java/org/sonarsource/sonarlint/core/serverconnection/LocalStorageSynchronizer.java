/*
ACR-30c4a6d353bd4fe4bb9428fecb7144e8
ACR-d82928d4004048669f635ef1cf5628ee
ACR-021a90955e0d49488dafb5724ac8120f
ACR-d10119510a744957af2dbf04c2ba890a
ACR-5511e69341c442329798c72a5faddc23
ACR-2cb8ad6217784e679c83c32041c88f31
ACR-948d2cc42653421e82e9bfd20c00545a
ACR-531093b89fe84693943b035d28edf2c8
ACR-2d4af39028c94097a648c028516f8f9e
ACR-489e8c4ca09c456c98d0cb63132be3da
ACR-f607149988e44fe09243930a6e7e01be
ACR-ddae1a7f1cde4bb493404dbef047ddb4
ACR-ddb675d8434740979905c18dcaa77b59
ACR-cf56756f8c64413196398c648d6b9f31
ACR-72e778b66f534a3695a360c47b2173fb
ACR-6983bd97b56b426b8be01cdd76dfdb5a
ACR-a32ae8c78f03492d9da111df2a504c90
 */
package org.sonarsource.sonarlint.core.serverconnection;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.qualityprofile.QualityProfile;
import org.sonarsource.sonarlint.core.serverconnection.storage.StorageException;

import static java.util.stream.Collectors.toSet;

public class LocalStorageSynchronizer {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Set<String> enabledLanguageKeys;
  private final ConnectionStorage storage;
  private final ServerInfoSynchronizer serverInfoSynchronizer;
  private final PluginsSynchronizer pluginsSynchronizer;

  public LocalStorageSynchronizer(Set<SonarLanguage> enabledLanguages, Set<String> embeddedPluginKeys, ServerInfoSynchronizer serverInfoSynchronizer, ConnectionStorage storage) {
    this.enabledLanguageKeys = enabledLanguages.stream().map(SonarLanguage::getSonarLanguageKey).collect(toSet());
    this.storage = storage;
    this.pluginsSynchronizer = new PluginsSynchronizer(enabledLanguages, storage, embeddedPluginKeys);
    this.serverInfoSynchronizer = serverInfoSynchronizer;
  }

  public Summary synchronizeServerInfosAndPlugins(ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    serverInfoSynchronizer.synchronize(serverApi, cancelMonitor);
    var version = storage.serverInfo().read().orElseThrow().version();
    var pluginSynchronizationSummary = pluginsSynchronizer.synchronize(serverApi, version, cancelMonitor);
    return new Summary(version, pluginSynchronizationSummary.anyPluginSynchronized());
  }

  private static AnalyzerSettingsUpdateSummary diffAnalyzerConfiguration(AnalyzerConfiguration original, AnalyzerConfiguration updated) {
    var originalSettings = original.getSettings().getAll();
    var updatedSettings = updated.getSettings().getAll();
    var diff = Maps.difference(originalSettings, updatedSettings);
    var updatedSettingsValueByKey = diff.entriesDiffering().entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().rightValue()));
    updatedSettingsValueByKey.putAll(diff.entriesOnlyOnRight());
    updatedSettingsValueByKey.putAll(diff.entriesOnlyOnLeft().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> "")));
    return new AnalyzerSettingsUpdateSummary(updatedSettingsValueByKey);
  }

  public AnalyzerSettingsUpdateSummary synchronizeAnalyzerConfig(ServerApi serverApi, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    var updatedAnalyzerConfiguration = downloadAnalyzerConfig(serverApi, projectKey, cancelMonitor);
    AnalyzerSettingsUpdateSummary configUpdateSummary;
    try {
      var originalAnalyzerConfiguration = storage.project(projectKey).analyzerConfiguration().read();
      configUpdateSummary = diffAnalyzerConfiguration(originalAnalyzerConfiguration, updatedAnalyzerConfiguration);
    } catch (StorageException e) {
      configUpdateSummary = new AnalyzerSettingsUpdateSummary(updatedAnalyzerConfiguration.getSettings().getAll());
    }

    storage.project(projectKey).analyzerConfiguration().store(updatedAnalyzerConfiguration);
    var version = storage.serverInfo().read().orElseThrow().version();
    serverApi.newCodeApi().getNewCodeDefinition(projectKey, null, version, cancelMonitor)
      .ifPresent(ncd -> storage.project(projectKey).newCodeDefinition().store(ncd));
    return configUpdateSummary;
  }

  private AnalyzerConfiguration downloadAnalyzerConfig(ServerApi serverApi, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    LOG.info("[SYNC] Synchronizing analyzer configuration for project '{}'", projectKey);
    LOG.info("[SYNC] Languages enabled for synchronization: {}", enabledLanguageKeys);
    Map<String, RuleSet> currentRuleSets;
    int currentSchemaVersion;
    try {
      var analyzerConfiguration = storage.project(projectKey).analyzerConfiguration().read();
      currentRuleSets = analyzerConfiguration.getRuleSetByLanguageKey();
      currentSchemaVersion = analyzerConfiguration.getSchemaVersion();
    } catch (StorageException e) {
      currentRuleSets = Map.of();
      currentSchemaVersion = 0;
    }
    var shouldForceRuleSetUpdate = outdatedSchema(currentSchemaVersion);
    var currentRuleSetsFinal = currentRuleSets;
    var settings = new Settings(serverApi.settings().getProjectSettings(projectKey, cancelMonitor));
    var ruleSetsByLanguageKey = serverApi.qualityProfile().getQualityProfiles(projectKey, cancelMonitor).stream()
      .filter(qualityProfile -> enabledLanguageKeys.contains(qualityProfile.getLanguage()))
      .collect(Collectors.toMap(QualityProfile::getLanguage, profile -> toRuleSet(serverApi, currentRuleSetsFinal, profile, shouldForceRuleSetUpdate, cancelMonitor)));
    return new AnalyzerConfiguration(settings, ruleSetsByLanguageKey, AnalyzerConfiguration.CURRENT_SCHEMA_VERSION);
  }

  private static RuleSet toRuleSet(ServerApi serverApi, Map<String, RuleSet> currentRuleSets, QualityProfile profile, boolean forceUpdate,
    SonarLintCancelMonitor cancelMonitor) {
    var language = profile.getLanguage();
    if (forceUpdate ||
      newlySupportedLanguage(currentRuleSets, language) ||
      profileModifiedSinceLastSync(currentRuleSets, profile, language)) {
      var profileKey = profile.getKey();
      LOG.info("[SYNC] Fetching rule set for language '{}' from profile '{}'", language, profileKey);
      var profileActiveRules = serverApi.rules().getAllActiveRules(profileKey, cancelMonitor);
      return new RuleSet(profileActiveRules, profile.getRulesUpdatedAt());
    } else {
      LOG.info("[SYNC] Active rules for '{}' are up-to-date", language);
      return currentRuleSets.get(language);
    }
  }

  private static boolean profileModifiedSinceLastSync(Map<String, RuleSet> currentRuleSets, QualityProfile profile, String language) {
    return !currentRuleSets.get(language).getLastModified().equals(profile.getRulesUpdatedAt());
  }

  private static boolean newlySupportedLanguage(Map<String, RuleSet> currentRuleSets, String language) {
    return !currentRuleSets.containsKey(language);
  }

  private static boolean outdatedSchema(int currentSchemaVersion) {
    return currentSchemaVersion < AnalyzerConfiguration.CURRENT_SCHEMA_VERSION;
  }

  public record Summary(Version version, boolean anyPluginSynchronized) {
  }
}
