/*
ACR-8b168479967d4faeafc2882d6c73b819
ACR-cc629d80159a4cf99325549a1d7bf8ac
ACR-b7b0c3a3d209479a85056478a402891b
ACR-b483fca98bfa4ddcb25993f6f106539b
ACR-d5408d7651e7494780eaf481b12a7599
ACR-82e17f233a534ebcb33bf4d5bfa21f96
ACR-4c2ebb3f3b024334ad6b681c4533fd3c
ACR-8c4937bb12f14e678ebbcd21ab716a97
ACR-6974456485c743ffa540906f5d25c354
ACR-f4b1427dd03944609ce4c53cb66c832d
ACR-cf615b75c08f4cd6baf9cf1c31ae5a7a
ACR-e1d0d140530545c38e8687fab313e9b8
ACR-896ae6f2d96e46f890c34b2edefd6889
ACR-ff022b9d7fd04296bb05abb821e26f44
ACR-d0f688b1667348ebab1a6dbb3d797da8
ACR-baba19732d4741e08934155a518e6a5e
ACR-ab5976573d8141e289a472d2f27fe981
 */
package org.sonarsource.sonarlint.core.serverapi.push;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.EventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.IssueChangedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.RuleSetChangedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.SecurityHotspotChangedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.SecurityHotspotClosedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.SecurityHotspotRaisedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.TaintVulnerabilityClosedEventParser;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.TaintVulnerabilityRaisedEventParser;
import org.sonarsource.sonarlint.core.serverapi.stream.Event;
import org.sonarsource.sonarlint.core.serverapi.stream.EventStream;

public class PushApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final String API_PATH = "api/push/sonarlint_events";
  private static final Map<String, EventParser<?>> parsersByType = Map.of(
    "RuleSetChanged", new RuleSetChangedEventParser(),
    "IssueChanged", new IssueChangedEventParser(),
    "TaintVulnerabilityRaised", new TaintVulnerabilityRaisedEventParser(),
    "TaintVulnerabilityClosed", new TaintVulnerabilityClosedEventParser(),
    "SecurityHotspotRaised", new SecurityHotspotRaisedEventParser(),
    "SecurityHotspotChanged", new SecurityHotspotChangedEventParser(),
    "SecurityHotspotClosed", new SecurityHotspotClosedEventParser());

  private final ServerApiHelper helper;

  public PushApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public EventStream subscribe(Set<String> projectKeys, Set<SonarLanguage> enabledLanguages, Consumer<SonarServerEvent> serverEventConsumer) {
    return new EventStream(helper, rawEvent -> handleRawEvent(rawEvent, serverEventConsumer))
      .connect(getWsPath(projectKeys, enabledLanguages));
  }

  private static String getWsPath(Set<String> projectKeys, Set<SonarLanguage> enabledLanguages) {
    return API_PATH + "?projectKeys=" +
      projectKeys.stream().map(UrlUtils::urlEncode).collect(Collectors.joining(",")) +
      "&languages=" +
      enabledLanguages.stream().map(SonarLanguage::getSonarLanguageKey).map(UrlUtils::urlEncode).collect(Collectors.joining(","));
  }

  private static void handleRawEvent(Event rawEvent, Consumer<SonarServerEvent> serverEventConsumer) {
    LOG.debug("Server event received: {}", rawEvent);
    parse(rawEvent).ifPresent(serverEventConsumer);
  }

  private static Optional<? extends SonarServerEvent> parse(Event event) {
    var eventType = event.getType();
    if (!parsersByType.containsKey(eventType)) {
      LOG.error("Unknown '{}' event type ", eventType);
      return Optional.empty();
    }
    try {
      return parsersByType.get(eventType).parse(event.getData());
    } catch (Exception e) {
      LOG.error("Cannot parse '{}' received event", eventType, e);
    }
    return Optional.empty();
  }

}
