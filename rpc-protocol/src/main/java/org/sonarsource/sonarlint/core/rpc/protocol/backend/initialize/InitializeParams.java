/*
ACR-448dc70cd7e543c39801e2bfb177e503
ACR-e7fd3b23807b4f0db813939043aed69a
ACR-9b2e08527e184d008cd8c2d987b99c88
ACR-3227eb4b9cbb4cf385dd28b227184410
ACR-f7dcdf174ec5442f801ff5ed97f926e4
ACR-367d93d2caf24e5a9545eb913b99a828
ACR-f9c46afe1fd746b3973b4c7fdc9984a7
ACR-54eb4cf3313f460cb9e95c87cbbb4c0c
ACR-e5b888a73e484128a42ce782a3657dd1
ACR-f81ac6045f324e39bd1e6ae9fb674ecb
ACR-8c1187b2516745059e38bce6b4bba28e
ACR-e1e15422bccd4cd1b020b4bf1d1b6b16
ACR-1c95065b91f747e9aef5b3636c55b781
ACR-78fb403287ee48f0b09dbbba7431f77e
ACR-037c9ce5b1aa41f8a1ca466e4df09b9c
ACR-d52718af872c4970b0c9d3df570a09b1
ACR-85896abd43d14156becebbcdc3472d70
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarCloudConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class InitializeParams {
  private final ClientConstantInfoDto clientConstantInfo;
  private final TelemetryClientConstantAttributesDto telemetryConstantAttributes;
  private final HttpConfigurationDto httpConfiguration;
  private final SonarCloudAlternativeEnvironmentDto alternativeSonarCloudEnvironment;
  private final Set<BackendCapability> backendCapabilities;
  private final Path storageRoot;
  private final Path workDir;
  private final Set<Path> embeddedPluginPaths;
  private final Map<String, Path> connectedModeEmbeddedPluginPathsByKey;
  private final Set<Language> enabledLanguagesInStandaloneMode;
  private final Set<Language> extraEnabledLanguagesInConnectedMode;
  private final Set<String> disabledPluginKeysForAnalysis;
  private final List<SonarQubeConnectionConfigurationDto> sonarQubeConnections;
  private final List<SonarCloudConnectionConfigurationDto> sonarCloudConnections;
  private final String sonarlintUserHome;
  private final Map<String, StandaloneRuleConfigDto> standaloneRuleConfigByKey;
  private final boolean isFocusOnNewCode;
  private final LanguageSpecificRequirements languageSpecificRequirements;
  private final boolean automaticAnalysisEnabled;
  private final TelemetryMigrationDto telemetryMigration;
  private final LogLevel logLevel;

  /*ACR-a6d92b9be8e74da29d8f579be5515859
ACR-f028f7c455854ff0b49ddf7f201bfba6
ACR-f8aa68bad68e4ed48f7ed4d3084b257c
ACR-f11af01fd2754416b0fcf638c7d9c077
ACR-e7bd68bcaa904b2184e29318f582e2dd
ACR-0e2ddd47c8284c478b9714897b702c01
ACR-ceb1edafb76d4df0ac12d98de6296d75
   */
  @Deprecated(since = "10.35", forRemoval = true)
  public InitializeParams(
    ClientConstantInfoDto clientConstantInfo,
    TelemetryClientConstantAttributesDto telemetryConstantAttributes,
    HttpConfigurationDto httpConfiguration,
    @Nullable SonarCloudAlternativeEnvironmentDto alternativeSonarCloudEnvironment,
    Set<BackendCapability> backendCapabilities,
    Path storageRoot,
    @Nullable Path workDir,
    @Nullable Set<Path> embeddedPluginPaths,
    @Nullable Map<String, Path> connectedModeEmbeddedPluginPathsByKey,
    @Nullable Set<Language> enabledLanguagesInStandaloneMode,
    @Nullable Set<Language> extraEnabledLanguagesInConnectedMode,
    @Nullable Set<String> disabledPluginKeysForAnalysis,
    @Nullable List<SonarQubeConnectionConfigurationDto> sonarQubeConnections,
    @Nullable List<SonarCloudConnectionConfigurationDto> sonarCloudConnections,
    @Nullable String sonarlintUserHome,
    @Nullable Map<String, StandaloneRuleConfigDto> standaloneRuleConfigByKey,
    boolean isFocusOnNewCode,
    @Nullable LanguageSpecificRequirements languageSpecificRequirements,
    boolean automaticAnalysisEnabled,
    @Nullable TelemetryMigrationDto telemetryMigration) {
    this(clientConstantInfo, telemetryConstantAttributes, httpConfiguration, alternativeSonarCloudEnvironment, backendCapabilities, storageRoot, workDir, embeddedPluginPaths,
      connectedModeEmbeddedPluginPathsByKey, enabledLanguagesInStandaloneMode, extraEnabledLanguagesInConnectedMode, disabledPluginKeysForAnalysis, sonarQubeConnections,
      sonarCloudConnections, sonarlintUserHome, standaloneRuleConfigByKey, isFocusOnNewCode, languageSpecificRequirements, automaticAnalysisEnabled, telemetryMigration,
      LogLevel.TRACE);
  }

  /*ACR-72c67787fd7c4dd7aebf27ad89a6c48d
ACR-c4b4dea54acd43f1af4c7283ae00e762
ACR-7d166d05fcca463cb04f8f27770f3c87
ACR-bbd65d9028e24e0b8ef0d7afcc8c05b1
ACR-991cf8e5ef3c4e6792ea90849315fa76
ACR-147a66c162c940ba85f2945968bdcd2e
   */
  public InitializeParams(
    ClientConstantInfoDto clientConstantInfo,
    TelemetryClientConstantAttributesDto telemetryConstantAttributes,
    HttpConfigurationDto httpConfiguration,
    @Nullable SonarCloudAlternativeEnvironmentDto alternativeSonarCloudEnvironment,
    Set<BackendCapability> backendCapabilities,
    Path storageRoot,
    @Nullable Path workDir,
    @Nullable Set<Path> embeddedPluginPaths,
    @Nullable Map<String, Path> connectedModeEmbeddedPluginPathsByKey,
    @Nullable Set<Language> enabledLanguagesInStandaloneMode,
    @Nullable Set<Language> extraEnabledLanguagesInConnectedMode,
    @Nullable Set<String> disabledPluginKeysForAnalysis,
    @Nullable List<SonarQubeConnectionConfigurationDto> sonarQubeConnections,
    @Nullable List<SonarCloudConnectionConfigurationDto> sonarCloudConnections,
    @Nullable String sonarlintUserHome,
    @Nullable Map<String, StandaloneRuleConfigDto> standaloneRuleConfigByKey,
    boolean isFocusOnNewCode,
    @Nullable LanguageSpecificRequirements languageSpecificRequirements,
    boolean automaticAnalysisEnabled,
    @Nullable TelemetryMigrationDto telemetryMigration,
    LogLevel logLevel) {
    this.clientConstantInfo = clientConstantInfo;
    this.telemetryConstantAttributes = telemetryConstantAttributes;
    this.httpConfiguration = httpConfiguration;
    this.alternativeSonarCloudEnvironment = alternativeSonarCloudEnvironment;
    this.backendCapabilities = backendCapabilities;
    this.storageRoot = storageRoot;
    this.workDir = workDir;
    this.embeddedPluginPaths = embeddedPluginPaths;
    this.connectedModeEmbeddedPluginPathsByKey = connectedModeEmbeddedPluginPathsByKey;
    this.enabledLanguagesInStandaloneMode = enabledLanguagesInStandaloneMode;
    this.extraEnabledLanguagesInConnectedMode = extraEnabledLanguagesInConnectedMode;
    this.disabledPluginKeysForAnalysis = disabledPluginKeysForAnalysis;
    this.sonarQubeConnections = sonarQubeConnections;
    this.sonarCloudConnections = sonarCloudConnections;
    this.sonarlintUserHome = sonarlintUserHome;
    this.standaloneRuleConfigByKey = standaloneRuleConfigByKey;
    this.isFocusOnNewCode = isFocusOnNewCode;
    this.languageSpecificRequirements = languageSpecificRequirements;
    this.automaticAnalysisEnabled = automaticAnalysisEnabled;
    this.telemetryMigration = telemetryMigration;
    this.logLevel = logLevel;
  }

  public ClientConstantInfoDto getClientConstantInfo() {
    return clientConstantInfo;
  }

  public TelemetryClientConstantAttributesDto getTelemetryConstantAttributes() {
    return telemetryConstantAttributes;
  }

  public HttpConfigurationDto getHttpConfiguration() {
    return httpConfiguration;
  }

  @CheckForNull
  public SonarCloudAlternativeEnvironmentDto getAlternativeSonarCloudEnvironment() {
    return alternativeSonarCloudEnvironment;
  }

  public Set<BackendCapability> getBackendCapabilities() {
    return backendCapabilities;
  }

  public Path getStorageRoot() {
    return storageRoot;
  }

  @CheckForNull
  public Path getWorkDir() {
    return workDir;
  }

  public Set<Path> getEmbeddedPluginPaths() {
    return embeddedPluginPaths != null ? embeddedPluginPaths : Set.of();
  }

  public Map<String, Path> getConnectedModeEmbeddedPluginPathsByKey() {
    return connectedModeEmbeddedPluginPathsByKey != null ? connectedModeEmbeddedPluginPathsByKey : Map.of();
  }

  public Set<Language> getEnabledLanguagesInStandaloneMode() {
    return enabledLanguagesInStandaloneMode != null ? enabledLanguagesInStandaloneMode : Set.of();
  }

  public Set<Language> getExtraEnabledLanguagesInConnectedMode() {
    return extraEnabledLanguagesInConnectedMode != null ? extraEnabledLanguagesInConnectedMode : Set.of();
  }

  public List<SonarQubeConnectionConfigurationDto> getSonarQubeConnections() {
    return sonarQubeConnections != null ? sonarQubeConnections : List.of();
  }

  public List<SonarCloudConnectionConfigurationDto> getSonarCloudConnections() {
    return sonarCloudConnections != null ? sonarCloudConnections : List.of();
  }

  @CheckForNull
  public String getSonarlintUserHome() {
    return sonarlintUserHome;
  }

  public Map<String, StandaloneRuleConfigDto> getStandaloneRuleConfigByKey() {
    return standaloneRuleConfigByKey != null ? standaloneRuleConfigByKey : Map.of();
  }

  public boolean isFocusOnNewCode() {
    return isFocusOnNewCode;
  }

  @Nullable
  public LanguageSpecificRequirements getLanguageSpecificRequirements() {
    return languageSpecificRequirements;
  }

  public boolean isAutomaticAnalysisEnabled() {
    return automaticAnalysisEnabled;
  }

  public Set<String> getDisabledPluginKeysForAnalysis() {
    return disabledPluginKeysForAnalysis != null ? disabledPluginKeysForAnalysis : Set.of();
  }

  @CheckForNull
  public TelemetryMigrationDto getTelemetryMigration() {
    return telemetryMigration;
  }

  public LogLevel getLogLevel() {
    return logLevel;
  }
}
