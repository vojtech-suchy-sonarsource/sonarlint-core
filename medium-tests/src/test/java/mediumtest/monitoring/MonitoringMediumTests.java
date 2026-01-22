/*
ACR-b592cb3d14404a77a8cad33e54e7c465
ACR-d42be707afb94271968d32a2ec4f1a34
ACR-9eba9b946aa14684816598f4df37acb4
ACR-c373804f3dd9453c9028b43402581f9e
ACR-a6ad62af0e06455bb9fee4d1fa6300bc
ACR-be2a7e2b80cc4b0b9e3c4c71a09e420f
ACR-8e2e13b02bb44ece94605cedb630363a
ACR-401aa5ba2c5c45d6a06bdcaf9c87a200
ACR-d0ff80d7615b405794a80a04554593b6
ACR-c143135a2fe443fe801e5c5416ae9db5
ACR-5d19a8a3bc624f0696b323125bdecfa4
ACR-6f4e139765844742bdebf3534deda3e7
ACR-95d2ba8767104e52b12819736bc2ddd6
ACR-8421aff76f3f4ff39f0c65293a310752
ACR-905987e1a3b24a558bd59607d78e008b
ACR-1c8790d1346b493b9f8b71196a03aa4a
ACR-1e4b4beb06414653ac174932aa560ddd
 */
package mediumtest.monitoring;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.sentry.Sentry;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.dogfood.DogfoodEnvironmentDetectionService;
import org.sonarsource.sonarlint.core.monitoring.MonitoringService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import utils.TestPlugin;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FLIGHT_RECORDER;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.MONITORING;
import static org.sonarsource.sonarlint.core.test.utils.plugins.SonarPluginBuilder.newSonarPlugin;
import static utils.AnalysisUtils.analyzeFileAndGetIssues;
import static utils.AnalysisUtils.createFile;

@ExtendWith(SystemStubsExtension.class)
class MonitoringMediumTests {
  private static final String CONFIGURATION_SCOPE_ID = "configScopeId";
  private WireMockServer sentryServer;

  @SystemStub
  private EnvironmentVariables environmentVariables;

