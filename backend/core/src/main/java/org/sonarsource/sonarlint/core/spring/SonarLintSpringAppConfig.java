/*
ACR-628f5bd66bc146d387de2ed3f8c76885
ACR-3ea54318479d40dd9a369e2efbc68879
ACR-a753f8e1864b4374815d51f8c80ccc01
ACR-369f561e31f44bef934b43ac8af76557
ACR-38b796eda3c04e769c5b1a13bc6b622d
ACR-f69f312467574c2cb8edc4d29161a79f
ACR-6d4e639743964b59bf4c924114f5fb6a
ACR-ac1e3bb67e40476cb06327c98ee47012
ACR-cdb69c663a7e405690f3d686cb591b8f
ACR-7e5b497d5ae842008d38c973857e42d1
ACR-c793d2e148d34fefbacb029f3399459d
ACR-333839f292fa45e0b9bbe887d75ab8a5
ACR-dccc1c19e2cd4fb5a157a7e15d2aa7c4
ACR-dce6d7ddaeaf43c8b94d9c48f0d2fb0a
ACR-0b80d4f80dcf45ff9a885454b59f8cfe
ACR-2d5ebcb158c2453681a780face263fd6
ACR-78703f837ba84646be9195662c78b7b9
 */
package org.sonarsource.sonarlint.core.spring;

