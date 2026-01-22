/*
ACR-534de0d7524a494b85bd72116a02bef3
ACR-a59e4499b60042d8bca19133b8d3be37
ACR-6a598441153541aabeffee1037c2ff58
ACR-2a56fb736ce7440d9d026b3f1934816f
ACR-5343f9a41d87428d9f34ccab2854ed10
ACR-a413f43573c9477496124b5a1329dc81
ACR-ea879c2fe873498a87c60ed7315c9d17
ACR-605ca9d7ea234f42a44078bb794010f2
ACR-a10efc20b62a42b7ae762590ff96ec62
ACR-0f8e80ae23fe4b37bdd85773cc235d10
ACR-593cd8f1e69b4e7fbb6d2608ec2fd5ad
ACR-eb8da7170dab48e3bd3ac91eaff7f5ab
ACR-8914bb794b4a482f84493449f540f178
ACR-d7d61304788f472ebdf4ed6c32c2e107
ACR-78807e87934f4e09bc05008a5ca6c561
ACR-540510e3d244433c92c679ae7eaf4319
ACR-1b5d84d8ef0e40dc8209f5bee0dc07a5
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

  /*ACR-7cb18e4e45534796b7bdda56ebe78125
ACR-9e16078152a94c96b5f4976e57ca6e54
ACR-8268ae91dd7542cc9b392c68dae66720
ACR-de6f6ef6b1764956a9c457b77dd5eefa
ACR-c1e3d266f6194a3da875760ee6bff777
ACR-98876afe676f46ecbced394665d00675
ACR-82afde166fcf4432acf409e4dc8857be
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

  /*ACR-af225e0f4e74431b9f7908e51ca399fb
ACR-a2d05273fcf84f45b29eb1bed593821a
ACR-ef2089a768274e20904b8f3e71e4a060
ACR-f11b0ef7838644db8a679f77c3385743
ACR-b60a71c660e241679c94054be8abc4d8
ACR-2dbbf4a383854498afcd6ddcd6aa1458
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
