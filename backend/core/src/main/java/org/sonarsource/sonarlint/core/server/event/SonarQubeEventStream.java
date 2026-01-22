/*
ACR-63cce8ca1baf40de9986bc1fece26ce9
ACR-abfbf6c5a2cd457d929ac0a6b0b8a256
ACR-daeea8dbcbc24c4890fdd53f235509c3
ACR-14d368c1c84b4dee95c325c5dd275c50
ACR-8684084778cc426a89a93383d02541c3
ACR-240794a1349e42b1b271fe57dab5c673
ACR-954974573dc341199d6a5b26ac4a41dc
ACR-21294cb4bb9a4d4a9bf89e70c1b94abd
ACR-eb2c2f67bc574da581cf45a1f3496249
ACR-caf36a60921244fbba7168485c58b241
ACR-f8b3d877feb14eaab1b9ed5b43857e25
ACR-fa12deb7449d43439e3a681e317a1552
ACR-c9dbfb6347844e449564a4b3aad4e078
ACR-a088815a2389400eb216bb7b14bef3a7
ACR-663cac19d17449afbaf52731a07b80e5
ACR-84a972424ac947ba85f7887801b4962c
ACR-8e75526d6fa848e7bb52ba5e0fcc89ae
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