  @BeforeEach
  void setup() {
    sentryServer = new WireMockServer(wireMockConfig().dynamicPort());
    sentryServer.start();
    System.setProperty(MonitoringService.DSN_PROPERTY, createValidSentryDsn(sentryServer));
    System.setProperty(MonitoringService.TRACES_SAMPLE_RATE_PROPERTY, "1");
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "1");
    setupSentryStubs();
  }

  @AfterEach
  void tearDown() {
    Sentry.close();
    sentryServer.stop();
  }

  private String createValidSentryDsn(WireMockServer server) {
    return "http://fake-public-key@localhost:" + server.port() + "/12345";
  }

  private void setupSentryStubs() {
    //ACR-d2d2deaa90584074b657d08482b5c698
    sentryServer.stubFor(post(urlPathMatching("/api/\\d+/store/"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{\"id\": \"event-id-12345\"}")));

    //ACR-b7c4ef6e4236484b9207e2da5dc78e06
    sentryServer.stubFor(post(urlPathMatching("/api/\\d+/envelope/"))
      .willReturn(aResponse()
        .withStatus(200)));
  }

  @SonarLintTest
  void simple_php_with_monitoring(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "foo.php", """
      <?php
      function writeMsg($fname) {
          $i = 0; // NOSONAR
          echo "Hello world!";
      }
      ?>
      """);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .withBackendCapability(MONITORING)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine()).contains(tuple("php:S1172", 2));

    //ACR-45e70a8ca29846c7ba10c9e6c2de6a8e
    await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->  assertThat(sentryServer.getAllServeEvents()).hasSize(1));
  }

  @SonarLintTest
  void analysis_errors_with_tracing(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var content = """
      <?php
      function writeMsg($fname) {
          echo "Hello world!;
      }
      ?>""";
    var inputFile = createFile(baseDir, "foo.php", content);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var throwingPluginPath = newSonarPlugin("php")
      .withSensor(ThrowingPhpSensor.class)
      .generate(baseDir);

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPlugin(throwingPluginPath)
      .withEnabledLanguageInStandaloneMode(Language.PHP)
      .withBackendCapability(MONITORING)
      .start(client);

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().during(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID)).isEmpty());
    //ACR-8e3737cc11d4439687812ab9e8cdd2dd
    await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->  assertThat(sentryServer.getAllServeEvents()).hasSize(1));
    assertThat(sentryServer.getAllServeEvents())
      .extracting(e -> e.getRequest().getBodyAsString())
      //ACR-7dcb492f665b451cb799fb6b2dd01146
      .noneMatch(m -> m.contains("server_name"));
  }

  @SonarLintTest
  void uncaught_exception_should_be_reported_to_sentry(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withBackendCapability(MONITORING)
      .start(client);

    var futureResponse = backend.getConnectionService().validateConnection(null);

    try {
      futureResponse.join();
    } catch (Exception e) {
    }

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(sentryServer.getAllServeEvents()).isNotEmpty());

    var exceptionEvent = sentryServer.getAllServeEvents().stream()
      .filter(e -> e.getRequest().getBodyAsString().contains("NullPointerException"))
      .findFirst();

    assertThat(exceptionEvent).isPresent();
    var eventBody = exceptionEvent.get().getRequest().getBodyAsString();
    assertThat(eventBody)
      .contains("NullPointerException")
      .contains("stacktrace.txt");
  }

  @SonarLintTest
  void should_not_capture_silenced_exception(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var content = """
      [3, 1, 4, 1, 5, 9]
      result = set(sorted(data))
      
      result = set(sordata))
      """;
    var newContent = """
      [3, 1, 4, 1, 5, 9]
      result = set(sorted(data))
      
      result = set(sordata))
      """;
    var filePath = createFile(baseDir, "invalid.py", content);
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIGURATION_SCOPE_ID, false, null, filePath, content, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .withBackendCapability(MONITORING)
      .start(client);
    //ACR-149579787c0c4830a158b97c742697f6
    backend.getAnalysisService().analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, UUID.randomUUID(), List.of(fileUri), Map.of(), false)).join();

    var updatedFile = new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIGURATION_SCOPE_ID, false, null, filePath, newContent, Language.PYTHON, true);
    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(), List.of(updatedFile), List.of()));

    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Error processing file event"));
    //ACR-acb6154ebf1f4472b33853c09fb6cfd0
    await().atLeast(100, TimeUnit.MILLISECONDS).untilAsserted(() ->  assertThat(sentryServer.getAllServeEvents()).hasSize(1));
  }

  @SonarLintTest
  void should_configure_dogfood_environment(SonarLintTestHarness harness) {
    startMonitoringBackend(harness);

    assertThat(Sentry.getCurrentScopes().getOptions().getEnvironment()).isEqualTo("dogfood");
  }

  @SonarLintTest
  void should_configure_production_environment_when_dogfood_disabled(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    var client = harness.newFakeClient().build();
    harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withBackendCapability(MONITORING)
      .withProductKey("idea")
      .withTelemetryEnabled()
      .start(client);

    assertThat(Sentry.getCurrentScopes().getOptions().getEnvironment()).isEqualTo("production");
  }

  @SonarLintTest
  void should_configure_production_environment_when_product_is_not_intellij(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    var client = harness.newFakeClient().build();
    harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withBackendCapability(MONITORING)
      .withProductKey("vscode")
      .withTelemetryEnabled()
      .start(client);

    assertThat(Sentry.isEnabled()).isTrue();
  }

  @SonarLintTest
  void should_configure_production_environment_when_product_is_not_intellij_and_telemetry_enabled_event_happens(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withBackendCapability(MONITORING)
      .withProductKey("vscode")
      .withTelemetryEnabled()
      .start(client);

    assertThat(Sentry.isEnabled()).isTrue();

    backend.getTelemetryService().disableTelemetry();
    await().atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(backend.getTelemetryService().getStatus().get(2, TimeUnit.SECONDS).isEnabled()).isFalse());

    backend.getTelemetryService().enableTelemetry();
    await().atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(backend.getTelemetryService().getStatus().get(2, TimeUnit.SECONDS).isEnabled()).isTrue());

    assertThat(Sentry.isEnabled()).isTrue();
  }

  @SonarLintTest
  void should_configure_production_environment_when_product_is_intellij_and_adapt_to_telemetry_event(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withBackendCapability(MONITORING)
      .withProductKey("idea")
      .withTelemetryEnabled()
      .start(client);

    assertThat(Sentry.isEnabled()).isTrue();

    backend.getTelemetryService().disableTelemetry();
    await().atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(backend.getTelemetryService().getStatus().get(2, TimeUnit.SECONDS).isEnabled()).isFalse());

    assertThat(Sentry.isEnabled()).isFalse();

    backend.getTelemetryService().enableTelemetry();
    await().atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(backend.getTelemetryService().getStatus().get(2, TimeUnit.SECONDS).isEnabled()).isTrue());

    assertThat(Sentry.isEnabled()).isTrue();
  }

  @SonarLintTest
  void should_configure_flight_recorder_environment_when_capability_enabled(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    startMonitoringBackend(harness, FLIGHT_RECORDER);

    assertThat(Sentry.getCurrentScopes().getOptions().getEnvironment()).isEqualTo("flight_recorder");
  }

  @SonarLintTest
  void should_use_sample_rate_from_system_property(SonarLintTestHarness harness) {
    withSampleRateProperty("0.42", () -> {
      startMonitoringBackend(harness);

      assertThat(Sentry.getCurrentScopes().getOptions().getTracesSampleRate()).isEqualTo(0.42);
    });
  }

  @SonarLintTest
  void should_default_sample_rate_to_zero_when_property_invalid_and_not_dogfood(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    withSampleRateProperty("invalid", () -> {
      var client = harness.newFakeClient().build();
      harness.newBackend()
        .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
        .withBackendCapability(MONITORING)
        .withProductKey("idea")
        .withTelemetryEnabled()
        .start(client);

      assertThat(Sentry.getCurrentScopes().getOptions().getTracesSampleRate()).isZero();
    });
  }

  @SonarLintTest
  void should_default_sample_rate_to_dogfood_value_when_property_invalid(SonarLintTestHarness harness) {
    withSampleRateProperty("invalid", () -> {
      startMonitoringBackend(harness);

      assertThat(Sentry.getCurrentScopes().getOptions().getTracesSampleRate()).isEqualTo(0.01);
    });
  }

  @SonarLintTest
  void should_use_flight_recorder_sample_rate_when_capability_enabled(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    withSampleRateProperty("invalid", () -> {
      startMonitoringBackend(harness, FLIGHT_RECORDER);

      assertThat(Sentry.getCurrentScopes().getOptions().getTracesSampleRate()).isEqualTo(1D);
    });
  }

  private void withSampleRateProperty(String value, Runnable action) {
    var previousValue = System.getProperty(MonitoringService.TRACES_SAMPLE_RATE_PROPERTY);
    try {
      System.setProperty(MonitoringService.TRACES_SAMPLE_RATE_PROPERTY, value);
      action.run();
    } finally {
      if (previousValue == null) {
        System.clearProperty(MonitoringService.TRACES_SAMPLE_RATE_PROPERTY);
      } else {
        System.setProperty(MonitoringService.TRACES_SAMPLE_RATE_PROPERTY, previousValue);
      }
    }
  }

  private void startMonitoringBackend(SonarLintTestHarness harness, BackendCapability... extraCapabilities) {
    var client = harness.newFakeClient().build();
    var backendBuilder = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withBackendCapability(MONITORING);
    for (var capability : extraCapabilities) {
      backendBuilder = backendBuilder.withBackendCapability(capability);
    }
    backendBuilder.start(client);
  }

  @SonarLintTest
  void should_not_initialize_sentry_when_monitoring_capability_not_enabled(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    var client = harness.newFakeClient().build();
    harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .start(client);

    assertThat(Sentry.isEnabled()).isFalse();
  }

  @SonarLintTest
  void should_verify_sentry_tags_are_set_correctly(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "test.php", """
      <?php
      function test($unused) {
          echo "Hello";
      }
      ?>
      """);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .withBackendCapability(MONITORING)
      .start(client);

    analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(sentryServer.getAllServeEvents()).isNotEmpty());

    var eventBody = sentryServer.getAllServeEvents().get(0).getRequest().getBodyAsString();
    assertThat(eventBody)
      .contains("productKey")
      .contains("sonarQubeForIDEVersion")
      .contains("ideVersion")
      .contains("platform")
      .contains("architecture");
  }

  @SonarLintTest
  void should_configure_flight_recorder_user_id_when_enabled(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    startMonitoringBackend(harness, FLIGHT_RECORDER);

    Sentry.configureScope(scope -> {
      var user = scope.getUser();
      assertThat(user).isNotNull();
      assertThat(user.getId()).isNotNull();
    });
  }

  @SonarLintTest
  void should_enable_sentry_logs_when_flight_recorder_enabled(SonarLintTestHarness harness) {
    environmentVariables.set(DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY, null);

    startMonitoringBackend(harness, FLIGHT_RECORDER);

    assertThat(Sentry.getCurrentScopes().getOptions().getLogs().isEnabled()).isTrue();
  }

  @SonarLintTest
  void should_not_enable_sentry_logs_when_flight_recorder_not_enabled(SonarLintTestHarness harness) {
    startMonitoringBackend(harness);

    assertThat(Sentry.getCurrentScopes().getOptions().getLogs().isEnabled()).isFalse();
  }

  @SonarLintTest
  void should_close_sentry_when_telemetry_is_disabled(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withBackendCapability(MONITORING)
      .withTelemetryEnabled()
      .start(client);

    assertThat(Sentry.isEnabled()).isTrue();

    backend.getTelemetryService().disableTelemetry();

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(Sentry.isEnabled()).isFalse());
  }

  @SonarLintTest
  void should_verify_dsn_is_configurable_via_system_property(SonarLintTestHarness harness) {
    startMonitoringBackend(harness);

    var options = Sentry.getCurrentScopes().getOptions();
    assertThat(options.getDsn()).contains("localhost:" + sentryServer.port());
  }

  @SonarLintTest
  void should_create_analysis_trace_with_system_metrics(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "test.php", """
      <?php
      echo "test";
      ?>
      """);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .withBackendCapability(MONITORING)
      .start(client);

    analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(sentryServer.getAllServeEvents()).isNotEmpty());

    var eventBody = sentryServer.getAllServeEvents().stream()
      .map(e -> e.getRequest().getBodyAsString())
      .filter(body -> body.contains("AnalysisService"))
      .findFirst();

    assertThat(eventBody).isPresent();
  }

  @SonarLintTest
  void sentry_should_be_enabled_in_dogfood_environment(SonarLintTestHarness harness) {
    startMonitoringBackend(harness);

    assertThat(Sentry.isEnabled()).isTrue();
  }

  @SonarLintTest
  void should_handle_multiple_analyses_with_tracing(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile1 = createFile(baseDir, "file1.php", """
      <?php
      function test1($unused) { echo "1"; }
      ?>
      """);
    var inputFile2 = createFile(baseDir, "file2.php", """
      <?php
      function test2($unused) { echo "2"; }
      ?>
      """);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile1.toUri(), baseDir.relativize(inputFile1), CONFIGURATION_SCOPE_ID, false, null, inputFile1, null, null, true),
        new ClientFileDto(inputFile2.toUri(), baseDir.relativize(inputFile2), CONFIGURATION_SCOPE_ID, false, null, inputFile2, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .withBackendCapability(MONITORING)
      .start(client);

    analyzeFileAndGetIssues(inputFile1.toUri(), client, backend, CONFIGURATION_SCOPE_ID);
    analyzeFileAndGetIssues(inputFile2.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(sentryServer.getAllServeEvents()).hasSize(2));
  }

  @SonarLintTest
  void should_reinitialize_sentry_when_telemetry_is_enabled_after_being_disabled(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withBackendCapability(MONITORING)
      .withTelemetryEnabled()
      .start(client);

    assertThat(Sentry.isEnabled()).isTrue();

    backend.getTelemetryService().disableTelemetry();

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(Sentry.isEnabled()).isFalse());

    backend.getTelemetryService().enableTelemetry();

    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(Sentry.isEnabled()).isTrue());
  }

  @SonarLintTest
  void should_not_send_events_when_sentry_is_closed(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "test.php", """
      <?php
      function test($unused) { echo "test"; }
      ?>
      """);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .withBackendCapability(MONITORING)
      .withTelemetryEnabled()
      .start(client);

    //ACR-f5ce2a36a6314bd68770bebb087be26f
    backend.getTelemetryService().disableTelemetry();
    await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(Sentry.isEnabled()).isFalse());

    //ACR-0be6aca3090e4c7382ea505742dbb3dc
    sentryServer.resetAll();
    setupSentryStubs();

    analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    await().during(2000, TimeUnit.MILLISECONDS).untilAsserted(() -> assertThat(sentryServer.getAllServeEvents()).isEmpty());
  }
}
