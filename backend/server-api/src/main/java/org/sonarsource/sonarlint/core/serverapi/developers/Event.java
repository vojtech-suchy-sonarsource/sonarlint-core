/*
ACR-20c2de691b7645af942775e45355ceb6
ACR-0d96138706284596a58a919101f4a017
ACR-c3f1214993a74828b19aa35d7c824b2b
ACR-9a8ff0dc61c74e63861c8bcb84684cba
ACR-e61d57c05810495a81fd4f7b24c8061d
ACR-ed85cf2756ef472dae3db524679c49d1
ACR-15b250040036479ca7f17fa2c17f3c68
ACR-a5ce46a8a6df42d9bd178055c25f7cb2
ACR-2143f8373539476ab89652324834dc89
ACR-479cc131f883455db6627e691f1c6972
ACR-7cc7e729a48443c0bcc012b5bf821291
ACR-1842600590544c068a1b691f4a2f9890
ACR-615c0f62c1724f16b1218ac36ddfaf39
ACR-69692ca2dd324eac916e08fb935f1078
ACR-12c74910f0054f9cb554120139c4b801
ACR-a3b634d846ff42afb25801e15ccd5470
ACR-00e043ee47f2458598e2055b5b7dabea
 */
package org.sonarsource.sonarlint.core.serverapi.developers;

import java.time.ZonedDateTime;

public class Event {
  private final String category;
  private final String message;
  private final String link;
  private final String projectKey;
  private final ZonedDateTime time;

  public Event(String category, String message, String link, String projectKey, ZonedDateTime time) {
    this.category = category;
    this.message = message;
    this.link = link;
    this.projectKey = projectKey;
    this.time = time;
  }

  public String getCategory() {
    return category;
  }

  public String getMessage() {
    return message;
  }

  public String getLink() {
    return link;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public ZonedDateTime getTime() {
    return time;
  }
}
