/*
ACR-b25019d1cd9b45619e03cd77d08bc7e2
ACR-c16def6cdb84429197ec2de964900460
ACR-dda705f6501a4a1c9b21748c190ae2c9
ACR-5a6d37dea4774b84b10ba1939cd47ae9
ACR-64736d0847c94ab1953a8b6d804644b7
ACR-8faef48039e249a7a81a81ae0c438288
ACR-1bf6eff387fa423ea76fc2108c61053f
ACR-f467e267c77043079b87a92ff6719b8d
ACR-d293ea387ffe41b5a404b96764676dc6
ACR-59e705f56e6f4b76aba3045552dc24d3
ACR-7fd5b2df2d974cd48e0e41e6a913322d
ACR-21a10a10e0f14b89abd5fcf70140d855
ACR-898f5140ef84461d9eac9f96ee35aab5
ACR-28c8ee0ef7f44ecd851bbc8a555307da
ACR-5e2c286b9719483291fc1c348b095d9c
ACR-1040df8eb14741bd8f6f9b38e069c48e
ACR-99e5629502e34ead840da557806fa1c2
 */
package org.sonarsource.sonarlint.core.monitoring;

import io.sentry.Hint;
import io.sentry.ScopeType;
import io.sentry.Sentry;
import io.sentry.SentryAttributeType;
import io.sentry.SentryBaseEvent;
import io.sentry.SentryLogEventAttributeValue;
import io.sentry.SentryOptions;
import io.sentry.protocol.User;
import jakarta.inject.Inject;
import org.apache.commons.lang3.SystemUtils;
import org.sonarsource.sonarlint.core.commons.SonarLintCoreVersion;
import org.sonarsource.sonarlint.core.commons.dogfood.DogfoodEnvironmentDetectionService;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.tracing.Trace;
import org.sonarsource.sonarlint.core.event.TelemetryUpdatedEvent;
import org.springframework.context.event.EventListener;

public class MonitoringService {

  public static final String DSN_PROPERTY = "sonarlint.internal.monitoring.dsn";
  private static final String DSN_DEFAULT = "https://ad1c1fe3cb2b12fc2d191ecd25f89866@o1316750.ingest.us.sentry.io/4508201175089152";

  public static final String TRACES_SAMPLE_RATE_PROPERTY = "sonarlint.internal.monitoring.tracesSampleRate";
  private static final double TRACES_SAMPLE_RATE_DEFAULT = 0D;
  private static final double TRACES_SAMPLE_RATE_DOGFOOD_DEFAULT = 0.01D;
  private static final double TRACES_SAMPLE_RATE_FLIGHT_RECORDER = 1D;

  private static final String ENVIRONMENT_FLIGHT_RECORDER = "flight_recorder";
  private static final String ENVIRONMENT_PRODUCTION = "production";
  private static final String ENVIRONMENT_DOGFOOD = "dogfood";

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  public static final String INTELLIJ_PRODUCT_KEY = "idea";

  private final MonitoringInitializationParams initializeParams;
  private final DogfoodEnvironmentDetectionService dogfoodEnvDetectionService;
  private final MonitoringUserIdStore userIdStore;

  private boolean active;

  @Inject
  public MonitoringService(MonitoringInitializationParams initializeParams, DogfoodEnvironmentDetectionService dogfoodEnvDetectionService,
    MonitoringUserIdStore userIdStore) {
    this.initializeParams = initializeParams;
    this.dogfoodEnvDetectionService = dogfoodEnvDetectionService;
    this.userIdStore = userIdStore;

    this.startIfNeeded();
  }

  public void startIfNeeded() {
    if (!initializeParams.monitoringEnabled()) {
      LOG.info("Monitoring is disabled by feature flag.");
      return;
    }
    if (shouldInitializeSentry()) {
      LOG.info("Initializing Sentry");
      start();
    }
  }

  private boolean shouldInitializeSentry() {
    return (dogfoodEnvDetectionService.isDogfoodEnvironment() || initializeParams.flightRecorderEnabled()) || initializeParams.isTelemetryEnabled();
  }

  private void start() {
    Sentry.init(this::configure);
    userIdStore.getOrCreate().ifPresent(userId -> {
      var user = new User();
      user.setId(userId.toString());
      Sentry.setUser(user);
    });
    active = true;
    if (initializeParams.flightRecorderEnabled()) {
      configureFlightRecorderSession();
    }
  }

  public boolean isActive() {
    return active;
  }

