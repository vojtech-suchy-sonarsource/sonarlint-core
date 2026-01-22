/*
ACR-76a47810a5894d1a89762f2830707a83
ACR-a52c472c7f7340bf946e4b657ba5ac6d
ACR-4458769a8e9845b68a397eb5113c663f
ACR-8b2be41a5af04ec6abc1183ad1b8aa04
ACR-1ece43f9795f456fa9009d8b1fa5c379
ACR-a0e45a2fd44f4c11824617a6783d8d35
ACR-efc4a8d8f607418fbd9384a5a004bcde
ACR-4829cc1fad5f4836b67b1d4036e00b48
ACR-d52e9123ff134d48a379cb4d30370481
ACR-83276315526349b1919a6cd2653d6ca6
ACR-ab2b4654006d4304b5e7263d69dbae16
ACR-f627e51677c64759873bd1a59966fdfb
ACR-7064a7e75a864d7ba0ec64da43b28f41
ACR-cfb017ed18834ff9a99e75e7d19ed026
ACR-c2b917846650464da3f7d6b4b73b9243
ACR-0b2ef02d7eaa4dcbaa1d6e3208fee556
ACR-a6ca943821cf45c6bdb771ac5537623d
 */
package org.sonarsource.sonarlint.core.test.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.client.ConfigScopeNotFoundException;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintCancelChecker;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarCloudConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.ClientConstantInfoDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.JsTsRequirementsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.LanguageSpecificRequirements;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.SonarCloudAlternativeEnvironmentDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.SonarQubeCloudRegionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.SslConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryMigrationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.NoBindingSuggestionFoundParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.ConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.embeddedserver.EmbeddedServerStartedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.event.DidReceiveServerHotspotEvent;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.FixSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.HotspotDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.GetProxyPasswordAuthenticationResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.ProxyDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.X509CertificateDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowSoonUnsupportedMessageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ReportProgressParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.StartProgressParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sca.DidChangeDependencyRisksParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.smartnotification.ShowSmartNotificationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.taint.vulnerability.DidChangeTaintVulnerabilitiesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.TelemetryClientLiveAttributesResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;
import org.sonarsource.sonarlint.core.test.utils.plugins.Plugin;
import org.sonarsource.sonarlint.core.test.utils.server.ServerFixture;
import org.sonarsource.sonarlint.core.test.utils.storage.ConfigurationScopeStorageFixture;
import org.sonarsource.sonarlint.core.test.utils.storage.StorageFixture;
import org.sonarsource.sonarlint.core.test.utils.storage.TestDatabase;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.labs.IdeLabsSpringConfig.PROPERTY_IDE_LABS_SUBSCRIPTION_URL;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.GESSIE_TELEMETRY;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.TELEMETRY;
import static org.sonarsource.sonarlint.core.telemetry.TelemetrySpringConfig.PROPERTY_TELEMETRY_ENDPOINT;
import static org.sonarsource.sonarlint.core.telemetry.gessie.GessieSpringConfig.PROPERTY_GESSIE_ENDPOINT;
import static org.sonarsource.sonarlint.core.test.utils.storage.StorageFixture.newStorage;

public class SonarLintBackendFixture {

  public static final String USER_AGENT_FOR_TESTS = "SonarLintBackendFixture";

  private SonarLintBackendFixture() {
    //ACR-978288774123483bb8f0ff20d9665ef8
  }

  public static SonarLintBackendBuilder newBackend() {
    return newBackend(null);
  }

  public static SonarLintBackendBuilder newBackend(@Nullable Consumer<SonarLintTestRpcServer> afterStartCallback) {
    return new SonarLintBackendBuilder(afterStartCallback);
  }

  public static SonarLintClientBuilder newFakeClient() {
    return new SonarLintClientBuilder();
  }

  public static class SonarLintBackendBuilder {
    private final Consumer<SonarLintTestRpcServer> afterStartCallback;
    private final List<SonarQubeConnectionConfigurationDto> sonarQubeConnections = new ArrayList<>();
    private final List<SonarCloudConnectionConfigurationDto> sonarCloudConnections = new ArrayList<>();
    private final List<ConfigurationScopeDto> configurationScopes = new ArrayList<>();
    private final List<ConfigurationScopeStorageFixture.ConfigurationScopeStorageBuilder> configurationScopeStorages = new ArrayList<>();
    private final Set<Path> embeddedPluginPaths = new HashSet<>();
    private final Map<String, Path> connectedModeEmbeddedPluginPathsByKey = new HashMap<>();
    private final Set<Language> enabledLanguages = EnumSet.noneOf(Language.class);
    private final Set<Language> extraEnabledLanguagesInConnectedMode = EnumSet.noneOf(Language.class);
    private final Set<String> disabledPluginKeysForAnalysis = new HashSet<>();
    private final Set<BackendCapability> backendCapabilities = EnumSet.noneOf(BackendCapability.class);
    private Path customStorageRoot;
    private Path customUserHome;
    private String userAgent = USER_AGENT_FOR_TESTS;
    private String clientName = "SonarLint Backend Fixture";

    private final Map<String, StandaloneRuleConfigDto> standaloneConfigByKey = new HashMap<>();
    private final List<StorageFixture.StorageBuilder> serverStorages = new ArrayList<>();
    private boolean isFocusOnNewCode;

