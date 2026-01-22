/*
ACR-66aaad774be54aa7a3d4bbcbb83b9c02
ACR-f2cc86d252b240bcbcafa8799617f8ed
ACR-0e06d2e747304d4ebcffdaef4d98bbae
ACR-a47f60815c6e4673a129c2798cab6481
ACR-3697d6f86a9f4c3dbc05d878769a970a
ACR-020410b6ab144b3e91a352400e26e89c
ACR-62de6e043f4b4b318878dee1acf5003c
ACR-d083b402404942159640a37b84c2cfec
ACR-e52b04f42ede46299145746c2cbfc475
ACR-413473f9fabb467f90fb5fb2101f29d8
ACR-9d6bdba85cfd487d84ae681ba0856650
ACR-819f832cff104469815c8ceff8f7864f
ACR-0fadfccf87854d2eab89ffd48da559fc
ACR-d55268e5e31e44d483d12173eb75e9ee
ACR-52abb0375ac243ae9f28f89cc8db0a96
ACR-2dbdef78884841bd930b91edfff4f1e3
ACR-60e34ee7471f4d47b54fc777ebc95a65
 */
package org.sonarsource.sonarlint.core.flight.recorder;

import io.sentry.Attachment;
import io.sentry.Hint;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.monitoring.MonitoringService;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.flightrecorder.FlightRecorderStartedParams;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

public class FlightRecorderService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final String[] PROXY_PROPERTIES = {
    "java.net.useSystemProxies",
    "http.proxyHost",
    "http.proxyPort",
    "https.proxyHost",
    "https.proxyPort",
    "http.nonProxyHosts"
  };

  private final boolean enabled;
  private final FlightRecorderSession session;
  private final TelemetryService telemetryService;
  private final SonarLintRpcClient client;

  public FlightRecorderService(InitializeParams initializeParams, FlightRecorderSession session, MonitoringService monitoringService, TelemetryService telemetryService,
    SonarLintRpcClient client) {
    this.enabled = initializeParams.getBackendCapabilities().contains(BackendCapability.FLIGHT_RECORDER)
      && monitoringService.isActive();
    this.session = session;
    this.telemetryService = telemetryService;
    this.client = client;
  }

  @PostConstruct
  public void launch() {
    if (!enabled) {
      LOG.debug("Not starting Flight Recorder service");
      return;
    }

    LOG.info("Starting Flight Recorder service for session ", session);
    telemetryService.flightRecorderStarted();

    var startEvent = newInfoEvent("Flight recorder started");
    var defaultLocale = Locale.getDefault();
    startEvent.getContexts().put("Default Locale", Map.of(
      "Display Name", defaultLocale.getDisplayName(),
      "Language", defaultLocale.getLanguage(),
      "Country", defaultLocale.getCountry()
    ));
    var proxyProperties = getProxyProperties();
    if (!proxyProperties.isEmpty()) {
      startEvent.getContexts().put("Proxy Settings", getProxyProperties());
    }
    Sentry.captureEvent(startEvent);

    client.flightRecorderStarted(new FlightRecorderStartedParams(session.sessionId().toString()));
  }

  @PreDestroy
  public void shutdown() {
    if (!enabled) {
      return;
    }

    sendInfoEvent("Flight recorder stopped");
  }

  public void captureThreadDump() {
    if (!enabled) {
      LOG.debug("Ignoring thread dump capture request, not in a flight recording session");
      return;
    }

    var threadDump = new StringBuilder();
    var threadBean = ManagementFactory.getThreadMXBean();
    Arrays.stream(threadBean.dumpAllThreads(true, true))
      .forEach(t -> threadDump.append(t.toString()).append(System.lineSeparator()));
    var threadDumpAttachment = new Attachment(threadDump.toString().getBytes(StandardCharsets.UTF_8), "threads.txt");

    Sentry.captureEvent(newInfoEvent("Captured thread dump"), Hint.withAttachment(threadDumpAttachment));
  }

  private static void sendInfoEvent(String message) {
    var flightRecorderStarted = newInfoEvent(message);
    Sentry.captureEvent(flightRecorderStarted);
  }

  private static SentryEvent newInfoEvent(String eventMessage) {
    var flightRecorderStarted = new SentryEvent();
    flightRecorderStarted.setLevel(SentryLevel.INFO);
    var message = new Message();
    message.setMessage(eventMessage);
    flightRecorderStarted.setMessage(message);
    return flightRecorderStarted;
  }

  private static Map<String, String> getProxyProperties() {
    var proxySettings = new LinkedHashMap<String, String>();
    Stream.of(PROXY_PROPERTIES).forEach(propKey -> {
      var propValue = System.getProperty(propKey);
      if (propValue != null) {
        proxySettings.put(propKey, propValue);
      }
    });
    return proxySettings;
  }
}
