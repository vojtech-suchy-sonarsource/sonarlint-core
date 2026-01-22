/*
ACR-3fb8cf8558984c4e8c220476d3e6f020
ACR-c956c4ca6a11492fa73824113637f44c
ACR-5c1252f30ef64ab6b6cb4ac05324cecc
ACR-e50904778d6b466aa902f40b90bfc147
ACR-99a87d5df5e44d7db52bac87a17e11e0
ACR-e88b285a03a64d1db1e847e092b22acf
ACR-78f57fa83ffd465a8bf52d32f17f2c22
ACR-e0e5c1656e0f42a3b7c6924054faac2d
ACR-901782f944a749339d8dac9bd31e0df2
ACR-8ca6e1f620364559bed45add236bbc6e
ACR-9b0ed7cb9edf4e518ed0fc7f08f4ff31
ACR-59e828985e9e4b0ca5c4961cd01c1451
ACR-21bab9a4702143f8814f17ece28eab0e
ACR-ea77b34a7ffd4d12a557cdd96d5173fb
ACR-49ada1853e2f476aabe8bdac219ca086
ACR-d9385d8d604a40c8805906f3adabca86
ACR-7f804127ea8942c7ad392c561447c182
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk;

public class ServerDependencyRiskFixtures {

  private ServerDependencyRiskFixtures() {
    //ACR-e1114f9101fa4f25991acbfd21add6ed
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