    @Nullable
    private String euRegionUri;
    @Nullable
    private String euRegionApiUri;
    @Nullable
    private String euRegionWebSocketUri;

    @Nullable
    private String usRegionUri;
    @Nullable
    private String usRegionApiUri;
    @Nullable
    private String usRegionWebSocketUri;

    private Duration responseTimeout;
    private Path keyStorePath;
    private String keyStorePassword;
    private String keyStoreType;
    private boolean automaticAnalysisEnabled = true;
    private TelemetryMigrationDto telemetryMigration;
    private LanguageSpecificRequirements languageSpecificRequirements;
    private final List<Consumer<SonarLintTestRpcServer>> beforeInitializeCallbacks = new ArrayList<>();
    private LogLevel logLevel = LogLevel.DEBUG;
    private String productKey = "mediumTests";

    public SonarLintBackendBuilder(@Nullable Consumer<SonarLintTestRpcServer> afterStartCallback) {
      this.afterStartCallback = afterStartCallback;
    }

    public SonarLintBackendBuilder withSonarQubeConnection() {
      return withSonarQubeConnection("connectionId");
    }

    public SonarLintBackendBuilder withSonarQubeConnection(String connectionId) {
      return withSonarQubeConnection(connectionId, "http://not-used", true, null);
    }

    public SonarLintBackendBuilder withSonarQubeConnection(String connectionId, Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      return withSonarQubeConnection(connectionId, "http://not-used", true, storageBuilder);
    }

    public SonarLintBackendBuilder withSonarQubeConnection(String connectionId, String serverUrl) {
      return withSonarQubeConnection(connectionId, serverUrl, true, null);
    }

    public SonarLintBackendBuilder withSonarQubeConnection(String connectionId, String serverUrl, Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      return withSonarQubeConnection(connectionId, serverUrl, true, storageBuilder);
    }

    public SonarLintBackendBuilder withSonarQubeConnection(String connectionId, ServerFixture.Server server) {
      return withSonarQubeConnection(connectionId, server.baseUrl(), true, null);
    }

    public SonarLintBackendBuilder withSonarQubeConnection(String connectionId, ServerFixture.Server server, Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      return withSonarQubeConnection(connectionId, server.baseUrl(), true, storageBuilder);
    }

    public SonarLintBackendBuilder withSonarQubeConnectionAndNotifications(String connectionId, String serverUrl) {
      return withSonarQubeConnection(connectionId, serverUrl, false, null);
    }

    public SonarLintBackendBuilder withSonarQubeConnectionAndNotifications(String connectionId, String serverUrl, Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      return withSonarQubeConnection(connectionId, serverUrl, false, storageBuilder);
    }

    public SonarLintBackendBuilder withSonarCloudConnectionAndNotifications(String connectionId, String organisationKey, Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      return withSonarCloudConnection(connectionId, organisationKey, false, storageBuilder);
    }

    private SonarLintBackendBuilder withSonarQubeConnection(String connectionId, String serverUrl, boolean disableNotifications,
      @Nullable Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      if (storageBuilder != null) {
        var storage = newStorage(connectionId);
        storageBuilder.accept(storage);
        serverStorages.add(storage);
      }
      sonarQubeConnections.add(new SonarQubeConnectionConfigurationDto(connectionId, serverUrl, disableNotifications));
      return this;
    }

    //ACR-69f57dc013d4413587a11669a507a9f4
    public SonarLintBackendBuilder withStorage(String connectionId, Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      var storage = newStorage(connectionId);
      storageBuilder.accept(storage);
      serverStorages.add(storage);
      return this;
    }

    public SonarLintBackendBuilder withSonarQubeCloudEuRegionUri(String euRegionUri) {
      this.euRegionUri = euRegionUri;
      return this;
    }

    public SonarLintBackendBuilder withSonarQubeCloudEuRegionApiUri(String euRegionApiUri) {
      this.euRegionApiUri = euRegionApiUri;
      return this;
    }

    public SonarLintBackendBuilder withSonarQubeCloudEuRegionWebSocketUri(String euRegionWebSocketUri) {
      this.euRegionWebSocketUri = euRegionWebSocketUri;
      return this;
    }

    public SonarLintBackendBuilder withSonarQubeCloudUsRegionUri(String usRegionUri) {
      this.usRegionUri = usRegionUri;
      return this;
    }

    public SonarLintBackendBuilder withSonarQubeCloudUsRegionApiUri(String usRegionApiUri) {
      this.usRegionApiUri = usRegionApiUri;
      return this;
    }

    public SonarLintBackendBuilder withSonarQubeCloudUsRegionWebSocketUri(String usRegionWebSocketUri) {
      this.usRegionWebSocketUri = usRegionWebSocketUri;
      return this;
    }

    public SonarLintBackendBuilder withSonarCloudConnection(String connectionId, String organizationKey, boolean disableNotifications,
      @Nullable Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      return withSonarCloudConnection(connectionId, organizationKey, SonarCloudRegion.EU.name(), disableNotifications, storageBuilder);
    }

    public SonarLintBackendBuilder withSonarCloudConnection(String connectionId, String organizationKey, String region, boolean disableNotifications,
      @Nullable Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      if (storageBuilder != null) {
        var storage = newStorage(connectionId);
        storageBuilder.accept(storage);
        serverStorages.add(storage);
      }
      sonarCloudConnections.add(new SonarCloudConnectionConfigurationDto(connectionId, organizationKey,
        SonarCloudRegion.valueOf(region), disableNotifications));
      return this;
    }

