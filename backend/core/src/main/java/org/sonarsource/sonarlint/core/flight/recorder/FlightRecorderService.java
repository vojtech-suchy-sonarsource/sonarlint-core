/*
ACR-cb54602a27c74a36a451984246620e9a
ACR-871f65a4ce564cddb621655d4d1570e7
ACR-d9c06a5a675e4bdca7452d72fca4a721
ACR-cfb22d36a057461194b92ef287f37ce1
ACR-5c668589a3d1453ca8312fb4994b7d86
ACR-1b6be4d90a504b4099da5224b183d301
ACR-8bd0143e6d3a40bea13abe1b8722654a
ACR-e8718061c1a144d895eb10dd7c3eb2f7
ACR-d0ce07abb3d2434e97204d2c0fba3c64
ACR-8244f4290d254e358e2a1c1a51bb4d15
ACR-40d92433ca0443d79d34800f310795f2
ACR-94eecdb7fd99408e9d6de4c96d877d83
ACR-2445f904a6e447a48e6aa1a41c6985ea
ACR-ff7b311ffa144d5990fb07c2c3a39b16
ACR-94e9612ae1564451af68f492ecc964ac
ACR-862dac046382477d9a0ef9141cd54a3f
ACR-7c6b7bb285d646d7b838e556ef0e7edd
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
