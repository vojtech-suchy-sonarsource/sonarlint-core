/*
ACR-006acdcaefa74157bf59fd8d8afcad6a
ACR-444786e694b3421d985fddf363797d62
ACR-e7e75a5f8da549e78bb2114c46888151
ACR-f45ed805cda54fd8a0826ef81394122d
ACR-cd16c2686241406c9f9c8068d76a98ff
ACR-604b2a0804984624b82f91c137086433
ACR-305803f9c9284906b8aecc89a8990bb4
ACR-4a1d1ccb3c824407bc9e28f4c622db1f
ACR-ebad0d71396646559f37a67cef458ac3
ACR-37adb9cc2e2a4a09bfcb66de96dbc030
ACR-93af5d3572d442a2b57b0b33bde86ea7
ACR-71e6207ca53e4ffbb2270a62d45521bc
ACR-44743d2d9ad94300a13d0652009c96c7
ACR-3ae246118a3a4a658438811c1884004f
ACR-72b5a76da05d4a5eb25636ec81536f95
ACR-bb77023d54c44c669340041d1f74d72c
ACR-bfbc21e5c14446c2b96d3c87bb924a35
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
