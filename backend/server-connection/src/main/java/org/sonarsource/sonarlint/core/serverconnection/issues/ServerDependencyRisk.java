/*
ACR-2bc3255fcee749828f4416d20c71ca6c
ACR-35ee089d431145a8a59e51fcc0aadb4a
ACR-48044abdfde5464eaf1ff7d888367039
ACR-10bc3f0f60ad46d98cd37c58ff24d767
ACR-71881c631b544bd5b95c659aecf9126f
ACR-99bb568a38204956bef473da6c4324ab
ACR-73083c9e91ae4d519a0d16b32579a720
ACR-b1ab6e8594c84d648d05878e97921c3e
ACR-89d97dfcfc4642d9946d32e9eee3d3f3
ACR-0e2d755c28364ed9bc20cf4245d2fa09
ACR-3c63347bd3244b4199dc7a13f89a2cd4
ACR-5b64dd355df74ed3bd4269e2de4de501
ACR-97b7d359bafd44f9a26c918aa8972647
ACR-8e2ed79729144436955e8d280b96a914
ACR-c8fc47e7abca4bd09b250489665d349a
ACR-a6a63745b42049cfbdf431780814e6c4
ACR-90a4362c1ce043c1bab313fa68e63e91
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
