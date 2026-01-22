/*
ACR-72b771d98b6440f1812d266a51820fd6
ACR-2dbaf9a1e2b1415080f10b222c24bab0
ACR-6e1f64b3fbe24d76a1996fe038beb8e5
ACR-2ab7f1a93d724c60bcec776786a72881
ACR-95d598ad4ac24d05b9e0eabdfa51993b
ACR-6c58bd7dd66a4981a7122d059c29954b
ACR-09d5ce7c5620442bbfe40a4e3d073417
ACR-f07d606026784a6082c853d34d9d872a
ACR-ba8f3aff25054942b81112a1dbad94e4
ACR-ae8eec9bedb141809520d12c349aa5c5
ACR-40d950f8e8944414b94fd713cbfc5bca
ACR-4a07dca2f6d24f7fae2e459dadd4e52b
ACR-2e66ba29ff654917bba5f70e97ec7f4b
ACR-86aaed8c82d24c269eaa67dc030c782d
ACR-a7a4e904f7174122934e80f61d46c3d3
ACR-8bf3ce8bbaa04909a31704c20595a02d
ACR-075a54aedd444e1aa1de74985ef94da7
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