    public SonarLintBackendBuilder withSonarCloudConnection(String connectionId, String organizationKey, boolean disableNotifications,
      @Nullable Consumer<StorageFixture.StorageBuilder> storageBuilder, SonarCloudRegion region) {
      if (storageBuilder != null) {
        var storage = newStorage(connectionId);
        storageBuilder.accept(storage);
        serverStorages.add(storage);
      }
      sonarCloudConnections.add(new SonarCloudConnectionConfigurationDto(connectionId, organizationKey,
        region, disableNotifications));
      return this;
    }

    public SonarLintBackendBuilder withSonarCloudConnection(String connectionId) {
      return withSonarCloudConnection(connectionId, "orgKey");
    }

    public SonarLintBackendBuilder withSonarCloudConnection(String connectionId, Consumer<StorageFixture.StorageBuilder> storageBuilder) {
      return withSonarCloudConnection(connectionId, "orgKey", true, storageBuilder);
    }

    public SonarLintBackendBuilder withSonarCloudConnection(String connectionId, String organizationKey) {
      return withSonarCloudConnection(connectionId, organizationKey, true, null);
    }

    public SonarLintBackendBuilder withSonarCloudConnection(String connectionId, String organizationKey, String region) {
      return withSonarCloudConnection(connectionId, organizationKey, region, true, null);
    }

    public SonarLintBackendBuilder withUnboundConfigScope(String configurationScopeId) {
      return withUnboundConfigScope(configurationScopeId, configurationScopeId);
    }

    public SonarLintBackendBuilder withUnboundConfigScope(String configurationScopeId, String name) {
      return withUnboundConfigScope(configurationScopeId, name, null);
    }

    public SonarLintBackendBuilder withUnboundConfigScope(String configurationScopeId, String name, @Nullable String parentId) {
      return withConfigScope(configurationScopeId, name, parentId, new BindingConfigurationDto(null, null, false));
    }

    public SonarLintBackendBuilder withBoundConfigScope(String configurationScopeId, String connectionId, String projectKey) {
      return withConfigScope(configurationScopeId, configurationScopeId, null, new BindingConfigurationDto(connectionId, projectKey, false));
    }

    public SonarLintBackendBuilder withBoundConfigScope(String configurationScopeId, String connectionId, String projectKey,
      Consumer<ConfigurationScopeStorageFixture.ConfigurationScopeStorageBuilder> storageBuilder) {
      return withConfigScope(configurationScopeId, configurationScopeId, null, new BindingConfigurationDto(connectionId, projectKey, false), storageBuilder);
    }

    public SonarLintBackendBuilder withChildConfigScope(String configurationScopeId, @Nullable String parentScopeId) {
      return withConfigScope(configurationScopeId, configurationScopeId, parentScopeId, new BindingConfigurationDto(null, null, false));
    }

    public SonarLintBackendBuilder withConfigScope(String configurationScopeId, String name, @Nullable String parentScopeId, BindingConfigurationDto bindingConfiguration) {
      return withConfigScope(configurationScopeId, name, parentScopeId, bindingConfiguration, null);
    }

    public SonarLintBackendBuilder withConfigScope(String configurationScopeId, String name, @Nullable String parentScopeId, BindingConfigurationDto bindingConfiguration,
      @Nullable Consumer<ConfigurationScopeStorageFixture.ConfigurationScopeStorageBuilder> storageBuilder) {
      if (storageBuilder != null) {
        var builder = ConfigurationScopeStorageFixture.newBuilder(configurationScopeId);
        storageBuilder.accept(builder);
        configurationScopeStorages.add(builder);
      }
      configurationScopes.add(new ConfigurationScopeDto(configurationScopeId, parentScopeId, true, name, bindingConfiguration));
      return this;
    }

    public SonarLintBackendBuilder withStandaloneEmbeddedPluginAndEnabledLanguage(Plugin plugin) {
      var builder = withStandaloneEmbeddedPlugin(plugin);
      for (var language : plugin.getLanguages()) {
        builder = builder.withEnabledLanguageInStandaloneMode(language);
      }
      return builder;
    }

    public SonarLintBackendBuilder withStandaloneEmbeddedPlugin(Plugin plugin) {
      return withStandaloneEmbeddedPlugin(plugin.getPath());
    }

    public SonarLintBackendBuilder withStandaloneEmbeddedPlugin(Path pluginPath) {
      this.embeddedPluginPaths.add(pluginPath);
      return this;
    }

    public SonarLintBackendBuilder withConnectedEmbeddedPluginAndEnabledLanguage(Plugin plugin) {
      this.embeddedPluginPaths.add(plugin.getPath());
      this.connectedModeEmbeddedPluginPathsByKey.put(plugin.getPluginKey(), plugin.getPath());
      var builder = this;
      for (var language : plugin.getLanguages()) {
        builder = builder.withEnabledLanguageInStandaloneMode(language);
      }
      return builder;
    }

    public SonarLintBackendBuilder withEnabledLanguageInStandaloneMode(Language language) {
      this.enabledLanguages.add(language);
      return this;
    }

