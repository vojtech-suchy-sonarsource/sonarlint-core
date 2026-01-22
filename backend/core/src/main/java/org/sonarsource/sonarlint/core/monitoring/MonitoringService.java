/*
ACR-83a943b429dd40c78589e9b7db98f257
ACR-b6a79f60621741d995a60cafe6edcd98
ACR-9db32e1a394b41188592df54ef828d30
ACR-1ddc06c64bf9411b8e497367e1e91223
ACR-8aeefce6f5274ff8adb12550cd2fd9e3
ACR-f6fbd2b7213f4fe0ac5d48b38a1ddbec
ACR-192ce04a68f542d784469b144ec7ad20
ACR-d86eb74699ce406d8e2d65ae7fa16d67
ACR-02103d280e294a6eabe2122fdf412ca0
ACR-437b736517fe4724be0045549b2763f4
ACR-d747c1cf88ae4007a575236d19b52727
ACR-523b5c08d1144e18bd50d5d6a00f95bd
ACR-03db15c9d9c9461cbea5fb5898634b8b
ACR-409dc885d2f2448394f327c69bec42a6
ACR-27a831a5a4fb480987ef6b7642cff1be
ACR-41e7eabaa5264c1788c61ec2aeb781f9
ACR-4c737586446249b7baf09b18ac578e4a
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

  /*ACR-f48867b8327b4a0abc15492cece98f3d
ACR-aad121c8915446bd9b348efb382080af
ACR-43bd12c8f6d34a959ee85a9a3d628017
ACR-83716af796294a57add89b72d03f631d
ACR-e97d6f22cce74afaa6c63a797cdba7f9
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
