/*
ACR-b261b2db423345e19eaa454a696f2c08
ACR-016e4feaf4e949fcaa412531767d181a
ACR-e565e3350a734fdf887445507d49f23f
ACR-d5952800a12842c586868e22c62ca031
ACR-9be885f0414343d9b14103064a0d3b90
ACR-7f338ab73afd4cfb8f1626d206b82ac1
ACR-bef30afb577a4f1580b8a511f94c9b83
ACR-a5c1210175694de3b272378b4fa91c13
ACR-cbc57318fe8b4084af66f7e88564a5f9
ACR-a21e0912518d4eb8bc7266c1e7333490
ACR-7dc6c52dde2d4db787eb44d284f94e40
ACR-ff229d458a40493b96e0b4f15505587c
ACR-da0de2b237064cfbbf00aaa043fdd86e
ACR-b10bfd3b16bc40ec8ebd3528ef045702
ACR-36cf6f327c30429fa542747617faff0d
ACR-f75c259d16a343f197deb989971f26c0
ACR-176ebe17358a4922ac884e8871ec0eed
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