    public SonarLintBackendBuilder withExtraEnabledLanguagesInConnectedMode(Language language) {
      this.extraEnabledLanguagesInConnectedMode.add(language);
      return this;
    }

    public SonarLintBackendBuilder withDisabledPluginsForAnalysis(String pluginKey) {
      this.disabledPluginKeysForAnalysis.add(pluginKey);
      return this;
    }

    public SonarLintBackendBuilder withBackendCapability(BackendCapability... capabilities) {
      this.backendCapabilities.addAll(Arrays.asList(capabilities));
      return this;
    }

    public SonarLintBackendBuilder withStandaloneRuleConfig(String ruleKey, boolean isActive, Map<String, String> params) {
      this.standaloneConfigByKey.put(ruleKey, new StandaloneRuleConfigDto(isActive, params));
      return this;
    }

    public SonarLintBackendBuilder withStorageRoot(Path storageRoot) {
      this.customStorageRoot = storageRoot;
      return this;
    }

    public SonarLintBackendBuilder withUserHome(Path userHome) {
      this.customUserHome = userHome;
      return this;
    }

    public SonarLintBackendBuilder withUserAgent(String userAgent) {
      this.userAgent = userAgent;
      return this;
    }

    public SonarLintBackendBuilder withClientName(String clientName) {
      this.clientName = clientName;
      return this;
    }

    public SonarLintBackendBuilder withFocusOnNewCode() {
      isFocusOnNewCode = true;
      return this;
    }

    public SonarLintBackendBuilder withClientNodeJsPath(Path clientNodeJsPath) {
      languageSpecificRequirements = new LanguageSpecificRequirements(new JsTsRequirementsDto(clientNodeJsPath, null), null);
      return this;
    }

    public SonarLintBackendBuilder withEslintBridgeServerBundlePath(Path eslintBridgeServerBundlePath) {
      languageSpecificRequirements = new LanguageSpecificRequirements(new JsTsRequirementsDto(null, eslintBridgeServerBundlePath), null);
      return this;
    }

    public SonarLintBackendBuilder withNoLanguageSpecificRequirements() {
      languageSpecificRequirements = null;
      return this;
    }

    public SonarLintBackendBuilder withHttpResponseTimeout(Duration responseTimeout) {
      this.responseTimeout = responseTimeout;
      return this;
    }

    public SonarLintBackendBuilder withKeyStore(Path keyStorePath, String password, String storeType) {
      this.keyStorePath = keyStorePath;
      this.keyStorePassword = password;
      this.keyStoreType = storeType;
      return this;
    }

    public SonarLintBackendBuilder withTelemetryEnabled() {
      return withTelemetryEnabled("http://telemetryEndpoint.sonarlint");
    }

    public SonarLintBackendBuilder withTelemetryEnabled(String endpointUrl) {
      this.backendCapabilities.add(TELEMETRY);
      System.setProperty(PROPERTY_TELEMETRY_ENDPOINT, endpointUrl);
      return this;
    }

    public SonarLintBackendBuilder withGessieTelemetryEnabled(String endpointUrl) {
      this.backendCapabilities.add(GESSIE_TELEMETRY);
      System.setProperty(PROPERTY_GESSIE_ENDPOINT, endpointUrl);
      return this;
    }

    public SonarLintBackendBuilder withIdeLabsSubscriptionUrl(String ideLabsSubscriptionUrl) {
      System.setProperty(PROPERTY_IDE_LABS_SUBSCRIPTION_URL, ideLabsSubscriptionUrl);
      return this;
    }

    public SonarLintBackendBuilder withAutomaticAnalysisEnabled(boolean enabled) {
      this.automaticAnalysisEnabled = enabled;
      return this;
    }

    public SonarLintBackendBuilder withTelemetryMigration(TelemetryMigrationDto telemetryMigration) {
      this.telemetryMigration = telemetryMigration;
      return this;
    }