import java.net.ProxySelector;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.core5.util.Timeout;
import org.jooq.DSLContext;
import org.sonarsource.sonarlint.core.BindingCandidatesFinder;
import org.sonarsource.sonarlint.core.BindingClueProvider;
import org.sonarsource.sonarlint.core.BindingSuggestionProvider;
import org.sonarsource.sonarlint.core.ConfigurationService;
import org.sonarsource.sonarlint.core.ConnectionService;
import org.sonarsource.sonarlint.core.ConnectionSuggestionProvider;
import org.sonarsource.sonarlint.core.MCPServerConfigurationProvider;
import org.sonarsource.sonarlint.core.OrganizationsCache;
import org.sonarsource.sonarlint.core.SharedConnectedModeSettingsProvider;
import org.sonarsource.sonarlint.core.SonarCloudActiveEnvironment;
import org.sonarsource.sonarlint.core.SonarCodeContextService;
import org.sonarsource.sonarlint.core.SonarProjectsCache;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.TokenGeneratorHelper;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.VersionSoonUnsupportedHelper;
import org.sonarsource.sonarlint.core.active.rules.ActiveRulesService;
import org.sonarsource.sonarlint.core.ai.ide.AiAgentService;
import org.sonarsource.sonarlint.core.ai.ide.AiHookService;
import org.sonarsource.sonarlint.core.analysis.AnalysisSchedulerCache;
import org.sonarsource.sonarlint.core.analysis.AnalysisService;
import org.sonarsource.sonarlint.core.analysis.NodeJsService;
import org.sonarsource.sonarlint.core.analysis.UserAnalysisPropertiesRepository;
import org.sonarsource.sonarlint.core.branch.SonarProjectBranchTrackingService;
import org.sonarsource.sonarlint.core.commons.dogfood.DogfoodEnvironmentDetectionService;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.embedded.server.AnalyzeFileListRequestHandler;
import org.sonarsource.sonarlint.core.embedded.server.AwaitingUserTokenFutureRepository;
import org.sonarsource.sonarlint.core.embedded.server.EmbeddedServer;
import org.sonarsource.sonarlint.core.embedded.server.RequestHandlerBindingAssistant;
import org.sonarsource.sonarlint.core.embedded.server.ToggleAutomaticAnalysisRequestHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.GeneratedUserTokenHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.ShowFixSuggestionRequestHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.ShowHotspotRequestHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.ShowIssueRequestHandler;
import org.sonarsource.sonarlint.core.embedded.server.handler.StatusRequestHandler;
import org.sonarsource.sonarlint.core.file.PathTranslationService;
import org.sonarsource.sonarlint.core.file.ServerFilePathsProvider;
import org.sonarsource.sonarlint.core.flight.recorder.FlightRecorderService;
import org.sonarsource.sonarlint.core.flight.recorder.FlightRecorderSession;
import org.sonarsource.sonarlint.core.flight.recorder.FlightRecorderStorageService;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.fs.FileExclusionService;
import org.sonarsource.sonarlint.core.fs.OpenFilesRepository;
import org.sonarsource.sonarlint.core.hotspot.HotspotService;
import org.sonarsource.sonarlint.core.http.AskClientCertificatePredicate;
import org.sonarsource.sonarlint.core.http.ClientProxyCredentialsProvider;
import org.sonarsource.sonarlint.core.http.ClientProxySelector;
import org.sonarsource.sonarlint.core.http.ConnectionAwareHttpClientProvider;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.http.HttpConfig;
import org.sonarsource.sonarlint.core.http.ssl.CertificateStore;
import org.sonarsource.sonarlint.core.http.ssl.SslConfig;
import org.sonarsource.sonarlint.core.issue.IssueService;
import org.sonarsource.sonarlint.core.languages.LanguageSupportRepository;
import org.sonarsource.sonarlint.core.local.only.XodusLocalOnlyIssueStorageService;
import org.sonarsource.sonarlint.core.log.LogService;
import org.sonarsource.sonarlint.core.mode.SeverityModeService;
import org.sonarsource.sonarlint.core.monitoring.MonitoringInitializationParams;
import org.sonarsource.sonarlint.core.monitoring.MonitoringService;
import org.sonarsource.sonarlint.core.monitoring.MonitoringUserIdStore;
import org.sonarsource.sonarlint.core.newcode.NewCodeService;
import org.sonarsource.sonarlint.core.plugin.PluginsRepository;
import org.sonarsource.sonarlint.core.plugin.PluginsService;
import org.sonarsource.sonarlint.core.plugin.skipped.SkippedPluginsNotifierService;
import org.sonarsource.sonarlint.core.plugin.skipped.SkippedPluginsRepository;
import org.sonarsource.sonarlint.core.progress.ClientAwareTaskManager;
import org.sonarsource.sonarlint.core.remediation.aicodefix.AiCodeFixService;
import org.sonarsource.sonarlint.core.reporting.FindingReportingService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.reporting.PreviouslyRaisedFindingsRepository;
import org.sonarsource.sonarlint.core.repository.rules.RulesRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.SslConfigurationDto;
import org.sonarsource.sonarlint.core.rules.RulesExtractionHelper;
import org.sonarsource.sonarlint.core.rules.RulesService;
import org.sonarsource.sonarlint.core.sca.DependencyRiskService;
import org.sonarsource.sonarlint.core.server.event.ServerEventsService;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixRepository;
import org.sonarsource.sonarlint.core.serverconnection.issues.KnownFindingsRepository;
import org.sonarsource.sonarlint.core.serverconnection.issues.LocalOnlyIssuesRepository;
import org.sonarsource.sonarlint.core.smartnotifications.SmartNotifications;
import org.sonarsource.sonarlint.core.storage.SonarLintDatabaseService;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.sync.FindingsSynchronizationService;
import org.sonarsource.sonarlint.core.sync.HotspotSynchronizationService;
import org.sonarsource.sonarlint.core.sync.IssueSynchronizationService;
import org.sonarsource.sonarlint.core.sync.ScaSynchronizationService;
import org.sonarsource.sonarlint.core.sync.SonarProjectBranchesSynchronizationService;
import org.sonarsource.sonarlint.core.sync.SynchronizationService;
import org.sonarsource.sonarlint.core.sync.TaintSynchronizationService;
import org.sonarsource.sonarlint.core.telemetry.TelemetryLocalStorageManager;
import org.sonarsource.sonarlint.core.tracking.LocalOnlyIssueRepository;
import org.sonarsource.sonarlint.core.tracking.TaintVulnerabilityTrackingService;
import org.sonarsource.sonarlint.core.tracking.TrackingService;
import org.sonarsource.sonarlint.core.tracking.XodusKnownFindingsStorageService;
import org.sonarsource.sonarlint.core.websocket.WebSocketService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.support.TaskUtils;

