/*
ACR-749d6b7346e040f0a4e5d60a1037b97e
ACR-3a4488452d8c4aaa889d883916483c5b
ACR-f04155aec79c430781f5eb84d5678423
ACR-c0e472efcdb24814b9eeb2617acc9d72
ACR-3b178ae04fcb41fca437f7aeb4a149bd
ACR-d49223da72a84decb96be71fc7822370
ACR-968ebc43417a41fcb28286dd5f46abd1
ACR-dc17c667529f4415ad79db37c3d98623
ACR-438ee98e47904474a0ae9e44ec8026d3
ACR-6f0dde2dcbf7493a9e5f8fcd59634e68
ACR-fb9f21fc48dc4b388eb31f7e7a197207
ACR-1b0ab0abe8334474b4292e8607f064b9
ACR-f9b12d54bdc54aac89086faa88cf1d77
ACR-65884b33075642f9925ab6b64d12c6b6
ACR-209c6e37f8b14da1b986c91cde1fcf41
ACR-e9acc3aef39c479eaf5c1b1aa1034b98
ACR-6d4cf0785fee422bac2b82d2ab411c69
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