    public SonarLintBackendBuilder withLogLevel(LogLevel logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    public SonarLintBackendBuilder withProductKey(String productKey) {
      this.productKey = productKey;
      return this;
    }

    public SonarLintBackendBuilder beforeInitialize(Consumer<SonarLintTestRpcServer> backendConsumer) {
      this.beforeInitializeCallbacks.add(backendConsumer);
      return this;
    }

    public SonarLintTestRpcServer start(SonarLintRpcClientDelegate client) {
      var sonarlintUserHome = customUserHome == null ? tempDirectory("slUserHome") : customUserHome;
      var workDir = tempDirectory("work");
      var storageParentPath = customStorageRoot == null ? tempDirectory("storage") : customStorageRoot.getParent();
      var storageRoot = storageParentPath.resolve("storage");
      try {
        var database = new TestDatabase(storageRoot);
        serverStorages.forEach(storage -> storage.populate(storageParentPath, database));
        if (!configurationScopeStorages.isEmpty()) {
          configurationScopeStorages.forEach(storage -> storage.populate(storageRoot, database));
        }
        database.shutdown();
        var sonarLintBackend = new SonarLintTestRpcServer(client);
        beforeInitializeCallbacks.forEach(callback -> callback.accept(sonarLintBackend));
        var telemetryInitDto = new TelemetryClientConstantAttributesDto(productKey, productKey,
          "1.2.3", "4.5.6", emptyMap());
        var clientInfo = new ClientConstantInfoDto(clientName, userAgent);

        //ACR-46f8a2cbc6ee4226b611465e70c9e4f1
        var sonarCloudAlternativeEnvironment = new SonarCloudAlternativeEnvironmentDto(Map.of(
          SonarCloudRegion.EU,
          new SonarQubeCloudRegionDto(createUriFromString(euRegionUri), createUriFromString(euRegionApiUri), createUriFromString(euRegionWebSocketUri)),
          SonarCloudRegion.US,
          new SonarQubeCloudRegionDto(createUriFromString(usRegionUri), createUriFromString(usRegionApiUri), createUriFromString(usRegionWebSocketUri))));

        var sslConfiguration = new SslConfigurationDto(null, null, null, keyStorePath, keyStorePassword, keyStoreType);
        var httpConfiguration = new HttpConfigurationDto(sslConfiguration, null, null, null, responseTimeout);
        sonarLintBackend
          .initialize(new InitializeParams(clientInfo, telemetryInitDto, httpConfiguration,
            sonarCloudAlternativeEnvironment,
            backendCapabilities,
            storageRoot, workDir, embeddedPluginPaths, connectedModeEmbeddedPluginPathsByKey,
            enabledLanguages, extraEnabledLanguagesInConnectedMode, disabledPluginKeysForAnalysis, sonarQubeConnections, sonarCloudConnections, sonarlintUserHome.toString(),
            standaloneConfigByKey, isFocusOnNewCode, languageSpecificRequirements, automaticAnalysisEnabled, telemetryMigration, logLevel))
          .get();
        sonarLintBackend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(configurationScopes));
        if (afterStartCallback != null) {
          afterStartCallback.accept(sonarLintBackend);
        }
        return sonarLintBackend;
      } catch (Exception e) {
        throw new IllegalStateException("Cannot initialize the backend", e);
      }
    }

    private static URI createUriFromString(@Nullable String uri) {
      return uri == null ? null : URI.create(uri);
    }

