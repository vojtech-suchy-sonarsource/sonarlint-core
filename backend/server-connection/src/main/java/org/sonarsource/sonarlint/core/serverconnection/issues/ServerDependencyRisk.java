/*
ACR-82aba425e2fb4d759326187312b4d0e4
ACR-f7f15b1b71254fe48570fa7a73fe95e1
ACR-626a017f8d034a19a0cd4be11b615f35
ACR-0a2e5fb874034e189e67e9241d116cdc
ACR-dd0c51c07065417e85c72bd16bf3f2e2
ACR-28c87ba4a46f4bfcaf4676a20be042c9
ACR-7cdf71831a48416a911e9bbc79c28233
ACR-ab4d1c8cc5384d47ba7b3a7eb4481202
ACR-d58aec21d7164efe9b71c84bd690a02f
ACR-860aa5998bea4dba933e530647b67bce
ACR-0e634f750dc84dab90c52c5e0423074c
ACR-a1fa1bfa46af4ae7ba7a9e43bf95a712
ACR-b8af7fb0d76e4c749b2b65de4a47a16a
ACR-b01881ea240b4bd187a56f8a28fba821
ACR-f675f0a1b7a649368e49c1fc041ea6df
ACR-72953a77396640cfb00056fe34310305
ACR-a12a2dcd0cc5408bb8002db32c2b4931
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public record ServerDependencyRisk(UUID key, Type type, Severity severity, SoftwareQuality quality,
                                   Status status, String packageName, String packageVersion, @Nullable String vulnerabilityId,
                                   @Nullable String cvssScore, List<Transition> transitions) {

  public ServerDependencyRisk withStatus(Status newStatus) {
    var newTransitions = new ArrayList<>(Arrays.asList(Transition.values()));
    newTransitions.remove(Transition.FIXED);
    newTransitions.remove(newStatus.equals(Status.OPEN) ? Transition.REOPEN : Transition.valueOf(newStatus.name()));
    return new ServerDependencyRisk(key, type, severity, quality, newStatus, packageName, packageVersion,
      vulnerabilityId, cvssScore, newTransitions);
  }

  public enum Severity {
    INFO, LOW, MEDIUM, HIGH, BLOCKER
  }

  public enum SoftwareQuality {
    MAINTAINABILITY,
    RELIABILITY,
    SECURITY
  }

  public enum Type {
    VULNERABILITY, PROHIBITED_LICENSE
  }

  public enum Status {
    OPEN, CONFIRM, ACCEPT, SAFE, FIXED
  }

  public enum Transition {
    CONFIRM, REOPEN, SAFE, FIXED, ACCEPT
  }
}