import static org.sonarsource.sonarlint.core.http.ssl.CertificateStore.DEFAULT_PASSWORD;
import static org.sonarsource.sonarlint.core.http.ssl.CertificateStore.DEFAULT_STORE_TYPE;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FLIGHT_RECORDER;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.MONITORING;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.TELEMETRY;

@Configuration
//ACR-8c9e908a76ec4077a448f79029e82862
//ACR-3397442376344f9bbc3f74e046e316dc
@Import({
  AskClientCertificatePredicate.class,
  ClientProxySelector.class,
  ClientProxyCredentialsProvider.class,
  ConnectionAwareHttpClientProvider.class,
  ConfigurationService.class,
  ConfigurationRepository.class,
  RulesService.class,
  SonarQubeClientManager.class,
  ConnectionConfigurationRepository.class,
  RulesRepository.class,
  RulesExtractionHelper.class,
  PluginsService.class,
  SkippedPluginsNotifierService.class,
  PluginsRepository.class,
  SkippedPluginsRepository.class,
  LanguageSupportRepository.class,
  ConnectionService.class,
  TokenGeneratorHelper.class,
  EmbeddedServer.class,
  StatusRequestHandler.class,
  GeneratedUserTokenHandler.class,
  AwaitingUserTokenFutureRepository.class,
  ShowHotspotRequestHandler.class,
  ShowIssueRequestHandler.class,
  ShowFixSuggestionRequestHandler.class,
  BindingSuggestionProvider.class,
  ConnectionSuggestionProvider.class,
  BindingClueProvider.class,
  SonarProjectsCache.class,
  SonarProjectBranchTrackingService.class,
  SynchronizationService.class,
  HotspotService.class,
  IssueService.class,
  AnalysisService.class,
  SmartNotifications.class,
  LocalOnlyIssueRepository.class,
  WebSocketService.class,
  ServerEventsService.class,
  VersionSoonUnsupportedHelper.class,
  XodusLocalOnlyIssueStorageService.class,
  StorageService.class,
  SeverityModeService.class,
  NewCodeService.class,
  RequestHandlerBindingAssistant.class,
  TaintVulnerabilityTrackingService.class,
  SonarProjectBranchesSynchronizationService.class,
  TaintSynchronizationService.class,
  IssueSynchronizationService.class,
  HotspotSynchronizationService.class,
  ClientFileSystemService.class,
  SonarCodeContextService.class,
  PathTranslationService.class,
  ServerFilePathsProvider.class,
  FileExclusionService.class,
  NodeJsService.class,
  OrganizationsCache.class,
  BindingCandidatesFinder.class,
  SharedConnectedModeSettingsProvider.class,
  MCPServerConfigurationProvider.class,
  AnalysisSchedulerCache.class,
  XodusKnownFindingsStorageService.class,
  TrackingService.class,
  FindingsSynchronizationService.class,
  FindingReportingService.class,
  PreviouslyRaisedFindingsRepository.class,
  UserAnalysisPropertiesRepository.class,
  OpenFilesRepository.class,
  DogfoodEnvironmentDetectionService.class,
  MonitoringService.class,
  MonitoringUserIdStore.class,
  AiCodeFixService.class,
  ClientAwareTaskManager.class,
  ScaSynchronizationService.class,
  DependencyRiskService.class,
  FlightRecorderService.class,
  FlightRecorderStorageService.class,
  ToggleAutomaticAnalysisRequestHandler.class,
  AnalyzeFileListRequestHandler.class,
  AiAgentService.class,
  AiHookService.class,
  LogService.class,
  ActiveRulesService.class,
  AiCodeFixRepository.class,
  SonarLintDatabaseService.class,
  LocalOnlyIssuesRepository.class,
  KnownFindingsRepository.class
})
public class SonarLintSpringAppConfig {