    private static Path tempDirectory(String prefix) {
      try {
        return Files.createTempDirectory(prefix);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public SonarLintTestRpcServer start() {
      return start(newFakeClient().build());
    }
  }

  public static class SonarLintClientBuilder {
    private final Map<String, Either<TokenDto, UsernamePasswordDto>> credentialsByConnectionId = new HashMap<>();
    private boolean printLogsToStdOut;
    private final Map<String, Path> baseDirsByConfigScope = new HashMap<>();
    private final Map<String, List<ClientFileDto>> initialFilesByConfigScope = new HashMap<>();
    private final Map<String, String> matchedBranchPerScopeId = new HashMap<>();
    private final Map<String, Set<String>> fileExclusionsByConfigScope = new HashMap<>();

    public SonarLintClientBuilder withCredentials(String connectionId, String user, String password) {
      credentialsByConnectionId.put(connectionId, Either.forRight(new UsernamePasswordDto(user, password)));
      return this;
    }

    public SonarLintClientBuilder withToken(String connectionId, String token) {
      credentialsByConnectionId.put(connectionId, Either.forLeft(new TokenDto(token)));
      return this;
    }

    public SonarLintClientBuilder withFileExclusions(String connectionId, Set<String> fileExclusions) {
      fileExclusionsByConfigScope.put(connectionId, fileExclusions);
      return this;
    }

    public FakeSonarLintRpcClient build() {
      return spy(new FakeSonarLintRpcClient(credentialsByConnectionId, printLogsToStdOut, matchedBranchPerScopeId, baseDirsByConfigScope, initialFilesByConfigScope,
        fileExclusionsByConfigScope));
    }

    public SonarLintClientBuilder printLogsToStdOut() {
      this.printLogsToStdOut = true;
      return this;
    }

    public SonarLintClientBuilder withMatchedBranch(String configurationScopeId, String matchedBranchName) {
      matchedBranchPerScopeId.put(configurationScopeId, matchedBranchName);
      return this;
    }

    public SonarLintClientBuilder withInitialFs(String configScopeId, List<ClientFileDto> clientFileDtos) {
      initialFilesByConfigScope.put(configScopeId, clientFileDtos);
      return this;
    }

    public SonarLintClientBuilder withInitialFs(String configScopeId, Path baseDir, List<ClientFileDto> clientFileDtos) {
      initialFilesByConfigScope.put(configScopeId, clientFileDtos);
      baseDirsByConfigScope.put(configScopeId, baseDir);
      return this;
    }
  }

  public static class FakeSonarLintRpcClient implements SonarLintRpcClientDelegate {
    private final Queue<ShowSoonUnsupportedMessageParams> soonUnsupportedMessagesToShow = new ConcurrentLinkedQueue<>();
    private final Queue<ShowSmartNotificationParams> smartNotificationsToShow = new ConcurrentLinkedQueue<>();
    private final Map<String, ProgressReport> progressReportsByTaskId = new ConcurrentHashMap<>();
    private final Set<String> synchronizedConfigScopeIds = new HashSet<>();
    private final Map<String, List<ConnectionSuggestionDto>> suggestionsByConfigScope = new HashMap<>();
    private final Map<String, Either<TokenDto, UsernamePasswordDto>> credentialsByConnectionId;
    private final boolean printLogsToStdOut;
    private final Queue<LogParams> logs = new ConcurrentLinkedQueue<>();
    private final List<DidChangeTaintVulnerabilitiesParams> taintVulnerabilityChanges = new CopyOnWriteArrayList<>();
    private final List<DidChangeDependencyRisksParams> dependencyRiskChanges = new CopyOnWriteArrayList<>();
    private final Map<String, String> matchedBranchPerScopeId;
    private final Map<String, Path> baseDirsByConfigScope;
    private final Map<String, Set<String>> fileExclusionsByConfigScope;
    private final Map<String, List<ClientFileDto>> initialFilesByConfigScope;
    Map<String, Map<URI, List<RaisedIssueDto>>> raisedIssuesByScopeId = new HashMap<>();
    Map<String, Map<URI, List<RaisedHotspotDto>>> raisedHotspotsByScopeId = new HashMap<>();
    Map<String, Map<String, String>> inferredAnalysisPropertiesByScopeId = new HashMap<>();
    Map<String, Boolean> analysisReadinessPerScopeId = new HashMap<>();
    Map<String, Integer> invalidTokenNotificationsCountPerConnectionId = new HashMap<>();
    int embeddedServerStartedPort = -1;

    public FakeSonarLintRpcClient(Map<String, Either<TokenDto, UsernamePasswordDto>> credentialsByConnectionId, boolean printLogsToStdOut,
      Map<String, String> matchedBranchPerScopeId, Map<String, Path> baseDirsByConfigScope, Map<String, List<ClientFileDto>> initialFilesByConfigScope,
      Map<String, Set<String>> fileExclusionsByConfigScope) {
      this.credentialsByConnectionId = credentialsByConnectionId;
      this.printLogsToStdOut = printLogsToStdOut;
      this.matchedBranchPerScopeId = matchedBranchPerScopeId;
      this.baseDirsByConfigScope = baseDirsByConfigScope;
      this.initialFilesByConfigScope = initialFilesByConfigScope;
      this.fileExclusionsByConfigScope = fileExclusionsByConfigScope;
    }

    @Override
    public void showSoonUnsupportedMessage(ShowSoonUnsupportedMessageParams params) {
      soonUnsupportedMessagesToShow.add(params);
    }

    @Override
    public void showSmartNotification(ShowSmartNotificationParams params) {
      smartNotificationsToShow.add(params);
    }

    @Override
    public String getClientLiveDescription() {
      return "";
    }

    @Override
    public void showHotspot(String configurationScopeId, HotspotDetailsDto hotspotDetails) {
      //ACR-d5bf5a86db844028b5034b1ab4cdce52
    }

    @Override
    public void showIssue(String configurationScopeId, IssueDetailsDto issueDetails) {
      //ACR-7f25439798e54172a5abf634f6e2977d
    }

    @Override
    public void showFixSuggestion(String configurationScopeId, String issueKey, FixSuggestionDto fixSuggestion) {
      //ACR-9a1868e54fe24663bf7bb95003e5e8d7
    }

    @Override
    public AssistCreatingConnectionResponse assistCreatingConnection(AssistCreatingConnectionParams params, SonarLintCancelChecker cancelChecker) {
      throw new CancellationException("Not stubbed in medium tests");
    }

    @Override
    public AssistBindingResponse assistBinding(AssistBindingParams params, SonarLintCancelChecker cancelChecker) {
      throw new CancellationException("Not stubbed in medium tests");
    }

    @Override
    public void startProgress(StartProgressParams params) throws UnsupportedOperationException {
      progressReportsByTaskId.put(params.getTaskId(),
        new ProgressReport(params.getConfigurationScopeId(), params.getTitle(), params.getMessage(), params.isIndeterminate(), params.isCancellable()));
    }

    @Override
    public void reportProgress(ReportProgressParams params) {
      var progressReport = progressReportsByTaskId.computeIfAbsent(params.getTaskId(), k -> {
        throw new IllegalStateException("Cannot update a progress that has not been started before");
      });
      var notification = params.getNotification();
      if (notification.isLeft()) {
        var updateNotification = notification.getLeft();
        progressReport.addStep(new ProgressStep(updateNotification.getMessage(), updateNotification.getPercentage()));
      } else {
        progressReport.complete();
      }
    }

    @Override
    public void invalidToken(String connectionId) {
      invalidTokenNotificationsCountPerConnectionId.merge(connectionId, 1, Integer::sum);
    }

    public Map<String, ProgressReport> getProgressReportsByTaskId() {
      return progressReportsByTaskId;
    }

    @Override
    public void didSynchronizeConfigurationScopes(Set<String> configurationScopeIds) {
      synchronizedConfigScopeIds.addAll(configurationScopeIds);
    }

    public Set<String> getSynchronizedConfigScopeIds() {
      return synchronizedConfigScopeIds;
    }

    public Map<String, List<ConnectionSuggestionDto>> getSuggestionsByConfigScope() {
      return suggestionsByConfigScope;
    }

    @Override
    public Either<TokenDto, UsernamePasswordDto> getCredentials(String connectionId) {
      return credentialsByConnectionId.getOrDefault(connectionId, Either.forLeft(new TokenDto("token")));
    }

    @Override
    public List<ProxyDto> selectProxies(URI uri) {
      return List.of();
    }

    @Override
    public GetProxyPasswordAuthenticationResponse getProxyPasswordAuthentication(String host, int port, String protocol, String prompt, String scheme, URL targetHost) {
      return null;
    }

    @Override
    public boolean checkServerTrusted(List<X509CertificateDto> chain, String authType) {
      return false;
    }

    public void setToken(String connectionId, String token) {
      credentialsByConnectionId.put(connectionId, Either.forLeft(new TokenDto(token)));
    }

    @Override
    public void didReceiveServerHotspotEvent(DidReceiveServerHotspotEvent params) {
      //ACR-c434df02b4144e71bd63ae8f4355afd3
    }

    @Override
    public String matchSonarProjectBranch(String configurationScopeId, String mainBranchName, Set<String> allBranchesNames, SonarLintCancelChecker cancelChecker) {
      if (matchedBranchPerScopeId.containsKey(configurationScopeId)) {
        return matchedBranchPerScopeId.get(configurationScopeId);
      }
      return mainBranchName;
    }

    @Override
    public void didChangeMatchedSonarProjectBranch(String configScopeId, String newMatchedBranchName) {
      //ACR-b2d927b89f8e41d1b3841666aeba9581

    }

    @Override
    public void suggestBinding(Map<String, List<BindingSuggestionDto>> suggestionsByConfigScope) {
      //ACR-dfbeb6d95d0147a2b7f727e141b2d002

    }

    @Override
    public void suggestConnection(Map<String, List<ConnectionSuggestionDto>> suggestionsByConfigScope) {
      this.suggestionsByConfigScope.putAll(suggestionsByConfigScope);
    }

    @Override
    public void openUrlInBrowser(URL url) {
      //ACR-737d501314b648008e10db10ea808948

    }

    @Override
    public void showMessage(MessageType type, String text) {
      //ACR-55bf4ed984d64aef9d5c30014ab263c5

    }

    @Override
    public TelemetryClientLiveAttributesResponse getTelemetryLiveAttributes() {
      return new TelemetryClientLiveAttributesResponse(emptyMap());
    }

    @Override
    public void didChangeTaintVulnerabilities(String configurationScopeId, Set<UUID> closedTaintVulnerabilityIds, List<TaintVulnerabilityDto> addedTaintVulnerabilities,
      List<TaintVulnerabilityDto> updatedTaintVulnerabilities) {
      this.taintVulnerabilityChanges
        .add(new DidChangeTaintVulnerabilitiesParams(configurationScopeId, closedTaintVulnerabilityIds, addedTaintVulnerabilities, updatedTaintVulnerabilities));
    }

    @Override
    public void didChangeDependencyRisks(String configurationScopeId, Set<UUID> closedDependencyRiskIds, List<DependencyRiskDto> addedDependencyRisks,
      List<DependencyRiskDto> updatedDependencyRisks) {
      this.dependencyRiskChanges
        .add(new DidChangeDependencyRisksParams(configurationScopeId, closedDependencyRiskIds, addedDependencyRisks, updatedDependencyRisks));
    }

    @Override
    public void log(LogParams params) {
      this.logs.add(params);
      if (printLogsToStdOut) {
        System.out.println((params.getThreadName() != null ? ("[" + params.getThreadName() + "] ") : "") + params.getLevel() + " "
          + (params.getConfigScopeId() != null ? ("[" + params.getConfigScopeId() + "] ") : "") + params.getMessage());
      }
    }

    @Override
    public Set<String> getFileExclusions(String configurationScopeId) {
      return fileExclusionsByConfigScope.getOrDefault(configurationScopeId, emptySet());
    }

    @Override
    public Path getBaseDir(String configurationScopeId) {
      return baseDirsByConfigScope.get(configurationScopeId);
    }

    @Override
    public List<ClientFileDto> listFiles(String configScopeId) {
      return initialFilesByConfigScope.getOrDefault(configScopeId, List.of());
    }

    @Override
    public void didChangeAnalysisReadiness(Set<String> configurationScopeIds, boolean areReadyForAnalysis) {
      configurationScopeIds.forEach(scopeId -> analysisReadinessPerScopeId.put(scopeId, areReadyForAnalysis));
    }

    public boolean isAnalysisReadyForScope(String configurationScopeId) {
      return analysisReadinessPerScopeId.getOrDefault(configurationScopeId, false);
    }

    @Override
    public void raiseIssues(String configurationScopeId, Map<URI, List<RaisedIssueDto>> issuesByFileUri, boolean isIntermediatePublication, @Nullable UUID analysisId) {
      raisedIssuesByScopeId.put(configurationScopeId, issuesByFileUri);
    }

    @Override
    public void raiseHotspots(String configurationScopeId, Map<URI, List<RaisedHotspotDto>> hotspotsByFileUri, boolean isIntermediatePublication,
      @org.jetbrains.annotations.Nullable UUID analysisId) {
      raisedHotspotsByScopeId.put(configurationScopeId, hotspotsByFileUri);
    }

    @Override
    public Map<String, String> getInferredAnalysisProperties(String configurationScopeId, List<URI> filePathsToAnalyze) throws ConfigScopeNotFoundException {
      return inferredAnalysisPropertiesByScopeId.getOrDefault(configurationScopeId, new HashMap<>());
    }

    @Override
    public void embeddedServerStarted(EmbeddedServerStartedParams params) {
      this.embeddedServerStartedPort = params.getPort();
    }

    public void setInferredAnalysisProperties(String configurationScopeId, Map<String, String> inferredAnalysisProperties) {
      inferredAnalysisPropertiesByScopeId.put(configurationScopeId, inferredAnalysisProperties);
    }

    public Map<URI, List<RaisedIssueDto>> getRaisedIssuesForScopeId(String configurationScopeId) {
      return raisedIssuesByScopeId.getOrDefault(configurationScopeId, Map.of());
    }

    public List<RaisedIssueDto> getRaisedIssuesForScopeIdAsList(String configurationScopeId) {
      return raisedIssuesByScopeId.getOrDefault(configurationScopeId, Map.of()).values().stream().flatMap(Collection::stream)
        .toList();
    }

    public Map<URI, List<RaisedHotspotDto>> getRaisedHotspotsForScopeId(String configurationScopeId) {
      return raisedHotspotsByScopeId.getOrDefault(configurationScopeId, Map.of());
    }

    public List<RaisedHotspotDto> getRaisedHotspotsForScopeIdAsList(String configurationScopeId) {
      return raisedHotspotsByScopeId.getOrDefault(configurationScopeId, Map.of()).values().stream().flatMap(Collection::stream)
        .toList();
    }

    public void cleanRaisedIssues() {
      raisedIssuesByScopeId.clear();
      raisedHotspotsByScopeId.clear();
    }

    public void cleanRaisedHotspots() {
      raisedHotspotsByScopeId.clear();
    }

    public Queue<ShowSmartNotificationParams> getSmartNotificationsToShow() {
      return smartNotificationsToShow;
    }

    public Queue<LogParams> getLogs() {
      return logs;
    }

    public List<String> getLogMessages() {
      return logs.stream().map(LogParams::getMessage).toList();
    }

    @Override
    public void noBindingSuggestionFound(NoBindingSuggestionFoundParams params) {
    }

    public List<DidChangeTaintVulnerabilitiesParams> getTaintVulnerabilityChanges() {
      return taintVulnerabilityChanges;
    }

    public List<DidChangeDependencyRisksParams> getDependencyRiskChanges() {
      return dependencyRiskChanges;
    }

    public void clearLogs() {
      logs.clear();
    }

    public void waitForSynchronization() {
      verify(this, timeout(5000)).didSynchronizeConfigurationScopes(any());
    }

    public Integer getConnectionIdsWithInvalidToken(String connectionId) {
      return invalidTokenNotificationsCountPerConnectionId.getOrDefault(connectionId, 0);
    }

    public int getEmbeddedServerPort() {
      return embeddedServerStartedPort;
    }

    public static class ProgressReport {
      @CheckForNull
      private final String configurationScopeId;
      private final String title;
      @CheckForNull
      private final String startingMessage;
      private final boolean indeterminate;
      private final boolean cancellable;
      private final Collection<ProgressStep> steps;
      private boolean complete;

      private ProgressReport(@Nullable String configurationScopeId, String title, @Nullable String startingMessage, boolean indeterminate, boolean cancellable) {
        this(configurationScopeId, title, startingMessage, indeterminate, cancellable, new ArrayList<>(), false);
      }

      public ProgressReport(@Nullable String configurationScopeId, String title, @Nullable String startingMessage, boolean indeterminate, boolean cancellable,
        Collection<ProgressStep> steps, boolean complete) {
        this.configurationScopeId = configurationScopeId;
        this.title = title;
        this.startingMessage = startingMessage;
        this.indeterminate = indeterminate;
        this.cancellable = cancellable;
        this.steps = steps;
        this.complete = complete;
      }

      private void addStep(ProgressStep step) {
        steps.add(step);
      }

      @CheckForNull
      public String getConfigurationScopeId() {
        return configurationScopeId;
      }

      public String getTitle() {
        return title;
      }

      @CheckForNull
      public String getStartingMessage() {
        return startingMessage;
      }

      public boolean isIndeterminate() {
        return indeterminate;
      }

      public boolean isCancellable() {
        return cancellable;
      }

      public Collection<ProgressStep> getSteps() {
        return steps;
      }

      public void complete() {
        this.complete = true;
      }

      public boolean isComplete() {
        return complete;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o)
          return true;
        if (o == null || getClass() != o.getClass())
          return false;
        ProgressReport that = (ProgressReport) o;
        return indeterminate == that.indeterminate && cancellable == that.cancellable && complete == that.complete
          && Objects.equals(configurationScopeId, that.configurationScopeId) && title.equals(that.title) && Objects.equals(startingMessage, that.startingMessage)
          && steps.equals(that.steps);
      }

      @Override
      public int hashCode() {
        return Objects.hash(configurationScopeId, title, startingMessage, indeterminate, cancellable, steps, complete);
      }

      @Override
      public String toString() {
        return "ProgressReport{" +
          "configurationScopeId='" + configurationScopeId + '\'' +
          ", title='" + title + '\'' +
          ", startingMessage='" + startingMessage + '\'' +
          ", indeterminate=" + indeterminate +
          ", cancellable=" + cancellable +
          ", steps=" + steps +
          ", complete=" + complete +
          '}';
      }
    }

    public record ProgressStep(@Nullable String message, @Nullable Integer percentage) {
    }
  }
}
