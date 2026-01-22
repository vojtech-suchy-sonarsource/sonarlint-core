/*
ACR-55bf6e5c181642eb93a1e6ad6aa3c1d9
ACR-265598ead17c463caaf73549454f987f
ACR-d0ade154d0104c7687043c7b53157d17
ACR-3c89d9f5773d489a86429c4eb45a46b2
ACR-9dd5cb09b1c74517a7d23294e4ef7c21
ACR-42763fa159c04d39995012d7f0c6a0a9
ACR-94cc555cdbe44ed891ba5faaf51cb0c9
ACR-85bbdc71524042a1a5a113920b37ecb6
ACR-601d040e92194d2695d2a5320bb1a89f
ACR-65c63694c6054d4db11f0ff7cae42f99
ACR-aadce7a7dff44e01bd344a30d5766eaa
ACR-ae90fba09cb94513b77496fa9f03d1fc
ACR-57ec32a902bf4a33883bd5c36d4dae36
ACR-f26a910855bc4c24aba943abf9271783
ACR-3e94ce7817dd4105a4781f4b4fa2e487
ACR-59f87ccedbe44420ba9de8eab60a5876
ACR-4f27d53759ed4fdf8c93767711bd0f5f
 */
package org.sonarsource.sonarlint.core.event;

import org.sonarsource.sonarlint.core.serverapi.push.SonarServerEvent;

public class SonarServerEventReceivedEvent {
  private final String connectionId;
  private final SonarServerEvent event;

  public SonarServerEventReceivedEvent(String connectionId, SonarServerEvent event) {
    this.connectionId = connectionId;
    this.event = event;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public SonarServerEvent getEvent() {
    return event;
  }
}
