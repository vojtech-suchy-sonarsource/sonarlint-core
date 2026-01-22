/*
ACR-c6cc9a94488644359de1cd4280328397
ACR-b56732c3a8a24cf4a3d228e9b8b84bdf
ACR-767c6487debb421f8ba657f939e78eaf
ACR-b121f65bb3b54bfaa999e92fe1d8355a
ACR-2df693537686475da7e4650b81e4d8c3
ACR-a3e35e51f5b445298c5f4192d4654a1c
ACR-db80b3154ba94f1ab2e7276def8ba704
ACR-0b7ff25005504dc39bf004ed62d3296d
ACR-4265900a8c414c23ab790b334ecc5831
ACR-e3722c3c70254fcca5d5a68a0eb02a67
ACR-2e7b7720f9d54eca976ab3e487a60e68
ACR-6aa875b09f464d6a969f5c35a098cae4
ACR-eb4e8a8f4f594ee5b3a851d50c84a2b7
ACR-a54aa766dd464b7bb4928a9a2e88ab4d
ACR-d5990115914f401a8d5c94876ce5c675
ACR-d4de4a3067814874b7911f1d06d4cf84
ACR-0597a1bbe36646eb92c07d0444a3d98c
 */
package org.sonarsource.sonarlint.core.websocket.events;

import org.sonarsource.sonarlint.core.serverapi.push.SonarServerEvent;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

public record SmartNotificationEvent(String message, String link, String project, String date,
                                     String category) implements SonarServerEvent {

  public SmartNotificationEvent(String message, String link, String project, String date, String category) {
    this.message = escapeHtml4(message);
    this.link = link;
    this.project = project;
    this.date = date;
    this.category = category;
  }

}