  private void configure(SentryOptions sentryOptions) {
    sentryOptions.setDsn(getDsn());
    sentryOptions.setRelease(SonarLintCoreVersion.getLibraryVersion());
    sentryOptions.setEnvironment(getEnvironment());
    if (initializeParams.flightRecorderEnabled()) {
      sentryOptions.getLogs().setEnabled(true);
      var sessionId = new SentryLogEventAttributeValue(SentryAttributeType.STRING, initializeParams.flightRecorderSessionId().toString());
      sentryOptions.getLogs().setBeforeSend(logEvent -> {
        logEvent.getAttributes().put("user.id", sessionId);
        return logEvent;
      });
    }
    sentryOptions.setTag("productKey", initializeParams.productKey());
    sentryOptions.setTag("sonarQubeForIDEVersion", initializeParams.sonarQubeForIdeVersion());
    sentryOptions.setTag("ideVersion", initializeParams.ideVersion());
    sentryOptions.setTag("platform", SystemUtils.OS_NAME);
    sentryOptions.setTag("architecture", SystemUtils.OS_ARCH);
    sentryOptions.addInAppInclude("org.sonarsource.sonarlint");
    sentryOptions.setTracesSampleRate(getTracesSampleRate());
    addCaptureIgnoreRule(sentryOptions, "(?s)com\\.sonar\\.sslr\\.api\\.RecognitionException.*");
    addCaptureIgnoreRule(sentryOptions, "(?s)com\\.sonar\\.sslr\\.impl\\.LexerException.*");
    sentryOptions.setBeforeSend(MonitoringService::beforeSend);
    sentryOptions.setBeforeSendTransaction(MonitoringService::beforeSend);
  }

  private String getEnvironment() {
    if (initializeParams.flightRecorderEnabled()) {
      return ENVIRONMENT_FLIGHT_RECORDER;
    } else if (dogfoodEnvDetectionService.isDogfoodEnvironment()) {
      return ENVIRONMENT_DOGFOOD;
    }

    return ENVIRONMENT_PRODUCTION;
  }

  private static <T extends SentryBaseEvent> T beforeSend(T event, Hint hint) {
    event.setServerName(null);
    return event;
  }

  private void configureFlightRecorderSession() {
    var user = new User();
    user.setId(initializeParams.flightRecorderSessionId().toString());
    Sentry.configureScope(ScopeType.GLOBAL, scope -> scope.setUser(user));
  }

  private static String getDsn() {
    return System.getProperty(DSN_PROPERTY, DSN_DEFAULT);
  }

  private double getTracesSampleRate() {
    try {
      var sampleRateFromSystemProperty = System.getProperty(TRACES_SAMPLE_RATE_PROPERTY);
      var parsedSampleRate = Double.parseDouble(sampleRateFromSystemProperty);
      LOG.debug("Overriding trace sample rate with value from system property: {}", parsedSampleRate);
      return parsedSampleRate;
    } catch (RuntimeException e) {
      var sampleRate = TRACES_SAMPLE_RATE_DEFAULT;
      if (dogfoodEnvDetectionService.isDogfoodEnvironment()) {
        sampleRate = TRACES_SAMPLE_RATE_DOGFOOD_DEFAULT;
      }
      if (initializeParams.flightRecorderEnabled()) {
        sampleRate = TRACES_SAMPLE_RATE_FLIGHT_RECORDER;
      }
      LOG.debug("Using default trace sample rate: {}", sampleRate);
      return sampleRate;
    }
  }

  /*ACR-8b92648946cc405f95d1579e6778f126
ACR-e8f9ecf237824812b0376c32e930148e
ACR-88b54bdc39b245edb0f5d2bce6b5b02b
ACR-f8a2122df63b48c6bcb509dce5852aac
ACR-27ea54a6929a471f85d238880f62f55f
   */
  private static void addCaptureIgnoreRule(SentryOptions sentryOptions, String regex) {
    sentryOptions.addIgnoredError(regex);
  }

  public Trace newTrace(String name, String operation) {
    return Trace.begin(name, operation);
  }

  @EventListener
  public void onTelemetryUpdated(TelemetryUpdatedEvent event) {
    if (!event.isTelemetryEnabled()) {
      Sentry.close();
      active = false;
    } else if (!active && initializeParams.monitoringEnabled() && shouldInitializeSentry()) {
      LOG.info("Initializing Sentry after telemetry was enabled");
      start();
    }

  }
}
