/*
ACR-38530b866e2c409583478f4c98f5174f
ACR-03977f86d21b40efb8a11ef2c22d7677
ACR-961e38825ebe4e069895e42ba169461f
ACR-31b6dc8797b04f908fb4ac3f51733030
ACR-73157f92a4b44350ab6ad47e64a33037
ACR-a511c1fa34084713b987e2e141eccfa7
ACR-cad224fd94c64f3e80cc5dd7692145f0
ACR-7b93d784c0104d1e99d5c9a753c8aae1
ACR-569120ffc6a64b3c92ece1e85b72cf63
ACR-bb0c64cd73fb412b86de035f1e5614f7
ACR-333a9c30bb6943f29a07b1497f5506e0
ACR-660465a938be4638af72d08dbb792117
ACR-f1ae66dd75bd4335a3612f6cc4f54f03
ACR-62af211b286a4f8099182e7f86c0f912
ACR-652d12f82eac4ba3a2d299cf19cc64a9
ACR-456c13a9969b4c45a5def7d08f547c2a
ACR-8dbd850f4bc84d95a208c03a0e33104b
 */
package org.sonarsource.sonarlint.core.server.event;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.push.SonarServerEvent;
import org.sonarsource.sonarlint.core.serverapi.stream.EventStream;

public class SonarQubeEventStream {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private EventStream eventStream;
  private final Set<String> subscribedProjectKeys = new LinkedHashSet<>();
  private final Set<SonarLanguage> enabledLanguages;
  private final String connectionId;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final Consumer<SonarServerEvent> eventConsumer;

  public SonarQubeEventStream(Set<SonarLanguage> enabledLanguages, String connectionId, SonarQubeClientManager sonarQubeClientManager, Consumer<SonarServerEvent> eventConsumer) {
    this.enabledLanguages = enabledLanguages;
    this.connectionId = connectionId;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.eventConsumer = eventConsumer;
  }

  public synchronized void subscribeNew(Set<String> possiblyNewProjectKeys) {
    if (!possiblyNewProjectKeys.isEmpty() && !subscribedProjectKeys.containsAll(possiblyNewProjectKeys)) {
      cancelSubscription();
      subscribedProjectKeys.addAll(possiblyNewProjectKeys);
      attemptSubscription(subscribedProjectKeys);
    }
  }

  public synchronized void resubscribe() {
    cancelSubscription();
    if (!subscribedProjectKeys.isEmpty()) {
      attemptSubscription(subscribedProjectKeys);
    }
  }

  public synchronized void unsubscribe(String projectKey) {
    cancelSubscription();
    subscribedProjectKeys.remove(projectKey);
    if (!subscribedProjectKeys.isEmpty()) {
      attemptSubscription(subscribedProjectKeys);
    }
  }

  private void attemptSubscription(Set<String> projectKeys) {
    if (!enabledLanguages.isEmpty()) {
      try {
        sonarQubeClientManager.withActiveClient(connectionId,
          serverApi -> eventStream = serverApi.push().subscribe(projectKeys, enabledLanguages, e -> notifyHandlers(e, eventConsumer)));
      } catch (Exception e) {
        LOG.debug("Error while subscribing to event-stream", e);
      }
    }
  }

  private static void notifyHandlers(SonarServerEvent sonarServerEvent, Consumer<SonarServerEvent> clientEventConsumer) {
    clientEventConsumer.accept(sonarServerEvent);
  }

  private void cancelSubscription() {
    if (eventStream != null) {
      eventStream.close();
      eventStream = null;
    }
  }

  public synchronized void stop() {
    subscribedProjectKeys.clear();
    cancelSubscription();
  }

}
