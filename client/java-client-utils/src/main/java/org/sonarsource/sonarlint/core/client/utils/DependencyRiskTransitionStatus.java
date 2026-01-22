/*
ACR-b80f3547e08f43f4bc7c08e675aa0f73
ACR-bf165fe206a54c1cb54e05b526e302fd
ACR-aa1ef7b3aa2e497ba4504cd2e78cdf53
ACR-235a2ea851594b05a8c0982686b15c78
ACR-18bbe491eec343cd8f2180ffc10fc5bd
ACR-dc54370d4881445c8244c3ee4935bd44
ACR-64c7d018f4e74ca9a40b8555c4762041
ACR-087498fba742437ba74e55229ef8ca40
ACR-a68e0283617c41a8910db5b103d35039
ACR-0a88e89008004a84b5bf49d55899e98a
ACR-b7b8b224e0464483a12af5405ccd0f5c
ACR-c330fe8868bb4e9ab6847d3334f0b695
ACR-9feb66279c6c42e6b6bb161517274e13
ACR-d9bdcd013f0e42d48f295a7f6d1af0f5
ACR-528cf473e32341799abb45430cb6f857
ACR-e7e751fdc9cc4b27a5f0cf8e439a460e
ACR-9fc106ecff58418592388fdbe58f3c50
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;

public enum DependencyRiskTransitionStatus {
  REOPEN("Open", "This finding has not yet been reviewed."),
  CONFIRM("Confirmed", "This finding has been reviewed and the risk is valid."),
  ACCEPT("Accepted", "This finding is valid, but it may not be fixed for a while."),
  SAFE("Safe", "This finding does not pose a risk. No fix is needed."),
  FIXED("Fixed", "This finding has been fixed.");

  private final String title;
  private final String description;

  DependencyRiskTransitionStatus(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public static DependencyRiskTransitionStatus fromDto(DependencyRiskDto.Transition status) {
    switch (status) {
      case REOPEN:
        return REOPEN;
      case CONFIRM:
        return CONFIRM;
      case ACCEPT:
        return ACCEPT;
      case SAFE:
        return SAFE;
      case FIXED:
        return FIXED;
      default:
        throw new IllegalArgumentException("Unknown status: " + status);
    }
  }
}
