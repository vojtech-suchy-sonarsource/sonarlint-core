/*
ACR-2ad81517219449499d0443da49cde988
ACR-78cfb1b9f885454890125b1de751296d
ACR-db9fc9f502ce434d8b38c8d174e995e0
ACR-79c26707cac74fc6ba9351fcda92e764
ACR-7c34cbae273d490c82d3651047f445d4
ACR-5890277c3d3c49c58aa4d6f155563b7f
ACR-0edf15eb9dfe4035a7e024c3a7f71487
ACR-80b459ad79954bf599aac370385756b4
ACR-a3317a95923b407092a32950d1c10d79
ACR-780d61396a4442159d17a7d3fc64ba15
ACR-394dec77dd8b4199aa4ea6ea4eb713b0
ACR-d156084a44724a26838caa533feb1821
ACR-8cd021200de14a77a95c90e540762dc3
ACR-f590502ff4004fc6ae15278724d3d753
ACR-deede0e3f6ab4ee5b1d8e093812141c9
ACR-f892dc46642c4484810a336fc6720815
ACR-3b466b14ef12419989c701c7abda4de7
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
