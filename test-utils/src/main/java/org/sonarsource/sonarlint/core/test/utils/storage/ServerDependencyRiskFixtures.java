/*
ACR-367e089b8f09489a82f37923a7c03628
ACR-2111e31aa06942919adf12860591c441
ACR-6dfb15e956af47fdbf207c32e0a3d395
ACR-a21bf28696d5426c9926fc91345e05b7
ACR-88323c40c51f44179e2f9c0950f448d6
ACR-4c0f8623da04492198b4fa9ba4a73254
ACR-892ef43e0a3b4f51848137c3ca88b9e2
ACR-1f079082d61e43b7a3241a68d5b50bff
ACR-2c2b9e9835354b759981eec48ccca2e7
ACR-d912554d7171408085d762173bb4e9db
ACR-d0ef463ac94f47e99f57c3c38f4bf898
ACR-46920d53db37451aa013ef85d8657afd
ACR-ae27b05258814745a820760685dbe24a
ACR-6dad2a96852c4fa98cb6759d0759581d
ACR-0834e0ff6e954873a3a84670008ac478
ACR-4a5d7b5379314b92b4abbe96dbfb93d1
ACR-69e8f260eeb440a2aa7c2065cf737a3c
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk;

public class ServerDependencyRiskFixtures {

  private ServerDependencyRiskFixtures() {
    //ACR-bd80cf326d434b71952659e04752d944
  }

  public static ServerDependencyRiskBuilder aServerDependencyRisk() {
    return new ServerDependencyRiskBuilder();
  }

  public static class ServerDependencyRiskBuilder {
    private UUID key = UUID.randomUUID();
    private ServerDependencyRisk.Type type = ServerDependencyRisk.Type.VULNERABILITY;
    private ServerDependencyRisk.Severity severity = ServerDependencyRisk.Severity.HIGH;
    private ServerDependencyRisk.SoftwareQuality quality = ServerDependencyRisk.SoftwareQuality.SECURITY;
    private ServerDependencyRisk.Status status = ServerDependencyRisk.Status.OPEN;
    private String packageName = "com.example.vulnerable";
    private String packageVersion = "1.0.0";
    @Nullable
    private String vulnerabilityId = null;
    @Nullable
    private String cvssScore = null;
    private List<ServerDependencyRisk.Transition> transitions = List.of(ServerDependencyRisk.Transition.CONFIRM, ServerDependencyRisk.Transition.ACCEPT);

    public ServerDependencyRiskBuilder withKey(UUID key) {
      this.key = key;
      return this;
    }

    public ServerDependencyRiskBuilder withType(ServerDependencyRisk.Type type) {
      this.type = type;
      return this;
    }

    public ServerDependencyRiskBuilder withSeverity(ServerDependencyRisk.Severity severity) {
      this.severity = severity;
      return this;
    }

    public ServerDependencyRiskBuilder withQuality(ServerDependencyRisk.SoftwareQuality quality) {
      this.quality = quality;
      return this;
    }

    public ServerDependencyRiskBuilder withStatus(ServerDependencyRisk.Status status) {
      this.status = status;
      return this;
    }

    public ServerDependencyRiskBuilder withPackageName(String packageName) {
      this.packageName = packageName;
      return this;
    }

    public ServerDependencyRiskBuilder withPackageVersion(String packageVersion) {
      this.packageVersion = packageVersion;
      return this;
    }

    public ServerDependencyRiskBuilder withVulnerabilityId(String vulnerabilityId) {
      this.vulnerabilityId = vulnerabilityId;
      return this;
    }

    public ServerDependencyRiskBuilder withCvssScore(String cvssScore) {
      this.cvssScore = cvssScore;
      return this;
    }

    public ServerDependencyRiskBuilder withTransitions(List<ServerDependencyRisk.Transition> transitions) {
      this.transitions = transitions;
      return this;
    }

    public ServerDependencyRisk build() {
      return new ServerDependencyRisk(key, type, severity, quality, status, packageName, packageVersion,
        vulnerabilityId, cvssScore, transitions);
    }
  }
}
