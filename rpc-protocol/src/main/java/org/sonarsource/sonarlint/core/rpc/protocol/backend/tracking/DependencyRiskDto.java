/*
ACR-4aa98cf9ae144af68c63e32c093cd918
ACR-827ac91a8da949e2963dd3b6ff8b6187
ACR-54d3a2c0540847f5929fe9e625a74bdf
ACR-3b3791e83f7148c7b9fcfd3c73099f61
ACR-00dc001ee32940df9a96005d56b6425f
ACR-8b7a9f816d3741468167c59e7e2891d5
ACR-8e09f7dbb1704664b608bdb3b8587e5c
ACR-de8de965682e4ea68c64c0bc1e5f472d
ACR-92ff5fa15f0f4075a1e68686d6707a08
ACR-72d3c4f7b87b449a9a6f19758d2ddc67
ACR-817800e523174532a96c5884d81beb80
ACR-76ecd765c23841beb1d6a2a525652256
ACR-834995d09f9841f1a3a0c226119ebcc3
ACR-0e72ea91a4b14767b7daab6cc06f6aa7
ACR-c87bfb22af4f49378b96b6c81d4e5e44
ACR-401c9dfae9f3447c989cf0e55e97677b
ACR-581fde9ef1d6407d96417ffa6ece6ede
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

import java.util.List;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class DependencyRiskDto {
  private final UUID id;
  private final Type type;
  private final Severity severity;
  private final SoftwareQuality quality;
  private final Status status;
  private final String packageName;
  private final String packageVersion;
  @Nullable
  private final String vulnerabilityId;
  @Nullable
  private final String cvssScore;
  private final List<Transition> transitions;

  public DependencyRiskDto(UUID id, Type type, Severity severity, SoftwareQuality quality, Status status, String packageName,
    String packageVersion, @Nullable String vulnerabilityId, @Nullable String cvssScore, List<Transition> transitions) {
    this.id = id;
    this.type = type;
    this.severity = severity;
    this.quality = quality;
    this.status = status;
    this.packageName = packageName;
    this.packageVersion = packageVersion;
    this.vulnerabilityId = vulnerabilityId;
    this.cvssScore = cvssScore;
    this.transitions = transitions;
  }

  public UUID getId() {
    return id;
  }

  public Type getType() {
    return type;
  }

  public Severity getSeverity() {
    return severity;
  }

  public SoftwareQuality getQuality() {
    return quality;
  }

  public Status getStatus() {
    return status;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getPackageVersion() {
    return packageVersion;
  }

  @CheckForNull
  public String getVulnerabilityId() {
    return vulnerabilityId;
  }

  @CheckForNull
  public String getCvssScore() {
    return cvssScore;
  }

  public List<Transition> getTransitions() {
    return transitions;
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
    FIXED, OPEN, CONFIRM, ACCEPT, SAFE
  }

  public enum Transition {
    CONFIRM, REOPEN, SAFE, FIXED, ACCEPT
  }
}
