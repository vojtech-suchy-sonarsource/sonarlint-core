/*
ACR-c223dff15abe4264b6fe3691adb52473
ACR-5f957876afb14fad9fee3a81bd78893f
ACR-43ff2c5b0a7b44d8a59a17a8967321f4
ACR-c998b80a89e1468eaebd307ca10e6019
ACR-79ecc8c8b1e84f028e9f12369917b753
ACR-b6abb01aed934b1aabcf0b78c2a893bf
ACR-09b02353cffe4988af0021da6fa862ad
ACR-72824bb3d99e49cab28f946fefb3ecf0
ACR-385e37c7644048d889f0d456c502d8c1
ACR-5a299fa741414dc9957a379982413bc8
ACR-0307b75c76f84fe39eed72f82440eaac
ACR-556448eabf4c45faa368219644b0712d
ACR-589c215a21e54ca39d463f3b591f3077
ACR-c1bec7e746af4cb5946d1924b5c41de8
ACR-339da70237c646a6bd4c68b0c2ed7b03
ACR-7eaadff188d64b658ba8c11a85409760
ACR-6c2984d0d28a452898252b5824992b12
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ResolutionStatus;

public enum IssueResolutionStatus {
  ACCEPT("Accepted", "The issue is valid but will not be fixed now. It represents accepted technical debt."),
  WONT_FIX("Won't Fix", "The issue is valid but does not need fixing. It represents accepted technical debt."),
  FALSE_POSITIVE("False Positive", "The issue is raised unexpectedly on code that should not trigger an issue.");

  private final String title;
  private final String description;

  IssueResolutionStatus(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public static IssueResolutionStatus fromDto(ResolutionStatus status) {
    switch (status) {
      case ACCEPT:
        return ACCEPT;
      case WONT_FIX:
        return WONT_FIX;
      case FALSE_POSITIVE:
        return FALSE_POSITIVE;
      default:
        throw new IllegalArgumentException("Unknown status: " + status);
    }
  }
}
