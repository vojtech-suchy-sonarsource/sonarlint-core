/*
ACR-51c6fceddb684c5e9e315d64065fbcbe
ACR-7ab62f5157d84d588fc61ae85847b755
ACR-d08f687a8f384ad6b879c367d3da753e
ACR-53cd437eec104ca1a735392d557046d7
ACR-b541e24f442548e8826354816172f172
ACR-5492286e44fd4763bf6da5cf6c6d534f
ACR-e24f9fac81754b569d741ab1ffc2120b
ACR-450a11b4576c46f1bc681dd2f5c80413
ACR-ab46788ce58b4d8286580e1e478727b7
ACR-3c6c6909f5a24b388a21cd805e6affe0
ACR-609d6cb8e70b47f79018a08b79e58edf
ACR-5d02f18cbfc54b0684f8c96e905c6073
ACR-f1a14960bad64ddd905001b2a22df398
ACR-5b59f57f8599499089d77699417bce97
ACR-7df0d4fec07b464a82b64ea434f76ca9
ACR-90ea729efc044b5bb78c1dc72a98ca69
ACR-85da72eb6032470a8c6ed7604b41bf8d
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