  @Bean(name = "applicationEventMulticaster")
  public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
    var eventMulticaster = new SimpleApplicationEventMulticaster();
    eventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
    return eventMulticaster;
  }

  @Bean
  UserPaths provideClientPaths(InitializeParams initializeParams) {
    return UserPaths.from(initializeParams);
  }

  @Bean
  SonarCloudActiveEnvironment provideSonarCloudActiveEnvironment(InitializeParams params) {
    var alternativeSonarCloudEnv = params.getAlternativeSonarCloudEnvironment();
    return alternativeSonarCloudEnv == null ? SonarCloudActiveEnvironment.prod()
      : new SonarCloudActiveEnvironment(alternativeSonarCloudEnv.getAlternateRegionUris());
  }

  @Bean
  HttpClientProvider provideHttpClientProvider(InitializeParams params, UserPaths userPaths, AskClientCertificatePredicate askClientCertificatePredicate,
    ProxySelector proxySelector, CredentialsProvider proxyCredentialsProvider) {
    return new HttpClientProvider(params.getClientConstantInfo().getUserAgent(), adapt(params.getHttpConfiguration(), userPaths.getUserHome()), askClientCertificatePredicate,
      proxySelector, proxyCredentialsProvider);
  }

  @Bean
  FlightRecorderSession provideFlightRecorderSession() {
    return new FlightRecorderSession(UUID.randomUUID());
  }

  @Bean
  MonitoringInitializationParams provideMonitoringInitParams(InitializeParams params, FlightRecorderSession flightRecorderSession, TelemetryLocalStorageManager telemetryService) {
    return new MonitoringInitializationParams(
      params.getBackendCapabilities().contains(MONITORING),
      params.getBackendCapabilities().contains(FLIGHT_RECORDER),
      params.getBackendCapabilities().contains(TELEMETRY) && telemetryService.isEnabled(),
      flightRecorderSession.sessionId(),
      params.getTelemetryConstantAttributes().getProductKey(),
      params.getTelemetryConstantAttributes().getProductVersion(),
      params.getTelemetryConstantAttributes().getIdeVersion());
  }

  //ACR-51c550ab3b634674a0c946e70b647c72
  //ACR-4ecf6ebff06c4ddba0d0af853c927398
  @Bean(destroyMethod = "")
  SonarLintDatabase provideDatabase(UserPaths userPaths, MonitoringService monitoringService) {
    return new SonarLintDatabase(userPaths.getStorageRoot());
  }

  @Bean
  DSLContext provideDSLContext(SonarLintDatabase database) {
    return database.dsl();
  }

  private static HttpConfig adapt(HttpConfigurationDto dto, @Nullable Path sonarlintUserHome) {
    return new HttpConfig(adapt(dto.getSslConfiguration(), sonarlintUserHome), toTimeout(dto.getConnectTimeout()), toTimeout(dto.getSocketTimeout()),
      toTimeout(dto.getConnectionRequestTimeout()), toTimeout(dto.getResponseTimeout()));
  }

  private static SslConfig adapt(SslConfigurationDto dto, @Nullable Path sonarlintUserHome) {
    return new SslConfig(
      adaptStore(dto.getKeyStorePath(), dto.getKeyStorePassword(), dto.getKeyStoreType(), sonarlintUserHome, "keystore"),
      adaptStore(dto.getTrustStorePath(), dto.getTrustStorePassword(), dto.getTrustStoreType(), sonarlintUserHome, "truststore"));
  }

  private static CertificateStore adaptStore(@Nullable Path storePathConfig, @Nullable String storePasswordConfig, @Nullable String storeTypeConfig,
    @Nullable Path sonarlintUserHome,
    String defaultStoreName) {
    var storePath = storePathConfig;
    if (storePath == null && sonarlintUserHome != null) {
      storePath = sonarlintUserHome.resolve("ssl/" + defaultStoreName + ".p12");
    }
    if (storePath != null) {
      var keyStorePassword = storePasswordConfig == null ? DEFAULT_PASSWORD : storePasswordConfig;
      var keyStoreType = storeTypeConfig == null ? DEFAULT_STORE_TYPE : storeTypeConfig;
      return new CertificateStore(storePath, keyStorePassword, keyStoreType);
    }
    return null;
  }

  @CheckForNull
  private static Timeout toTimeout(@Nullable Duration duration) {
    return duration == null ? null : Timeout.of(duration);
  }
}
