/*
ACR-e4902651b51b49fcb1c26ad5980f26fa
ACR-ee9a50965cbb4defb88a9b419ebde22b
ACR-816dd7aed31144c49f5d9a31ebd7a414
ACR-cee9c53214b94bb4a6185a628b48e93f
ACR-778e92f5c2664fbea973335c91c8acfe
ACR-33cd5b0cd6c84fe3b037277f20b734d5
ACR-330152a3a66b42258c8e03e2bf78be7f
ACR-725da6e287504dbaa80c71372305b85c
ACR-1ed8aa265415424682857a38ed6f9f87
ACR-7260903530bf4b3a94ea9e298fa5543c
ACR-75b86fc0f0d04ab4af46d857775963bd
ACR-55dfeea256974197b01a6449929af1f1
ACR-533d61f7ca0d46e083441fcd2032053d
ACR-805142c23e3e43c0afaf5af5c05cd088
ACR-8b81506022d14b97a5f3b5d977944fe7
ACR-08b565e0711a401c993c039e8b6deea8
ACR-22218fc95fe84eb2ac5045213a11a8b6
 */
package org.sonarsource.sonarlint.core.smartnotifications;

import java.time.ZonedDateTime;

public class ServerNotification {
  private final String category;
  private final String message;
  private final String link;
  private final String projectKey;
  private final ZonedDateTime time;

  public ServerNotification(String category, String message, String link, String projectKey, ZonedDateTime time) {
    this.category = category;
    this.message = message;
    this.link = link;
    this.projectKey = projectKey;
    this.time = time;
  }

  public String category() {
    return category;
  }

  public String message() {
    return message;
  }

  public String link() {
    return link;
  }

  public String projectKey() {
    return projectKey;
  }

  public ZonedDateTime time() {
    return time;
  }

}
