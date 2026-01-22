/*
ACR-e7112409e2d04414844761cc1f4d6acc
ACR-b2c6c3df7a984a2ca4e5ea68c4715275
ACR-6c4b32e556654e8aba8fe33d21f0b24f
ACR-36b8e6fabb554a4084c9396adf3e7841
ACR-aa8bbfbc3ac24907bfd8a4d673eb4f91
ACR-c12a5291d27540da9232067f410c2c9d
ACR-467dea205c6441fba5508aa8c0b3a0cf
ACR-a9127e4257c1402a9d1c129cfe4c6a97
ACR-3657ab3b7f4948eba419a476cffa2bcd
ACR-7dcbf06c23e74281a4f04b2d86ca2bb2
ACR-c17a389052b74e05ac4a940c2c4bbbb8
ACR-3e95ecb98a4f4dd290a9963722371246
ACR-6b3ef836157243558d7280a739c34a65
ACR-3f88041025014ee59c2d774d1e730008
ACR-8052e8e18ff744b392f1a9f30988e810
ACR-cee0ba3595a349459ad07057bdd3c9d8
ACR-dae8ecffe41344788551ab85f5e2a7f2
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
